package me.rhys.anticheat.base.processor.impl.processors;

import lombok.Getter;
import lombok.Setter;
import me.rhys.anticheat.base.event.PacketEvent;
import me.rhys.anticheat.base.processor.api.Processor;
import me.rhys.anticheat.base.processor.api.ProcessorInformation;
import me.rhys.anticheat.base.user.User;
import me.rhys.anticheat.tinyprotocol.api.Packet;
import me.rhys.anticheat.tinyprotocol.packet.in.WrappedInKeepAlivePacket;
import me.rhys.anticheat.tinyprotocol.packet.in.WrappedInTransactionPacket;
import me.rhys.anticheat.util.evicting.EvictingMap;
import org.bukkit.Bukkit;

import java.util.Map;

@ProcessorInformation(name = "Connection")
@Getter @Setter
public class ConnectionProcessor extends Processor {

    private final Map<Long, Long> sentKeepAlives = new EvictingMap<>(100);
    private final Map<Long, Long> sentTransactions = new EvictingMap<>(100);
    private int ping, transPing, lastTransPing, dropTransTime;
    private int clientTick, flyingTick;
    private boolean isLagging = false;
    private int dropTick;

    @Override
    public void onPacket(PacketEvent event) {
        switch (event.getType()) {

            case Packet.Client.KEEP_ALIVE: {
                WrappedInKeepAlivePacket wrappedInKeepAlivePacket = new WrappedInKeepAlivePacket(
                        event.getPacket(), event.getUser().getPlayer());

                this.processK(user, wrappedInKeepAlivePacket.getTime());
                break;
            }

            case Packet.Client.TRANSACTION: {
                WrappedInTransactionPacket wrappedInTransactionPacket = new WrappedInTransactionPacket(
                        event.getPacket(), event.getUser().getPlayer());

                this.processT(user, wrappedInTransactionPacket.getAction());
                break;
            }
        }
    }

    void processT(User user, long time) {
        if (this.user.getConnectionMap().containsKey(time)) {
            this.lastTransPing = transPing;
            this.transPing = (int) (System.currentTimeMillis() - this.user.getConnectionMap()
                    .get(time));
            this.dropTransTime = Math.abs(transPing - lastTransPing);
            this.sentTransactions.put(time, System.currentTimeMillis());
            this.clientTick = (int) Math.ceil(this.transPing / 50.0);

            this.flyingTick = 0;
            this.dropTick++;

            user.getCheckManager().getCheckList().forEach(check -> check.onConnection(user));
        }
    }

    void processK(User user, long time) {
        if (this.user.getConnectionMap2().containsKey(time)) {
            this.ping = (int) (System.currentTimeMillis() - this.user.getConnectionMap2()
                    .get(time));
            this.sentKeepAlives.put(time, System.currentTimeMillis());
            this.clientTick = (int) Math.ceil(this.ping / 50.0);

            user.getCheckManager().getCheckList().forEach(check -> check.onConnection(user));
        }
    }
}
