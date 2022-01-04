package me.rhys.anticheat.util.box;

import me.rhys.anticheat.base.user.User;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.List;

public interface BlockBox {

    boolean isChunkLoaded(Location loc);

    boolean isRiptiding(LivingEntity entity);

    float getMovementFactor(Player player);

    float getWidth(Entity entity);

    float getHeight(Entity entity);
}