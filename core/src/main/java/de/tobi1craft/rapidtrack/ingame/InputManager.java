package de.tobi1craft.rapidtrack.ingame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.utils.ObjectIntMap;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class InputManager extends InputAdapter {
    private final Map<Integer, Consumer<Boolean>> keyBindings = new HashMap<>(); //! Verwirrend → erklären
    private final ObjectIntMap<Inputs> keyDown = new ObjectIntMap<>(); //! Integer anstatt Boolean als Value wegen reference counting; ObjectIntMap für increment Methode

    public InputManager() {
        //TODO: Do this in a menu

        keyBindings.put(Input.Keys.W, (Boolean pressed) -> keyDown.getAndIncrement(Inputs.ACCELERATE, 0, pressed ? 1 : -1));
        keyBindings.put(Input.Keys.UP, (Boolean pressed) -> keyDown.getAndIncrement(Inputs.ACCELERATE, 0, pressed ? 1 : -1));

        keyBindings.put(Input.Keys.A, (Boolean pressed) -> keyDown.getAndIncrement(Inputs.LEFT, 0, pressed ? 1 : -1));
        keyBindings.put(Input.Keys.LEFT, (Boolean pressed) -> keyDown.getAndIncrement(Inputs.LEFT, 0, pressed ? 1 : -1));

        keyBindings.put(Input.Keys.S, (Boolean pressed) -> keyDown.getAndIncrement(Inputs.BRAKE, 0, pressed ? 1 : -1));
        keyBindings.put(Input.Keys.DOWN, (Boolean pressed) -> keyDown.getAndIncrement(Inputs.BRAKE, 0, pressed ? 1 : -1));
        keyBindings.put(Input.Keys.SPACE, (Boolean pressed) -> keyDown.getAndIncrement(Inputs.BRAKE, 0, pressed ? 1 : -1));

        keyBindings.put(Input.Keys.D, (Boolean pressed) -> keyDown.getAndIncrement(Inputs.RIGHT, 0, pressed ? 1 : -1));
        keyBindings.put(Input.Keys.RIGHT, (Boolean pressed) -> keyDown.getAndIncrement(Inputs.RIGHT, 0, pressed ? 1 : -1));

        refocus();
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keyBindings.containsKey(keycode)) {
            keyBindings.get(keycode).accept(true);
            return true;
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        if (keyBindings.containsKey(keycode)) {
            keyBindings.get(keycode).accept(false);
            return true;
        }
        return false;
    }

    public boolean isKeyDown(Inputs input) {
        return keyDown.get(input, 0) > 0;
    }

    public void unfocus() {
        keyDown.clear();
    }

    public void refocus() {
        for (int key : keyBindings.keySet()) {
            if (Gdx.input.isKeyPressed(key)) keyBindings.get(key).accept(true);
        }
    }

    public enum Inputs {
        LEFT, RIGHT, ACCELERATE, BRAKE
    }
}
