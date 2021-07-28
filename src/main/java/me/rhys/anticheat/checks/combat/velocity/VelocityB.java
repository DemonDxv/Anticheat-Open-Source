package me.rhys.anticheat.checks.combat.velocity;

import me.rhys.anticheat.base.check.api.Check;
import me.rhys.anticheat.base.check.api.CheckInformation;
import me.rhys.anticheat.base.event.PacketEvent;
import me.rhys.anticheat.base.user.User;
import me.rhys.anticheat.tinyprotocol.api.Packet;
import org.bukkit.Bukkit;

@CheckInformation(checkName = "Velocity",  checkType = "B", canPunish = false, description = "99% Vertical Velocity")
public class VelocityB extends Check {

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
                        || user.getTick() < 60
                        || user.getBlockData().underBlockTicks > 0
                        || user.getLastFireTickTimer().hasNotPassed(20)
                        || user.getBlockData().collidesHorizontal
                        || user.shouldCancel()) {
                    threshold = 0;
                    return;
                }

                double deltaY = user.getMovementProcessor().getDeltaY();

                double velocity = user.getCombatProcessor().getVelocityV();

                double ratio = deltaY / velocity;

                if (deltaY < 0.42f && velocity < 2 && velocity > 0.2) {
                    if (user.getCombatProcessor().getVelocityTicks() == 1
                            && !user.getMovementProcessor().isOnGround() && user.getMovementProcessor().isLastGround()) {

                        if (ratio <= 0.99 && ratio >= 0.0) {
                            if (threshold++ > 2) {
                                flag(user, "Vertical Knockback: " + ratio);
                            }
                        } else {
                            threshold -= Math.min(threshold, 0.0626f);
                        }
                    }
                }
                break;
            }
        }
    }
}
