package de.tobi1craft.rapidtrack.util.bullet;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;

public record BulletEntity(btRigidBody body, ModelInstance modelInstance) {
}
