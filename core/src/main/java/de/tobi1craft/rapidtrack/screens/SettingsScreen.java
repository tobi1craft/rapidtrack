package de.tobi1craft.rapidtrack.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import de.tobi1craft.rapidtrack.MusicManager;
import de.tobi1craft.rapidtrack.RapidTrack;
import de.tobi1craft.rapidtrack.ResourceManager;
import de.tobi1craft.rapidtrack.UI;
import de.tobi1craft.rapidtrack.enums.Screens;

public class SettingsScreen extends Menu {

    public SettingsScreen(Preferences settings, MusicManager musicManager) {
        stage = new Stage(new ScreenViewport(), ResourceManager.getInstance().getBatch());

        Image background = new Image(assets.get("screens/asphalt_bg.png", Texture.class));
        stage.addActor(background);

        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        resize = (width, height) -> {
            background.setSize(width, height);

            table.clearChildren();


            Label fpsLabel = UI.getLabel(height * 0.15f, "fpsSetting");
            table.add(fpsLabel).expand().left().pad(height * 0.05f);
            TextField fps = UI.getLiteralTextField(height * 0.15f, settings.getInteger("fps", Gdx.graphics.getDisplayMode().refreshRate) + "");
            table.add(fps).width(500).expand().left().pad(height * 0.05f).row();
            fps.addListener(new ChangeListener() {
                public void changed(ChangeEvent event, Actor actor) {
                    int fps;
                    try {
                        fps = Integer.parseInt(((TextField) actor).getText());
                        if (fps < 10) fps = 10;
                        settings.putInteger("fps", fps);
                        settings.flush();
                        Gdx.graphics.setForegroundFPS(fps);
                    } catch (NumberFormatException _) {
                    }
                }
            });

            Label volumeLabel = UI.getLabel(height * 0.15f, "volumeSetting");
            table.add(volumeLabel).expand().left().pad(height * 0.05f);
            TextField volume = UI.getLiteralTextField(height * 0.15f, settings.getInteger("volume", 2) + "");
            table.add(volume).width(500).expand().left().pad(height * 0.05f).row();
            volume.addListener(new ChangeListener() {
                public void changed(ChangeEvent event, Actor actor) {
                    int volume;
                    try {
                        volume = Integer.parseInt(((TextField) actor).getText());
                        volume = Math.min(Math.max(volume, 0), 100);
                        settings.putInteger("volume", volume);
                        settings.flush();
                        musicManager.setVolume(volume / 100f);
                    } catch (NumberFormatException _) {
                    }
                }
            });


            TextButton exit = UI.getTextButton(height * 0.15f, "mainMenu");
            table.add(exit).expand().left().padLeft(height * 0.05f).row();
            exit.addListener(new ChangeListener() {
                public void changed(ChangeEvent event, Actor actor) {
                    RapidTrack.getInstance().setScreen(Screens.MAIN_MENU);
                }
            });
        };
    }
}
