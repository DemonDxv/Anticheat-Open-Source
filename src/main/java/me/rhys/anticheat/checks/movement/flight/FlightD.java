package me.rhys.anticheat.checks.movement.flight;

import me.rhys.anticheat.base.check.api.Check;
import me.rhys.anticheat.base.check.api.CheckInformation;
import me.rhys.anticheat.base.event.PacketEvent;
import me.rhys.anticheat.base.user.User;
import me.rhys.anticheat.tinyprotocol.api.Packet;

@CheckInformation(checkName = "Flight", checkType = "D", punishmentVL = 45, description = "Checks if player is using yPort")
public class FlightD extends Check {

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
                        || user.getMovementProcessor().isBouncedOnSlime()
                        || user.getVehicleTicks() > 0
                        || user.getBlockData().webTicks > 0
                        || user.getBlockData().cakeTicks > 0
                        || user.getCombatProcessor().getVelocityTicks() <= 20
                        || checkConditions(user)) {
                    threshold = 0;
                    return;
                }

                double deltaY = user.getMovementProcessor().getDeltaY();

                double lastDeltaY = user.getMovementProcessor().getLastDeltaY();

                double prediction = (lastDeltaY - 0.08D) * 0.98F;

                double difference = Math.abs(deltaY - prediction);

                if (prediction > 0.005 && deltaY < 0.0) {
                    if (difference > 0.005) {
                        if (threshold++ > 2) {
                            flag(user, "Falling Abnormally");
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
                || user.getBlockData().underBlockTicks > 0
                || user.getBlockData().stairTicks > 0
                || user.getBlockData().slabTicks > 0
                || user.shouldCancel()
                || user.getBlockData().climbableTicks > 0
                || user.getBlockData().climbableTimer.hasNotPassed();
    }
}
