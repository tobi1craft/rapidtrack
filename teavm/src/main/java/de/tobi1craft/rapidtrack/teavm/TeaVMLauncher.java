package de.tobi1craft.rapidtrack.teavm;

import com.github.xpenatan.gdx.backends.teavm.TeaApplication;
import com.github.xpenatan.gdx.backends.teavm.TeaApplicationConfiguration;
import de.tobi1craft.rapidtrack.RapidTrack;

/**
 * Launches the TeaVM/HTML application.
 */
public class TeaVMLauncher {
    @SuppressWarnings("deprecation")
    static void main() {
        TeaApplicationConfiguration config = new TeaApplicationConfiguration();
        config.width = 0;
        config.height = 0;

        config.showDownloadLogs = true;
        config.useGL30 = true;

        config.preloadListener = assetLoader -> assetLoader.loadScript("freetype.js"); //? Was in GH examples once, maybe remove

        new TeaApplication(new RapidTrack(), config); //TODO: Audio fix available on GitHub
    }
}
