package de.tobi1craft.rapidtrack.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Cubemap;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.utils.FirstPersonCameraController;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import de.tobi1craft.rapidtrack.RapidTrack;
import de.tobi1craft.rapidtrack.ResourceManager;
import de.tobi1craft.rapidtrack.UI;
import de.tobi1craft.rapidtrack.enums.Screens;
import de.tobi1craft.rapidtrack.ingame.Block;
import de.tobi1craft.rapidtrack.ingame.Car;
import de.tobi1craft.rapidtrack.ingame.Track;
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
    private FirstPersonCameraController controller; //? Do my own controller


    public GameScreen() {
        stage = new Stage(new ScreenViewport(), ResourceManager.getInstance().getBatch());

        Table table = new Table();
        table.setFillParent(true);
        table.setDebug(true);
        stage.addActor(table);


        sceneManager = new SceneManager();

        track = new Track();
        for (Block block : track.grid) {
            sceneManager.addScene(block.getScene());
        }

        car = new Car();
        sceneManager.addScene(car.getScene());


        // setup camera (use a reasonable near/far to preserve depth precision)
        camera = new PerspectiveCamera(60f, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.near = 0.1f;
        camera.far = 100;
        sceneManager.setCamera(camera);


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
        // Keep camera controller in sync (movement/rotation)
        if (controller != null) {
            controller.update(delta);
        }

        car.render(delta);


        //TODO: - Seitwärts Drehung auch bei Kamera?!
        //      - X-Achsen Drehung -> Kamera springt
        //      - Freecam

        // animate camera using car-local offset transformed by car rotation
        camera.up.set(Vector3.Y);
        Vector3 carPosition = car.getTranslation();
        Quaternion carRotation = car.getRotation();
        Vector3 localOffset = new Vector3(0f, 0.8f, 1.5f); // slightly above and behind (relative to car's local space)
        Vector3 worldOffset = localOffset.cpy().mul(carRotation);
        Vector3 desiredPos = carPosition.cpy().add(worldOffset);
        // ensure camera doesn't coincide with target
        if (desiredPos.epsilonEquals(carPosition, 1e-6f)) {
            desiredPos.add(0.01f, 0.02f, 0.03f);
        }
        camera.position.set(desiredPos);
        camera.lookAt(carPosition);
        // guard against colinearity between direction and up
        if (Math.abs(camera.direction.dot(Vector3.Y)) > 0.999f) {
            camera.up.set(Vector3.X);
        }

        camera.update();

        sceneManager.update(delta);
        sceneManager.render();

        super.render(delta);
    }

    @Override
    public void show() {
        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(stage);
        multiplexer.addProcessor(car);
        Gdx.input.setInputProcessor(multiplexer);
    }
}
