package de.tobi1craft.rapidtrack.ingame.physics;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.physics.bullet.collision.Collision;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.dynamics.btDefaultVehicleRaycaster;
import com.badlogic.gdx.physics.bullet.dynamics.btRaycastVehicle;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.physics.bullet.dynamics.btVehicleRaycaster;
import de.tobi1craft.rapidtrack.screens.GameScreen;

public class CarPhysics {

    private final btRaycastVehicle vehicle;
    private final float mass;
    private final ModelInstance modelInstance;
    private float acceleration = 0;


    public CarPhysics(GameScreen screen, ModelInstance modelInstance, float mass) {
        this.modelInstance = modelInstance;
        this.mass = mass;

        btRigidBody body = createBody();
        btRaycastVehicle.btVehicleTuning tuning = new btRaycastVehicle.btVehicleTuning();
        btVehicleRaycaster raycaster = new btDefaultVehicleRaycaster(screen.getPhysicsSystem().getDynamicsWorld());
        vehicle = new btRaycastVehicle(tuning, body, raycaster);
        vehicle.setCoordinateSystem(0, 1, 2);
        Vector3 connectionPoint = new Vector3(0.125f, 0.025f, 0.225f);
        Vector3 wheelDirection = new Vector3(0, -0.12f, 0);
        Vector3 wheelAxle = new Vector3(-0.12f, 0, 0);
        vehicle.addWheel(new Vector3(connectionPoint.x, connectionPoint.y, connectionPoint.z), wheelDirection, wheelAxle, 0.1f, 0.5f, tuning, true);
        vehicle.addWheel(new Vector3(-connectionPoint.x, connectionPoint.y, connectionPoint.z), wheelDirection, wheelAxle, 0.1f, 0.5f, tuning, true);
        vehicle.addWheel(new Vector3(connectionPoint.x, connectionPoint.y, -connectionPoint.z), wheelDirection, wheelAxle, 0.1f, 0.5f, tuning, false);
        vehicle.addWheel(new Vector3(-connectionPoint.x, connectionPoint.y, -connectionPoint.z), wheelDirection, wheelAxle, 0.1f, 0.5f, tuning, false);
        screen.getPhysicsSystem().getDynamicsWorld().addVehicle(vehicle);
        screen.getPhysicsSystem().getDynamicsWorld().addRigidBody(body);
    }

    private btRigidBody createBody() {
        BoundingBox boundingBox = new BoundingBox();
        modelInstance.calculateBoundingBox(boundingBox);

        CarMotionState motionState = new CarMotionState(modelInstance.transform, boundingBox);

        btCollisionShape shape = new btBoxShape(boundingBox.getDimensions(new Vector3()).scl(0.5f));

        Vector3 inertia = new Vector3();
        shape.calculateLocalInertia(mass, inertia);

        btRigidBody.btRigidBodyConstructionInfo info = new btRigidBody.btRigidBodyConstructionInfo(mass, motionState, shape, inertia);
        btRigidBody body = new btRigidBody(info);

        body.setActivationState(Collision.DISABLE_DEACTIVATION);
        //TODO: Friction?! --> Eher friction, weil damping auch Beschleunigungsverhalten verändert
        //TODO: Massenverteilung / center of mass
        body.setFriction(0.5f);


        return body;
    }

    public void setAcceleration(float acceleration) {
        this.acceleration = acceleration;
    }

    public void setSteering(float steering) {
        vehicle.setSteeringValue(steering, 0);
        vehicle.setSteeringValue(steering, 1);
    }

    public float getSpeed() {
        return vehicle.getCurrentSpeedKmHour();
    }

    public void render(float delta) {
        vehicle.updateVehicle(delta);
        vehicle.applyEngineForce(acceleration * mass, 0);
        vehicle.applyEngineForce(acceleration * mass, 1);
        vehicle.applyEngineForce(acceleration * mass, 2);
        vehicle.applyEngineForce(acceleration * mass, 3);
    }
}
