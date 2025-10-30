package de.tobi1craft.rapidtrack.screens;

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
        table.setDebug(true);
        stage.addActor(table);

        resize = (width, height) -> {
            background.setSize(width, height);

            table.clearChildren();

            TextButton button = UI.getTextButton(height * 0.15f, "play");
            table.add(button).expandX();

            table.add().expandX();
            table.add().expandX();

            // start the game when the button is clicked
            button.addListener(new ChangeListener() {
                public void changed(ChangeEvent event, Actor actor) {
                    RapidTrack.getInstance().setScreen(Screens.BULLET_TEST_ADVANCED);
                }
            });
        };
    }
}
