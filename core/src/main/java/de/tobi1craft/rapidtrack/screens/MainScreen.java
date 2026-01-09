package de.tobi1craft.rapidtrack.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import de.tobi1craft.rapidtrack.RapidTrack;
import de.tobi1craft.rapidtrack.ResourceManager;
import de.tobi1craft.rapidtrack.UI;
import de.tobi1craft.rapidtrack.enums.Screens;

public class MainScreen extends Menu {

    public MainScreen() {
        stage = new Stage(new ScreenViewport(), ResourceManager.getInstance().getBatch());

        Image background = new Image(assets.get("screens/main_temp.jpg", Texture.class));
        stage.addActor(background);

        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        resize = (width, height) -> {
            background.setSize(width, height);

            table.clearChildren();

            TextButton play = UI.getTextButton(height * 0.15f, "play");
            table.add(play).expand().left().padLeft(width * 0.1f).row();
            play.addListener(new ChangeListener() {
                public void changed(ChangeEvent event, Actor actor) {
                    RapidTrack.getInstance().setScreen(Screens.GAME);
                }
            });

            TextButton settings = UI.getTextButton(height * 0.15f, "settings");
            table.add(settings).expand().left().padLeft(width * 0.1f).row();
            settings.addListener(new ChangeListener() {
                public void changed(ChangeEvent event, Actor actor) {
                    RapidTrack.getInstance().setScreen(Screens.SETTINGS);
                }
            });

            TextButton exit = UI.getTextButton(height * 0.15f, "exit");
            table.add(exit).expand().left().padLeft(width * 0.1f).row();
            exit.addListener(new ChangeListener() {
                public void changed(ChangeEvent event, Actor actor) {
                    Gdx.app.exit();
                }
            });
        };
    }
}
