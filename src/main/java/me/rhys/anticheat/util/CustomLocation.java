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

    private float yaw, pitch;

    private boolean onGround;

    public CustomLocation(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;

        this.timestamp = System.currentTimeMillis();
    }

    public CustomLocation(Location loc) {
        this.x = loc.getX();
        this.y = loc.getY();
        this.z = loc.getZ();
        this.yaw = loc.getYaw();
        this.pitch = loc.getPitch();

        this.timestamp = System.currentTimeMillis();
    }



    public CustomLocation(double x, double y, double z, float yaw, float pitch, boolean onGround) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.onGround = onGround;

        this.timestamp = System.currentTimeMillis();
    }

    public CustomLocation(double x, double y, double z, float yaw, float pitch, boolean onGround, long timestamp) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.onGround = onGround;

        this.timestamp = timestamp;
    }


    public CustomLocation clone() {
        return new CustomLocation(x, y, z, yaw, pitch, onGround, timestamp);
    }

    public CustomLocation add(double x, double y, double z) {
        return new CustomLocation(this.x + x, this.y + y, this.z + z, this.yaw, this.pitch, this.onGround);
    }

    public double distance(@NotNull CustomLocation o) {
        return Math.sqrt(this.distanceSquared(o));
    }

    public double distanceSquared(@NotNull CustomLocation o) {
            return NumberConversions.square(this.getX() - o.getX())
                    + NumberConversions.square(this.getY() - o.getY())
                    + NumberConversions.square(this.getZ() - o.getZ());
    }

    public Vector toVector() {
        return new Vector(x, y, z);
    }

}