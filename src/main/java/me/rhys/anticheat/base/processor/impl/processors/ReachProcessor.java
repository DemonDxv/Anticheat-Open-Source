package me.rhys.anticheat.base.processor.impl.processors;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.rhys.anticheat.base.event.PacketEvent;
import me.rhys.anticheat.base.processor.api.Processor;
import me.rhys.anticheat.base.processor.api.ProcessorInformation;
import me.rhys.anticheat.base.user.User;
import me.rhys.anticheat.tinyprotocol.api.Packet;
import me.rhys.anticheat.tinyprotocol.api.TinyProtocolHandler;
import me.rhys.anticheat.tinyprotocol.packet.in.WrappedInFlyingPacket;
import me.rhys.anticheat.tinyprotocol.packet.in.WrappedInTransactionPacket;
import me.rhys.anticheat.tinyprotocol.packet.out.*;
import me.rhys.anticheat.util.CustomLocation;
import me.rhys.anticheat.util.PastLocation;
import me.rhys.anticheat.util.PlayerLocation;
import me.rhys.anticheat.util.evicting.EvictingList;

import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ProcessorInformation(name = "Reach")
@Getter
@Setter
public class ReachProcessor extends Processor {

  //  private final Map<Integer, TrackedEntity> trackedEntityMap = new ConcurrentHashMap<>();
    private LinkedList<CustomLocation> customLocations = new EvictingList<>(20);

    private Deque<CustomLocation> locationsQueue = new LinkedList<>();

    private PlayerLocation currentLocation, lastLocation;
    private short reachID = 10000;
    public HashMap<Short, ReachData> reachTestMap = new HashMap();
    private ReachData reachData;


    @Override
    public void onPacket(PacketEvent event) {
        if (event.getType().equals(Packet.Server.ENTITY)
                || event.getType().equals(Packet.Server.REL_POSITION)
                || event.getType().equals(Packet.Server.REL_LOOK)
                || event.getType().equals(Packet.Server.REL_POSITION_LOOK)) {

            WrappedOutRelativePosition relativePosition =
                    new WrappedOutRelativePosition(event.getPacket(), user.getPlayer());

            double x = (double) relativePosition.getX() / 32D;
            double y = (double) relativePosition.getY() / 32D;
            double z = (double) relativePosition.getZ() / 32D;

            queueTransaction(new ReachData(user, System.currentTimeMillis(),
                    new PlayerLocation(x,y,z, System.currentTimeMillis())));
        }

        if (event.getType().equals(Packet.Server.ENTITY_TELEPORT)) {
            WrappedOutEntityTeleport wrappedOutEntityTeleport = new WrappedOutEntityTeleport(event.getPacket());
            double x = wrappedOutEntityTeleport.getX() / 32D;
            double y = wrappedOutEntityTeleport.getY() / 32D;
            double z = wrappedOutEntityTeleport.getZ() / 32D;

            queueTransaction(new ReachData(user, System.currentTimeMillis(),
                    new PlayerLocation(x,y,z, System.currentTimeMillis())));
        }

        if (event.getType().equals(Packet.Server.NAMED_ENTITY_SPAWN)) {
            WrappedOutNamedEntitySpawn entitySpawn =
                    new WrappedOutNamedEntitySpawn(event.getPacket(), user.getPlayer());

            double x = entitySpawn.x / 32D;
            double y = entitySpawn.y / 32D;
            double z = entitySpawn.z / 32D;

            queueTransaction(new ReachData(user, System.currentTimeMillis(),
                    new PlayerLocation(x,y,z, System.currentTimeMillis())));
        }

        if (event.getType().equalsIgnoreCase(Packet.Client.TRANSACTION)) {
            WrappedInTransactionPacket transactionPacket = new WrappedInTransactionPacket(event.getPacket(), user.getPlayer());

            double id = user.getReachProcessor().getReachID();
            double action = transactionPacket.getAction();

            if (id == action) {

                for (Map.Entry<Short, ReachData> doubleShortEntry : reachTestMap.entrySet()) {

                    reachData = doubleShortEntry.getValue();

                    if (user.getCombatProcessor().getLastAttackedEntity() != null) {
                        user.getCombatProcessor().getHitboxLocations().addLocation(user.getCombatProcessor()
                                .getLastAttackedEntity().getLocation());
                    }

                    reachTestMap.clear();
                }
            }
        }

    }


    private static void queueTransaction(ReachData reachData) {
        reachData.getUser().getReachProcessor().reachID++;

        short random = reachData.getUser().getReachProcessor().getReachID();

        if (reachData.getUser().getReachProcessor().getReachID() > 11000) {
            reachData.getUser().getReachProcessor().setReachID((short) 10000);
        }

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
      //  private final PlayerLocation to;
     //   private final PlayerLocation from;
    }
}