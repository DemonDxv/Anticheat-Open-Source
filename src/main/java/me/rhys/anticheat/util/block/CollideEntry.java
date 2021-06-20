package me.rhys.anticheat.util.block;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.rhys.anticheat.util.BlockUtil;
import me.rhys.anticheat.util.box.BoundingBox;
import org.bukkit.block.Block;

@AllArgsConstructor @Getter
public class CollideEntry {
    private final Block block;
    private final BoundingBox boundingBox;

    public boolean isChunkLoaded() {
        return BlockUtil.isChunkLoaded(block.getLocation());
    }
}
