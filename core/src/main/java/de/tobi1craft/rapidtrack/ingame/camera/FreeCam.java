package de.tobi1craft.rapidtrack.ingame.camera;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import de.tobi1craft.rapidtrack.bullet.camera.CameraController;

import java.util.ArrayList;

public class FreeCam extends CameraController {

    private final Camera camera;
    private final ArrayList<Integer> keys = new ArrayList<>();
    private float yaw;
    private float pitch;


    public FreeCam(Camera camera) {
        this.camera = camera;
    }

    @Override
    public boolean keyDown(int keycode) {
        keys.add(keycode);
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        keys.remove(Integer.valueOf(keycode));
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        float sensitivity = 0.2f; //TODO: Settings
        float deltaX = -Gdx.input.getDeltaX() * sensitivity;
        float deltaY = Gdx.input.getDeltaY() * sensitivity;

        yaw = ((yaw + deltaX + 180) % 360) - 180;
        pitch = MathUtils.clamp(pitch + deltaY, -89, 89);

        Vector3 direction = new Vector3(0, 0, 1)
            .rotate(Vector3.Y, yaw)
            .rotate(camera.direction.cpy().crs(Vector3.Y).nor(), -pitch) //! Kreuzprodukt, weil wenn um x-Achse würde es mal richtig und mal falschrum hoch/runter bewegen
            .nor();

        camera.direction.set(direction);

        return true;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        Gdx.input.setCursorCatched(true); //! Cursor verschwindet sonst vom Bildschirm
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        Gdx.input.setCursorCatched(false);
        return false;
    }

    @Override
    public void update(float delta) {
        //TODO: use global input processor?! --> only accept keyUp/Down when right key is pressed
        float speed = 2f;
        for (int key : keys) {
            switch (key) {
                case Input.Keys.W -> camera.position.add(camera.direction.cpy().scl(delta * speed));
                case Input.Keys.A -> camera.position.add(camera.direction.cpy().crs(camera.up).scl(-delta * speed));
                case Input.Keys.D -> camera.position.add(camera.direction.cpy().crs(camera.up).scl(delta * speed));
                case Input.Keys.S -> camera.position.add(camera.direction.cpy().scl(-delta * speed));
                case Input.Keys.SPACE -> camera.position.add(camera.up.cpy().scl(delta * speed));
                case Input.Keys.SHIFT_LEFT -> camera.position.add(camera.up.cpy().scl(-delta * speed));
            }
        }
        camera.update();
    }
}
