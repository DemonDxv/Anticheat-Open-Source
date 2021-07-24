package me.rhys.anticheat.base.processor.impl.processors;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.rhys.anticheat.Anticheat;
import me.rhys.anticheat.base.event.PacketEvent;
import me.rhys.anticheat.base.processor.api.Processor;
import me.rhys.anticheat.base.processor.api.ProcessorInformation;
import me.rhys.anticheat.base.user.User;
import me.rhys.anticheat.tinyprotocol.api.NMSObject;
import me.rhys.anticheat.tinyprotocol.api.Packet;
import me.rhys.anticheat.tinyprotocol.api.TinyProtocolHandler;
import me.rhys.anticheat.tinyprotocol.packet.in.WrappedInTransactionPacket;
import me.rhys.anticheat.tinyprotocol.packet.in.WrappedInUseEntityPacket;
import me.rhys.anticheat.tinyprotocol.packet.out.*;
import me.rhys.anticheat.util.EventTimer;
import me.rhys.anticheat.util.PlayerLocation;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;

@ProcessorInformation(name = "Combat")
@Getter
@Setter
public class CombatProcessor extends Processor {

    private EventTimer preVelocityTimer, useEntityTimer;

    private double velocityH, velocityV;

    private Vector velocity = new Vector();

    private int velocityTicks, velocityNoTransTicks;

    private short velocityID = 9000, relMoveID = -9, reachID = 9001;

    private Player lastAttackedEntity, lastLastAttackedEntity;
    private PlayerLocation relCachedLocation, lastLastRelMove, lastRelMove;

    public HashMap<PlayerLocation, Short> lastRelLocation = new HashMap();


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

            World world = user.getPlayer().getWorld();

        //    if (!this.isValidEntity(relativePosition.getId())) return;

            queueTransaction(new ReachData(user, System.currentTimeMillis(),
                    new PlayerLocation(world, (double) relativePosition.getX() / 32D,
                            (double) relativePosition.getY() / 32D, (double) relativePosition.getZ() / 32D,
                            0, 0, false, System.currentTimeMillis()),
                    user.getCurrentLocation().clone(),
                    user.getLastLocation().clone()));

            relCachedLocation = new PlayerLocation(world, (double) relativePosition.getX() / 32D,
                    (double) relativePosition.getY() / 32D, (double) relativePosition.getZ() / 32D,
                    0, 0, false, System.currentTimeMillis());
        }

        if (event.getType().equals(Packet.Server.ENTITY_TELEPORT)) {
            WrappedOutEntityTeleport teleport =
                    new WrappedOutEntityTeleport(event.getPacket());

            World world = user.getPlayer().getWorld();

        //    if (!this.isValidEntity(teleport.getId())) return;

            queueTransaction(new ReachData(user, System.currentTimeMillis(),
                    new PlayerLocation(world, teleport.getX() / 32D,
                            teleport.getY() / 32D, teleport.getZ() / 32D,
                            0, 0, false, System.currentTimeMillis()),
                    user.getCurrentLocation().clone(),
                    user.getLastLocation().clone()));

            relCachedLocation = new PlayerLocation(world, (double) teleport.getX() / 32D,
                    (double) teleport.getY() / 32D, (double) teleport.getZ() / 32D,
                    0, 0, false, System.currentTimeMillis());
        }


        if (event.getType().equals(Packet.Server.NAMED_ENTITY_SPAWN)) {
            WrappedOutNamedEntitySpawn entitySpawn =
                    new WrappedOutNamedEntitySpawn(event.getPacket(), user.getPlayer());

            World world = user.getPlayer().getWorld();

       //     if (!this.isValidEntity(entitySpawn.entityId)) return;

            queueTransaction(new ReachData(user, System.currentTimeMillis(),
                    new PlayerLocation(world, entitySpawn.x / 32D,
                            entitySpawn.y / 32D, entitySpawn.z / 32D,
                            0, 0, false, System.currentTimeMillis()),
                    user.getCurrentLocation().clone(),
                    user.getLastLocation().clone()));

            relCachedLocation = new PlayerLocation(world, (double) entitySpawn.x / 32D,
                    (double) entitySpawn.y / 32D, (double) entitySpawn.z / 32D,
                    0, 0, false, System.currentTimeMillis());
        }



        switch (event.getType()) {

            case Packet.Client.USE_ENTITY: {
                WrappedInUseEntityPacket useEntityPacket = new WrappedInUseEntityPacket(event.getPacket(), user.getPlayer());

                if (useEntityPacket.getAction() == WrappedInUseEntityPacket.EnumEntityUseAction.ATTACK) {
                    lastLastAttackedEntity = lastAttackedEntity;
                    lastAttackedEntity = (Player) useEntityPacket.getEntity();
                    useEntityTimer.reset();

                    velocity.setX(velocity.getX() * 0.6F);
                    velocity.setZ(velocity.getZ() * 0.6F);
                }
            }

            case Packet.Client.FLYING:
            case Packet.Client.LOOK:
            case Packet.Client.POSITION_LOOK:
            case Packet.Client.POSITION: {
                velocityTicks++;
                velocityNoTransTicks++;

                break;
            }


            case Packet.Server.ENTITY_VELOCITY: {
                WrappedOutVelocityPacket wrappedOutVelocityPacket = new WrappedOutVelocityPacket(event.getPacket(),
                        event.getUser().getPlayer());

                if (wrappedOutVelocityPacket.getId() == event.getUser().getPlayer().getEntityId()) {

                    this.preVelocityTimer.reset();

             //       user.getActionProcessor().add(ActionProcessor.Actions.VELOCITY);

                    velocity = new Vector(wrappedOutVelocityPacket.getX(), wrappedOutVelocityPacket.getY(),
                            wrappedOutVelocityPacket.getZ());

            ///        velocityH = Math.hypot(velocity.getX(), velocity.getZ());

           ///         velocityV = Math.pow(velocity.getY() + 2.0, 2.0) * 5.0;

                    velocityNoTransTicks = 0;

                    velocityID--;

                    WrappedOutTransaction transaction = new WrappedOutTransaction(0, (short) velocityID, false);

                    TinyProtocolHandler.sendPacket(user.getPlayer(), transaction.getObject());

                    if (velocityID <= -1) {
                        velocityID = 9000;
                    }

                }
                break;
            }

            case Packet.Client.TRANSACTION: {
                WrappedInTransactionPacket transaction = new WrappedInTransactionPacket(event.getPacket(),
                        event.getUser().getPlayer());

                short idRel = relMoveID;
                short idVel = velocityID;
                short idReach = reachID;

                if (transaction.getAction() == idVel) {
                    velocityH = Math.hypot(velocity.getX(), velocity.getZ());
                    velocityV = velocity.getY();

                    velocityTicks = 0;
                }

                if (idReach == transaction.getAction()) {
                    for (Map.Entry<Short, ReachData> doubleShortEntry : reachTestMap.entrySet()) {
                        reachData = doubleShortEntry.getValue();
                        reachTestMap.clear();
                    }
                }


                if (transaction.getAction() == idRel) {
                    lastLastRelMove = lastRelMove;
                    lastRelMove = relCachedLocation;
                }

                break;
            }
        }
    }

    @Override
    public void setupTimers(User user) {
        this.preVelocityTimer = new EventTimer(20, user);
        this.useEntityTimer = new EventTimer(20, user);
    }

    private void queueTransaction(ReachData reachData) {
        reachID++;

        if (reachID >= 10000) {
            reachID = 9001;
        }

        short random = (short) reachID;
        reachTestMap.put(random, reachData);
        TinyProtocolHandler.sendPacket(reachData.getUser().getPlayer(),
                new WrappedOutTransaction(0, random, false).getObject());
    }

    @Getter
    @AllArgsConstructor
    public static class ReachData {
        private final User user;
        private final long time;
        private final PlayerLocation customLocation;
        private final PlayerLocation to;
        private final PlayerLocation from;
    }

    void handleRelMove(double x, double y, double z) {
    }

    boolean isValidEntity(int id) {
        return lastAttackedEntity != null && lastAttackedEntity.getEntityId() == id;
    }
}

