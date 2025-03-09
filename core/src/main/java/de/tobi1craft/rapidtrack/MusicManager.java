package de.tobi1craft.rapidtrack;

import com.badlogic.gdx.audio.Music;
import de.tobi1craft.rapidtrack.enums.Screens;
import de.tobi1craft.rapidtrack.util.RTAssetManager;

public class MusicManager {
    private final RTAssetManager assets;
    private final String[] mainMusic = {
        "start.mp3",
        "menu_short.wav",
        "menu_ultrashort.wav"
    };
    private String current;
    private Music music;
    private float volume;

    public MusicManager(RTAssetManager assetManager, float volume) {
        assets = assetManager;
        this.volume = volume;
        if (volume != 0) {
            startup();
        }
    }

    public void setScreen(Screens screen) {
        switch (screen) {
            case MAIN_MENU, SETTINGS -> {
                Screens s = RapidTrack.getInstance().getScreen();
                if (s == Screens.MAIN_MENU || s == Screens.SETTINGS) break;
                mainMenu(true);
            }
            //TODO: start music for other screens
        }
    }

    public void pause() {
        if (volume != 0) music.pause();
    }

    public void resume() {
        if (volume != 0 && !music.isPlaying()) music.play();
    }

    public void setVolume(float volume) {
        if (volume == this.volume) return;
        if (this.volume == 0) {
            switch (RapidTrack.getInstance().getScreen()) {
                case MAIN_MENU, SETTINGS -> mainMenu(false);
                //TODO: start music for other screens
            }
        }
        this.volume = volume;
        music.setVolume(volume);
    }

    public void startup() {
        music = assets.loadAndGet("music/startup.wav", Music.class);
        music.setVolume(volume);
        music.setLooping(false);
        music.play();

        assets.load("music/main/" + mainMusic[0], Music.class);
        music.setOnCompletionListener(completedMusic -> {
            assets.unload("music/startup.wav");
            RapidTrack.getInstance().setScreen(Screens.MAIN_MENU);
        });
    }

    private void mainMenu(boolean start) {
        if (music.isPlaying()) music.stop();
        if (volume == 0) return;
        if (start) current = "music/main/" + mainMusic[0];
        music = assets.get(current, Music.class);
        music.setVolume(volume);
        music.setLooping(false);
        music.play();

        String old = current;
        current = "music/main/" + mainMusic[(int) (Math.random() * (mainMusic.length - 1)) + 1];
        assets.load(current, Music.class);

        music.setOnCompletionListener(completedMusic -> {
            assets.unload(old);
            if (RapidTrack.getInstance().getScreen() == Screens.MAIN_MENU) mainMenu(false);
        });
    }
}
