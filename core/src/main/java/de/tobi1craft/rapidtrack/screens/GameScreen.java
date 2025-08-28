package de.tobi1craft.rapidtrack.screens;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import de.tobi1craft.rapidtrack.RapidTrack;
import de.tobi1craft.rapidtrack.ResourceManager;
import de.tobi1craft.rapidtrack.UI;
import de.tobi1craft.rapidtrack.enums.Screens;

public class GameScreen extends Menu {

    public GameScreen() {
        stage = new Stage(new ScreenViewport(), ResourceManager.getInstance().getBatch());

        Table table = new Table();
        table.setFillParent(true);
        table.setDebug(true);
        stage.addActor(table);

        resize = (width, height) -> {
            table.clearChildren();

            TextButton button = UI.getLiteralTextButton(height * 0.15f, "hi");
            table.add(button).expandX();

            table.add().expandX();
            table.add().expandX();

            // start the game when the button is clicked
            button.addListener(new ChangeListener() {
                public void changed(ChangeEvent event, Actor actor) {
                    RapidTrack.getInstance().setScreen(Screens.GAME);
                }
            });
        };
    }
}
