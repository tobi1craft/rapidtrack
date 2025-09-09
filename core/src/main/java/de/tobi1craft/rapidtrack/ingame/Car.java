package de.tobi1craft.rapidtrack.ingame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import net.mgsx.gltf.loaders.gltf.GLTFLoader;
import net.mgsx.gltf.scene3d.scene.Scene;
import net.mgsx.gltf.scene3d.scene.SceneAsset;

public class Car {
    private final Scene scene;
    private float rotation = 0;

    public Car() {
        //?! STUCK: SceneAsset asset = RapidTrack.getInstance().getAssets().loadAndGet("models/car.gltf", SceneAsset.class);
        SceneAsset asset = new GLTFLoader().load(Gdx.files.internal("models/car.gltf"));
        scene = new Scene(asset.scene.model, true);

        //Animation example:
        //scene.animationController.setAnimation("idle", -1);
    }

    public Scene getScene() {
        return scene;
    }

    public void render(float delta) {
        // Rotate model
        rotation += delta * 45f; // 45 degrees per second
        Quaternion rotation = new Quaternion().setFromAxis(Vector3.Y, this.rotation);


        scene.modelInstance.transform.set(new Vector3(0, 0.0625f, 0), rotation, new Vector3(0.5f, 0.5f, 0.5f));

    }

    public Vector3 getTranslation() {
        return scene.modelInstance.transform.getTranslation(new Vector3());
    }

    public Quaternion getRotation() {
        return scene.modelInstance.transform.getRotation(new Quaternion(), true);
    }

    public Vector3 getScale() {
        return scene.modelInstance.transform.getScale(new Vector3());
    }
}
