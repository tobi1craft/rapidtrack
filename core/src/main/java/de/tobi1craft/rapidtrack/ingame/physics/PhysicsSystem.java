package de.tobi1craft.rapidtrack.ingame.physics;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.DebugDrawer;
import com.badlogic.gdx.physics.bullet.collision.*;
import com.badlogic.gdx.physics.bullet.dynamics.*;
import com.badlogic.gdx.physics.bullet.linearmath.btIDebugDraw;
import com.badlogic.gdx.utils.Disposable;
import de.tobi1craft.rapidtrack.ingame.Car;

import java.util.ArrayList;
import java.util.Map;

public class PhysicsSystem implements Disposable {
    /**
     * Stores all btCollisionObjects and provides an interface to
     * perform queries.
     */
    private final btDynamicsWorld dynamicsWorld;
    /**
     * Allows to configure Bullet collision detection
     * stack allocator size, default collision algorithms and persistent manifold pool size
     */
    private final btCollisionConfiguration collisionConfig;
    /**
     * A collision dispatcher iterates over each pair, searches for a matching collision algorithm based on the
     * types of objects involved, and executes the collision algorithm computing contact points.
     */
    private final btDispatcher dispatcher;
    /**
     * Broadphase collision detection provides acceleration structure to quickly reject pairs of objects
     * based on axis-aligned bounding box (AABB) overlap.
     */
    private final btBroadphaseInterface broadphase;
    private final btConstraintSolver constraintSolver;
    private final DebugDrawer debugDrawer;

    private final ArrayList<btRigidBody> staticBodies = new ArrayList<>();
    private final ArrayList<btCollisionShape> staticShapes = new ArrayList<>();


    public PhysicsSystem() {
        collisionConfig = new btDefaultCollisionConfiguration();
        dispatcher = new btCollisionDispatcher(collisionConfig);

        // General purpose, well-optimized broadphase, adapts dynamically to the dimensions of the world.
        broadphase = new btDbvtBroadphase(); //! Broadphase checkt, ob sich 2 bodies ungefähr in die Nähe kommen
        constraintSolver = new btSequentialImpulseConstraintSolver();
        dynamicsWorld = new btDiscreteDynamicsWorld(dispatcher, broadphase, constraintSolver, collisionConfig);
        dynamicsWorld.setGravity(new Vector3(0, -3.5f, 0));

        debugDrawer = new DebugDrawer();
        debugDrawer.setDebugMode(btIDebugDraw.DebugDrawModes.DBG_DrawWireframe);

        dynamicsWorld.setDebugDrawer(debugDrawer);
    }

    public void update(float delta) {
        // performs collision detection and physics simulation
        dynamicsWorld.stepSimulation(delta, 5, 1 / 100f); //! maxSubSteps: Lags aufholen; fixedTimeStep: update rate von den physics
    }

    public void render(Camera camera) {
        debugDrawer.begin(camera);
        for (Map.Entry<Vector3, Vector3> entry : Car.raycasts.entrySet())
            debugDrawer.drawLine(entry.getKey(), entry.getValue(), new Vector3(1, 0, 1));
        dynamicsWorld.debugDrawWorld();
        debugDrawer.end();
    }

    public btDynamicsWorld getDynamicsWorld() {
        return dynamicsWorld;
    }

    public void addStaticBody(btRigidBody body, btCollisionShape shape) {
        staticBodies.add(body);
        staticShapes.add(shape);
        dynamicsWorld.addRigidBody(body);
    }

    @Override
    public void dispose() {
        for (btRigidBody body : staticBodies) {
            dynamicsWorld.removeRigidBody(body);
            body.dispose();
        }
        staticBodies.clear();

        for (btCollisionShape shape : staticShapes) {
            shape.dispose();
        }
        staticShapes.clear();

        debugDrawer.dispose();
        dynamicsWorld.dispose();
        constraintSolver.dispose();
        broadphase.dispose();
        dispatcher.dispose();
        collisionConfig.dispose();
    }
}
