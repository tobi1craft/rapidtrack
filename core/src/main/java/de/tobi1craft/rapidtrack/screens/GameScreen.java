package de.tobi1craft.rapidtrack.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.IntAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import de.tobi1craft.rapidtrack.ResourceManager;
import de.tobi1craft.rapidtrack.UI;
import de.tobi1craft.rapidtrack.bullet.camera.CameraController;
import de.tobi1craft.rapidtrack.ingame.Block;
import de.tobi1craft.rapidtrack.ingame.Car;
import de.tobi1craft.rapidtrack.ingame.InputManager;
import de.tobi1craft.rapidtrack.ingame.Track;
import de.tobi1craft.rapidtrack.ingame.camera.Cam1;
import de.tobi1craft.rapidtrack.ingame.camera.FreeCam;
import de.tobi1craft.rapidtrack.ingame.physics.PhysicsSystem;
import net.mgsx.gltf.scene3d.attributes.PBRColorAttribute;
import net.mgsx.gltf.scene3d.attributes.PBRCubemapAttribute;
import net.mgsx.gltf.scene3d.attributes.PBRTextureAttribute;
import net.mgsx.gltf.scene3d.lights.DirectionalLightEx;
import net.mgsx.gltf.scene3d.scene.Scene;
import net.mgsx.gltf.scene3d.scene.SceneAsset;
import net.mgsx.gltf.scene3d.scene.SceneManager;
import net.mgsx.gltf.scene3d.scene.SceneSkybox;
import net.mgsx.gltf.scene3d.utils.IBLBuilder;

import java.util.ArrayList;

public class GameScreen extends Menu {

    private final Track track;
    private final SceneManager sceneManager;
    private final PerspectiveCamera camera;
    private final Cubemap diffuseCubemap;
    private final Cubemap environmentCubemap;
    private final Cubemap specularCubemap;
    private final Texture brdfLUT;
    private final SceneSkybox skybox;
    private final PhysicsSystem physicsSystem;
    private final ArrayList<Block> finishes = new ArrayList<>();
    private final InputMultiplexer inputMultiplexer = new InputMultiplexer();
    public InputManager inputManager;
    long resetAt;
    private Car car;
    private CameraController cameraController;
    private boolean drawDebug = false;
    private Vector3 startPos = new Vector3();
    private long startTimestamp = System.nanoTime() + TimeUtils.millisToNanos(3000); //TODO
    private long pauseTimestamp;
    private long finishTime;
    private Label timeLabel;
    private boolean paused;
    private long pb;
    private String pbText;
    private Label pbLabel;
    private Model finishWallModel;
    private Label countdownLabel;
    private Label speedLabel;

    public GameScreen() {
        setupStage();
        physicsSystem = new PhysicsSystem();
        sceneManager = new SceneManager();
        inputManager = new InputManager(this);

        track = new Track();

        for (Block block : track.grid) {
            sceneManager.addScene(block.getScene());
            if (block.hasCollision()) {
                btCollisionShape shape = Bullet.obtainStaticNodeShape(block.getScene().modelInstance.nodes);
                shape.setMargin(0f);
                btRigidBody.btRigidBodyConstructionInfo sceneInfo = new btRigidBody.btRigidBodyConstructionInfo(0, null, shape, Vector3.Zero);
                btRigidBody sceneBody = new btRigidBody(sceneInfo);
                sceneInfo.dispose();
                sceneBody.setWorldTransform(block.getScene().modelInstance.transform);
                sceneBody.setFriction(block.getFriction()); //! 0.5 is default
                physicsSystem.getDynamicsWorld().addRigidBody(sceneBody);
            }
            if (block.isStart()) startPos = block.getGridPos();
            if (block.isFinish()) finishes.add(block);
        }
        createFinishWalls();

        assets.loadAndGet("models/car.glb", SceneAsset.class); //TODO: global loading screen
        car = new Car(this, startPos, finishes);
        sceneManager.addScene(car.getScene());


        // setup camera (use a reasonable near/far to preserve depth precision)
        camera = new PerspectiveCamera(60f, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.near = 0.3f;
        camera.far = 1000;
        camera.position.set(startPos.cpy().scl(Track.SCALE).add(0, 3, 5));
        sceneManager.setCamera(camera);

        if (drawDebug) physicsSystem.render(camera);
        cameraController = new Cam1(camera, car);


        //TODO: improve rendering according to gdx-gltf docs

        // setup light
        DirectionalLightEx light = new DirectionalLightEx();
        light.direction.set(1, -3, 1).nor();
        light.color.set(Color.WHITE);
        sceneManager.environment.add(light);

        // setup quick IBL (image based lighting)
        IBLBuilder iblBuilder = IBLBuilder.createOutdoor(light);
        // Set ground colors to match sky colors for all-around sky (removes brown ground)
        iblBuilder.nearGroundColor.set(iblBuilder.nearSkyColor);
        iblBuilder.farGroundColor.set(iblBuilder.nearSkyColor);
        iblBuilder.farSkyColor.set(iblBuilder.nearSkyColor);
        environmentCubemap = iblBuilder.buildEnvMap(1024);
        diffuseCubemap = iblBuilder.buildIrradianceMap(256);
        specularCubemap = iblBuilder.buildRadianceMap(10);
        iblBuilder.dispose();

        // This texture is provided by the library, no need to have it in your assets.
        brdfLUT = new Texture(Gdx.files.classpath("net/mgsx/gltf/shaders/brdfLUT.png"));

        sceneManager.setAmbientLight(1f);
        sceneManager.environment.set(new PBRTextureAttribute(PBRTextureAttribute.BRDFLUTTexture, brdfLUT));
        sceneManager.environment.set(PBRCubemapAttribute.createSpecularEnv(specularCubemap));
        sceneManager.environment.set(PBRCubemapAttribute.createDiffuseEnv(diffuseCubemap));

        // setup skybox
        skybox = new SceneSkybox(environmentCubemap);
        sceneManager.setSkyBox(skybox);
    }

    private static String formatTime(long time) {
        long totalMillis = Math.abs(TimeUtils.nanosToMillis(time));

        int hours = (int) (totalMillis / 3600000);
        int minutes = (int) ((totalMillis % 3600000) / 60000);
        int seconds = (int) ((totalMillis % 60000) / 1000);
        int millis = (int) (totalMillis % 1000);

        StringBuilder builder = new StringBuilder();
        if (time < 0) builder.append("-");
        if (hours > 0) {
            builder.append(hours).append(":").append(String.format("%02d:", minutes)).append(String.format("%02d", seconds));
        } else if (minutes > 0) {
            builder.append(minutes).append(":").append(String.format("%02d", seconds));
        } else {
            builder.append(seconds);
        }
        builder.append(String.format(".%03d", millis));
        return builder.toString();
    }

    private void createFinishWalls() {
        ModelBuilder builder = new ModelBuilder();


        Material material = new Material();
        material.set(PBRColorAttribute.createBaseColorFactor(new Color(1, 0, 0, 0.2f)));
        material.set(new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA));
        material.set(IntAttribute.createCullFace(0)); //! Von beiden Seiten sichtbar

        float x = Track.SCALE.x / 2;
        float y = Track.SCALE.y;

        finishWallModel = builder.createRect(
            -x, 0, 0,
            -x, y, 0,
            x, y, 0,
            x, 0, 0,
            0, 0, 1,
            material,
            VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal
        );

        for (Block block : finishes) {
            ModelInstance instance = new ModelInstance(finishWallModel);
            Vector3 position = block.getGridPos().cpy().scl(Track.SCALE);
            instance.transform.setTranslation(position);

            Scene scene = new Scene(instance);
            sceneManager.addScene(scene);
        }
    }

    @Override
    public void pause() {
        paused = true;
        pauseTimestamp = System.nanoTime();
        super.pause();
    }

    @Override
    public void resume() {
        startTimestamp += System.nanoTime() - pauseTimestamp;
        pauseTimestamp = 0;
        paused = false;
        super.resume();
    }

    public long timer() {
        return System.nanoTime() - startTimestamp;
    }

    public boolean isFinished() {
        return finishTime != 0;
    }

    public void finish() {
        if (isFinished()) return; //! Methode wird mehrmals aufgerufen
        finishTime = timer();
        if (pb == 0 || pb > finishTime) {
            pb = finishTime;
            pbText = formatTime(pb);
        }
        resetAt = System.nanoTime() + TimeUtils.millisToNanos(3000);
    }

    private void reset() {
        resetAt = 0;
        sceneManager.removeScene(car.getScene());
        car.dispose();
        car = new Car(this, startPos, finishes);
        sceneManager.addScene(car.getScene());
        camera.position.set(startPos.cpy().scl(Track.SCALE).add(0, 3, 5));
        if (cameraController instanceof Cam1) {
            inputMultiplexer.removeProcessor(cameraController);
            cameraController = new Cam1(camera, car);
            inputMultiplexer.addProcessor(0, cameraController);
        }
        finishTime = 0;
        startTimestamp = System.nanoTime() + TimeUtils.millisToNanos(1500);
    }

    public PhysicsSystem getPhysicsSystem() {
        return physicsSystem;
    }

    private void setupStage() {
        stage = new Stage(new ScreenViewport(), ResourceManager.getInstance().getBatch());

        Table timeTable = new Table();
        timeTable.setFillParent(true);
        stage.addActor(timeTable);

        Table speedTable = new Table();
        speedTable.setFillParent(true);
        stage.addActor(speedTable);

        resize = (width, height) -> {
            timeTable.clearChildren();
            speedTable.clearChildren();

            pbLabel = UI.getLiteralLabel(height * 0.1f, "", Color.WHITE);
            timeTable.add(pbLabel).expand().top().right().pad(height * 0.02f).row();

            countdownLabel = UI.getLiteralLabel(height * 0.4f, "", new Color(0, 0.8f, 0.3f, 1));
            timeTable.add(countdownLabel).expand().pad(height * 0.02f).row();

            timeLabel = UI.getLiteralLabel(height * 0.1f, "", Color.WHITE);
            timeTable.add(timeLabel).expand().bottom().pad(height * 0.02f);

            speedLabel = UI.getLiteralLabel(height * 0.1f, "0", Color.CYAN);
            speedTable.add(speedLabel).expand().bottom().right().pad(height * 0.02f);

            sceneManager.updateViewport(width, height);
        };
    }

    @Override
    public void render(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.F1)) drawDebug = !drawDebug;
        if (Gdx.input.isKeyJustPressed(Input.Keys.F5)) switchCamera();

        if (Gdx.input.isKeyJustPressed(Input.Keys.FORWARD_DEL)) reset();
        if (resetAt != 0 && resetAt < System.nanoTime()) reset();

        if (!paused) {
            car.update(delta);
            cameraController.update(delta);
            physicsSystem.update(delta);

            if (pauseTimestamp == 0) {
                if (!isFinished()) timeLabel.setText(formatTime(timer()));
                else timeLabel.setText(formatTime(finishTime));
            }
            if (timer() < 0) countdownLabel.setText(String.valueOf(TimeUtils.nanosToMillis(timer()) / -500L + 1));
            else countdownLabel.setText("");
            if (pbText != null && !pbLabel.textEquals("PB: " + pbText)) pbLabel.setText("PB: " + pbText);
            if (car != null && timer() > 0) speedLabel.setText(String.valueOf((int) car.getSpeed()));
        }
        sceneManager.update(delta);
        sceneManager.render();

        if (drawDebug) physicsSystem.render(camera);

        super.render(delta); //Rendert hauptsächlich die Stage
    }

    private void switchCamera() {
        inputMultiplexer.removeProcessor(cameraController);

        if (cameraController instanceof Cam1) {
            cameraController = new FreeCam(camera);
            inputManager.unfocus();
        } else {
            cameraController = new Cam1(camera, car);
            inputManager.refocus();
        }

        inputMultiplexer.addProcessor(0, cameraController);
    }

    @Override
    public void show() {
        inputMultiplexer.addProcessor(0, cameraController);
        inputMultiplexer.addProcessor(1, stage);
        inputMultiplexer.addProcessor(2, inputManager);
        Gdx.input.setInputProcessor(inputMultiplexer);
    }

    @Override
    public void dispose() {
        finishWallModel.dispose();
        physicsSystem.dispose();
        sceneManager.dispose();
        brdfLUT.dispose();
        diffuseCubemap.dispose();
        specularCubemap.dispose();
        environmentCubemap.dispose();
        skybox.dispose();
        car.dispose();
        assets.unload("models/car.glb");
        super.dispose();
    }
}
