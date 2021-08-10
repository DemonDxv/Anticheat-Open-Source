package me.rhys.anticheat.util;

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
    private World world;
    private double x, y, z;
    private double minX, maxX;
    private double minZ, maxZ;
    private float yaw, pitch;
    private boolean clientGround;
    private long timeStamp;

    public PlayerLocation(Location loc) {
        this.x = loc.getX();
        this.y = loc.getY();
        this.z = loc.getZ();
        this.yaw = loc.getYaw();
        this.pitch = loc.getPitch();

        this.timeStamp = System.currentTimeMillis();
    }


    public PlayerLocation(double x, double y, double z, long timeStamp) {
        this.x = x;

        minX = x - 0.4;
        maxX = x + 0.4;

        this.y = y;

        this.z = z;
        minZ = z - 0.4;
        maxZ = z + 0.4;


        this.timeStamp = timeStamp;

    }

    public PlayerLocation(World world, double x, double y, double z, float yaw, float pitch, boolean clientGround, long timeStamp) {
        this.x = x;
        minX = x - 0.4;
        maxX = x + 0.4;

        this.y = y;

        this.z = z;
        minZ = z - 0.4;
        maxZ = z + 0.4;

        this.yaw = yaw;
        this.pitch = pitch;

        this.clientGround = clientGround;

        this.timeStamp = System.currentTimeMillis();

        this.world = world;

    }

    public void add(double x, double y, double z) {
        this.x += x;
        this.y += y;
        this.z += z;
    }

    public Vector toVector() {
        return new Vector(x, y, z);
    }

    public Location toBukkitLocation(World world) {
        return new Location(world, x, y, z, yaw, pitch);
    }

    public PlayerLocation clone() {
        return new PlayerLocation(this.world, this.x, this.y, this.z, this.yaw, this.pitch, this.clientGround, this.timeStamp);
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

    public double getDistanceSquared(PlayerLocation location, PlayerLocation lastLocation) {
        double dx = Math.min(Math.abs(location.x - minX), Math.abs(lastLocation.x - maxX));
        double dz = Math.min(Math.abs(location.z - minZ), Math.abs(lastLocation.z - maxZ));

        return Math.sqrt(Math.pow(dx, 2.0) + Math.pow(dz, 2.0));
    }

    public double distanceSquaredXZ(@NotNull PlayerLocation o) {
        if (o.getWorld() != null && getWorld() != null && o.getWorld() == getWorld()) {
            return NumberConversions.square(this.getX() - o.getX())
                    + NumberConversions.square(this.getZ() - o.getZ());
        }
        return 0.0;
    }
}
