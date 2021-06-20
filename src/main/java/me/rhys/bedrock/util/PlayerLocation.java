package me.rhys.bedrock.util;

import com.sun.istack.internal.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.NumberConversions;
import org.bukkit.util.Vector;

@AllArgsConstructor @Setter @Getter
public class PlayerLocation {
    private final World world;
    private final double x, y, z;
    private final float yaw, pitch;
    private final boolean clientGround;

    public Vector toVector() {
        return new Vector(x, y, z);
    }

    public Location toBukkitLocation(World world) {
        return new Location(world, x, y, z, yaw, pitch);
    }

    public PlayerLocation clone() {
        return new PlayerLocation(this.world, this.x, this.y, this.z, this.yaw, this.pitch, this.clientGround);
    }

    public double distance(@NotNull PlayerLocation o) {
        return Math.sqrt(this.distanceSquared(o));
    }

    public double distanceSquared(@NotNull PlayerLocation o) {
        if (o.getWorld() != null && getWorld() != null && o.getWorld() == getWorld()) {
            return NumberConversions.square(this.getX() - o.getX())
                    + NumberConversions.square(this.getY() - o.getY())
                    + NumberConversions.square(this.getZ() - o.getZ());
        }
        return 0.0;
    }

    public double distanceSquaredXZ(@NotNull PlayerLocation o) {
        if (o.getWorld() != null && getWorld() != null && o.getWorld() == getWorld()) {
            return NumberConversions.square(this.getX() - o.getX())
                    + NumberConversions.square(this.getZ() - o.getZ());
        }
        return 0.0;
    }
}
