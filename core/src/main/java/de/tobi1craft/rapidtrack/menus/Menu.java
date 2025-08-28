package de.tobi1craft.rapidtrack.menus;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Stage;
import de.tobi1craft.rapidtrack.RapidTrack;
import de.tobi1craft.rapidtrack.util.RTAssetManager;

import java.util.function.BiConsumer;

public abstract class Menu implements Screen {
    protected final RTAssetManager assets = RapidTrack.getInstance().getAssets();
    protected Stage stage;
    protected BiConsumer<Integer, Integer> resize;

    protected abstract void load();

    public Stage getStage() {
        if (stage == null) load();
        return stage;
    }

    @Override
    public void show() {
        if (resize == null) load();
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        if (stage != null) {
            stage.act();
            stage.draw();
        }
    }

    @Override
    public void resize(int width, int height) {
        if (resize == null) load();
        if (stage != null) stage.getViewport().update(width, height, true);
        resize.accept(width, height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        if(stage != null) stage.dispose();
    }
}
