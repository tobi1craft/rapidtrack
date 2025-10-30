package de.tobi1craft.rapidtrack.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.BoxShapeBuilder;
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.ConeShapeBuilder;
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.CylinderShapeBuilder;
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.SphereShapeBuilder;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.*;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import de.tobi1craft.rapidtrack.util.MotionState;

public class AdvancedBulletScreen extends BaseScreen {

    public AdvancedBulletScreen(Game game) {
        super(game);

        createFloor(20, 1, 20);
        createObjects();
    }

    private void createObjects() {
        for (int i = -6; i < 6; i += 2) {
            for (int j = -6; j < 6; j += 2) {
                ModelBuilder modelBuilder = new ModelBuilder();
                modelBuilder.begin();
                Material material = new Material();
                material.set(ColorAttribute.createDiffuse(getRandomColor()));
                MeshPartBuilder builder = modelBuilder.part("box", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, material);

                btCollisionShape shape;

                int random = MathUtils.random(1, 4);
                shape = switch (random) {
                    case 1 -> {
                        BoxShapeBuilder.build(builder, 0, 0, 0, 1f, 1f, 1f);
                        yield new btBoxShape(new Vector3(0.5f, 0.5f, 0.5f));
                    }
                    case 2 -> {
                        ConeShapeBuilder.build(builder, 1, 1, 1, 8);
                        yield new btConeShape(0.5f, 1f);
                    }
                    case 3 -> {
                        SphereShapeBuilder.build(builder, 1, 1, 1, 8, 8);
                        yield new btSphereShape(0.5f);
                    }
                    default -> {
                        CylinderShapeBuilder.build(builder, 1, 1, 1, 8);
                        yield new btCylinderShape(new Vector3(0.5f, 0.5f, 0.5f));
                    }
                };

                ModelInstance box = new ModelInstance(modelBuilder.end());
                box.transform.setToTranslation(i, MathUtils.random(10, 20), j);
                box.transform.rotate(new Quaternion(Vector3.Z, MathUtils.random(0f, 270f)));

                float mass = 1f;

                Vector3 localInertia = new Vector3();
                shape.calculateLocalInertia(mass, localInertia);

                btRigidBody.btRigidBodyConstructionInfo info = new btRigidBody.btRigidBodyConstructionInfo(mass, null, shape, localInertia);
                btRigidBody body = new btRigidBody(info);

                MotionState motionState = new MotionState(box.transform);
                body.setMotionState(motionState);


                renderInstances.add(box);
                bulletPhysicsSystem.addBody(body);
            }
        }
    }
}
