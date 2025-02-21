package de.tobi1craft.rapidtrack.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class RTAssetManager extends AssetManager {

    //synchronized: prevents parallel exec, copied because of use in AssetManager
    public synchronized <T> T loadAndGet(String fileName, Class<T> type) {
        if (!contains(fileName, type)) load(fileName, type);
        if (!isLoaded(fileName, type)) finishLoadingAsset(fileName);
        return get(fileName, type, true);
    }

    @Override
    public synchronized <T> T get(String fileName, Class<T> type) {
        if(!contains(fileName, type)) {
            Gdx.app.error(getClass().getName(), "Asset not queued: " + fileName, new GdxRuntimeException(""));
            load(fileName, type);
        }
        if (!isLoaded(fileName, type)) finishLoadingAsset(fileName);
        return get(fileName, type, true);
    }
}
