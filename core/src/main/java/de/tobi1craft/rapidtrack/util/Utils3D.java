package de.tobi1craft.rapidtrack.util;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;

public class Utils3D {
    private static final Vector3 tmp = new Vector3();

    /**
     * Gets the current facing direction of transform, assuming the default forward is Z vector.
     *
     * @param transform modelInstance transform
     * @param out       vector to be populated with the direction
     */
    public static void getDirection(Matrix4 transform, Vector3 out) {
        tmp.set(Vector3.Z);
        out.set(tmp.rot(transform).nor());
    }
}
