package me.rhys.anticheat.util.math;

import me.rhys.anticheat.base.user.User;
import org.bukkit.Bukkit;

public class TrigHandler {
    private User player;
    private double buffer = 0;
    private boolean isVanillaMath = true;

    public TrigHandler(User player) {
        this.player = player;
    }

    public void setOffset(double offset) {
        // Offset too high, this is an outlier, ignore
        // We are checking in the range of 1e-3 to 5e-5, around what using the wrong trig system results in
        // Also ignore if the player didn't move
        if (offset > 1e-3 || offset == 0) {
            // Minor movements can sometimes end up between 1e-4 to 1e-5 due to < 0.03 lost precision
            buffer -= 0.25;
            return;
        }

        buffer += offset < 5e-5 ? -1 : 1;

        if (buffer > 10) {
            buffer = 0;
            isVanillaMath = !isVanillaMath;
        }

        Bukkit.broadcastMessage(""+isVanillaMath);

        // Try and identify the math system within 0.5 seconds (At best) of joining
        // Switch systems in 2 seconds (At best) if the player changes their math system
        buffer = clamp(buffer, -30, 10);
    }

    public float sin(float f) {
        return isVanillaMath ? VanillaMath.sin(f) : OptifineMath.sin(f);
    }

    public float cos(float f) {
        return isVanillaMath ? VanillaMath.cos(f) : OptifineMath.cos(f);
    }

    public static double clamp(double d, double d2, double d3) {
        if (d < d2) {
            return d2;
        }
        return Math.min(d, d3);
    }}