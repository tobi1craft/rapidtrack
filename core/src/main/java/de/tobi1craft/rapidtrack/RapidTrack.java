package de.tobi1craft.rapidtrack;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.scenes.scene2d.Stage;
import de.tobi1craft.rapidtrack.destinations.Destination;
import de.tobi1craft.rapidtrack.enums.Screens;
import de.tobi1craft.rapidtrack.menus.MainMenu;
import de.tobi1craft.rapidtrack.menus.Menu;
import de.tobi1craft.rapidtrack.menus.StartupMenu;
import de.tobi1craft.rapidtrack.util.ForceAssetManager;

import java.util.HashMap;

/**
 * {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms.
 */
public class RapidTrack extends ApplicationAdapter {

    private static RapidTrack instance;
    private final HashMap<Screens, Menu> menus = new HashMap<>();
    private MusicManager musicManager;
    private Preferences settings;
    private ForceAssetManager assets;
    private Stage stage;
    private Screens screen;
    private Destination destination;

    public static RapidTrack getInstance() {
        return instance;
    }

    public ForceAssetManager getAssets() {
        return assets;
    }

    public Screens getScreen() {
        return screen;
    }

    public void setScreen(Screens screen) {
        musicManager.setScreen(screen);
        if (menus.get(screen) == null) menus.put(screen, getMenu(screen));
        this.stage = menus.get(screen).getStage();
        Gdx.input.setInputProcessor(stage);
        this.screen = screen;
    }

    @Override
    public void create() {
        instance = this;
        settings = Gdx.app.getPreferences("settings");
        assets = new ForceAssetManager();
        musicManager = new MusicManager(this, assets, 0.02f);

        setScreen(Screens.STARTUP);
    }

    private Menu getMenu(Screens screen) {
        return switch (screen) {
            case STARTUP -> new StartupMenu();
            case MAIN_MENU -> new MainMenu();
            //TODO all Menus
            default -> null;
        };
    }

    @Override
    public void render() {
        //ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);

        if (assets.update(1000 / settings.getInteger("fps", 60))) {


        } else if (screen == Screens.STARTUP) {

        } else {
            setScreen(Screens.LOADING);
        }

        if (stage != null) {
            stage.act();
            stage.draw();
        }
    }

    @Override
    public void resize(int width, int height) {
        if (stage != null) stage.getViewport().update(width, height, true);
        if (menus.get(screen) != null) menus.get(screen).resize(width, height);
    }

    @Override
    public void pause() {
        musicManager.pause();
    }

    @Override
    public void resume() {
        musicManager.resume();
    }

    @Override
    public void dispose() {
        ResourceManager.getInstance().dispose();
        stage.dispose();
        assets.dispose();
        for (Menu menu : menus.values()) if (menu != null) menu.dispose();
    }

    public void start() {
        setScreen(Screens.MAIN_MENU);
        if (assets.isLoaded("screens/startup.png")) assets.unload("screens/startup.png");
    }
}
