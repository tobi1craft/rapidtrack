package de.tobi1craft.rapidtrack.screens;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import de.tobi1craft.rapidtrack.RapidTrack;
import de.tobi1craft.rapidtrack.ResourceManager;
import de.tobi1craft.rapidtrack.UI;

public class StartupScreen extends Menu {

    public StartupScreen() {
        stage = new Stage(new ScreenViewport(), ResourceManager.getInstance().getBatch());

        Image background = new Image(assets.loadAndGet("screens/startup.png", Texture.class));
        stage.addActor(background);

        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        resize = (width, height) -> {
            background.setSize(width, height);

            table.clearChildren();

            TextButton start = UI.getTextButton(height * 0.15f, "start");
            table.add(start).expandY().bottom().pad(height * 0.05f);

            // start the game when the button is clicked
            start.addListener(new ChangeListener() {
                public void changed(ChangeEvent event, Actor actor) {
                    if (assets.isFinished()) RapidTrack.getInstance().actualStart();
                }
            });
        };
        assets.load("screens/asphalt_bg.png", Texture.class);
        assets.load("i18n/messages", I18NBundle.class);
    }

    @Override
    public void hide() {
        assets.unload("screens/startup.png");
    }
}
