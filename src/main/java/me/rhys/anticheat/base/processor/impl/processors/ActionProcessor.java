package me.rhys.anticheat.base.processor.impl.processors;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.rhys.anticheat.Anticheat;
import me.rhys.anticheat.base.event.PacketEvent;
import me.rhys.anticheat.base.processor.api.Processor;
import me.rhys.anticheat.base.processor.api.ProcessorInformation;
import me.rhys.anticheat.base.user.User;
import me.rhys.anticheat.tinyprotocol.api.Packet;
import me.rhys.anticheat.tinyprotocol.packet.in.WrappedInKeepAlivePacket;
import me.rhys.anticheat.tinyprotocol.packet.in.WrappedInTransactionPacket;
import me.rhys.anticheat.util.EventTimer;

import java.util.HashMap;
import java.util.Map;

@ProcessorInformation(name = "Action")
@Getter @Setter
public class ActionProcessor extends Processor {

    private final Map<Long, WrappedData> wrappedDataMap = new HashMap<>();
    private EventTimer velocityTimer, serverPositionTimer;

    @Override
    public void onPacket(PacketEvent event) {
        if (event.getType().equalsIgnoreCase(Packet.Server.KEEP_ALIVE)) {
            WrappedInTransactionPacket transactionPacket = new WrappedInTransactionPacket(event.getPacket(),
                    this.user.getPlayer());

            long time = transactionPacket.getAction();

            if (this.wrappedDataMap.containsKey(time)) {
                WrappedData wrappedData = this.wrappedDataMap.get(time);
                switch (wrappedData.getAction()) {
                    case VELOCITY: {
                //        this.velocityTimer.reset();
                        break;
                    }

                    case SERVER_POSITION: {
                        this.serverPositionTimer.reset();
                        break;
                    }

                }

                this.wrappedDataMap.remove(time);
            }
        }
    }

    @Override
    public void setupTimers(User user) {
        this.velocityTimer = new EventTimer(20, user);
        this.serverPositionTimer = new EventTimer(20, user);
    }

    public void add(Actions action) {
        long time = Anticheat.getInstance().getTransactionHandler().getTime() - 2L;
        this.wrappedDataMap.put(time, new WrappedData(System.currentTimeMillis(), action));
    }

    public enum Actions {
        VELOCITY,
        SERVER_POSITION,
        REACH_POSITION,
    }

    @Getter @AllArgsConstructor
    public static class WrappedData {
        private final long timestamp;
        private final Actions action;
    }
}
