package me.rhys.anticheat.base.connection;

import lombok.Getter;
import lombok.Setter;
import me.rhys.anticheat.Anticheat;
import me.rhys.anticheat.tinyprotocol.api.ProtocolVersion;
import me.rhys.anticheat.tinyprotocol.packet.out.WrappedOutKeepAlivePacket;
import me.rhys.anticheat.tinyprotocol.packet.out.WrappedOutTransaction;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

@Getter
@Setter
public class TransactionHandler implements Runnable {
    public TransactionHandler() {
        this.start();
    }

    private long time = 999L;
    private short timeshort = 999;
    private BukkitTask bukkitTask;

    public void start() {
        if (this.bukkitTask == null) {
            this.bukkitTask = Bukkit.getScheduler().runTaskTimer(Anticheat.getInstance(),
                    this, 0L, 0L);
        }
    }

    @Override
    public void run() {
        timeshort = (short) time;

        if (this.time-- < 1) {
            this.time = 999L;
        }

        if (ProtocolVersion.getGameVersion().isAbove(ProtocolVersion.V1_9_4)) {
            //Fix for 1.9+ servers, because keepalives are broken for some reason...?
            this.processTransaction();
        } else {
            this.processTransaction();
            this.processKeepAlive();
        }
    }

    void processKeepAlive() {
        WrappedOutKeepAlivePacket wrappedOutKeepAlivePacket = new WrappedOutKeepAlivePacket(this.time);
        Anticheat.getInstance().getUserManager().getUserMap().forEach((uuid, user) -> {
            user.getConnectionMap2().put(this.time, System.currentTimeMillis());
            user.sendPacket(wrappedOutKeepAlivePacket.getObject());
        });
    }

    void processTransaction() {
        WrappedOutTransaction wrappedOutTransaction = new WrappedOutTransaction(0, this.timeshort,
                false);

        Anticheat.getInstance().getUserManager().getUserMap().forEach((uuid, user) -> {
            user.getConnectionMap().put(this.timeshort, System.currentTimeMillis());
            user.sendPacket(wrappedOutTransaction.getObject());
        });
    }
}
