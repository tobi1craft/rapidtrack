package de.tobi1craft.rapidtrack.bullet.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.physics.bullet.collision.Collision;
import com.badlogic.gdx.physics.bullet.collision.btCapsuleShape;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import de.tobi1craft.rapidtrack.bullet.BulletEntity;
import de.tobi1craft.rapidtrack.bullet.MotionState;
import de.tobi1craft.rapidtrack.bullet.Utils3D;
import de.tobi1craft.rapidtrack.bullet.camera.ThirdPersonCameraController;
import de.tobi1craft.rapidtrack.bullet.character.DynamicCharacterController;

public class DynamicCharacterBulletScreen extends BaseScreen {

    private final DynamicCharacterController controller;

    public DynamicCharacterBulletScreen(Game game) {
        super(game);

        createObjects();

        BulletEntity player = createPlayer();
        controller = new DynamicCharacterController(player, bulletPhysicsSystem);

        setCameraController(new ThirdPersonCameraController(camera, player.modelInstance()));

        camera.position.set(new Vector3(0, 10, -10));
        camera.lookAt(Vector3.Zero);

        // Load the environment
        Model sceneModel = Utils3D.loadOBJ(Gdx.files.internal("models/bullet-scene.obj"));
        ModelInstance sceneInstance = new ModelInstance(sceneModel);
        sceneInstance.materials.get(0).set(ColorAttribute.createDiffuse(Color.FOREST));
        sceneInstance.materials.get(1).set(ColorAttribute.createDiffuse(Color.TEAL));
        sceneInstance.materials.get(2).set(ColorAttribute.createDiffuse(Color.DARK_GRAY));
        sceneInstance.materials.get(3).set(ColorAttribute.createDiffuse(Color.TAN));

        renderInstances.add(sceneInstance);

        btCollisionShape shape = Bullet.obtainStaticNodeShape(sceneInstance.nodes);
        btRigidBody.btRigidBodyConstructionInfo sceneInfo = new btRigidBody.btRigidBodyConstructionInfo(0, null, shape, Vector3.Zero);
        btRigidBody sceneBody = new btRigidBody(sceneInfo);
        bulletPhysicsSystem.addBody(sceneBody);

        /* (AI generated)
        sceneBody.setWorldTransform(sceneInstance.transform);
        sceneBody.setCollisionFlags(sceneBody.getCollisionFlags() | Collision.DISABLE_DEACTIVATION);
        sceneBody.setActivationState(Collision.DISABLE_DEACTIVATION);
        sceneBody.setFriction(0.5f);
        sceneBody.setRestitution(0.5f);
        sceneBody.setContactProcessingThreshold(0.1f);
         */
    }

    @Override
    public void render(float delta) {
        controller.update(delta);
        super.render(delta);
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
        body.setDamping(0.75f, .99f);
        //? body.setFriction();

        renderInstances.add(playerModelInstance);
        bulletPhysicsSystem.addBody(body);

        return new BulletEntity(body, playerModelInstance);
    }
}
