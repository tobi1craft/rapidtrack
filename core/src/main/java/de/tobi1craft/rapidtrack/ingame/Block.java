package de.tobi1craft.rapidtrack.ingame;

import com.badlogic.gdx.math.Vector3;
import de.tobi1craft.rapidtrack.enums.Blocks;
import net.mgsx.gltf.scene3d.scene.Scene;

public class Block {
    private final Vector3 gridPos;
    private final Scene scene;
    private final float friction;
    private boolean isStart = false;
    private boolean isFinish = false;
    private boolean hasCollision = true;
    //TODO: Orientation

    public Block(Track track, Blocks block, Vector3 gridPosition) {
        gridPos = gridPosition;
        scene = new Scene(track.getBlock(block).scene);
        friction = block.getFriction();
        switch (block.getSpecialProperty()) {
            case Blocks.Props.NONE -> {
            }
            case Blocks.Props.START -> isStart = true;
            case Blocks.Props.FINISH -> isFinish = true;
            case Blocks.Props.NO_COLLISION -> hasCollision = false;
        }

        scene.modelInstance.transform.setTranslation(this.gridPos.cpy().scl(Track.SCALE));
    }

    public Vector3 getGridPos() {
        return gridPos;
    }

    public Scene getScene() {
        return scene;
    }

    public float getFriction() {
        return friction;
    }

    public boolean isStart() {
        return isStart;
    }

    public boolean isFinish() {
        return isFinish;
    }

    public boolean hasCollision() {
        return hasCollision;
    }
}
