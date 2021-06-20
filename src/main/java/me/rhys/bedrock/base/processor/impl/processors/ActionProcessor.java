package me.rhys.bedrock.base.processor.impl.processors;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.rhys.bedrock.Anticheat;
import me.rhys.bedrock.base.event.PacketEvent;
import me.rhys.bedrock.base.processor.api.Processor;
import me.rhys.bedrock.base.processor.api.ProcessorInformation;
import me.rhys.bedrock.base.user.User;
import me.rhys.bedrock.tinyprotocol.api.Packet;
import me.rhys.bedrock.tinyprotocol.packet.in.WrappedInKeepAlivePacket;
import me.rhys.bedrock.util.EventTimer;

import java.util.HashMap;
import java.util.Map;

@ProcessorInformation(name = "Action")
@Getter @Setter
public class ActionProcessor extends Processor {

    private final Map<Long, WrappedData> wrappedDataMap = new HashMap<>();
    private EventTimer velocityTimer, serverPositionTimer;

    @Override
    public void onPacket(PacketEvent event) {
        if (event.getType().equalsIgnoreCase(Packet.Client.KEEP_ALIVE)) {
            WrappedInKeepAlivePacket wrappedInKeepAlivePacket = new WrappedInKeepAlivePacket(event.getPacket(),
                    this.user.getPlayer());
            long time = wrappedInKeepAlivePacket.getTime();

            if (this.wrappedDataMap.containsKey(time)) {
                WrappedData wrappedData = this.wrappedDataMap.get(time);
                switch (wrappedData.getAction()) {
                    case VELOCITY: {
                        this.velocityTimer.reset();
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
        long time = Anticheat.getInstance().getKeepaliveHandler().getTime() - 2L;
        this.wrappedDataMap.put(time, new WrappedData(System.currentTimeMillis(), action));
    }

    public enum Actions {
        VELOCITY,
        SERVER_POSITION
    }

    @Getter @AllArgsConstructor
    public static class WrappedData {
        private final long timestamp;
        private final Actions action;
    }
}
