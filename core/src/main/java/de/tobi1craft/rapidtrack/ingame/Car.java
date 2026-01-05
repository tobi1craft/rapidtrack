package de.tobi1craft.rapidtrack.ingame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.physics.bullet.collision.AllHitsRayResultCallback;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObjectConstArray;
import de.tobi1craft.rapidtrack.RapidTrack;
import de.tobi1craft.rapidtrack.ingame.physics.CarPhysics;
import de.tobi1craft.rapidtrack.screens.GameScreen;
import de.tobi1craft.rapidtrack.util.RTAssetManager;
import net.mgsx.gltf.scene3d.scene.Scene;
import net.mgsx.gltf.scene3d.scene.SceneAsset;

import java.util.ArrayList;
import java.util.HashMap;

public class Car {

    public static HashMap<Vector3, Vector3> raycasts = new HashMap<>();
    private final GameScreen screen;
    private final ArrayList<Block> finishes;
    private final CarPhysics PHYSICS;
    private final RTAssetManager assets = RapidTrack.getInstance().getAssets();
    private final Scene scene;
    private final SceneAsset asset;
    private final AllHitsRayResultCallback raycastCallback;
    private float rotation = 0;
    private float speed = 0;
    private float acceleration = 0;
    private float visualFrontSteerDeg = 0f;
    private boolean isDrifting = false;

    public Car(GameScreen screen, Vector3 blockPos, ArrayList<Block> finishes) {
        this.screen = screen;
        this.finishes = finishes;

        asset = assets.get("models/car.glb", SceneAsset.class);
        scene = new Scene(asset.scene.model, true);
        scene.modelInstance.transform.translate(blockPos.cpy().scl(Track.SCALE).add(0, 2f, scene.modelInstance.calculateBoundingBox(new BoundingBox()).getDepth() / 2));

        PHYSICS = new CarPhysics(screen, scene.modelInstance, 500f);

        raycastCallback = new AllHitsRayResultCallback(new Vector3(), new Vector3());
    }

    public Scene getScene() {
        return scene;
    }

    public void update(float delta) {
        speed = PHYSICS.getSpeed();

        if (checkForFinish()) {
            screen.finish();
            return;
        }

        acceleration = 0;//-0.5f * Math.signum(speed) * (float) Math.sqrt(Math.abs(speed)); //! Default air friction
        rotation = 0;

        //Is Drifting? Only forwards TODO: movement in other direction than the car is looking
        if (screen.inputManager.isKeyDown(InputManager.Inputs.BRAKE)) {
            isDrifting = speed > 0 && (screen.inputManager.isKeyDown(InputManager.Inputs.LEFT) || screen.inputManager.isKeyDown(InputManager.Inputs.RIGHT));
        } else isDrifting = false;

        if (isDrifting) acceleration += screen.inputManager.isKeyDown(InputManager.Inputs.ACCELERATE) ? -0.5f : -2f;
        else if (screen.inputManager.isKeyDown(InputManager.Inputs.ACCELERATE))
            acceleration += screen.inputManager.isKeyDown(InputManager.Inputs.BRAKE) ? 1f : 3f;
        else
            acceleration += screen.inputManager.isKeyDown(InputManager.Inputs.BRAKE) ? -2f : 0f; //! 0 bei release, wegen air resistance

        if (screen.inputManager.isKeyDown(InputManager.Inputs.LEFT)) rotation += isDrifting ? 80f : 30f;
        if (screen.inputManager.isKeyDown(InputManager.Inputs.RIGHT)) rotation -= isDrifting ? 80f : 30f;


        Gdx.app.debug("Car", "Acceleration: " + acceleration + " | Speed: " + speed);
        PHYSICS.setAcceleration(0.3f * acceleration);
        PHYSICS.setSteering(rotation * MathUtils.degreesToRadians);
        PHYSICS.update(delta);


        float alpha = 1f - (float) Math.exp(-12f * delta);
        // Smooth toward target steering angle (degrees)
        visualFrontSteerDeg = MathUtils.lerp(visualFrontSteerDeg, Math.max(Math.min(this.rotation, 30), -30), alpha);

        for (String wheel : new String[]{"Axle_FL", "Axle_FR"}) {
            Node node = scene.modelInstance.getNode(wheel, true);
            node.rotation.setFromAxis(Vector3.Y, visualFrontSteerDeg).nor();
        }

        for (String wheel : new String[]{"Wheel_FL", "Wheel_FR", "Rear_Axle"}) {
            Node node = scene.modelInstance.getNode(wheel, true);
            node.rotation.mul(new Quaternion().setFromAxis(Vector3.X, 0.025f * speed)).nor();
            //TODO: Drehgeschwindigkeit abhängig von delta und passend zur Radgröße
        }

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


    public Vector3 getPosition() {
        return scene.modelInstance.transform.getTranslation(new Vector3());
    }

    public float getSpeed() {
        return PHYSICS.getSpeed();
    }

    public void dispose() {
        PHYSICS.dispose();
        raycastCallback.dispose();
    }
}
