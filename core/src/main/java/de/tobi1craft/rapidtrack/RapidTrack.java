package de.tobi1craft.rapidtrack;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.ScreenUtils;
import de.tobi1craft.rapidtrack.destinations.Destination;
import de.tobi1craft.rapidtrack.enums.Screens;
import de.tobi1craft.rapidtrack.screens.GameScreen;
import de.tobi1craft.rapidtrack.screens.MainScreen;
import de.tobi1craft.rapidtrack.screens.StartupScreen;
import de.tobi1craft.rapidtrack.screens.Test3D;
import de.tobi1craft.rapidtrack.screens.bullet.AdvancedBulletScreen;
import de.tobi1craft.rapidtrack.screens.bullet.BasicBulletScreen;
import de.tobi1craft.rapidtrack.screens.bullet.DynamicCharacterBulletScreen;
import de.tobi1craft.rapidtrack.util.RTAssetManager;

import java.util.HashMap;

public class RapidTrack extends Game {

    private static RapidTrack instance;
    private final HashMap<Screens, Screen> menus = new HashMap<>();
    private Test3D test;
    private MusicManager musicManager;
    private Preferences settings;
    private RTAssetManager assets;
    private Screens screen;
    private Destination destination;

    public static RapidTrack getInstance() {
        return instance;
    }

    public RTAssetManager getAssets() {
        return assets;
    }

    public Screens whichScreen() {
        return screen;
    }

    public void setScreen(Screens screen) {
        musicManager.setScreen(screen);
        if (menus.get(screen) == null) menus.put(screen, getMenu(screen));
        this.setScreen(menus.get(screen));
        this.screen = screen;

    }

    @Override
    public void create() {
        Gdx.app.setLogLevel(Application.LOG_DEBUG); //TODO: Production
        instance = this;
        settings = Gdx.app.getPreferences("settings");
        assets = new RTAssetManager();
        Bullet.init(false, true);
        musicManager = new MusicManager(assets, 0.02f);

        setScreen(Screens.STARTUP);
    }

    private Screen getMenu(Screens screen) {
        return switch (screen) {
            case STARTUP -> new StartupScreen();
            case MAIN_MENU -> new MainScreen();
            case GAME -> new GameScreen();
            case BULLET_TEST -> new BasicBulletScreen(this);
            case BULLET_TEST_ADVANCED -> new AdvancedBulletScreen(this);
            case BULLET_TEST_DYNAMIC_CHARACTER -> new DynamicCharacterBulletScreen(this);
            //TODO all Menus
            default -> null;
        };
    }

    @Override
    public void render() {
        ScreenUtils.clear(Color.CLEAR, true); //TODO: mit anti-aliasing
        super.render();

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
        ResourceManager.getInstance().dispose();
        assets.dispose();
        for (Screen menu : menus.values()) if (menu != null) menu.dispose();
    }
}
