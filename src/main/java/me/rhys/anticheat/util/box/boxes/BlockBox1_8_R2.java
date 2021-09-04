package me.rhys.anticheat.util.box.boxes;

import me.rhys.anticheat.base.user.User;
import me.rhys.anticheat.util.BlockUtil;
import me.rhys.anticheat.util.MathUtil;
import me.rhys.anticheat.util.box.BlockBox;
import me.rhys.anticheat.util.box.BoundingBox;

import net.minecraft.server.v1_8_R2.*;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R2.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BlockBox1_8_R2 implements BlockBox {

    @Override
    public List<BoundingBox> getCollidingBoxes(org.bukkit.World world, BoundingBox box, User user) {
        int minX = MathUtil.floor(box.minX);
        int maxX = MathUtil.floor(box.maxX + 1);
        int minY = MathUtil.floor(box.minY);
        int maxY = MathUtil.floor(box.maxY + 1);
        int minZ = MathUtil.floor(box.minZ);
        int maxZ = MathUtil.floor(box.maxZ + 1);

        List<Location> locs = new ArrayList<>();

        for (int x = minX; x < maxX; x++) {
            for (int z = minZ; z < maxZ; z++) {
                for (int y = minY - 1; y < maxY; y++) {
                    Location loc = new Location(world, x, y, z);
                    locs.add(loc);
                }
            }
        }

        List<BoundingBox> boxes = Collections.synchronizedList(new ArrayList<>());

        //boolean chunkLoaded = BlockUtil.isChunkLoaded(box.getMinimum().toLocation(world));

        if(user.isChunkLoaded()) {
            locs.parallelStream().forEach(loc -> {
                org.bukkit.block.Block block = BlockUtil.getBlock(loc);
                if (block != null && !block.getType().equals(Material.AIR)) {
                    if(BlockUtil.collisionBoundingBoxes.containsKey(block.getType())) {
                        BoundingBox box2 = BlockUtil.collisionBoundingBoxes.get(block.getType()).add(block.getLocation().toVector());
                        boxes.add(box2);
                    } else {
                        int x = block.getX(), y = block.getY(), z = block.getZ();

                        BlockPosition pos = new BlockPosition(x, y, z);
                        World nmsWorld = ((CraftWorld) world).getHandle();
                        IBlockData nmsiBlockData = ((CraftWorld) world).getHandle().getType(pos);
                        Block nmsBlock = nmsiBlockData.getBlock();
                        List<AxisAlignedBB> preBoxes = new ArrayList<>();

                        nmsBlock.updateShape(nmsWorld, pos);
                        nmsBlock.a(nmsWorld,
                                pos,
                                nmsiBlockData,
                                (AxisAlignedBB) box.toAxisAlignedBB(),
                                preBoxes,
                                null);

                        if (preBoxes.size() > 0) {
                            for (AxisAlignedBB aabb : preBoxes) {
                                boxes.add(new BoundingBox(
                                        (float)aabb.a,
                                        (float)aabb.b,
                                        (float)aabb.c,
                                        (float)aabb.d,
                                        (float)aabb.e,
                                        (float)aabb.f));
                            }
                        } else {
                            boxes.add(new BoundingBox(
                                    (float)nmsBlock.B(),
                                    (float)nmsBlock.D(),
                                    (float)nmsBlock.F(),
                                    (float)nmsBlock.C(),
                                    (float)nmsBlock.E(),
                                    (float)nmsBlock.G()).add(x, y, z, x, y, z));
                        }
                    }
                }
            });
        }

        return boxes;
    }

    @Override
    public List<BoundingBox> getSpecificBox(Location loc, User user) {
        return Collections.synchronizedList(getCollidingBoxes(loc.getWorld(), new BoundingBox(loc.toVector(), loc.toVector()), user));
    }

    @Override
    public boolean isChunkLoaded(Location loc) {

        return BlockUtil.isChunkLoaded(loc);

       // net.minecraft.server.v1_8_R2.World world = ((CraftWorld) loc.getWorld()).getHandle();

     //   return !world.isClientSide && world.isLoaded(new BlockPosition(loc.getBlockX(), 0, loc.getBlockZ())) && world.getChunkAtWorldCoords(new BlockPosition(loc.getBlockX(), 0, loc.getBlockZ())).o();
    }

    @Override
    public boolean isRiptiding(LivingEntity entity) {
        return false;
    }

    @Override
    public boolean isUsingItem(Player player) {
        EntityHuman entity = ((org.bukkit.craftbukkit.v1_8_R2.entity.CraftHumanEntity) player).getHandle();
        return entity.bZ() != null && entity.bZ().getItem().e(entity.bZ()) != EnumAnimation.NONE;
    }

/*    @Override
    public float getMovementFactor(Player player) {
        return (float) ((CraftPlayer) player).getHandle().getAttributeInstance(GenericAttributes.d).getValue();
    } */

    @Override
    public float getMovementFactor(Player player) {
        return (float) ((CraftPlayer) player).getHandle().getWorld().getType(
                new BlockPosition(player.getLocation().getX(), player.getLocation().getY() - 1,
                        player.getLocation().getZ())).getBlock().frictionFactor;
    }

    @Override
    public float getWidth(Entity entity) {
        return 0;
    }

    @Override
    public float getHeight(Entity entity) {
        return 0;
    }

    @Override
    public int getTrackerId(Player player) {
        EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
        EntityTrackerEntry entry = ((WorldServer) entityPlayer.getWorld()).tracker.trackedEntities.get(entityPlayer.getId());
        return entry.tracker.getId();
    }

    @Override
    public float getAiSpeed(Player player) {
        return ((CraftPlayer) player).getHandle().bI();
    }
}
