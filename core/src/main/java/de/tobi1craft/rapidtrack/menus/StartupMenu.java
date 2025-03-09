package de.tobi1craft.rapidtrack.menus;

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
import de.tobi1craft.rapidtrack.enums.Screens;

public class StartupMenu extends Menu {

    @Override
    protected void load() {
        stage = new Stage(new ScreenViewport(), ResourceManager.getInstance().getBatch());

        Image background = new Image(assets.loadAndGet("screens/startup_temp.png", Texture.class));
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

            TextButton button = UI.getLiteralTextButton(height * 0.15f, "Start");
            table.add(button).expandY();

            // start the game when the button is clicked
            button.addListener(new ChangeListener() {
                public void changed(ChangeEvent event, Actor actor) {
                    if (assets.isFinished()) RapidTrack.getInstance().setScreen(Screens.MAIN_MENU);
                }
            });
        };
        assets.load("screens/main_temp.jpg", Texture.class);
        assets.load("i18n/messages", I18NBundle.class);
    }
}
