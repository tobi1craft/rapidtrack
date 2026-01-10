package de.tobi1craft.rapidtrack;

import com.badlogic.gdx.*;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.ScreenUtils;
import de.tobi1craft.rapidtrack.enums.Screens;
import de.tobi1craft.rapidtrack.screens.GameScreen;
import de.tobi1craft.rapidtrack.screens.MainScreen;
import de.tobi1craft.rapidtrack.screens.SettingsScreen;
import de.tobi1craft.rapidtrack.screens.StartupScreen;
import de.tobi1craft.rapidtrack.util.RTAssetManager;

import java.util.HashMap;

public class RapidTrack extends Game {

    private static RapidTrack instance;
    private final HashMap<Screens, Screen> menus = new HashMap<>();
    private MusicManager musicManager;
    private Preferences settings;
    private RTAssetManager assets;
    private Screens screen;

    public static RapidTrack getInstance() {
        return instance;
    }

    public RTAssetManager getAssets() {
        return assets;
    }

    public void setScreen(Screens screen) {
        setScreen(screen, false);
    }

    public void setScreen(Screens screen, boolean removeCurrent) {
        Screens old = this.screen;
        if (menus.get(screen) == null) menus.put(screen, getMenu(screen));
        this.setScreen(menus.get(screen));
        this.screen = screen;
        if (removeCurrent) {
            menus.get(old).dispose();
            menus.remove(old);
        }
    }

    @Override
    public void create() {
        Gdx.app.setLogLevel(Application.LOG_DEBUG); //TODO: Production
        instance = this;
        settings = Gdx.app.getPreferences("RapidTrack-Settings");
        assets = new RTAssetManager();
        UI.lang = assets.loadAndGet("i18n/messages", I18NBundle.class);
        Bullet.init(false, true);
        musicManager = new MusicManager(assets, settings.getInteger("volume", 2) / 100f);
        Gdx.graphics.setForegroundFPS(settings.getInteger("fps", Gdx.graphics.getDisplayMode().refreshRate));

        setScreen(Screens.STARTUP);
    }

    public void actualStart() {
        musicManager.mainMusic(true);
        setScreen(Screens.MAIN_MENU, true);
    }

    private Screen getMenu(Screens screen) {
        return switch (screen) {
            case STARTUP -> new StartupScreen();
            case MAIN_MENU -> new MainScreen();
            case GAME -> new GameScreen();
            case SETTINGS -> new SettingsScreen(settings, musicManager);
        };
    }

    @Override
    public void render() {
        ScreenUtils.clear(0, 0, 0, 0, true, true);
        super.render();

        assets.update(1000 / settings.getInteger("fps", Gdx.graphics.getDisplayMode().refreshRate));
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
    }

    @Override
    public void pause() {
        super.pause();
        musicManager.pause();
    }

    @Override
    public void resume() {
        super.resume();
        musicManager.resume();
    }

    @Override
    public void dispose() {
        super.dispose();
        for (Screen menu : menus.values()) if (menu != null) menu.dispose();
        ResourceManager.getInstance().dispose();
        assets.dispose();
    }
}
