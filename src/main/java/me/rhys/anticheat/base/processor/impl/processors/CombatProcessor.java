package me.rhys.anticheat.base.processor.impl.processors;

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
import me.rhys.anticheat.tinyprotocol.packet.in.WrappedInUseEntityPacket;
import me.rhys.anticheat.tinyprotocol.packet.out.WrappedOutEntityTeleport;
import me.rhys.anticheat.tinyprotocol.packet.out.WrappedOutTransaction;
import me.rhys.anticheat.tinyprotocol.packet.out.WrappedOutVelocityPacket;
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

    private short velocityID = 9000, relMoveID = 9;

    private Player lastAttackedEntity, lastLastAttackedEntity;
    private PlayerLocation relCachedLocation, relLocation;

    public HashMap<PlayerLocation, Short> lastRelLocation = new HashMap();

    @Override
    public void onPacket(PacketEvent event) {

        if (event.getType().equals(Packet.Server.ENTITY)
                || event.getType().equals(Packet.Server.ENTITY_TELEPORT)
                || event.getType().equals(Packet.Server.REL_POSITION_LOOK)
                || event.getType().equals(Packet.Server.REL_LOOK)
                || event.getType().equals(Packet.Server.REL_POSITION)
                || event.getType().equals(Packet.Server.ENTITY_HEAD_ROTATION)) {

            boolean process = false;
            int id;
            double x = 0;
            double y = 0;
            double z = 0;

            if (event.getType().equalsIgnoreCase(NMSObject.Server.ENTITY_TELEPORT)) {
                WrappedOutEntityTeleport wrappedOutEntityTeleport = new WrappedOutEntityTeleport(event.getPacket());

                id = wrappedOutEntityTeleport.getId();

                if (lastAttackedEntity != null && lastAttackedEntity.getEntityId() != id) {
                    return;
                }

                x = wrappedOutEntityTeleport.getX();
                y = wrappedOutEntityTeleport.getY();
                z = wrappedOutEntityTeleport.getZ();
                process = true;
            }

            if (process) {
                x = x / 32;
                y = y / 32;
                z = z / 32;

                World world = user.getPlayer().getWorld();

                relCachedLocation = new PlayerLocation(world, x,y,z,
                        0, 0, false, System.currentTimeMillis());


                relMoveID--;

                if (relMoveID < -65) {
                    relMoveID = -9;
                }

                WrappedOutTransaction transaction = new WrappedOutTransaction(0, relMoveID, false);

                TinyProtocolHandler.sendPacket(user.getPlayer(), transaction.getObject());
            }
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

                    velocityH = Math.hypot(velocity.getX(), velocity.getZ());

                    velocityV = Math.pow(velocity.getY() + 2.0, 2.0) * 5.0;

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

            case Packet.Server.TRANSACTION: {
                WrappedOutTransaction transaction = new WrappedOutTransaction(event.getPacket(),
                        event.getUser().getPlayer());

                if (transaction.getAction() == velocityID) {
                    velocityH = Math.hypot(velocity.getX(), velocity.getZ());
                    velocityV = Math.pow(velocity.getY() + 2.0, 2.0) * 5.0;
                    velocityTicks = 0;
                }

                if (transaction.getAction() == relMoveID) {
                //    user.getPastLocations().addLocation(relCachedLocation);
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
}

