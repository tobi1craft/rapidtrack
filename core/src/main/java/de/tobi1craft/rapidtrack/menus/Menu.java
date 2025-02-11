package de.tobi1craft.rapidtrack.menus;

import com.badlogic.gdx.scenes.scene2d.Stage;
import de.tobi1craft.rapidtrack.RapidTrack;
import de.tobi1craft.rapidtrack.util.ForceAssetManager;

import java.util.function.BiConsumer;

public abstract class Menu {
    protected final ForceAssetManager assets = RapidTrack.getInstance().getAssets();
    protected Stage stage;
    protected BiConsumer<Integer, Integer> resize;

    protected abstract void load();

    public Stage getStage() {
        if (stage == null) load();
        return stage;
    }

    public void resize(int width, int height) {
        if (resize == null) load();
        resize.accept(width, height);
    }

    public void dispose() {
        stage.dispose();
    }
}
