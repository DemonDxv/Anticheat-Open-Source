package me.rhys.bedrock.util;

import me.rhys.bedrock.tinyprotocol.api.ProtocolVersion;
import me.rhys.bedrock.util.box.BoundingBox;
import net.minecraft.server.v1_16_R3.World;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BlockUtil {
    public static Map<Material, BoundingBox> collisionBoundingBoxes;

    public BlockUtil() {
        collisionBoundingBoxes = new HashMap<>();
        setupCollisionBB();
    }

    public static boolean isChunkLoaded(Location location) {
        return (location.getWorld().isChunkLoaded(location.getBlockX() >> 4, location.getBlockZ() >> 4));
    }

    public static Block getBlock(Location location) {
        if (location.getWorld().isChunkLoaded(location.getBlockX() >> 4, location.getBlockZ() >> 4)) {
            return location.getBlock();
        } else {
            return null;
        }
    }

    public static Block getBlockNoChunk(Location location) {
        return location.getBlock();
    }

    public static boolean isStair(Block block) {
        return block.getType().toString().contains("STAIR");
    }

    public static boolean isSlab(Block block) {
        return block.getType().toString().contains("STEP") || block.getType().toString().contains("SLAB");
    }

    public static boolean isPiston(Block block) {
        return block.getType().getId() == 36 || block.getType().getId() == 34 || block.getType().getId() == 33 || block.getType().getId() == 29;
    }

    private void setupCollisionBB() {
        if (ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_13)) {
            collisionBoundingBoxes.put(Material.getMaterial("FIRE"), new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
            collisionBoundingBoxes.put(Material.getMaterial("STONE_PLATE"), new BoundingBox((float) 0.0625, (float) 0.0, (float) 0.0625, (float) 0.9375, (float) 0.0625, (float) 0.9375));
            collisionBoundingBoxes.put(Material.getMaterial("GRAVEL"), new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
            collisionBoundingBoxes.put(Material.getMaterial("COBBLESTONE"), new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
            collisionBoundingBoxes.put(Material.getMaterial("NETHER_BRICK"), new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
            collisionBoundingBoxes.put(Material.getMaterial("PUMPKIN"), new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
            collisionBoundingBoxes.put(Material.getMaterial("CARROT"), new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 0.25, (float) 1.0));
            collisionBoundingBoxes.put(Material.getMaterial("TNT"), new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
            collisionBoundingBoxes.put(Material.getMaterial("SOUL_SAND"), new BoundingBox(0f, 0f,0f, 1f, 0.875f, 1f));
            collisionBoundingBoxes.put(Material.getMaterial("SAND"), new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
            collisionBoundingBoxes.put(Material.getMaterial("WOOD_PLATE"), new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
            collisionBoundingBoxes.put(Material.getMaterial("SIGN_POST"), new BoundingBox((float) 0.25, (float) 0.0, (float) 0.25, (float) 0.75, (float) 1.0, (float) 0.75));
            collisionBoundingBoxes.put(Material.getMaterial("COCOA"), new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
            collisionBoundingBoxes.put(Material.getMaterial("DETECTOR_RAIL"), new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 0.125, (float) 1.0));
            collisionBoundingBoxes.put(Material.getMaterial("HARD_CLAY"), new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
            collisionBoundingBoxes.put(Material.getMaterial("NETHERRACK"), new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
            collisionBoundingBoxes.put(Material.getMaterial("STONE_BUTTON"), new BoundingBox((float) 0.3125, (float) 0.0, (float) 0.375, (float) 0.6875, (float) 0.125, (float) 0.625));
            collisionBoundingBoxes.put(Material.getMaterial("CLAY"), new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
            collisionBoundingBoxes.put(Material.getMaterial("QUARTZ_BLOCK"), new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
            collisionBoundingBoxes.put(Material.getMaterial("HUGE_MUSHROOM_1"), new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
            collisionBoundingBoxes.put(Material.getMaterial("HUGE_MUSHROOM_2"), new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
            collisionBoundingBoxes.put(Material.getMaterial("LAVA"), new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
            collisionBoundingBoxes.put(Material.getMaterial("BEACON"), new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
            collisionBoundingBoxes.put(Material.getMaterial("GRASS"), new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
            collisionBoundingBoxes.put(Material.getMaterial("DEAD_BUSH"), new BoundingBox((float) 0.09999999403953552, (float) 0.0, (float) 0.09999999403953552, (float) 0.8999999761581421, (float) 0.800000011920929, (float) 0.8999999761581421));
            collisionBoundingBoxes.put(Material.getMaterial("GLOWSTONE"), new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
            collisionBoundingBoxes.put(Material.getMaterial("ICE"), new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
            collisionBoundingBoxes.put(Material.getMaterial("BRICK"), new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
            collisionBoundingBoxes.put(Material.getMaterial("REDSTONE_TORCH_ON"), new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
            collisionBoundingBoxes.put(Material.getMaterial("REDSTONE_TORCH_OFF"), new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
            collisionBoundingBoxes.put(Material.getMaterial("POWERED_RAIL"), new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 0.125, (float) 1.0));
            collisionBoundingBoxes.put(Material.getMaterial("DISPENSER"), new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
            collisionBoundingBoxes.put(Material.getMaterial("JUKEBOX"), new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
            collisionBoundingBoxes.put(Material.getMaterial("EMERALD_BLOCK"), new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
            collisionBoundingBoxes.put(Material.getMaterial("STONE"), new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
            collisionBoundingBoxes.put(Material.getMaterial("BOOKSHELF"), new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
            collisionBoundingBoxes.put(Material.getMaterial("MYCEL"), new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
            collisionBoundingBoxes.put(Material.getMaterial("OBSIDIAN"), new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
            collisionBoundingBoxes.put(Material.getMaterial("PORTAL"), new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
            collisionBoundingBoxes.put(Material.getMaterial("GOLD_PLATE"), new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
            collisionBoundingBoxes.put(Material.getMaterial("COAL_BLOCK"), new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
            collisionBoundingBoxes.put(Material.getMaterial("GOLD_BLOCK"), new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
            collisionBoundingBoxes.put(Material.getMaterial("STAINED_CLAY"), new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
            collisionBoundingBoxes.put(Material.getMaterial("MOB_SPAWNER"), new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
            collisionBoundingBoxes.put(Material.getMaterial("BEDROCK"), new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
            collisionBoundingBoxes.put(Material.getMaterial("IRON_ORE"), new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
            collisionBoundingBoxes.put(Material.getMaterial("REDSTONE_BLOCK"), new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
            collisionBoundingBoxes.put(Material.getMaterial("SIGN"), new BoundingBox((float) 0.25, (float) 0.0, (float) 0.25, (float) 0.75, (float) 1.0, (float) 0.75));
            collisionBoundingBoxes.put(Material.getMaterial("IRON_PLATE"), new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
            collisionBoundingBoxes.put(Material.getMaterial("GOLD_ORE"), new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
            collisionBoundingBoxes.put(Material.getMaterial("POTATO"), new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 0.25, (float) 1.0));
            collisionBoundingBoxes.put(Material.getMaterial("MOSSY_COBBLESTONE"), new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
            collisionBoundingBoxes.put(Material.getMaterial("RAILS"), new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 0.125, (float) 1.0));
            collisionBoundingBoxes.put(Material.getMaterial("HAY_BLOCK"), new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
            collisionBoundingBoxes.put(Material.getMaterial("TORCH"), new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
            collisionBoundingBoxes.put(Material.getMaterial("CARPET"), new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 0.0625, (float) 1.0));
            collisionBoundingBoxes.put(Material.getMaterial("DIRT"), new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
            collisionBoundingBoxes.put(Material.getMaterial("EMERALD_ORE"), new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
            collisionBoundingBoxes.put(Material.getMaterial("REDSTONE_LAMP_ON"), new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
            collisionBoundingBoxes.put(Material.getMaterial("REDSTONE_LAMP_OFF"), new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
            collisionBoundingBoxes.put(Material.getMaterial("NETHER_WARTS"), new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 0.25, (float) 1.0));
            collisionBoundingBoxes.put(Material.getMaterial("SPONGE"), new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
            collisionBoundingBoxes.put(Material.getMaterial("WORKBENCH"), new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
            collisionBoundingBoxes.put(Material.getMaterial("SANDSTONE"), new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
            collisionBoundingBoxes.put(Material.getMaterial("LAPIS_BLOCK"), new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
            collisionBoundingBoxes.put(Material.getMaterial("NOTE_BLOCK"), new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
            collisionBoundingBoxes.put(Material.getMaterial("WOOL"), new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
            collisionBoundingBoxes.put(Material.getMaterial("COMMAND"), new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
            collisionBoundingBoxes.put(Material.getMaterial("ENDER_STONE"), new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
            collisionBoundingBoxes.put(Material.getMaterial("TRIPWIRE"), new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 0.15625, (float) 1.0));
            collisionBoundingBoxes.put(Material.getMaterial("SAPLING"), new BoundingBox((float) 0.09999999403953552, (float) 0.0, (float) 0.09999999403953552, (float) 0.8999999761581421, (float) 0.800000011920929, (float) 0.8999999761581421));
            collisionBoundingBoxes.put(Material.getMaterial("PACKED_ICE"), new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
            collisionBoundingBoxes.put(Material.getMaterial("LAPIS_ORE"), new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
            collisionBoundingBoxes.put(Material.getMaterial("SMOOTH_BRICK"), new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
            collisionBoundingBoxes.put(Material.getMaterial("RED_MUSHROOM"), new BoundingBox((float) 0.30000001192092896, (float) 0.0, (float) 0.30000001192092896, (float) 0.699999988079071, (float) 0.4000000059604645, (float) 0.699999988079071));
            collisionBoundingBoxes.put(Material.getMaterial("BROWN_MUSHROOM"), new BoundingBox((float) 0.30000001192092896, (float) 0.0, (float) 0.30000001192092896, (float) 0.699999988079071, (float) 0.4000000059604645, (float) 0.699999988079071));
            collisionBoundingBoxes.put(Material.getMaterial("DIAMOND_BLOCK"), new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
            collisionBoundingBoxes.put(Material.getMaterial("CROPS"), new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 0.25, (float) 1.0));
            collisionBoundingBoxes.put(Material.getMaterial("IRON_BLOCK"), new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
            collisionBoundingBoxes.put(Material.getMaterial("MELON"), new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
            collisionBoundingBoxes.put(Material.getMaterial("DIAMOND_ORE"), new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
            collisionBoundingBoxes.put(Material.getMaterial("LEVER"), new BoundingBox((float) 0.25, (float) 0.0, (float) 0.25, (float) 0.75, (float) 0.6000000238418579, (float) 0.75));
            collisionBoundingBoxes.put(Material.getMaterial("SUGAR_CANE"), new BoundingBox((float) 0.125, (float) 0.0, (float) 0.125, (float) 0.875, (float) 1.0, (float) 0.875));
            collisionBoundingBoxes.put(Material.getMaterial("COAL_ORE"), new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
            collisionBoundingBoxes.put(Material.getMaterial("WATER_LILY"), new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 0.015625, (float) 1.0));
            collisionBoundingBoxes.put(Material.getMaterial("QUARTZ_ORE"), new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
            collisionBoundingBoxes.put(Material.getMaterial("GLASS"), new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
            collisionBoundingBoxes.put(Material.getMaterial("TRIPWIRE_HOOK"), new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
            collisionBoundingBoxes.put(Material.getMaterial("VINE"), new BoundingBox((float) 0.0, (float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0, (float) 1.0));
            collisionBoundingBoxes.put(Material.getMaterial("WEB"), new BoundingBox(0, 0, 0, 1, 1, 1));
            collisionBoundingBoxes.put(Material.getMaterial("WATER"), new BoundingBox(0, 0, 0, 0.9f, 0.9f, 0.9f));
            collisionBoundingBoxes.put(Material.getMaterial("STATIONARY_WATER"), new BoundingBox(0, 0, 0, 0.9f, 0.9f, 0.9f));
            collisionBoundingBoxes.put(Material.getMaterial("STATIONARY_LAVA"), new BoundingBox(0, 0, 0, 0.9f, 0.9f, 0.9f));
        }
    }
}
