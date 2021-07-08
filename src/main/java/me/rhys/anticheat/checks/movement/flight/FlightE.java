package me.rhys.anticheat.checks.movement.flight;

import me.rhys.anticheat.base.check.api.Check;
import me.rhys.anticheat.base.check.api.CheckInformation;
import me.rhys.anticheat.base.event.PacketEvent;
import me.rhys.anticheat.base.user.User;
import me.rhys.anticheat.tinyprotocol.api.Packet;

@CheckInformation(checkName = "Flight", checkType = "E", lagBack = true, description = "Checks if player is jumping lower than legit")
public class FlightE extends Check {

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
                        || user.getBlockData().underBlockTicks > 0
                        || checkConditions(user)) {
                    return;
                }

                double deltaY = user.getMovementProcessor().getDeltaY();

                double maxJumpHeight = 0.42F;

                if (!user.getCurrentLocation().isClientGround() && user.getLastLocation().isClientGround()) {
                    if (deltaY < maxJumpHeight && deltaY > 0) {
                        flag(user, "Jumping Lower Than Legit "+deltaY);
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
