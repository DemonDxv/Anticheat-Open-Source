package me.rhys.bedrock.base.processor.impl.processors;

import lombok.Getter;
import lombok.Setter;
import me.rhys.bedrock.base.event.PacketEvent;
import me.rhys.bedrock.base.processor.api.Processor;
import me.rhys.bedrock.base.processor.api.ProcessorInformation;
import me.rhys.bedrock.base.user.User;
import me.rhys.bedrock.tinyprotocol.api.Packet;
import me.rhys.bedrock.tinyprotocol.packet.in.WrappedInKeepAlivePacket;
import me.rhys.bedrock.tinyprotocol.packet.in.WrappedInTransactionPacket;
import me.rhys.bedrock.util.evicting.EvictingMap;

import java.util.Map;

@ProcessorInformation(name = "Connection")
@Getter @Setter
public class ConnectionProcessor extends Processor {

    private final Map<Long, Long> sentKeepAlives = new EvictingMap<>(100);
    private final Map<Long, Long> sentTransactions = new EvictingMap<>(100);
    private int ping;
    private int clientTick;

    @Override
    public void onPacket(PacketEvent event) {
        switch (event.getType()) {
            case Packet.Client.KEEP_ALIVE: {
                WrappedInKeepAlivePacket wrappedInKeepAlivePacket = new WrappedInKeepAlivePacket(event.getPacket(),
                        this.user.getPlayer());
                this.process(user, wrappedInKeepAlivePacket.getTime());
                break;
            }

            case Packet.Client.TRANSACTION: {
                WrappedInTransactionPacket wrappedInTransactionPacket = new WrappedInTransactionPacket(
                        event.getPacket(), event.getUser().getPlayer());

                this.process(user, wrappedInTransactionPacket.getAction());
                break;
            }
        }
    }

    void process(User user, long time) {
        if (this.user.getConnectionMap().containsKey(time)) {
            this.ping = (int) (System.currentTimeMillis() - this.user.getConnectionMap()
                    .get(time));
            this.sentKeepAlives.put(time, System.currentTimeMillis());
            this.clientTick = (int) Math.ceil(this.ping / 50.0);
            user.getCheckManager().getCheckList().forEach(check -> check.onConnection(user));
        }
    }
}
