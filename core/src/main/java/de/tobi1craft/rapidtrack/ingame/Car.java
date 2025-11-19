package de.tobi1craft.rapidtrack.ingame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ObjectIntMap;
import de.tobi1craft.rapidtrack.ingame.physics.CarPhysics;
import de.tobi1craft.rapidtrack.screens.GameScreen;
import net.mgsx.gltf.loaders.gltf.GLTFLoader;
import net.mgsx.gltf.scene3d.scene.Scene;
import net.mgsx.gltf.scene3d.scene.SceneAsset;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class Car extends InputAdapter {

    private final GameScreen screen;
    private final CarPhysics PHYSICS;

    private final Scene scene;
    private final SceneAsset asset;
    private final Map<Integer, Consumer<Boolean>> keyBindings = new HashMap<>(); //! Verwirrend → erklären
    private final ObjectIntMap<Inputs> keyDown = new ObjectIntMap<>(); //! Integer anstatt Boolean als Value wegen reference counting; ObjectIntMap für increment Methode
    private float rotation = 0;
    private float speed = 0;
    private float acceleration = 0;
    private float visualFrontSteerDeg = 0f;
    private boolean isDrifting = false;


    public Car(GameScreen screen) {
        this.screen = screen;

        //?! STUCK: SceneAsset asset = RapidTrack.getInstance().getAssets().loadAndGet("models/car.gltf", SceneAsset.class);
        asset = new GLTFLoader().load(Gdx.files.internal("models/car.gltf"));
        scene = new Scene(asset.scene.model, true);
        scene.modelInstance.transform.translate(new Vector3(0, 0.2625f, 0));
        scene.modelInstance.transform.scale(0.5f, 0.5f, 0.5f);


        PHYSICS = new CarPhysics(screen, scene.modelInstance, 500f);


        //scene.animationController.setAnimation("drive", -1, 10, null);


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
        // Input Handling
        ArrayList<Inputs> pressed = new ArrayList<>();
        for (Inputs input : keyDown.keys()) {
            if (keyDown.get(input, 0) == 0) continue;
            pressed.add(input);
        }

        acceleration = -0.5f * Math.signum(speed) * (float) Math.sqrt(Math.abs(speed)); //! Default air friction
        rotation = 0;

        //Is Drifting? Only forwards TODO: movement in other direction than the car is looking
        if (pressed.contains(Inputs.BRAKE)) {
            isDrifting = speed > 0 && (pressed.contains(Inputs.LEFT) || pressed.contains(Inputs.RIGHT));
        } else isDrifting = false;

        if (isDrifting) acceleration += pressed.contains(Inputs.ACCELERATE) ? -0.5f : -2f;
        else if (pressed.contains(Inputs.ACCELERATE)) acceleration += pressed.contains(Inputs.BRAKE) ? 1f : 3f;
        else acceleration += pressed.contains(Inputs.BRAKE) ? -2f : 0f; //! 0 bei release, wegen air resistance

        if (pressed.contains(Inputs.LEFT)) rotation += isDrifting ? 80f : 50f;
        if (pressed.contains(Inputs.RIGHT)) rotation -= isDrifting ? 80f : 50f;


        for (Inputs input : pressed) {
            if (keyDown.get(input, 0) == 0) continue;
            switch (input) {
                case ACCELERATE -> acceleration += 3f;
                case LEFT -> rotation += 50f;
                case RIGHT -> rotation -= 50f;
            }
        }

        Gdx.app.debug("Car", "Acceleration: " + acceleration + " | Speed: " + speed);
        PHYSICS.setAcceleration(-0.01f * acceleration);
        PHYSICS.render(delta);

        speed = PHYSICS.getSpeed();


        Node wheels = scene.modelInstance.getNode("wheels", true);
        for (Node axle : wheels.getChildren()) {
            //Lenkung
            if (axle.id.equals("front_wheels")) {

                float alpha = 1f - (float) Math.exp(-12f * delta);
                // Smooth toward target steering angle (degrees)
                visualFrontSteerDeg = MathUtils.lerp(visualFrontSteerDeg, Math.max(Math.min(this.rotation, 50), -50), alpha);

                for (Node wheel : axle.getChildren()) {
                    wheel.rotation.setFromAxis(Vector3.Y, visualFrontSteerDeg).nor();
                }
            }

            //Räder vorwärts/rückwärts drehen
            for (Node wheel : axle.getChildren()) {
                wheel.getChild(0).rotation.mul(new Quaternion().setFromAxis(Vector3.X, -2.0f * speed)).nor();
            }

            //axle.rotation.mul(new Quaternion().setFromAxis(Vector3.X, -2.0f * speed)).nor();
        }

        scene.modelInstance.calculateTransforms();
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

    public void dispose() {
        asset.dispose();
    }

    private enum Inputs {
        LEFT, RIGHT, ACCELERATE, BRAKE
    }
}
