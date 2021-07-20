package me.rhys.anticheat.checks.combat.reach;

import me.rhys.anticheat.Anticheat;
import me.rhys.anticheat.base.check.api.Check;
import me.rhys.anticheat.base.check.api.CheckInformation;
import me.rhys.anticheat.base.event.PacketEvent;
import me.rhys.anticheat.base.user.User;
import me.rhys.anticheat.tinyprotocol.api.Packet;
import me.rhys.anticheat.tinyprotocol.packet.in.WrappedInUseEntityPacket;
import me.rhys.anticheat.tinyprotocol.packet.out.WrappedOutEntityTeleport;
import me.rhys.anticheat.tinyprotocol.packet.out.WrappedOutRelativePosition;
import me.rhys.anticheat.util.MathUtil;
import me.rhys.anticheat.util.PastLocation;
import me.rhys.anticheat.util.PlayerLocation;
import me.rhys.anticheat.util.world.CollisionBox;
import me.rhys.anticheat.util.world.EntityData;
import me.rhys.anticheat.util.world.types.RayCollision;
import me.rhys.anticheat.util.world.types.SimpleCollisionBox;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@CheckInformation(checkName = "Reach", lagBack = false, punishmentVL = 7, description = "Detects reach at 3.01")
public class ReachA extends Check {

    private PastLocation reachCTargetLocations = new PastLocation();
    private double threshold;

    @Override
    public void onPacket(PacketEvent event) {
        User user = event.getUser();

        switch (event.getType()) {

            case Packet.Client.USE_ENTITY: {
                WrappedInUseEntityPacket useEntityPacket = new WrappedInUseEntityPacket(event.getPacket(), user.getPlayer());

                if (useEntityPacket.getAction() == WrappedInUseEntityPacket.EnumEntityUseAction.ATTACK) {
                    ///  User target = Anticheat.getInstance().getUserManager().getUser(user.getCombatProcessor().getLastLastAttackedEntity().getPlayer());


                    List<SimpleCollisionBox> simpleBoxes = new ArrayList<>();

                    reachCTargetLocations.getEstimatedLocation(event.getTimestamp(),
                            user.getConnectionProcessor().getTransPing(), 200L)
                            .stream()
                            .map(loc -> getHitbox(user.getCombatProcessor().getLastAttackedEntity(), loc)).collect(Collectors.toList())
                            .forEach(box -> box.downCast(simpleBoxes));

                    double distance = 69, horzDistance = 69, totalDistance = 69;
                    int a = 0, collided = 0;

                    for (PlayerLocation location : Arrays.asList(user.getCurrentLocation().clone(),
                            user.getLastLocation().clone())) {
                        location.setY(location.getY() + 1.62f);


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


                    if (collided > 2) {
                        if (horzDistance > 3.01 && distance > 3.01 && distance <= 6.5 && horzDistance <= 6.5) {
                            if (++threshold > 4.5) {
                                flag(user, "Distance", "" + distance);
                            }
                        } else {
                            threshold -= Math.min(threshold, 0.115f);
                        }
                    }

                    reachCTargetLocations.addLocation(user.getCombatProcessor().getReachData().getCustomLocation());

                }

                break;
            }
        }
    }


    private static CollisionBox getHitbox(Entity entity, PlayerLocation loc) {
        return EntityData.getEntityBox(loc, entity);
    }
}
