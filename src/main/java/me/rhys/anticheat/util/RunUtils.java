package me.rhys.anticheat.util;

import me.rhys.anticheat.Anticheat;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

/*
   The whole purpose of this class is just to save disk space and make development more efficient
   with the use of lambdas and with less verbose conventions. This does not affect performance.
 */
public class RunUtils {

    public static BukkitTask taskTimer(Runnable runnable, Plugin plugin, long delay, long interval) {
        return Bukkit.getScheduler().runTaskTimer(plugin, runnable, delay, interval);
    }

    public static BukkitTask taskTimer(Runnable runnable, long delay, long interval) {
        return taskTimer(runnable, Anticheat.getInstance(), delay, interval);
    }

    public static BukkitTask taskTimerAsync(Runnable runnable, Plugin plugin, long delay, long interval) {
        return Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, runnable, delay, interval);
    }

    public static BukkitTask taskTimerAsync(Runnable runnable, long delay, long interval) {
        return taskTimerAsync(runnable, Anticheat.getInstance(), delay, interval);
    }

    public static BukkitTask task(Runnable runnable, Plugin plugin) {
        return Bukkit.getScheduler().runTask(plugin, runnable);
    }

    public static BukkitTask task(Runnable runnable) {
        return task(runnable, Anticheat.getInstance());
    }

    public static BukkitTask taskAsync(Runnable runnable, Plugin plugin) {
        return Bukkit.getScheduler().runTaskAsynchronously(plugin, runnable);
    }

    public static BukkitTask taskAsync(Runnable runnable) {
        return taskAsync(runnable, Anticheat.getInstance());
    }

    public static BukkitTask taskLater(Runnable runnable, Plugin plugin, long delay) {
        return Bukkit.getScheduler().runTaskLater(plugin, runnable, delay);
    }

    public static BukkitTask taskLater(Runnable runnable, long delay) {
        return taskLater(runnable, Anticheat.getInstance(), delay);
    }

    public static BukkitTask taskLaterAsync(Runnable runnable, Plugin plugin, long delay) {
        return Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, runnable, delay);
    }

    public static BukkitTask taskLaterAsync(Runnable runnable, long delay) {
        return taskLaterAsync(runnable, Anticheat.getInstance(), delay);
    }
}