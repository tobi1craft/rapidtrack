package de.tobi1craft.rapidtrack.bullet.character;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.ClosestNotMeRayResultCallback;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import de.tobi1craft.rapidtrack.bullet.BulletEntity;
import de.tobi1craft.rapidtrack.bullet.BulletPhysicsSystem;
import de.tobi1craft.rapidtrack.bullet.Utils3D;

public class DynamicCharacterController {
    private final float MOVE_SPEED = 10f;
    private final float JUMP_FACTOR = 15f;

    private final Vector3 position = new Vector3();
    private final Vector3 tmpPosition = new Vector3();
    private final Vector3 normal = new Vector3();

    private final Vector3 currentDirection = new Vector3();
    private final Vector3 linearVelocity = new Vector3();
    private final Vector3 angularVelocity = new Vector3();

    private final BulletEntity character;
    private final BulletPhysicsSystem physicsSystem;
    private ClosestNotMeRayResultCallback callback;

    public DynamicCharacterController(BulletEntity entity, BulletPhysicsSystem bulletPhysicsSystem) {
        character = entity;
        physicsSystem = bulletPhysicsSystem;
    }

    public void update(float delta) {
        Utils3D.getDirection(character.modelInstance().transform, currentDirection);
        btRigidBody body = character.body();
        callback = new ClosestNotMeRayResultCallback(character.body());

        resetVelocity();

        boolean isOnGround = isGrounded();

        //Workaround for slopes
        if (isOnGround) {
            callback.getHitNormalWorld(normal);

            //! Skalarprodukt: 1 bei gleicher Richtung (beide Vektoren sind normalisiert)
            float dot = normal.dot(Vector3.Y);
            if (dot != 1) {
                //? AI: body.setGravity(new Vector3(0, -dot * 10f, 0));
                body.setGravity(Vector3.Zero);
            }
        } else body.setGravity(BulletPhysicsSystem.DEFAULT_GRAVITY);

        //Forwards/Backwards movement
        if (Gdx.input.isKeyPressed(Input.Keys.W)) linearVelocity.set(currentDirection).scl(delta * MOVE_SPEED);
        else if (Gdx.input.isKeyPressed(Input.Keys.S)) linearVelocity.set(currentDirection).scl(-delta * MOVE_SPEED);

        //Left/Right turning
        if (Gdx.input.isKeyPressed(Input.Keys.A)) angularVelocity.set(0, 1, 0);
        else if (Gdx.input.isKeyPressed(Input.Keys.D)) angularVelocity.set(0, -1, 0);

        //Jump
        if (isOnGround && Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) linearVelocity.y += JUMP_FACTOR;

        if (!linearVelocity.isZero()) body.applyCentralImpulse(linearVelocity);
        if (!angularVelocity.isZero()) body.setAngularVelocity(angularVelocity);
    }

    private boolean isGrounded() {
        //Reset the callback
        callback.setCollisionObject(null);
        callback.setClosestHitFraction(1f);

        Utils3D.getPosition(character.modelInstance().transform, position);

        //The position we are casting a ray to, slightly below the players current position.
        tmpPosition.set(position).sub(0, 1.4f, 0);

        physicsSystem.raycast(position, tmpPosition, callback);

        return callback.hasHit();
    }

    private void resetVelocity() {
        angularVelocity.set(0, 0, 0);
        linearVelocity.set(0, 0, 0);
    }
}
