package me.rhys.anticheat.util.box.boxes;


import me.rhys.anticheat.base.user.User;
import me.rhys.anticheat.util.BlockUtil;
import me.rhys.anticheat.util.MathUtil;
import me.rhys.anticheat.util.box.BlockBox;
import me.rhys.anticheat.util.box.BoundingBox;
import net.minecraft.server.v1_8_R1.*;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R1.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BlockBox1_8_R1 implements BlockBox {



    @Override
    public boolean isChunkLoaded(Location loc) {

        return BlockUtil.isChunkLoaded(loc);

        //net.minecraft.server.v1_8_R1.World world = ((CraftWorld) loc.getWorld()).getHandle();

        //return !world.isStatic && world.isLoaded(new BlockPosition(loc.getBlockX(), 0, loc.getBlockZ())) && world.getChunkAtWorldCoords(new BlockPosition(loc.getBlockX(), 0, loc.getBlockZ())).o();
    }

    @Override
    public boolean isRiptiding(LivingEntity entity) {
        return false;
    }


   /* @Override
    public float getMovementFactor(Player player) {
        return (float) ((CraftPlayer) player).getHandle().getAttributeInstance(GenericAttributes.d).getValue();
    }*/

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

}
