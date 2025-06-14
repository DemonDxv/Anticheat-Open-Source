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
import me.rhys.anticheat.tinyprotocol.packet.in.WrappedInTransactionPacket;
import me.rhys.anticheat.tinyprotocol.packet.out.*;
import me.rhys.anticheat.util.PlayerLocation;

import java.util.HashMap;

@ProcessorInformation(name = "Reach")
@Getter
@Setter
public class ReachProcessor extends Processor {

    public HashMap<Short, ReachData> reachTestMap = new HashMap();

    private ReachData reachData;

    @Override
    public void onPacket(PacketEvent event) {
        if (event.getType().equals(Packet.Server.ENTITY)
                || event.getType().equals(Packet.Server.REL_LOOK)
                || event.getType().equals(Packet.Server.REL_POSITION_LOOK)
                || event.getType().equals(Packet.Server.REL_POSITION)) {

            WrappedOutRelativePosition relativePosition =
                    new WrappedOutRelativePosition(event.getPacket(), user.getPlayer());


            double x = relativePosition.getX() / 32D;
            double y = relativePosition.getY() / 32D;
            double z = relativePosition.getZ() / 32D;

            float f = relativePosition.isLook() ? (float) (relativePosition.getYaw() * 360) / 256.0F :
                    relativePosition.getPlayer().getLocation().getYaw();

            float f1 = relativePosition.isLook() ? (float) (relativePosition.getPitch() * 360) / 256.0F :
                    relativePosition.getPlayer().getLocation().getPitch();

            queueTransaction(new ReachData(user, System.currentTimeMillis(),
                    new PlayerLocation(x,y,z, f, f1, System.currentTimeMillis())));

        }

        if (event.getType().equals(Packet.Server.ENTITY_TELEPORT)) {
            WrappedOutEntityTeleport wrappedOutEntityTeleport = new WrappedOutEntityTeleport(event.getPacket());
            double x = wrappedOutEntityTeleport.getX() / 32.0D;
            double y = wrappedOutEntityTeleport.getY() / 32.0D;
            double z = wrappedOutEntityTeleport.getZ() / 32.0D;

            float f = (float) (wrappedOutEntityTeleport.getYaw() * 360) / 256.0F;
            float f1 = (float) (wrappedOutEntityTeleport.getPitch() * 360) / 256.0F;

            queueTransaction(new ReachData(user, System.currentTimeMillis(),
                    new PlayerLocation(x,y,z, f, f1, System.currentTimeMillis())));

        }


        if (event.getType().equals(Packet.Server.NAMED_ENTITY_SPAWN)) {
            WrappedOutNamedEntitySpawn entitySpawn =
                    new WrappedOutNamedEntitySpawn(event.getPacket(), user.getPlayer());

            double x = entitySpawn.x / 32D;
            double y = entitySpawn.y / 32D;
            double z = entitySpawn.z / 32D;

            float f = (float) (entitySpawn.yaw * 360) / 256.0F;
            float f1 = (float) (entitySpawn.pitch * 360) / 256.0F;

            queueTransaction(new ReachData(user, System.currentTimeMillis(),
                    new PlayerLocation(x,y,z, f, f1, System.currentTimeMillis())));

        }

        if (event.getType().equalsIgnoreCase(Packet.Client.TRANSACTION)) {
            WrappedInTransactionPacket transactionPacket = new WrappedInTransactionPacket(event.getPacket(), user.getPlayer());

            short action = transactionPacket.getAction();

            if (reachTestMap.containsKey(action)) {

                reachData = user.getReachProcessor().reachTestMap.get(action);

                user.getReachProcessor().reachTestMap.remove(action);
            }
        }
    }


    private static void queueTransaction(ReachData reachData) {
        short random = (short) (Anticheat.getInstance().getTransactionHandler().getTime() - 3);

        reachData.user.getReachProcessor().reachTestMap.put(random, reachData);

        TinyProtocolHandler.sendPacket(reachData.getUser().getPlayer(),
                new WrappedOutTransaction(0, random, false).getObject());

    }

    @Getter
    @AllArgsConstructor
    public static class ReachData {
        private final User user;
        private final long time;
        private final PlayerLocation customLocation;
    }
}