package me.rhys.anticheat.base.connection;

import lombok.Getter;
import lombok.Setter;
import me.rhys.anticheat.Anticheat;
import me.rhys.anticheat.tinyprotocol.api.ProtocolVersion;
import me.rhys.anticheat.tinyprotocol.packet.out.WrappedOutKeepAlivePacket;
import me.rhys.anticheat.tinyprotocol.packet.out.WrappedOutTransaction;
import me.rhys.anticheat.util.RunUtils;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

@Getter
@Setter
public class TransactionHandler implements Runnable {
    public TransactionHandler() {
        this.start();
    }

    private short time = 32000;
    private BukkitTask bukkitTask;

    public void start() {
        if (this.bukkitTask == null) {
            this.bukkitTask = RunUtils.taskTimerAsync(
                    this, 0L, 0L);
        }
    }

    @Override
    public void run() {

        if (this.time-- < 1) {
            this.time = 32000;
        }

        if (ProtocolVersion.getGameVersion().isAbove(ProtocolVersion.V1_9_4)) {
            //Fix for 1.9+ servers, because keepalives are broken for some reason...?
            this.processTransaction();
        } else {
            this.processTransaction();
        }
    }

   /* void processKeepAlive() {
        WrappedOutKeepAlivePacket wrappedOutKeepAlivePacket = new WrappedOutKeepAlivePacket(this.time);
        Anticheat.getInstance().getUserManager().getUserMap().forEach((uuid, user) -> {
            user.getConnectionMap2().put(this.time, System.currentTimeMillis());
            user.sendPacket(wrappedOutKeepAlivePacket.getObject());
        });
    } */

    void processTransaction() {
        WrappedOutTransaction wrappedOutTransaction = new WrappedOutTransaction(0, this.time,
                false);

        Anticheat.getInstance().getUserManager().getUserMap().forEach((uuid, user) -> {
            user.getConnectionMap().put(this.time, System.currentTimeMillis());
            user.sendPacket(wrappedOutTransaction.getObject());
        });
    }
}
