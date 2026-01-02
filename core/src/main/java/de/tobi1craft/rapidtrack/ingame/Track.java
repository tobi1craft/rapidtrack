package de.tobi1craft.rapidtrack.ingame;

import com.badlogic.gdx.math.Vector3;
import de.tobi1craft.rapidtrack.RapidTrack;
import de.tobi1craft.rapidtrack.enums.Blocks;
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
        grid.add(new Block(this, Blocks.ROAD_START, new Vector3(0, 0, 11)));
        for (int i = -10; i <= 10; i++) grid.add(new Block(this, Blocks.ROAD_STRAIGHT, new Vector3(0, 0, i)));
        grid.add(new Block(this, Blocks.ROAD_FINISH, new Vector3(0, 0, -11)));
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
        for (Blocks block : getUsedBlocks()) {
            RapidTrack.getInstance().getAssets().unload("blocks/" + block.getPath() + ".glb");
        }
    }
}
