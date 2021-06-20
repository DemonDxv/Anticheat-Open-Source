package me.rhys.bedrock.util.block;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.rhys.bedrock.util.BlockUtil;
import me.rhys.bedrock.util.box.BoundingBox;
import org.bukkit.block.Block;

@AllArgsConstructor @Getter
public class CollideEntry {
    private final Block block;
    private final BoundingBox boundingBox;

    public boolean isChunkLoaded() {
        return BlockUtil.isChunkLoaded(block.getLocation());
    }
}
