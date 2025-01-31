package de.tobi1craft.rapidtrack;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import de.tobi1craft.rapidtrack.Enums.Screens;

public class MusicManager {
    private final RapidTrack rapidTrack;
    private final AssetManager assets;
    private final String[] mainMusic = {
        "music/main/start.mp3",
        "menu_short.wav",
        "menu_ultrashort.wav"
    };
    private String current;
    private Music music;
    private float volume;

    public MusicManager(RapidTrack instance, AssetManager assetManager, float volume) {
        rapidTrack = instance;
        assets = assetManager;
        this.volume = volume;
        if (volume != 0) {
            assets.load("music/startup.wav", Music.class);
            assets.finishLoadingAsset("music/startup.wav");
            startup();
        }
    }

    public void setScreen(Screens screen) {
        if (screen == Screens.MAIN_MENU) mainMenu(true);
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
            switch (rapidTrack.getScreen()) {
                case MAIN_MENU -> mainMenu(false);
                //TODO: start music for other screens
            }
        }
        this.volume = volume;
        music.setVolume(volume);
    }

    public void startup() {
        music = assets.get("music/startup.wav", Music.class);
        music.setVolume(volume);
        music.setLooping(false);
        music.play();

        assets.load(mainMusic[0], Music.class);
        music.setOnCompletionListener(completedMusic -> {
            assets.unload("music/startup.wav");
            rapidTrack.start();
        });
    }

    private void mainMenu(boolean start) {
        if (music.isPlaying()) music.stop();
        if (volume == 0) return;
        if (start) current = mainMusic[0];
        music = assets.get(current, Music.class);
        music.setVolume(volume);
        music.setLooping(false);
        music.play();

        String old = current;
        current = "music/main/" + mainMusic[(int) (Math.random() * (mainMusic.length - 1)) + 1];
        assets.load(current, Music.class);

        music.setOnCompletionListener(completedMusic -> {
            assets.unload(old);
            if (rapidTrack.getScreen() == Screens.MAIN_MENU) mainMenu(false);
        });
    }
}
