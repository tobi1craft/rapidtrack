package de.tobi1craft.rapidtrack.ingame.physics;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.Collision;
import com.badlogic.gdx.physics.bullet.collision.btCompoundShape;
import com.badlogic.gdx.physics.bullet.collision.btCylinderShape;
import com.badlogic.gdx.physics.bullet.collision.btMultiSphereShape;
import com.badlogic.gdx.physics.bullet.dynamics.*;
import de.tobi1craft.rapidtrack.screens.GameScreen;

public class CarPhysics {

    public final btRigidBody body;
    private final btRaycastVehicle vehicle;
    private final float mass;
    private final ModelInstance modelInstance;
    private final btDynamicsWorld dynamicsWorld;
    private float acceleration = 0;


    public CarPhysics(GameScreen screen, ModelInstance modelInstance, float mass) {
        this.modelInstance = modelInstance;
        this.mass = mass;
        dynamicsWorld = screen.getPhysicsSystem().getDynamicsWorld();

        body = createBody();
        btRaycastVehicle.btVehicleTuning tuning = new btRaycastVehicle.btVehicleTuning();
        btVehicleRaycaster raycaster = new btDefaultVehicleRaycaster(dynamicsWorld);
        vehicle = new btRaycastVehicle(tuning, body, raycaster);
        vehicle.setCoordinateSystem(0, 1, 2);

        Vector3 frontConnectionPoint = new Vector3(0.7669f, 0.4534f, -2.086f);
        Vector3 backConnectionPoint = new Vector3(0.7389f, 0.4534f, 1.608f);
        Vector3 wheelDirection = new Vector3(0, -1f, 0);
        Vector3 wheelAxle = new Vector3(1f, 0, 0);
        float wheelRadius = 0.3297f;
        float maxSuspensionTravel = 0.1f;
        float suspensionRestLength = wheelRadius + maxSuspensionTravel + 0.05f;

        vehicle.addWheel(new Vector3(frontConnectionPoint.x, frontConnectionPoint.y, frontConnectionPoint.z), wheelDirection, wheelAxle, suspensionRestLength, wheelRadius, tuning, true);
        vehicle.addWheel(new Vector3(-frontConnectionPoint.x, frontConnectionPoint.y, frontConnectionPoint.z), wheelDirection, wheelAxle, suspensionRestLength, wheelRadius, tuning, true);
        vehicle.addWheel(new Vector3(backConnectionPoint.x, backConnectionPoint.y, backConnectionPoint.z), wheelDirection, wheelAxle, suspensionRestLength, wheelRadius, tuning, false);
        vehicle.addWheel(new Vector3(-backConnectionPoint.x, backConnectionPoint.y, backConnectionPoint.z), wheelDirection, wheelAxle, suspensionRestLength, wheelRadius, tuning, false);

        for (int i = 0; i < vehicle.getNumWheels(); i++) {
            btWheelInfo wheel = vehicle.getWheelInfo(i);
            wheel.setMaxSuspensionTravelCm(maxSuspensionTravel * 100);
            wheel.setSuspensionStiffness(20f);
        }

        screen.getPhysicsSystem().getDynamicsWorld().addVehicle(vehicle);
        screen.getPhysicsSystem().getDynamicsWorld().addRigidBody(body);
        dynamicsWorld.addVehicle(vehicle);
        dynamicsWorld.addRigidBody(body);
    }

    private btRigidBody createBody() {
        CarMotionState motionState = new CarMotionState(modelInstance.transform);

        float y = 0.15f;
        float size = 0.2f;
        Vector3[] positions = {
            new Vector3(0, y, -3),
            new Vector3(0.7f, y, 0),
            new Vector3(-0.7f, y, 0),
            new Vector3(0, y, 1.5f)};

        btMultiSphereShape multiSphereShape = new btMultiSphereShape(positions, new float[]{size, size, size, size}, 4);

        btCylinderShape axis = new btCylinderShape(new Vector3(0.3297f, 0.9446f, 0.3297f));

        btCompoundShape shape = new btCompoundShape();
        shape.addChildShape(new Matrix4(), multiSphereShape);
        shape.addChildShape(new Matrix4(new Vector3(0, 0.1238f, 1.608f), new Quaternion(Vector3.Z, 90), Vector3.One), axis);
        shape.addChildShape(new Matrix4(new Vector3(0, 0.1238f, -2.086f), new Quaternion(Vector3.Z, 90), Vector3.One), axis);

        shape.setMargin(0.04f); //! 0.0 is default

        Vector3 inertia = new Vector3();
        shape.calculateLocalInertia(mass, inertia);

        btRigidBody.btRigidBodyConstructionInfo info = new btRigidBody.btRigidBodyConstructionInfo(mass, motionState, shape, inertia);
        btRigidBody body = new btRigidBody(info);

        body.setActivationState(Collision.DISABLE_DEACTIVATION);

        body.setFriction(0.5f); //! 0.5 is default

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

    public void dispose() {
        dynamicsWorld.removeRigidBody(body);
        dynamicsWorld.removeVehicle(vehicle);
        body.dispose();
        vehicle.dispose();
    }
}
