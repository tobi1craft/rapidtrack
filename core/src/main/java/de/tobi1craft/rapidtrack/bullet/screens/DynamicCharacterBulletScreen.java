package de.tobi1craft.rapidtrack.bullet.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.physics.bullet.collision.Collision;
import com.badlogic.gdx.physics.bullet.collision.btCapsuleShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import de.tobi1craft.rapidtrack.bullet.BulletEntity;
import de.tobi1craft.rapidtrack.bullet.MotionState;
import de.tobi1craft.rapidtrack.bullet.Utils3D;

public class DynamicCharacterBulletScreen extends BaseScreen {

    public DynamicCharacterBulletScreen(Game game) {
        super(game);

        createFloor(20, 1, 20);
        createObjects();
        BulletEntity player = createPlayer();

        // Disable the FPS camera for this test.
        //? --> ??
        Gdx.input.setInputProcessor(null);

        camera.position.set(new Vector3(0, 10, -10));
        camera.lookAt(Vector3.Zero);
    }

    private BulletEntity createPlayer() {
        ModelInstance playerModelInstance = new ModelInstance(Utils3D.buildCapsuleCharacter());

        //Move him up above the ground
        playerModelInstance.transform.setToTranslation(0, 4, 0);

        //Calculate dimension
        BoundingBox boundingBox = new BoundingBox();
        playerModelInstance.calculateBoundingBox(boundingBox);

        Vector3 dimensions = boundingBox.getDimensions(new Vector3());

        dimensions.scl(0.5f);

        MotionState motionState = new MotionState(playerModelInstance.transform);
        btCapsuleShape capsuleShape = new btCapsuleShape(dimensions.len() / 2.5f, dimensions.y);

        float mass = 2f;

        Vector3 inertia = new Vector3();
        capsuleShape.calculateLocalInertia(mass, inertia);

        btRigidBody.btRigidBodyConstructionInfo info = new btRigidBody.btRigidBodyConstructionInfo(mass, motionState, capsuleShape, inertia);
        btRigidBody body = new btRigidBody(info);

        body.setAngularFactor(Vector3.Y); //! Fällt nicht um --> TODO: bei Auto eher center of mass
        body.setActivationState(Collision.DISABLE_DEACTIVATION);

        renderInstances.add(playerModelInstance);
        bulletPhysicsSystem.addBody(body);

        return new BulletEntity(body, playerModelInstance);
    }
}
