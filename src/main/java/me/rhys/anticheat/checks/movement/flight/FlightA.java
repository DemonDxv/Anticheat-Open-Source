package me.rhys.anticheat.checks.movement.flight;

import me.rhys.anticheat.base.check.api.Check;
import me.rhys.anticheat.base.check.api.CheckInformation;
import me.rhys.anticheat.base.event.PacketEvent;
import me.rhys.anticheat.base.user.User;
import me.rhys.anticheat.tinyprotocol.api.Packet;
import org.bukkit.Bukkit;

@CheckInformation(checkName = "Flight", lagBack = true, description = "Checks if the players predicted y delta")
public class FlightA extends Check {

    private double threshold;
    private int jumpTicks;

    @Override
    public void onPacket(PacketEvent event) {
        User user = event.getUser();

        switch (event.getType()) {
            case Packet.Client.FLYING:
            case Packet.Client.LOOK:
            case Packet.Client.POSITION_LOOK:
            case Packet.Client.POSITION: {

                if (user.shouldCancel()
                        || user.getActionProcessor().getServerPositionTimer().hasNotPassed(20)
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

                double prediction = (lastDeltaY - 0.08D) * 0.9800000190734863D;

                if (deltaY >= 0.42f) {
                    prediction += 0.7F;
                }

                if (user.getBlockData().climbable) {
                    if (deltaY == 0.42f) {
                        jumpTicks = 5;
                    }

                    if (jumpTicks > 0) {
                        prediction = 0.42f + (user.getPotionProcessor().getJumpAmplifier() * 0.2D);
                    }

                    if (jumpTicks-- <= 0) {
                        prediction = 0.2D;
                    }
                }

                double difference = deltaY - prediction;


                if (user.getMovementProcessor().getLastBlockPlacePacketTimer().hasNotPassed(20) && (deltaY >= .404f && deltaY <= .405f || lastDeltaY >= .404f && lastDeltaY <= .405F)) {
                    difference = 0.0;
                }

                if (!user.getCurrentLocation().isClientGround()
                        && !user.getLastLocation().isClientGround()
                        && !user.getLastLastLocation().isClientGround()) {

                    if (difference > 0.005) {
                        if (threshold++ > 3) {
                            flag(user, "Moved the wrong prediction");
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
                || user.shouldCancel();
    }
}
