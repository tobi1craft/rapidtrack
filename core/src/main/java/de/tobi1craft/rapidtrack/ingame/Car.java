package de.tobi1craft.rapidtrack.ingame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ObjectIntMap;
import net.mgsx.gltf.loaders.gltf.GLTFLoader;
import net.mgsx.gltf.scene3d.scene.Scene;
import net.mgsx.gltf.scene3d.scene.SceneAsset;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class Car extends InputAdapter {
    private final Scene scene;
    private final Map<Integer, Consumer<Boolean>> keyBindings = new HashMap<>(); //! Verwirrend → erklären
    private final ObjectIntMap<Inputs> keyDown = new ObjectIntMap<>(); //! Integer anstatt Boolean als Value wegen reference counting; ObjectIntMap für increment Methode
    private float rotation = 0;

    public Car() {
        //?! STUCK: SceneAsset asset = RapidTrack.getInstance().getAssets().loadAndGet("models/car.gltf", SceneAsset.class);
        SceneAsset asset = new GLTFLoader().load(Gdx.files.internal("models/car.gltf"));
        scene = new Scene(asset.scene.model, true);

        //Animation example:
        //scene.animationController.setAnimation("idle", -1);

        //TODO: Do this in a menu
        keyBindings.put(Input.Keys.W, (Boolean pressed) -> keyDown.getAndIncrement(Inputs.ACCELERATE, 0, pressed ? 1 : -1));
        keyBindings.put(Input.Keys.A, (Boolean pressed) -> keyDown.getAndIncrement(Inputs.LEFT, 0, pressed ? 1 : -1));
        keyBindings.put(Input.Keys.S, (Boolean pressed) -> keyDown.getAndIncrement(Inputs.BRAKE, 0, pressed ? 1 : -1));
        keyBindings.put(Input.Keys.D, (Boolean pressed) -> keyDown.getAndIncrement(Inputs.RIGHT, 0, pressed ? 1 : -1));
        keyBindings.put(Input.Keys.SPACE, (Boolean pressed) -> keyDown.getAndIncrement(Inputs.BRAKE, 0, pressed ? 1 : -1));

    }

    public Scene getScene() {
        return scene;
    }

    public void render(float delta) {
        // Rotate model
        for (Inputs input : keyDown.keys()) {
            if (keyDown.get(input, 0) == 0) continue;
            switch (input) {
                case Inputs.LEFT -> rotation += delta;
                case Inputs.RIGHT -> rotation -= delta;
            }
        }
        Quaternion rotation = new Quaternion().setFromAxis(Vector3.Y, this.rotation);


        scene.modelInstance.transform.set(new Vector3(0, 0.0625f, 0), rotation, new Vector3(0.5f, 0.5f, 0.5f));

        for (Inputs input : keyDown.keys()) {
            if (keyDown.get(input, 0) != 0) Gdx.app.log("Car", "Key " + input + " is pressed.");
        }
    }


    @Override
    public boolean keyDown(int keycode) {
        if (keyBindings.containsKey(keycode)) {
            keyBindings.get(keycode).accept(true);
            return true;
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        if (keyBindings.containsKey(keycode)) {
            keyBindings.get(keycode).accept(false);
            return true;
        }
        return false;
    }


    public Vector3 getTranslation() {
        return scene.modelInstance.transform.getTranslation(new Vector3());
    }

    public Quaternion getRotation() {
        return scene.modelInstance.transform.getRotation(new Quaternion(), true);
    }

    public Vector3 getScale() {
        return scene.modelInstance.transform.getScale(new Vector3());
    }

    private enum Inputs {
        LEFT, RIGHT, ACCELERATE, BRAKE
    }
}
