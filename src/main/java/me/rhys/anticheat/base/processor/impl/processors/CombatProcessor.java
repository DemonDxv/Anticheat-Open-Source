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
import me.rhys.anticheat.util.MathUtil;
import me.rhys.anticheat.util.PastLocation;
import me.rhys.anticheat.util.PlayerLocation;
import me.rhys.anticheat.util.block.RayTrace;
import me.rhys.anticheat.util.box.BoundingBox;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ProcessorInformation(name = "Combat")
@Getter
@Setter
public class CombatProcessor extends Processor {

    private EventTimer preVelocityTimer, useEntityTimer;

    private double velocityH, velocityV, velocityHNoTrans;

    private Vector velocity = new Vector(), velocityNoTrans = new Vector();

    private int velocityTicks, velocityNoTransTicks;

    private short velocityID = 9000, relMoveID = -9, reachID = 9001;

    private Player lastAttackedEntity, lastLastAttackedEntity;

    private PastLocation hitboxLocations = new PastLocation();
    private List<BoundingBox> boundingBoxList = new ArrayList<>();
    private boolean insideHitbox;

    @Override
    public void onPacket(PacketEvent event) {

        switch (event.getType()) {

            case Packet.Client.USE_ENTITY: {
                WrappedInUseEntityPacket useEntityPacket = new WrappedInUseEntityPacket(event.getPacket(), user.getPlayer());

                if (useEntityPacket.getAction() == WrappedInUseEntityPacket.EnumEntityUseAction.ATTACK) {

                    lastLastAttackedEntity = lastAttackedEntity;
                    lastAttackedEntity = (Player) useEntityPacket.getEntity();
                    useEntityTimer.reset();


                    velocity.setX(velocity.getX() * 0.6F);
                    velocity.setZ(velocity.getZ() * 0.6F);

                    Location location = user.getCurrentLocation().clone()
                            .toBukkitLocation(user.getPlayer().getWorld());

                    LivingEntity livingEntity = (LivingEntity) useEntityPacket.getEntity();

                    List<PlayerLocation> pastLocation = hitboxLocations.getEstimatedLocation(event.getTimestamp(),
                            user.getConnectionProcessor().getTransPing(), 200L);

                    if (pastLocation.size() > 0) {

                        if (livingEntity != null && location != null) {

                            pastLocation.forEach(loc1 -> boundingBoxList.add(MathUtil.getHitbox(livingEntity, loc1, user)));

                            location.setY(location.getY() + (user.getPlayer().isSneaking() ? 1.53 : user.getPlayer().getEyeHeight()));

                            RayTrace trace = new RayTrace(location.toVector(), user.getPlayer().getEyeLocation().getDirection());


                            boolean outsideHitbox = boundingBoxList.stream().noneMatch(box ->
                                    trace.intersects(box, box.getMinimum().distance(location.toVector()) + 1.0,
                                            .4));

                            insideHitbox = !outsideHitbox;

                            boundingBoxList.clear();
                            pastLocation.clear();
                        }
                    }
                }
            }

            case Packet.Client.FLYING:
            case Packet.Client.LOOK:
            case Packet.Client.POSITION_LOOK:
            case Packet.Client.POSITION: {
                velocityTicks++;
                velocityNoTransTicks++;

                if (lastAttackedEntity != null) {
                    hitboxLocations.addLocation(lastAttackedEntity.getLocation());
                }

                break;
            }


            case Packet.Server.ENTITY_VELOCITY: {
                WrappedOutVelocityPacket wrappedOutVelocityPacket = new WrappedOutVelocityPacket(event.getPacket(),
                        event.getUser().getPlayer());

                if (wrappedOutVelocityPacket.getId() == event.getUser().getPlayer().getEntityId()) {

                    this.preVelocityTimer.reset();


                    velocity = new Vector(wrappedOutVelocityPacket.getX(), wrappedOutVelocityPacket.getY(),
                            wrappedOutVelocityPacket.getZ());

                    velocityNoTrans = new Vector(wrappedOutVelocityPacket.getX(), wrappedOutVelocityPacket.getY(),
                            wrappedOutVelocityPacket.getZ());

                    velocityNoTransTicks = 0;

                    velocityID--;

                    velocityHNoTrans = Math.hypot(velocityNoTrans.getX(), velocityNoTrans.getZ());

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

                short idVel = velocityID;

                if (transaction.getAction() == idVel) {
                    velocityH = Math.hypot(velocity.getX(), velocity.getZ());
                    velocityV = velocity.getY();

                    velocityTicks = 0;
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

