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
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@CheckInformation(checkName = "Reach", checkType = "B", lagBack = false, punishmentVL = 7, description = "Detects reach at 3.01 using the Average Reach")
public class ReachB extends Check {

    private PastLocation reachBTargetLocations = new PastLocation();

    private double threshold;
    private List<Double> averageReachThreshold = new ArrayList<>();

    @Override
    public void onPacket(PacketEvent event) {
        User user = event.getUser();

        switch (event.getType()) {

            case Packet.Client.USE_ENTITY: {
                WrappedInUseEntityPacket useEntityPacket = new WrappedInUseEntityPacket(event.getPacket(), user.getPlayer());

                if (useEntityPacket.getAction() == WrappedInUseEntityPacket.EnumEntityUseAction.ATTACK) {

                    if (user.shouldCancel() || user.getTick() < 60 || user.getCombatProcessor().getCancelTicks() > 0) {
                        threshold = 0;
                        return;
                    }

                    List<SimpleCollisionBox> simpleBoxes = new ArrayList<>();

                    reachBTargetLocations.getEstimatedLocation(event.getTimestamp(),
                            user.getConnectionProcessor().getTransPing(), 200L)
                            .stream()
                            .map(loc -> getHitbox(user.getCombatProcessor().getLastAttackedEntity(), loc)).collect(Collectors.toList())
                            .forEach(box -> box.downCast(simpleBoxes));

                    double distance = 69, horzDistance = 69;
                    int a = 0, collided = 0;

                    for (PlayerLocation location : Arrays.asList(user.getCurrentLocation().clone(),
                            user.getLastLocation().clone())) {
                        location.setY(location.getY() + 1.905f);


                        RayCollision ray = new RayCollision(location.toVector(), MathUtil.getDirection(location));


                        for (SimpleCollisionBox collisionBox : simpleBoxes) {

                            SimpleCollisionBox simpleCollisionBox = collisionBox.copy();
                            simpleCollisionBox.expand(0.1);
                            double hitBoxExpand = RayCollision.distance(ray, simpleCollisionBox);

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

                    if (horzDistance > 3.01 && horzDistance <= 6.5) {
                        threshold++;
                        averageReachThreshold.add(threshold);
                    } else if (horzDistance > 0.0 && horzDistance <= 3.0) {
                        threshold -= Math.min(threshold, 0.75);
                    }



                    if (averageReachThreshold.size() > 9) {

                        double averageThreshold = MathUtil.getAverage(averageReachThreshold);

                        if (averageThreshold > 4.39) {
                            flag(user, "Possibly using low reach settings");
                        }

                        averageReachThreshold.clear();
                    }

                    reachBTargetLocations.addLocation(user.getReachProcessor().getReachData().getCustomLocation());
                }

                break;
            }
        }
    }


    private static CollisionBox getHitbox(Entity entity, PlayerLocation loc) {
        return EntityData.getEntityBox(loc, entity);
    }
}
