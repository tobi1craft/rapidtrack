package de.tobi1craft.rapidtrack;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.I18NBundle;
import de.tobi1craft.rapidtrack.destinations.Destination;
import de.tobi1craft.rapidtrack.enums.Screens;
import de.tobi1craft.rapidtrack.menus.GameOverlay;
import de.tobi1craft.rapidtrack.menus.MainMenu;
import de.tobi1craft.rapidtrack.menus.Menu;
import de.tobi1craft.rapidtrack.menus.StartupMenu;
import de.tobi1craft.rapidtrack.util.RTAssetManager;

import java.util.HashMap;

/**
 * {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms.
 */
public class RapidTrack extends ApplicationAdapter {

    private Test3D test;

    private static RapidTrack instance;
    private final HashMap<Screens, Menu> menus = new HashMap<>();
    private MusicManager musicManager;
    private Preferences settings;
    private RTAssetManager assets;
    private Stage stage;
    private Screens screen;
    private Destination destination;

    public static RapidTrack getInstance() {
        return instance;
    }

    public RTAssetManager getAssets() {
        return assets;
    }

    public Screens getScreen() {
        return screen;
    }

    public void setScreen(Screens screen) {
        switch (screen) {
            //case GAME -> TODO: Unload main menu background
            //case MAIN_MENU bzw. LOADING -> TODO: Load main menu bg
            case MAIN_MENU -> {
                if (this.screen == Screens.STARTUP && assets.isLoaded("screens/startup.png"))
                    assets.unload("screens/startup.png");
            }
        }
        musicManager.setScreen(screen);
        if (menus.get(screen) == null) menus.put(screen, getMenu(screen));
        this.stage = menus.get(screen).getStage();
        Gdx.input.setInputProcessor(stage);
        this.screen = screen;
    }

    @Override
    public void create() {
        Gdx.app.setLogLevel(Application.LOG_DEBUG); //TODO: Production
        instance = this;
        settings = Gdx.app.getPreferences("settings");
        assets = new RTAssetManager();
        musicManager = new MusicManager(assets, 0.02f);

        setScreen(Screens.STARTUP);

        test = new Test3D();
        test.create();
    }

    private Menu getMenu(Screens screen) {
        return switch (screen) {
            case STARTUP -> new StartupMenu();
            case MAIN_MENU -> new MainMenu();
            case GAME -> new GameOverlay();
            //TODO all Menus
            default -> null;
        };
    }

    @Override
    public void render() {
        //ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);

        test.render();

        if (stage != null) {
            stage.act();
            stage.draw();
        }

        if (assets.isFinished()) return;

        if (assets.update(1000 / settings.getInteger("fps", 60))) {
            if (screen == Screens.STARTUP) {
                Gdx.app.debug(this.getClass().getName(), "startup loaded");
                UI.lang = assets.get("i18n/messages", I18NBundle.class);
            }


        } else if (screen == Screens.STARTUP) {

        } else {
            setScreen(Screens.LOADING);
        }
    }

    @Override
    public void resize(int width, int height) {
        if (stage != null) stage.getViewport().update(width, height, true);
        if (menus.get(screen) != null) menus.get(screen).resize(width, height);

        test.resize(width, height);
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
        assets.dispose();
        for (Menu menu : menus.values()) if (menu != null) menu.dispose();

        test.dispose();
    }
}
