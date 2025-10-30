package de.tobi1craft.rapidtrack.screens.bullet;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;

public class DynamicCharacterBulletScreen extends BaseScreen {

    public DynamicCharacterBulletScreen(Game game) {
        super(game);

        createFloor(20, 1, 20);
        createObjects();

        // Disable the FPS camera for this test.
        //? --> ??
        Gdx.input.setInputProcessor(null);

        camera.position.set(new Vector3(0, 10, -10));
        camera.lookAt(Vector3.Zero);
    }
}
