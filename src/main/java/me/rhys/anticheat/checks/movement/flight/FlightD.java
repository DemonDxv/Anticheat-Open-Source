package me.rhys.anticheat.checks.movement.flight;

import me.rhys.anticheat.base.check.api.Check;
import me.rhys.anticheat.base.check.api.CheckInformation;
import me.rhys.anticheat.base.event.PacketEvent;
import me.rhys.anticheat.base.user.User;
import me.rhys.anticheat.tinyprotocol.api.Packet;

@CheckInformation(checkName = "Flight", checkType = "D", description = "Checks if the player is to far up from the ground")
public class FlightD extends Check {

    private double serverGroundY;
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
                        || user.getMovementProcessor().isBouncedOnSlime()
                        || checkConditions(user)) {
                    return;
                }

                double currentY = user.getCurrentLocation().getY();
                double deltaY = user.getMovementProcessor().getDeltaY();

                double max = 1.25;

                switch ((int) user.getPotionProcessor().getJumpAmplifier()) {
                    case 1: {
                        max = 1.8;
                        break;
                    }

                    case 2: {
                        max = 2.0;
                        break;
                    }

                    case 3: {
                        max = 2.5;
                        break;
                    }
                }

                if (user.getBlockData().onGround) {
                    serverGroundY = currentY;
                }

                double change = currentY - serverGroundY;

                if (!user.getBlockData().onGround) {
                    if (change > max && deltaY > 0.0) {
                        if (++threshold > 3) {
                            flag(user, "Flying up to high? " + change);
                        }
                    } else {
                        threshold -= Math.min(threshold, 0.1);
                    }
                }
            }
        }
    }
    boolean checkConditions(User user) {
        return user.getBlockData().liquidTicks > 0
                || user.getTick() < 100
                || user.shouldCancel()
                || user.getBlockData().climbableTicks > 0
                || user.getBlockData().climbableTimer.hasNotPassed();
    }
}
