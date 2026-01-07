package de.tobi1craft.rapidtrack.ingame.camera;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;

import java.util.ArrayList;

public class FreeCam extends CameraController {

    private final Camera camera;
    private final ArrayList<Integer> keys = new ArrayList<>();
    private float yaw; //! horizontal (links/rechts um Y)
    private float pitch; //! vertical (hoch/runter um X)
    private float speed = 15f;


    @SuppressWarnings("SuspiciousNameCombination")
    //atan2Deg parameters are in the right order, but not according to variable naming
    public FreeCam(Camera camera) {
        this.camera = camera;
        // Init yaw and pitch from the current camera direction
        Vector3 dir = camera.direction.cpy().nor();
        pitch = MathUtils.asinDeg(-dir.y);
        yaw = MathUtils.atan2Deg(dir.x, dir.z); //TODO: arctan2 erklären
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

        yaw = ((yaw + deltaX + 540) % 360) - 180;
        pitch = MathUtils.clamp(pitch + deltaY, -89, 89);

        Vector3 direction = new Vector3(
            MathUtils.cosDeg(pitch) * MathUtils.sinDeg(yaw), // x
            -MathUtils.sinDeg(pitch),                           // y (negative to match previous control feel)
            MathUtils.cosDeg(pitch) * MathUtils.cosDeg(yaw)     // z
        ).nor();
        //! X und Z sind mit MathUtils.cosDeg(pitch) skaliert, weil der Wert kleiner ist, je nachdem, wie hoch/runter man guckt
        //TODO: --> visualisieren

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
    public boolean scrolled(float amountX, float amountY) {
        if (amountY == 0) return false;
        speed = MathUtils.clamp(speed - amountY, 10, 30);
        return true;
    }

    @Override
    public void update(float delta) {
        //TODO: use global input processor?!
        float scaledSpeed = (float) Math.pow(200, (speed - 10) / 20); //! Skalierter output, damit man nicht ewig scrollen muss
        for (int key : keys) {
            switch (key) {
                case Input.Keys.W ->
                    camera.position.add(camera.direction.cpy().scl(1, 0, 1).nor().scl(delta * scaledSpeed));
                case Input.Keys.S ->
                    camera.position.add(camera.direction.cpy().scl(1, 0, 1).nor().scl(-delta * scaledSpeed));
                case Input.Keys.A ->
                    camera.position.add(camera.direction.cpy().crs(Vector3.Y).scl(-delta * scaledSpeed));
                case Input.Keys.D ->
                    camera.position.add(camera.direction.cpy().crs(Vector3.Y).scl(delta * scaledSpeed));
                case Input.Keys.SPACE -> camera.position.add(Vector3.Y.cpy().scl(delta * scaledSpeed));
                case Input.Keys.SHIFT_LEFT -> camera.position.add(Vector3.Y.cpy().scl(-delta * scaledSpeed));
            }
        }
        camera.update();
    }
}
