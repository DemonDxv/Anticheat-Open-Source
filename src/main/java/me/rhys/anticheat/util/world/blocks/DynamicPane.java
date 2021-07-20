package me.rhys.anticheat.util.world.blocks;

import me.rhys.anticheat.tinyprotocol.api.ProtocolVersion;
import me.rhys.anticheat.util.world.CollisionBox;
import me.rhys.anticheat.util.world.types.CollisionFactory;
import me.rhys.anticheat.util.world.types.ComplexCollisionBox;
import me.rhys.anticheat.util.world.types.SimpleCollisionBox;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.material.Stairs;

@SuppressWarnings("Duplicates")
public class DynamicPane implements CollisionFactory {

    private static final double width = 0.0625;
    private static final double min = .5 - width;
    private static final double max = .5 + width;

    @Override
    public CollisionBox fetch(ProtocolVersion version, Block b) {
        ComplexCollisionBox box = new ComplexCollisionBox(new SimpleCollisionBox(min, 0, min, max, 1, max));
        boolean east =  fenceConnects(version,b, BlockFace.EAST );
        boolean north = fenceConnects(version,b, BlockFace.NORTH);
        boolean south = fenceConnects(version,b, BlockFace.SOUTH);
        boolean west =  fenceConnects(version,b, BlockFace.WEST );

        if (version.isBelow(ProtocolVersion.V1_9) && !(east||north||south||west)) {
            east = true;
            west = true;
            north = true;
            south = true;
        }

        if (east) box.add(new SimpleCollisionBox(max, 0, min, 1, 1, max));
        if (west) box.add(new SimpleCollisionBox(0, 0, min, max, 1, max));
        if (north) box.add(new SimpleCollisionBox(min, 0, 0, max, 1, min));
        if (south) box.add(new SimpleCollisionBox(min, 0, max, max, 1, 1));
        return box;
    }


    private static boolean fenceConnects(ProtocolVersion v, Block fenceBlock, BlockFace direction) {
        Block targetBlock = fenceBlock.getRelative(direction,1);
        BlockState sFence = fenceBlock.getState();
        BlockState sTarget = targetBlock.getState();
        Material target = sTarget.getType();
        Material fence = sFence.getType();

        if (!isPane(target)&&DynamicFence.isBlacklisted(target))
            return false;

        if(target.name().contains("STAIRS")) {
            if (v.isBelow(ProtocolVersion.V1_12)) return false;
            Stairs stairs = (Stairs) sTarget.getData();
            return stairs.getFacing() == direction;
        } else return isPane(target) || (target.isSolid() && !target.isTransparent());
    }

    private static boolean isPane(Material m) {
        int id = m.getId();
        return id == 101 || id == 102 || id == 160;
    }

}