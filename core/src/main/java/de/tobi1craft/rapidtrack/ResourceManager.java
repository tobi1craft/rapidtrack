package de.tobi1craft.rapidtrack;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class ResourceManager {

    private static ResourceManager instance = new ResourceManager();

    private final Skin skin;
    private final SpriteBatch batch;

    private ResourceManager() {
        skin = UI.createSkin();
        batch = new SpriteBatch();
    }

    public static ResourceManager getInstance() {
        if (instance == null) instance = new ResourceManager();
        return instance;
    }

    public void dispose() {
        skin.dispose();
        batch.dispose();
        instance = null;
    }

    public Skin getSkin() {
        return skin;
    }

    public SpriteBatch getBatch() {
        return batch;
    }
}
