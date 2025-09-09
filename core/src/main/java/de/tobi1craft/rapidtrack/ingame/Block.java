package de.tobi1craft.rapidtrack.ingame;

import com.badlogic.gdx.math.Vector3;
import net.mgsx.gltf.scene3d.scene.Scene;
import net.mgsx.gltf.scene3d.scene.SceneAsset;

public class Block {
    private final Vector3 position;
    private final Scene scene;

    public Block(SceneAsset asset, Vector3 position) {
        this.position = position;
        scene = new Scene(asset.scene);
        scene.modelInstance.transform.setTranslation(this.position);
    }

    public Scene getScene() {
        return scene;
    }
}
