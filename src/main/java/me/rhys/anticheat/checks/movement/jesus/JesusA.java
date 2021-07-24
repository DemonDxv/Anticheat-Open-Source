package me.rhys.anticheat.checks.movement.jesus;

import me.rhys.anticheat.base.check.api.Check;
import me.rhys.anticheat.base.check.api.CheckInformation;
import me.rhys.anticheat.base.event.PacketEvent;
import me.rhys.anticheat.base.user.User;
import me.rhys.anticheat.tinyprotocol.api.Packet;
import org.bukkit.Bukkit;

@CheckInformation(checkName = "Jesus", punishmentVL = 14, description = "Checks if the player is walking on water")
public class JesusA extends Check {

    private double threshold;

    @Override
    public void onPacket(PacketEvent event) {
        User user = event.getUser();

        switch (event.getType()) {
            case Packet.Client.FLYING:
            case Packet.Client.LOOK:
            case Packet.Client.POSITION_LOOK:
            case Packet.Client.POSITION: {

                if (user.shouldCancel()
                        || user.getLastTeleportTimer().hasNotPassed(20)
                        || user.getVehicleTicks() > 0
                        || user.getActionProcessor().getServerPositionTimer().hasNotPassed(3)) {
                    threshold = 0;
                    return;
                }

                if (user.getBlockData().nearWater || user.getBlockData().nearLava) {
                    double deltaY = user.getMovementProcessor().getDeltaY();

                    if (user.getBlockData().underBlockTicks > 0 && deltaY == 0.0) {
                        threshold -= 1;
                    }

                    if (deltaY > -0.02 && deltaY < 0.02) {
                        if (!user.getBlockData().onGround) {
                            if (threshold++ > 12) {
                                flag(user, "Abnormal movements in water");
                            }
                        } else {
                            threshold -= Math.min(threshold, 0.75);
                        }
                    } else {
                        threshold -= Math.min(threshold, 1.75);
                    }
                } else {
                    threshold -= Math.min(threshold, 1.0);
                }

                break;
            }
        }
    }
}