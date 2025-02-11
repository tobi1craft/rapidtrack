package de.tobi1craft.rapidtrack.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;

public class ForceAssetManager extends AssetManager {
    //synchronized: prevents parallel exec, copied because of use in AssetManager
    public synchronized <T> T forceGet(String fileName, Class<T> type) {
        if (!contains(fileName, type)) {
            load(fileName, type);
            Gdx.app.debug(getClass().getName(), fileName + " force queued");
        }
        if (!isLoaded(fileName, type)) finishLoadingAsset(fileName);
        return get(fileName, type, true);
    }
}
