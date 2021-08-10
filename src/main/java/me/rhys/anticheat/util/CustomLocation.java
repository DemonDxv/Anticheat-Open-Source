package me.rhys.anticheat.util;

import com.sun.istack.internal.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.Location;
import org.bukkit.util.NumberConversions;
import org.bukkit.util.Vector;

@Data
@AllArgsConstructor
public class CustomLocation {
    private long timestamp;

    private double x, y, z;
    private double minX, minZ, maxX, maxZ;

    private float yaw, pitch;

    private boolean onGround;

    public CustomLocation(CustomLocation loc) {
        this.x = loc.getX();
        this.y = loc.getY();
        this.z = loc.getZ();
        this.yaw = loc.getYaw();
        this.pitch = loc.getPitch();

        minX = x - .3F;
        minZ = z - .3F;

        maxX = x + .3F;
        maxZ = x + .3F;

        this.timestamp = System.currentTimeMillis();
    }

    public CustomLocation(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;

        minX = x - .3F;
        minZ = z - .3F;

        maxX = x + .3F;
        maxZ = x + .3F;

        this.timestamp = System.currentTimeMillis();
    }

    public CustomLocation(Location loc) {
        this.x = loc.getX();
        this.y = loc.getY();
        this.z = loc.getZ();
        this.yaw = loc.getYaw();
        this.pitch = loc.getPitch();

        minX = x - .3F;
        minZ = z - .3F;

        maxX = x + .3F;
        maxZ = x + .3F;

        this.timestamp = System.currentTimeMillis();
    }



    public CustomLocation(double x, double y, double z, float yaw, float pitch, boolean onGround) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.onGround = onGround;

        minX = x - .3F;
        minZ = z - .3F;

        maxX = x + .3F;
        maxZ = x + .3F;

        this.timestamp = System.currentTimeMillis();
    }

    public CustomLocation(double x, double y, double z, float yaw, float pitch, boolean onGround, long timestamp) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.onGround = onGround;

        minX = x - .3F;
        minZ = z - .3F;

        maxX = x + .3F;
        maxZ = x + .3F;

        this.timestamp = timestamp;
    }


    public CustomLocation clone() {
        return new CustomLocation(x, y, z, yaw, pitch, onGround, timestamp);
    }

    public CustomLocation add(double x, double y, double z) {
        return new CustomLocation(this.x + x, this.y + y, this.z + z, this.yaw, this.pitch, this.onGround);
    }

    public double getDistanceSquared(CustomLocation location, CustomLocation lastLocation) {
        double dx = Math.min(Math.abs(location.x - minX), Math.abs(lastLocation.x - maxX));
        double dz = Math.min(Math.abs(location.z - minZ), Math.abs(lastLocation.z - maxZ));

        return Math.sqrt(Math.pow(dx, 2.0) + Math.pow(dz, 2.0));
    }

    public Vector toVector() {
        return new Vector(x, y, z);
    }

}