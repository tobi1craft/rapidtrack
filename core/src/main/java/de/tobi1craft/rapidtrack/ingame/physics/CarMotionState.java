package de.tobi1craft.rapidtrack.ingame.physics;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.physics.bullet.linearmath.btMotionState;

public class CarMotionState extends btMotionState {
    private final Matrix4 transform;

    public CarMotionState(Matrix4 transform) {
        this.transform = transform;
    }

    /**
     * Called to initialize body position.
     *
     * @param worldTrans Matrix4 to be populated with world position
     */
    @Override
    public void getWorldTransform(Matrix4 worldTrans) {
        worldTrans.set(transform);
    }

    /**
     * Called when the rigid body changes position. Update your render matrix with worldTrans.
     *
     * @param worldTrans Position calculated by Bullet
     */
    @Override
    public void setWorldTransform(Matrix4 worldTrans) {
        transform.set(worldTrans);
    }
}
