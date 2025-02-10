package de.tobi1craft.rapidtrack.Menus;

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

public class StartupMenu extends Menu {

    @Override
    protected void load() {
        assets.load("screens/startup.png", Texture.class);
        assets.finishLoading();

        stage = new Stage(new ScreenViewport(), ResourceManager.getInstance().getBatch());

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

            TextButton button = UI.getTextButton("Start", height * 0.15f);
            table.add(button).expandY();

            // start the game when the button is clicked
            button.addListener(new ChangeListener() {
                public void changed(ChangeEvent event, Actor actor) {
                    if (assets.isFinished()) RapidTrack.getInstance().start();
                }
            });
        };
    }
}
