package de.tobi1craft.rapidtrack;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.math.MathUtils;
import de.tobi1craft.rapidtrack.util.AssetsHelper;
import de.tobi1craft.rapidtrack.util.RTAssetManager;

import java.util.List;

public class MusicManager {
    private final RTAssetManager assets;
    private final String startupMusic = AssetsHelper.getFilesInDirectory("music", ".*startup.*\\.(mp3|wav|ogg)$", null).getFirst();
    private final String mainMusicStart = AssetsHelper.getFilesInDirectory("music/d70", ".*start.*\\.(mp3|wav|ogg)$", null).getFirst();
    private final List<String> mainMusic = AssetsHelper.getFilesInDirectory("music/d70", ".*\\.(mp3|wav|ogg)$", ".*start.*");
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

    public void pause() {
        if (volume != 0) music.pause();
    }

    public void resume() {
        if (volume != 0 && !music.isPlaying()) music.play();
    }

    public void setVolume(float volume) {
        if (volume == this.volume) return;
        if (this.volume == 0) mainMusic(false);
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
            RapidTrack.getInstance().actualStart();
        });
    }

    public void mainMusic(boolean start) {
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
            mainMusic(false);
            assets.unload(old);
        });
    }
}
