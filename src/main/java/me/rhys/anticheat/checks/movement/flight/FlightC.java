package me.rhys.anticheat.checks.movement.flight;

import me.rhys.anticheat.base.check.api.Check;
import me.rhys.anticheat.base.check.api.CheckInformation;
import me.rhys.anticheat.base.event.PacketEvent;
import me.rhys.anticheat.base.user.User;
import me.rhys.anticheat.tinyprotocol.api.Packet;

@CheckInformation(checkName = "Flight", checkType = "C", lagBack = true, description = "Checks if the player is jumping higher than usual")
public class FlightC extends Check {

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

                double deltaY = user.getMovementProcessor().getDeltaY();

                double maxJumpHeight = 0.42F + (user.getPotionProcessor().getJumpAmplifier() * 0.2D);

                if (user.getBlockData().slab
                        || user.getBlockData().fence
                        || user.getBlockData().stair) {
                    maxJumpHeight = 0.5;
                }

                if (user.getBlockData().bed) {
                    maxJumpHeight = 0.5625F;
                }

                if (!user.getCurrentLocation().isClientGround() && user.getLastLocation().isClientGround()) {
                    if (deltaY > maxJumpHeight) {
                        flag(user, "Jumping Higher Than Legit ", "" + deltaY);
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
