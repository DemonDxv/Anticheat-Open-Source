package me.rhys.anticheat.checks.combat.reach;

import me.rhys.anticheat.base.check.api.Check;
import me.rhys.anticheat.base.check.api.CheckInformation;
import me.rhys.anticheat.base.event.PacketEvent;
import me.rhys.anticheat.base.user.User;
import me.rhys.anticheat.tinyprotocol.api.Packet;
import me.rhys.anticheat.tinyprotocol.packet.in.WrappedInUseEntityPacket;
import me.rhys.anticheat.util.MathUtil;
import me.rhys.anticheat.util.PastLocation;
import me.rhys.anticheat.util.PlayerLocation;
import me.rhys.anticheat.util.world.CollisionBox;
import me.rhys.anticheat.util.world.EntityData;
import me.rhys.anticheat.util.world.types.RayCollision;
import me.rhys.anticheat.util.world.types.SimpleCollisionBox;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@CheckInformation(checkName = "Reach", lagBack = false, punishmentVL = 15, description = "Basic PastLocation 3.5 Reach Check.")
public class ReachA extends Check {

    private final PastLocation reachATargetLocations = new PastLocation();

    private double threshold;

    @Override
    public void onPacket(PacketEvent event) {
        User user = event.getUser();

        if (event.getType().equals(Packet.Client.USE_ENTITY)) {
            WrappedInUseEntityPacket useEntityPacket = new WrappedInUseEntityPacket(event.getPacket(), user.getPlayer());

            if (useEntityPacket.getAction() == WrappedInUseEntityPacket.EnumEntityUseAction.ATTACK) {

                if (user.shouldCancel()
                    || user.getConnectionProcessor().isLagging()
                    || user.getTick() < 60
                    || user.getCombatProcessor().getLastAttackedEntity() == null
                    || user.getCombatProcessor().getCancelTicks() > 0) {
                    threshold = 0;
                    return;
                }

                List<SimpleCollisionBox> simpleBoxes = new ArrayList<>();

                reachATargetLocations.getEstimatedLocation(event.getTimestamp(),
                        user.getConnectionProcessor().getTransPing(), 500L)
                    .stream()
                    .map(loc -> getHitbox(user.getCombatProcessor().getLastAttackedEntity(), loc))
                    .collect(Collectors.toList())
                    .forEach(box -> box.downCast(simpleBoxes));

                double distance = 69, horzDistance = 69, rayDistance = 69;
                int a = 0, collided = 0;

                for (PlayerLocation location : Arrays.asList(user.getCurrentLocation().clone())) {

                    location.setY(location.getY() + 1.64f);


                    RayCollision ray = new RayCollision(location.toVector(), MathUtil.getDirection(location));


                    for (SimpleCollisionBox collisionBox : simpleBoxes) {

                        SimpleCollisionBox simpleCollisionBox = collisionBox.copy();

                        simpleCollisionBox.expand(0.1);
                        double hitBoxExpand = RayCollision.distance(ray, simpleCollisionBox);

                        Vector vectorRay = ray.collisionPoint(collisionBox);

                        if (vectorRay != null) {
                            rayDistance = Math.min(rayDistance, vectorRay.distanceSquared(location.toVector()));
                        }


                        horzDistance = Math.min(horzDistance, simpleCollisionBox.max()
                            .midpoint(simpleCollisionBox.min())
                            .setY(0).distance(location.toVector().setY(0)) - .4);

                        if (hitBoxExpand == -1) {
                            a++;
                            continue;
                        } else {
                            collided++;
                        }

                        distance = Math.min(distance, hitBoxExpand);

                    }
                }

                if (user.getMovementProcessor().isOnGround() && user.getMovementProcessor().isLastGround()) {
                    distance -= user.getMovementProcessor().getDeltaXZ();
                }

                if (user.getCombatProcessor().getLastAttackedEntity() != null
                    && user.getCombatProcessor().getLastAttackedEntity().getVelocity() != null) {

                    double velocityX = user.getCombatProcessor().getLastAttackedEntity().getVelocity().getX();
                    double velocityZ = user.getCombatProcessor().getLastAttackedEntity().getVelocity().getZ();

                    double total = Math.hypot(velocityX, velocityZ);

                    distance -= total;
                }


                if (distance >= 3.5 && distance <= 6.5) {
                    if (++threshold > 4.5) {
                        flag(user, "r=" + distance + " hr=" + horzDistance + " c=" + collided);
                    }
                } else {
                    if (distance != 69) {
                        threshold -= Math.min(threshold, 0.123);
                    }
                }

                reachATargetLocations.addLocation(user.getCombatProcessor().getLastAttackedEntity().getLocation());
            }
        }
    }


    private static CollisionBox getHitbox(Entity entity, PlayerLocation loc) {
        return EntityData.getEntityBox(loc, entity);
    }
}
