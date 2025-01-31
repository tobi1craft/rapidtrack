package de.tobi1craft.rapidtrack;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import de.tobi1craft.rapidtrack.Destionations.Destination;
import de.tobi1craft.rapidtrack.Enums.Screens;

import java.util.HashMap;
import java.util.function.BiConsumer;

/**
 * {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms.
 */
public class RapidTrack extends ApplicationAdapter {
    private MusicManager musicManager;

    private Preferences settings;

    private AssetManager assets;
    private Stage stage;
    private Skin skin;
    private SpriteBatch batch;
    private Screens screen;
    private BiConsumer<Integer, Integer> resize;
    private Destination destination;
    private final HashMap<Integer, BitmapFont> fonts = new HashMap<>();

    public Screens getScreen() {
        return screen;
    }

    public void setScreen(Screens screen) {
        this.screen = screen;
        musicManager.setScreen(screen);
    }

    @Override
    public void create() {
        screen = Screens.STARTUP;
        settings = Gdx.app.getPreferences("settings");
        assets = new AssetManager();
        musicManager = new MusicManager(this, assets, 0.02f);
        setupSkin();

        batch = new SpriteBatch();

        Gdx.input.setInputProcessor(startupStage());


    }

    @Override
    public void render() {
        //ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);

        if (assets.update(1000 / settings.getInteger("fps", 60))) {


        } else if (screen == Screens.STARTUP) {

        } else {
            screen = Screens.LOADING;
        }

        if (stage != null) {
            stage.act();
            stage.draw();
        }
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        if (resize != null) resize.accept(width, height);
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
        stage.dispose();
        skin.dispose();
        batch.dispose();
        assets.dispose();
    }

    public void start() {
        setScreen(Screens.MAIN_MENU);
        if (assets.isLoaded("screens/startup.png")) assets.unload("screens/startup.png");
    }

    private Stage startupStage() {
        assets.load("screens/startup.png", Texture.class);
        assets.finishLoading();

        stage = new Stage(new ScreenViewport(), batch);

        Image background = new Image(assets.get("screens/startup.png", Texture.class));
        stage.addActor(background);

        Table table = new Table();
        table.setFillParent(true);
        table.setDebug(true);
        stage.addActor(table);

        resize = (width, height) -> {
            background.setSize(width, height);

            table.clearChildren();
            table.add().expandY().row();
            table.add().expandY().row();

            TextButton button = getTextButton("Start", height * 0.15f);
            table.add(button).expandY();

            // start the game when the button is clicked
            button.addListener(new ChangeListener() {
                public void changed(ChangeEvent event, Actor actor) {
                    if (assets.isFinished()) start();
                }
            });
        };

        return stage;
    }

    private void setupSkin() {
        skin = new Skin(Gdx.files.internal("skin/quantum-horizon-ui.json")); //TODO: Maybe do own UI skin
    }

    private BitmapFont getFont(int size, Color color) {
        if (fonts.containsKey(size)) return fonts.get(size);

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/copse.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = size;
        BitmapFont font = generator.generateFont(parameter);
        fonts.put(size, font);
        generator.dispose();
        return font;
    }

    private TextButton getTextButton(String text, float height) {
        return getTextButton(text, height, Color.WHITE);
    }

    private TextButton getTextButton(String text, float height, Color color) {
        TextButton.TextButtonStyle style = skin.get(TextButton.TextButtonStyle.class);
        height = Math.max(height, style.up.getMinHeight());
        style.font = getFont((int) (height / 1.618f), color);
        TextButton button = new TextButton(text, style);
        button.setHeight(height);
        return button;
    }
}
