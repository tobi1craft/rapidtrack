package de.tobi1craft.rapidtrack.ingame;

import com.badlogic.gdx.math.Vector3;
import de.tobi1craft.rapidtrack.RapidTrack;
import de.tobi1craft.rapidtrack.enums.Blocks;
import de.tobi1craft.rapidtrack.util.RTAssetManager;
import net.mgsx.gltf.scene3d.scene.SceneAsset;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;

public class Track {
    //TODO: - Map/List = blocks;

    public static final Vector3 SCALE = new Vector3(32, 8, 32);

    public final List<Block> grid = new ArrayList<>();
    //? EnumMap -> Map aber nur ein Enum für alle möglichen Keys
    private final java.util.Map<Blocks, SceneAsset> blocks = new EnumMap<>(Blocks.class);

    public Track() {
        load();
        /* Test Track:
        grid.add(new Block(this, Blocks.ROAD_START, new Vector3(0, 0, 11)));
        for (int i = -10; i <= 10; i++) grid.add(new Block(this, Blocks.ROAD_STRAIGHT, new Vector3(0, 0, i)));
        grid.add(new Block(this, Blocks.ROAD_FINISH, new Vector3(0, 0, -11)));
         */

        //Example Track
        grid.add(new Block(this, Blocks.ROAD_START, new Vector3(0, 0, 0)));
        grid.add(new Block(this, Blocks.ROAD_TURN, new Vector3(0, 0, -1)));
        grid.add(new Block(this, Blocks.ROAD_STRAIGHT, new Vector3(1, 0, -1), 90));
        grid.add(new Block(this, Blocks.ROAD_TURN, new Vector3(2, 0, -1), 180));
        grid.add(new Block(this, Blocks.ROAD_STRAIGHT, new Vector3(2, 0, -2)));
        grid.add(new Block(this, Blocks.ROAD_STRAIGHT, new Vector3(2, 0, -3)));
        grid.add(new Block(this, Blocks.ROAD_TURN, new Vector3(2, 0, -4), 270));
        grid.add(new Block(this, Blocks.ROAD_STRAIGHT, new Vector3(1, 0, -4), 90));
        grid.add(new Block(this, Blocks.ROAD_TURN, new Vector3(0, 0, -4)));
        grid.add(new Block(this, Blocks.ROAD_TURN, new Vector3(0, 0, -3), 180));
        grid.add(new Block(this, Blocks.ROAD_STRAIGHT, new Vector3(-1, 0, -3), 90));
        grid.add(new Block(this, Blocks.ROAD_STRAIGHT, new Vector3(-2, 0, -3), 90));
        grid.add(new Block(this, Blocks.ROAD_STRAIGHT, new Vector3(-3, 0, -3), 90));
        grid.add(new Block(this, Blocks.ROAD_STRAIGHT, new Vector3(-4, 0, -3), 90));
        grid.add(new Block(this, Blocks.ROAD_TURN, new Vector3(-5, 0, -3), 90));
        grid.add(new Block(this, Blocks.ROAD_STRAIGHT, new Vector3(-5, 0, -4)));
        grid.add(new Block(this, Blocks.ROAD_STRAIGHT, new Vector3(-5, 0, -5)));
        grid.add(new Block(this, Blocks.ROAD_TURN, new Vector3(-5, 0, -6)));
        grid.add(new Block(this, Blocks.ROAD_TURN, new Vector3(-4, 0, -6), 180));
        grid.add(new Block(this, Blocks.ROAD_TURN, new Vector3(-4, 0, -7), 270));
        grid.add(new Block(this, Blocks.ROAD_TURN, new Vector3(-5, 0, -7), 90));
        grid.add(new Block(this, Blocks.ROAD_STRAIGHT, new Vector3(-5, 0, -8)));
        grid.add(new Block(this, Blocks.ROAD_STRAIGHT, new Vector3(-5, 0, -9)));
        grid.add(new Block(this, Blocks.ROAD_TURN, new Vector3(-5, 0, -10)));
        grid.add(new Block(this, Blocks.ROAD_STRAIGHT, new Vector3(-4, 0, -10), 90));
        grid.add(new Block(this, Blocks.ROAD_TURN, new Vector3(-3, 0, -10), 270));
        grid.add(new Block(this, Blocks.ROAD_TURN, new Vector3(-3, 0, -9), 90));
        grid.add(new Block(this, Blocks.ROAD_STRAIGHT, new Vector3(-2, 0, -9), 90));
        grid.add(new Block(this, Blocks.ROAD_TURN, new Vector3(-1, 0, -9), 270));
        grid.add(new Block(this, Blocks.ROAD_TURN, new Vector3(-1, 0, -8), 90));
        grid.add(new Block(this, Blocks.ROAD_STRAIGHT, new Vector3(0, 0, -8), 90));
        grid.add(new Block(this, Blocks.ROAD_STRAIGHT, new Vector3(1, 0, -8), 90));
        grid.add(new Block(this, Blocks.ROAD_TURN, new Vector3(2, 0, -8), 180));
        grid.add(new Block(this, Blocks.ROAD_STRAIGHT, new Vector3(2, 0, -9)));
        grid.add(new Block(this, Blocks.ROAD_STRAIGHT, new Vector3(2, 0, -10)));
        grid.add(new Block(this, Blocks.ROAD_TURN, new Vector3(2, 0, -11)));
        grid.add(new Block(this, Blocks.ROAD_STRAIGHT, new Vector3(3, 0, -11), 90));
        grid.add(new Block(this, Blocks.ROAD_STRAIGHT, new Vector3(4, 0, -11), 90));
        grid.add(new Block(this, Blocks.ROAD_FINISH, new Vector3(5, 0, -11), 90));
    }

    public void load() {
        for (Blocks block : getUsedBlocks()) {
            blocks.put(block, RapidTrack.getInstance().getAssets().loadAndGet("blocks/" + block.getPath() + ".glb", SceneAsset.class));
        }
    }

    private List<Blocks> getUsedBlocks() {
        return Arrays.stream(Blocks.values()).toList();
    }

    public SceneAsset getBlock(Blocks block) {
        return blocks.get(block);
    }

    public void dispose() {
        RTAssetManager assets = RapidTrack.getInstance().getAssets();
        for (Blocks block : getUsedBlocks()) {
            String path = "blocks/" + block.getPath() + ".glb";
            if (assets.isLoaded(path)) assets.unload(path);
        }
    }
}
