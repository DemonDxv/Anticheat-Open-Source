package me.rhys.bedrock.base.connection;

import lombok.Getter;
import me.rhys.bedrock.Anticheat;
import me.rhys.bedrock.tinyprotocol.api.ProtocolVersion;
import me.rhys.bedrock.tinyprotocol.packet.out.WrappedOutKeepAlivePacket;
import me.rhys.bedrock.tinyprotocol.packet.out.WrappedOutTransaction;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

@Getter
public class KeepaliveHandler implements Runnable {
    public KeepaliveHandler() {
        this.start();
    }

    private long time = 999L;
    private BukkitTask bukkitTask;

    public void start() {
        if (this.bukkitTask == null) {
            this.bukkitTask = Bukkit.getScheduler().runTaskTimer(Anticheat.getInstance(),
                    this, 0L, 0L);
        }
    }

    @Override
    public void run() {
        if (this.time-- < 1) {
            this.time = 999L;
        }

        if (ProtocolVersion.getGameVersion().isAbove(ProtocolVersion.V1_9_4)) {
            //Fix for 1.9+ servers, because keepalives are broken for some reason...?
            this.processTransaction();
        } else {
            this.processTransaction();
        }
    }

    void processKeepAlive() {
        WrappedOutKeepAlivePacket wrappedOutKeepAlivePacket = new WrappedOutKeepAlivePacket(this.time);
        Anticheat.getInstance().getUserManager().getUserMap().forEach((uuid, user) -> {
            user.getConnectionMap().put(this.time, System.currentTimeMillis());
            user.sendPacket(wrappedOutKeepAlivePacket.getObject());
        });
    }

    void processTransaction() {
        WrappedOutTransaction wrappedOutTransaction = new WrappedOutTransaction(0, (short) this.time,
                false);

        Anticheat.getInstance().getUserManager().getUserMap().forEach((uuid, user) -> {
            user.getConnectionMap().put(this.time, System.currentTimeMillis());
            user.sendPacket(wrappedOutTransaction.getObject());
        });
    }
}
