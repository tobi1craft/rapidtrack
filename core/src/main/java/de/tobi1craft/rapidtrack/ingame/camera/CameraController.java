package de.tobi1craft.rapidtrack.ingame.camera;

import com.badlogic.gdx.InputAdapter;

abstract public class CameraController extends InputAdapter {
    abstract public void update(float delta);
}
