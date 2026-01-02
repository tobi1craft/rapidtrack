package de.tobi1craft.rapidtrack.ingame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.physics.bullet.collision.AllHitsRayResultCallback;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObjectConstArray;
import com.badlogic.gdx.utils.ObjectIntMap;
import de.tobi1craft.rapidtrack.RapidTrack;
import de.tobi1craft.rapidtrack.ingame.physics.CarPhysics;
import de.tobi1craft.rapidtrack.screens.GameScreen;
import de.tobi1craft.rapidtrack.util.RTAssetManager;
import net.mgsx.gltf.scene3d.scene.Scene;
import net.mgsx.gltf.scene3d.scene.SceneAsset;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class Car extends InputAdapter {

    public static HashMap<Vector3, Vector3> raycasts = new HashMap<>();
    private final GameScreen screen;
    private final ArrayList<Block> finishes;
    private final CarPhysics PHYSICS;
    private final RTAssetManager assets = RapidTrack.getInstance().getAssets();
    private final Scene scene;
    private final SceneAsset asset;
    private final Map<Integer, Consumer<Boolean>> keyBindings = new HashMap<>(); //! Verwirrend → erklären
    private final ObjectIntMap<Inputs> keyDown = new ObjectIntMap<>(); //! Integer anstatt Boolean als Value wegen reference counting; ObjectIntMap für increment Methode
    private final AllHitsRayResultCallback raycastCallback;
    private float rotation = 0;
    private float speed = 0;
    private float acceleration = 0;
    private final float visualFrontSteerDeg = 0f;
    private boolean isDrifting = false;

    public Car(GameScreen screen, Vector3 blockPos, ArrayList<Block> finishes) {
        this.screen = screen;
        this.finishes = finishes;

        asset = assets.get("models/car.glb", SceneAsset.class);
        scene = new Scene(asset.scene.model, true);
        scene.modelInstance.transform.translate(blockPos.cpy().scl(Track.SCALE).add(0, 2f, scene.modelInstance.calculateBoundingBox(new BoundingBox()).getDepth() / 2));

        PHYSICS = new CarPhysics(screen, scene.modelInstance, 500f);

        raycastCallback = new AllHitsRayResultCallback(new Vector3(), new Vector3());

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
        speed = PHYSICS.getSpeed();

        // Input Handling
        ArrayList<Inputs> pressed = new ArrayList<>();
        for (Inputs input : keyDown.keys()) {
            if (keyDown.get(input, 0) == 0) continue;
            pressed.add(input);
        }

        if (checkForFinish()) Gdx.app.debug("Car", "Finished!");

        acceleration = 0;//-0.5f * Math.signum(speed) * (float) Math.sqrt(Math.abs(speed)); //! Default air friction
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


        Gdx.app.debug("Car", "Acceleration: " + acceleration + " | Speed: " + speed);
        PHYSICS.setAcceleration(0.1f * acceleration);
        PHYSICS.setSteering(rotation / 360);
        PHYSICS.render(delta);


        /*
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
                wheel.getChild(0).rotation.mul(new Quaternion().setFromAxis(Vector3.X, 2.0f * speed)).nor();
                //TODO: Drehgeschwindigkeit abhängig von delta und passend zur Radgröße
            }

        }
        */

        scene.modelInstance.calculateTransforms();
    }

    private boolean checkForFinish() {
        raycasts.clear();
        for (Block block : finishes) {
            Vector3 worldPos = block.getGridPos().cpy().scl(Track.SCALE);
            if (getPosition().dst(worldPos) > 50) continue; //! Wenn zu weit weg

            int rayCount = 160;
            float stepSize = 8f / rayCount;
            for (int i = 0; i < rayCount; i++) {
                raycastCallback.setCollisionObject(null);
                raycastCallback.getCollisionObjects().clear();
                raycastCallback.setClosestHitFraction(1f);

                screen.getPhysicsSystem().getDynamicsWorld().rayTest(worldPos.cpy().add(-16, i * stepSize, 0), worldPos.cpy().add(16, i * stepSize, 0), raycastCallback);
                raycasts.put(worldPos.cpy().add(-16, i * stepSize, 0), worldPos.cpy().add(16, i * stepSize, 0));

                if (raycastCallback.hasHit()) {
                    btCollisionObjectConstArray collisionObjects = raycastCallback.getCollisionObjects();
                    for (int j = 0; j < collisionObjects.size(); j++)
                        if (PHYSICS.body.equals(collisionObjects.atConst(j))) return true;
                }
            }
        }
        return false;
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


    public Vector3 getPosition() {
        return scene.modelInstance.transform.getTranslation(new Vector3());
    }

    public float getSpeed() {
        return PHYSICS.getSpeed();
    }

    public void dispose() {
        PHYSICS.dispose();

    }

    private enum Inputs {
        LEFT, RIGHT, ACCELERATE, BRAKE
    }
}
