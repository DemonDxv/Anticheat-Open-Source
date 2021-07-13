package me.rhys.anticheat.util;

import me.rhys.anticheat.base.user.User;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import java.util.LinkedList;
import java.util.List;

public class EntityUtil {

    public static boolean isOnBoat(User user) {
        double offset = user.getCurrentLocation().getY() % 0.015625;
        if ((user.getMovementProcessor().isOnGround() && offset > 0 && offset < 0.009)) {
            return getEntitiesWithinRadius(user.getPlayer().getLocation(), 2).stream()
                    .anyMatch(entity -> entity.getType() == EntityType.BOAT);
        }

        return false;
    }

    public static boolean isNearBoat(User user) {
            return getEntitiesWithinRadius(user.getPlayer().getLocation(), 2).stream()
                    .anyMatch(entity -> entity.getType() == EntityType.BOAT);
    }

    private static List<Entity> getEntitiesWithinRadius(Location location, double radius) {
        double x = location.getX();
        double z = location.getZ();

        World world = location.getWorld();
        List<Entity> entities = new LinkedList<>();

        for (int locX = (int) Math.floor((x - radius) / 16.0D);
             locX <= (int) Math.floor((x + radius) / 16.0D); locX++) {
            for (int locZ = (int) Math.floor((z - radius) / 16.0D);
                 locZ <= (int) Math.floor((z + radius) / 16.0D); locZ++) {
                if (!world.isChunkLoaded(locX, locZ)) continue;

                for (Entity entity : world.getChunkAt(locX, locZ).getEntities()) {
                    if (entity == null || entity.getLocation()
                            .distanceSquared(location) > radius * radius) continue;
                    entities.add(entity);
                }
            }
        }

        return entities;
    }
}
