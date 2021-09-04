package me.rhys.anticheat.checks.misc.scaffold;

import me.rhys.anticheat.base.check.api.Check;
import me.rhys.anticheat.base.check.api.CheckInformation;
import me.rhys.anticheat.base.event.PacketEvent;
import me.rhys.anticheat.base.user.User;
import me.rhys.anticheat.tinyprotocol.api.Packet;
import me.rhys.anticheat.tinyprotocol.packet.in.WrappedInBlockPlacePacket;
import me.rhys.anticheat.util.MathUtil;
import me.rhys.anticheat.util.TimeUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;

@CheckInformation(checkName = "Scaffold", checkType = "J", punishmentVL = 15)
public class ScaffoldJ extends Check {

    private double threshold, lastYaw;

    @Override
    public void onPacket(PacketEvent event) {
        User user = event.getUser();

        switch (event.getType()) {

            case Packet.Client.BLOCK_PLACE: {

                WrappedInBlockPlacePacket blockPlace =
                        new WrappedInBlockPlacePacket(event.getPacket(), user.getPlayer());

                if (user.shouldCancel() || user.getTick() < 60) {
                    threshold = 0;
                    return;
                }

             //   double pitch = user.getMovementProcessor().getPitchDelta();

                double calculate = MathUtil.yawCheck(user.getCurrentLocation().getYaw(), lastYaw);

                double yawCompare = Double.compare(user.getCurrentLocation().getYaw(), 0.0);
                double yawDiffCompare = Double.compare(user.getCurrentLocation().getYaw(), lastYaw);

                if (user.getMovementProcessor().getDeltaY() != 0) {
                    threshold -= Math.min(threshold, 0.25);
                }
                if (user.getLastBlockPlaceTimer().hasNotPassed(2)) {
                    if (user.getPlayer().getEyeLocation().add(0, -1, 0).getBlock().getType() == Material.AIR) {

                        int faceInt = blockPlace.getFace().b();

                        if (faceInt >= 0 && faceInt <= 3) {
                            if (calculate <= 94) {
                                if (yawCompare == -1 || yawDiffCompare != 0) {
                                    if (++threshold > 15) {
                                        flag(user);
                                    }
                                } else {
                                    threshold -= Math.min(threshold, 0.85f);
                                }
                            }
                        }
                    }
                }

                this.lastYaw = user.getCurrentLocation().getYaw();


                break;
            }
        }
    }
}