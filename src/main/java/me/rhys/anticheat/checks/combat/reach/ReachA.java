package me.rhys.anticheat.checks.combat.reach;

import me.rhys.anticheat.base.check.api.Check;
import me.rhys.anticheat.base.check.api.CheckInformation;
import me.rhys.anticheat.base.event.PacketEvent;
import me.rhys.anticheat.base.user.User;
import me.rhys.anticheat.tinyprotocol.api.Packet;
import me.rhys.anticheat.tinyprotocol.packet.in.WrappedInUseEntityPacket;
import me.rhys.anticheat.tinyprotocol.packet.out.WrappedOutEntityTeleport;
import me.rhys.anticheat.tinyprotocol.packet.out.WrappedOutRelativePosition;
import me.rhys.anticheat.util.PastLocation;
import me.rhys.anticheat.util.PlayerLocation;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.stream.Collectors;

@CheckInformation(checkName = "Reach", lagBack = false, punishmentVL = 7, description = "Detects reach at 3.3 using Past Locations")
public class ReachA extends Check {

    private double threshold;
    private long lastReachHit;

    @Override
    public void onPacket(PacketEvent event) {
        User user = event.getUser();

        switch (event.getType()) {

            case Packet.Client.FLYING:
            case Packet.Client.LOOK:
            case Packet.Client.POSITION_LOOK:
            case Packet.Client.POSITION: {

                if (user.getCombatProcessor().getLastAttackedEntity() != null) {
                    user.getPastLocations().addLocation(user.getCombatProcessor().getLastAttackedEntity().getLocation());
                }
                break;
            }

            case Packet.Client.USE_ENTITY: {
                WrappedInUseEntityPacket useEntityPacket = new WrappedInUseEntityPacket(event.getPacket(),user.getPlayer());

                if (useEntityPacket.getAction() == WrappedInUseEntityPacket.EnumEntityUseAction.ATTACK) {
                    PlayerLocation origin = user.getCurrentLocation();

                    List<Vector> pastLocation = user.getPastLocations()
                            .getEstimatedLocation(user.getConnectionProcessor().getTransPing(),
                                    150).stream().map
                                    (PlayerLocation::toVector).collect(Collectors.toList());

                    float distance = (float) pastLocation.stream().mapToDouble(vec ->
                            vec.clone().setY(0).distance(origin.toVector().clone().setY(0)) - 0.4f)
                            .min().orElse(0);

                    if (user.getCombatProcessor().getLastAttackedEntity()
                            != user.getCombatProcessor().getLastLastAttackedEntity()) {
                        distance = 0;
                    }


                    if ((System.currentTimeMillis() - lastReachHit) > 10000L) {
                        threshold = 0;
                    }

                    if ((System.currentTimeMillis() - lastReachHit) > 3000L) {
                        threshold -= Math.min(threshold, .5);
                    }

                    if ((System.currentTimeMillis() - lastReachHit) > 1000L) {
                        threshold -= Math.min(threshold, .09);
                    }

                    if (distance >= 3.1 && distance <= 6.5) {
                        lastReachHit = System.currentTimeMillis();

                        if (threshold++ > 3) {
                            flag(user, "Reach", "" + distance);
                        }
                    }



                }

                break;
            }
        }
    }
}
