package de.tobi1craft.rapidtrack;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.math.MathUtils;
import de.tobi1craft.rapidtrack.enums.Screens;
import de.tobi1craft.rapidtrack.util.AssetsHelper;
import de.tobi1craft.rapidtrack.util.RTAssetManager;

import java.util.List;

public class MusicManager {
    private final RTAssetManager assets;
    private final String startupMusic = AssetsHelper.getFilesInDirectory("music", ".*startup.*\\.(mp3|wav|ogg)$", null).getFirst();
    private final String mainMusicStart = AssetsHelper.getFilesInDirectory("music/main", ".*start.*\\.(mp3|wav|ogg)$", null).getFirst();
    private final List<String> mainMusic = AssetsHelper.getFilesInDirectory("music/main", ".*\\.(mp3|wav|ogg)$", ".*start.*");
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
                Screens s = RapidTrack.getInstance().whichScreen();
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
            switch (RapidTrack.getInstance().whichScreen()) {
                case MAIN_MENU, SETTINGS -> mainMenu(false);
                //TODO: start music for other screens
            }
        }
        this.volume = volume;
        music.setVolume(volume);
    }

    public void startup() {
        music = assets.loadAndGet(startupMusic, Music.class);
        music.setVolume(volume);
        music.setLooping(false);
        music.play();

        assets.load(mainMusicStart, Music.class);
        music.setOnCompletionListener(_ -> {
            assets.unload(startupMusic);
            RapidTrack.getInstance().setScreen(Screens.MAIN_MENU);
        });
    }

    private void mainMenu(boolean start) {
        if (music.isPlaying()) music.stop();
        if (volume == 0) return;
        if (start) current = mainMusicStart;
        music = assets.get(current, Music.class);
        music.setVolume(volume);
        music.setLooping(false);
        music.play();

        String old = current;
        current = mainMusic.get(MathUtils.random(mainMusic.size() - 1));
        assets.load(current, Music.class);

        music.setOnCompletionListener(_ -> {
            assets.unload(old);
            if (RapidTrack.getInstance().whichScreen() == Screens.MAIN_MENU) mainMenu(false);
        });
    }
}
