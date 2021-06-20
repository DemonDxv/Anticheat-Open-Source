package me.rhys.anticheat.checks.combat.velocity;

import me.rhys.anticheat.base.check.api.Check;
import me.rhys.anticheat.base.check.api.CheckInformation;
import me.rhys.anticheat.base.event.PacketEvent;
import me.rhys.anticheat.base.user.User;
import me.rhys.anticheat.tinyprotocol.api.Packet;

@CheckInformation(checkName = "Velocity", lagBack = false, description = "99% Vertical Velocity")
public class VelocityA extends Check {

    private double threshold;

    @Override
    public void onPacket(PacketEvent event) {
        switch (event.getType()) {
            case Packet.Client.FLYING:
            case Packet.Client.LOOK:
            case Packet.Client.POSITION_LOOK:
            case Packet.Client.POSITION: {
                User user = event.getUser();

                if (user.getCurrentLocation().getY() > user.getLastLocation().getY()
                        || user.getLastFallDamageTimer().hasNotPassed(20)
                        || user.shouldCancel()) {
                    threshold = 0;
                    return;
                }

                double deltaY = user.getMovementProcessor().getDeltaY();

                double velocity = user.getCombatProcessor().getVelocity().getY();

                if (user.getCombatProcessor().getVelocityTicks() == 1
                        && user.getCurrentLocation().isClientGround() && user.getLastLocation().isClientGround()) {

                    if ((deltaY / velocity) == 0.0) {
                        if (threshold++ > 2) {
                            flag(user, "No Vertical Knockback");
                        }
                    } else {
                        threshold -= Math.min(threshold, 0.0626f);
                    }
                }

                break;
            }
        }
    }
}