package me.rhys.anticheat.checks.combat.velocity;

import me.rhys.anticheat.base.check.api.Check;
import me.rhys.anticheat.base.check.api.CheckInformation;
import me.rhys.anticheat.base.event.PacketEvent;
import me.rhys.anticheat.base.user.User;
import me.rhys.anticheat.tinyprotocol.api.Packet;
import org.bukkit.Bukkit;

@CheckInformation(checkName = "Velocity",  checkType = "H", canPunish = false, description = "99% Vertical Velocity [2 Tick]")
public class VelocityH extends Check {

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

                velocity -= 0.08D;

                velocity *= 0.98F;

                double ratio = deltaY / velocity;

                if (deltaY < 0.42f && velocity < 2 && velocity > 0.2) {
                    if (user.getCombatProcessor().getVelocityTicks() <= 5) {
                        if (ratio < .3) {
                            if ((threshold += 0.95) > 5) {
                                flag(user, "Vertical Velocity (tick)");
                            }
                        } else {
                            threshold -= Math.min(threshold, 0.75);
                        }
                    }
                }
                break;
            }
        }
    }
}
