package de.tobi1craft.rapidtrack.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Cubemap;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import de.tobi1craft.rapidtrack.RapidTrack;
import de.tobi1craft.rapidtrack.ResourceManager;
import de.tobi1craft.rapidtrack.UI;
import de.tobi1craft.rapidtrack.bullet.camera.CameraController;
import de.tobi1craft.rapidtrack.enums.Screens;
import de.tobi1craft.rapidtrack.ingame.Block;
import de.tobi1craft.rapidtrack.ingame.Car;
import de.tobi1craft.rapidtrack.ingame.Track;
import de.tobi1craft.rapidtrack.ingame.camera.Cam1;
import de.tobi1craft.rapidtrack.ingame.camera.FreeCam;
import de.tobi1craft.rapidtrack.ingame.physics.PhysicsSystem;
import net.mgsx.gltf.scene3d.attributes.PBRCubemapAttribute;
import net.mgsx.gltf.scene3d.attributes.PBRTextureAttribute;
import net.mgsx.gltf.scene3d.lights.DirectionalLightEx;
import net.mgsx.gltf.scene3d.scene.SceneManager;
import net.mgsx.gltf.scene3d.scene.SceneSkybox;
import net.mgsx.gltf.scene3d.utils.IBLBuilder;

public class GameScreen extends Menu {

    private final Track track;
    private final Car car;
    private final SceneManager sceneManager;
    private final PerspectiveCamera camera;
    private final Cubemap diffuseCubemap;
    private final Cubemap environmentCubemap;
    private final Cubemap specularCubemap;
    private final Texture brdfLUT;
    private final SceneSkybox skybox;
    private final DirectionalLightEx light;
    private final PhysicsSystem physicsSystem;
    private CameraController cameraController;
    private boolean drawDebug = false; //TODO: Input handling on this screen for debug and maybe cam switch --> maybe somewhere else

    public GameScreen() {
        setupStage();

        physicsSystem = new PhysicsSystem();

        sceneManager = new SceneManager();

        track = new Track();
        for (Block block : track.grid) {
            sceneManager.addScene(block.getScene());
            btCollisionShape shape = Bullet.obtainStaticNodeShape(block.getScene().modelInstance.nodes);
            shape.setMargin(0f);
            btRigidBody.btRigidBodyConstructionInfo sceneInfo = new btRigidBody.btRigidBodyConstructionInfo(0, null, shape, Vector3.Zero);
            btRigidBody sceneBody = new btRigidBody(sceneInfo);
            sceneBody.setWorldTransform(block.getScene().modelInstance.transform);
            physicsSystem.getDynamicsWorld().addRigidBody(sceneBody);
        }

        car = new Car(this);
        sceneManager.addScene(car.getScene());


        // setup camera (use a reasonable near/far to preserve depth precision)
        camera = new PerspectiveCamera(60f, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.near = 0.2f;
        camera.far = 1000;
        sceneManager.setCamera(camera);

        if (drawDebug) physicsSystem.render(camera);
        cameraController = new Cam1(camera, car);


        // setup light
        light = new DirectionalLightEx();
        light.direction.set(1, -3, 1).nor();
        light.color.set(Color.WHITE);
        sceneManager.environment.add(light);

        // setup quick IBL (image based lighting)
        IBLBuilder iblBuilder = IBLBuilder.createOutdoor(light);
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

    public PhysicsSystem getPhysicsSystem() {
        return physicsSystem;
    }

    private void setupStage() {
        stage = new Stage(new ScreenViewport(), ResourceManager.getInstance().getBatch());

        Table table = new Table();
        table.setFillParent(true);
        table.setDebug(true);
        stage.addActor(table);

        resize = (width, height) -> {
            table.clearChildren();

            TextButton button = UI.getLiteralTextButton(height * 0.15f, "hi");
            table.add(button).expandX();

            table.add().expandX();
            table.add().expandX();

            // start the game when the button is clicked
            button.addListener(new ChangeListener() {
                public void changed(ChangeEvent event, Actor actor) {
                    RapidTrack.getInstance().setScreen(Screens.GAME);
                }
            });


            sceneManager.updateViewport(width, height);


        };
    }

    @Override
    public void render(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.F1)) drawDebug = !drawDebug;
        if (Gdx.input.isKeyJustPressed(Input.Keys.F5)) {
            if (cameraController instanceof Cam1) cameraController = new FreeCam(camera);
            else cameraController = new Cam1(camera, car);
            show();
        }

        car.render(delta);

        sceneManager.update(delta);
        sceneManager.render();

        physicsSystem.update(delta);

        // Keep camera controller in sync (movement/rotation)
        cameraController.update(delta);

        if (drawDebug) physicsSystem.render(camera);

        super.render(delta); //Rendert hauptsächlich die Stage
    }

    @Override
    public void show() {
        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(cameraController);
        multiplexer.addProcessor(car);
        multiplexer.addProcessor(stage);
        Gdx.input.setInputProcessor(multiplexer);
    }

    @Override
    public void dispose() {
        sceneManager.dispose();
        brdfLUT.dispose();
        diffuseCubemap.dispose();
        specularCubemap.dispose();
        environmentCubemap.dispose();
        skybox.dispose();
        car.dispose();
        super.dispose();
    }
}
