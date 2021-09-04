package me.rhys.anticheat.checks.combat.velocity;

import me.rhys.anticheat.base.check.api.Check;
import me.rhys.anticheat.base.check.api.CheckInformation;
import me.rhys.anticheat.base.event.PacketEvent;
import me.rhys.anticheat.base.user.User;
import me.rhys.anticheat.tinyprotocol.api.Packet;
import org.bukkit.Bukkit;

@CheckInformation(checkName = "Velocity",  checkType = "F", canPunish = false, description = "More Vertical Velocity [2]")
public class VelocityF extends Check {

    private double threshold;

    @Override
    public void onPacket(PacketEvent event) {
        switch (event.getType()) {
            case Packet.Client.FLYING:
            case Packet.Client.LOOK:
            case Packet.Client.POSITION_LOOK:
            case Packet.Client.POSITION: {
                User user = event.getUser();

                if (user.getLastFallDamageTimer().hasNotPassed(20)
                        || user.getVehicleTicks() > 0
                        || user.getBlockData().underBlockTicks > 0
                        || user.getLastFireTickTimer().hasNotPassed(20)
                        || user.getBlockData().collidesHorizontal
                        || user.getTick() < 60
                        || user.shouldCancel()) {
                    threshold = 0;
                    return;
                }

                double deltaY = user.getMovementProcessor().getDeltaY();

                double velocity = user.getCombatProcessor().getVelocityV();

                double ratio = deltaY / velocity;

                if (user.getCombatProcessor().getVelocityTicks() == 1) {
                    if (deltaY < 0.42f && velocity < 2 && velocity > 0.2) {
                        if (ratio > 1.00001) {
                            if (threshold++ > 1) {
                                flag(user, "Vertical velocity to high " + ratio);
                            }
                        } else {
                            threshold -= Math.min(threshold, 0.001);
                        }
                    }
                }

                break;
            }
        }
    }
}
