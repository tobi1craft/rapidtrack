package de.tobi1craft.rapidtrack.ingame.camera;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import de.tobi1craft.rapidtrack.bullet.camera.CameraController;
import de.tobi1craft.rapidtrack.ingame.Car;
import de.tobi1craft.rapidtrack.util.Utils3D;

public class Cam1 extends CameraController {
    private final Camera camera;
    private final Car car;
    private final ModelInstance modelInstance;

    private final Vector3 newPosition = new Vector3();
    private final Vector3 position = new Vector3();
    private final Vector3 direction = new Vector3();

    public Cam1(Camera camera, Car car) {
        this.camera = camera;
        this.car = car;
        modelInstance = car.getScene().modelInstance;
    }

    @Override
    public void update(float delta) {
        Utils3D.getDirection(modelInstance.transform, direction);
        modelInstance.transform.getTranslation(position);

        newPosition.set(position);

        camera.position.lerp(newPosition.add(direction.scl(4f)), Math.abs(car.getSpeed()) * delta * 0.5f);
        camera.position.y = MathUtils.lerp(camera.position.y, camera.position.y + 2f, Math.abs(car.getSpeed()) * delta * 0.5f);

        camera.lookAt(position);
        camera.up.set(Vector3.Y); //! camera.up wird durch camera.lookAt geändert
        camera.update();
    }
}
