package me.rhys.anticheat.checks.old;

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
                        || user.getBlockData().snowTicks > 0
                        || user.getBlockData().skullTicks > 0
                        || user.getLastBlockPlaceCancelTimer().hasNotPassed(20)
                        || user.getBlockData().slimeTimer.hasNotPassed(20)
                        || user.getMovementProcessor().isBouncedOnSlime()
                        || checkConditions(user)) {
                    return;
                }

                double currentY = user.getCurrentLocation().getY();
                double deltaY = user.getMovementProcessor().getDeltaY();

                double max = 1.25;

                if (user.getLastBlockPlaceCancelTimer().hasNotPassed(20))

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

                if (user.getLastExplosionTimer().hasNotPassed(40)) {
                    max = 256;
                }

                if (user.getBlockData().onGround) {
                    serverGroundY = currentY;
                }

                double change = currentY - serverGroundY;

                if (!user.getMovementProcessor().isOnGround()) {
                    if (change > max && deltaY > 0.0) {
                        if (++threshold > 3) {
                            flag(user, "To far from the ground?", "" + change);
                        }
                    } else {
                        threshold -= Math.min(threshold, 0.1);
                    }
                }
            }
        }
    }
    boolean checkConditions(User user) {
        return user.getBlockData().waterTicks > 0
                || user.getTick() < 100
                || user.shouldCancel()
                || user.getBlockData().climbableTicks > 0
                || user.getBlockData().climbableTimer.hasNotPassed();
    }
}
