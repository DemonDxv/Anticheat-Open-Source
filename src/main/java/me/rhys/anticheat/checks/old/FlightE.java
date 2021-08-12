package me.rhys.anticheat.checks.old;

import me.rhys.anticheat.base.check.api.Check;
import me.rhys.anticheat.base.check.api.CheckInformation;
import me.rhys.anticheat.base.event.PacketEvent;
import me.rhys.anticheat.base.user.User;
import me.rhys.anticheat.tinyprotocol.api.Packet;

@CheckInformation(checkName = "Flight", checkType = "E", description = "Checks if player is jumping lower than legit")
public class FlightE extends Check {

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
                        || user.getActionProcessor().getServerPositionTimer().hasNotPassed(3)
                        || user.getLastTeleportTimer().hasNotPassed(20)
                        || user.getCombatProcessor().getVelocityTicks() <= 20
                        || user.getVehicleTicks() > 0
                        || user.getBlockData().webTicks > 0
                        || user.getMovementProcessor().isBouncedOnSlime()
                        || user.getBlockData().snowTicks > 0
                        || user.getBlockData().pistonTicks > 0
                        || user.getBlockData().underBlockTicks > 0
                        || checkConditions(user)) {
                    return;
                }

                double deltaY = user.getMovementProcessor().getDeltaY();

                if (deltaY >= .404f && deltaY <= .405f) {
                    threshold = 0;
                    return;
                }

                double maxJumpHeight = 0.42F;

                if (user.getBlockData().fenceTicks > 0) {
                    maxJumpHeight = 0.5;
                }

                if (!user.getMovementProcessor().isOnGround() && user.getMovementProcessor().isLastGround()) {
                    if (deltaY < maxJumpHeight && deltaY >= 0.0) {
                        if (threshold++ > 1) {
                            flag(user, "Jumping Lower Than Legit", "" + deltaY);
                        }
                    } else {
                        threshold -= Math.min(threshold, 0.001f);
                    }
                }
            }
        }
    }
    boolean checkConditions(User user) {
        return user.getBlockData().liquidTicks > 0
                || user.getTick() < 60
                || user.shouldCancel()
                || user.getBlockData().climbableTicks > 0
                || user.getBlockData().climbableTimer.hasNotPassed();
    }
}
