package me.rhys.anticheat.base.connection;

import lombok.Getter;
import lombok.Setter;
import me.rhys.anticheat.Anticheat;
import me.rhys.anticheat.tinyprotocol.packet.out.WrappedOutKeepAlivePacket;
import me.rhys.anticheat.util.RunUtils;
import org.bukkit.scheduler.BukkitTask;

@Getter
@Setter
public class KeepAliveHandler implements Runnable {
    public KeepAliveHandler() {
        this.start();
    }

    private long time = -999;
    private BukkitTask bukkitTask;

    public void start() {
        if (this.bukkitTask == null) {
            this.bukkitTask = RunUtils.taskTimerAsync(
                    this, 0L, 25L);
        }
    }

    @Override
    public void run() {

        if (this.time++ > -2) {
            this.time = -999;
        }

        this.processKeepAlive();
    }

    void processKeepAlive() {
        WrappedOutKeepAlivePacket wrappedOutKeepAlivePacket = new WrappedOutKeepAlivePacket(this.time);
        Anticheat.getInstance().getUserManager().getUserMap().forEach((uuid, user) -> {
            user.getConnectionMap2().put(this.time, System.currentTimeMillis());
            user.sendPacket(wrappedOutKeepAlivePacket.getObject());
        });
    }
}
