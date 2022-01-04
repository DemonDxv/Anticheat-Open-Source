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
import me.rhys.anticheat.tinyprotocol.api.TinyProtocolHandler;
import me.rhys.anticheat.tinyprotocol.packet.in.WrappedInKeepAlivePacket;
import me.rhys.anticheat.tinyprotocol.packet.in.WrappedInTransactionPacket;
import me.rhys.anticheat.tinyprotocol.packet.out.WrappedOutKeepAlivePacket;
import me.rhys.anticheat.util.EventTimer;

import java.util.HashMap;
import java.util.Map;

@ProcessorInformation(name = "Action")
@Getter @Setter
public class ActionProcessor extends Processor {

    private final Map<Long, WrappedData> wrappedDataMap = new HashMap<>();
    private EventTimer velocityTimer, serverPositionTimer, respawnTimer;

    @Override
    public void onPacket(PacketEvent event) {
        if (event.getType().equalsIgnoreCase(Packet.Client.KEEP_ALIVE)) {
            WrappedInKeepAlivePacket keepAlivePacket = new WrappedInKeepAlivePacket(event.getPacket(),
                    this.user.getPlayer());

            long time = keepAlivePacket.getTime();

            if (this.wrappedDataMap.containsKey(time)) {
                WrappedData wrappedData = this.wrappedDataMap.get(time);
                switch (wrappedData.getAction()) {
                    case VELOCITY: {
                        this.velocityTimer.reset();

                        user.getCombatProcessor().setVelocityTicks(0);

                        user.getCombatProcessor().setVelocityH(
                                Math.hypot(user.getCombatProcessor().getVelocity().getX(),
                                        user.getCombatProcessor().getVelocity().getZ()));

                        user.getCombatProcessor().setVelocityV(user.getCombatProcessor().getVelocity().getY());
                        break;
                    }

                    case SERVER_POSITION: {
                        this.serverPositionTimer.reset();
                        break;
                    }

                    case RESPAWN: {
                        this.respawnTimer.reset();
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
        this.respawnTimer = new EventTimer(20, user);
    }

    public void add(Actions action) {
        long time = Anticheat.getInstance().getTransactionHandler().getTime() - 1L;
        Anticheat.getInstance().getKeepAliveHandler().setTime(time);
        this.wrappedDataMap.put(time, new WrappedData(System.currentTimeMillis(), action));

        TinyProtocolHandler.sendPacket(user.getPlayer(), new WrappedOutKeepAlivePacket(time).getObject());
    }

    public enum Actions {
        VELOCITY,
        SERVER_POSITION,
        RESPAWN,
        REACH_POSITION,
    }

    @Getter @AllArgsConstructor
    public static class WrappedData {
        private final long timestamp;
        private final Actions action;
    }
}
