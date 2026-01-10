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
import net.mgsx.gltf.scene3d.scene.Scene;
import net.mgsx.gltf.scene3d.scene.SceneAsset;

import java.util.ArrayList;
import java.util.HashMap;

public class Car {

    public static HashMap<Vector3, Vector3> raycasts = new HashMap<>();
    private final GameScreen screen;
    private final ArrayList<Block> finishes;
    private final CarPhysics PHYSICS;
    private final Scene scene;
    private final AllHitsRayResultCallback raycastCallback;
    private float smoothedSteering = 0f;

    public Car(GameScreen screen, Vector3 blockPos, ArrayList<Block> finishes) {
        this.screen = screen;
        this.finishes = finishes;

        SceneAsset asset = RapidTrack.getInstance().getAssets().get("models/car.glb", SceneAsset.class);
        scene = new Scene(asset.scene.model);
        scene.modelInstance.transform.translate(blockPos.cpy().scl(Track.SCALE).add(0, 1.5f, scene.modelInstance.calculateBoundingBox(new BoundingBox()).getDepth() / 2));

        PHYSICS = new CarPhysics(screen, scene.modelInstance, 500f);

        raycastCallback = new AllHitsRayResultCallback(new Vector3(), new Vector3());
    }

    public Scene getScene() {
        return scene;
    }

    public void update(float delta) {
        float speed = -PHYSICS.getSpeed();

        if (checkForFinish()) {
            screen.finish();
            return;
        }

        if (getPosition().y < -10) {
            screen.reset();
            return;
        }

        float acceleration = 0;
        float brake = 0;
        float rotation = 0;
        boolean isDrifting = false;

        if (screen.inputManager.isKeyDown(InputManager.Inputs.BRAKE) && speed > 0) {
            isDrifting = screen.inputManager.isKeyDown(InputManager.Inputs.LEFT) || screen.inputManager.isKeyDown(InputManager.Inputs.RIGHT);
        }

        if (!isDrifting) acceleration += screen.inputManager.isKeyDown(InputManager.Inputs.ACCELERATE) ? 3f : 0f;
        else acceleration += screen.inputManager.isKeyDown(InputManager.Inputs.ACCELERATE) ? 1f : 0f;
        if (screen.inputManager.isKeyDown(InputManager.Inputs.BRAKE)) acceleration -= isDrifting ? 1f : 2f;

        if (acceleration == 0) brake = 1f;

        if (screen.inputManager.isKeyDown(InputManager.Inputs.LEFT)) rotation += 30f;
        if (screen.inputManager.isKeyDown(InputManager.Inputs.RIGHT)) rotation -= 30f;


        Gdx.app.debug("Car", "Acceleration: " + acceleration + " | Brake: " + brake + " | Drift: " + isDrifting + " | Speed: " + speed);


        float alpha = 1f - (float) Math.exp(-12f * delta);
        smoothedSteering = MathUtils.lerp(smoothedSteering, rotation, alpha);

        PHYSICS.setSteering(smoothedSteering * MathUtils.degreesToRadians);
        PHYSICS.update(delta, .3f * acceleration, .3f * brake, isDrifting);


        for (String wheel : new String[]{"Axle_FL", "Axle_FR"}) {
            Node node = scene.modelInstance.getNode(wheel, true);
            node.rotation.setFromAxis(Vector3.Y, smoothedSteering).nor();
        }

        for (String wheel : new String[]{"Wheel_FL", "Wheel_FR", "Rear_Axle"}) {
            Node node = scene.modelInstance.getNode(wheel, true);
            node.rotation.mul(new Quaternion().setFromAxis(Vector3.X, -delta * 10f * speed)).nor();
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

                if (block.getRotation() == 0 || block.getRotation() == 180)
                    screen.getPhysicsSystem().getDynamicsWorld().rayTest(worldPos.cpy().add(-16, i * stepSize, 0), worldPos.cpy().add(16, i * stepSize, 0), raycastCallback);
                else if (block.getRotation() == 90 || block.getRotation() == 270)
                    screen.getPhysicsSystem().getDynamicsWorld().rayTest(worldPos.cpy().add(0, i * stepSize, -16), worldPos.cpy().add(0, i * stepSize, 16), raycastCallback);
                else
                    throw new IllegalStateException("Invalid rotation: " + block.getRotation() + " for finish at " + block.getGridPos() + ". Only 0, 90, 180 and 270 are allowed.");
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
        return screen.timer() < 0 ? 0 : -PHYSICS.getSpeed();
    }

    public void dispose() {
        PHYSICS.dispose();
        raycastCallback.dispose();
    }
}
