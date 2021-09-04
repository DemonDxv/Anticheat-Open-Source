package me.rhys.anticheat.checks.movement.flight;

import me.rhys.anticheat.base.check.api.Check;
import me.rhys.anticheat.base.check.api.CheckInformation;
import me.rhys.anticheat.base.event.PacketEvent;
import me.rhys.anticheat.base.user.User;
import me.rhys.anticheat.tinyprotocol.api.Packet;

@CheckInformation(checkName = "Flight", checkType = "F", canPunish = false, description = "Checks if going upwards incorrectly")
public class FlightF extends Check {

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
                        || user.getLastTeleportTimer().hasNotPassed(20
                        + user.getConnectionProcessor().getClientTick())
                        || user.getLastBlockPlaceCancelTimer().hasNotPassed(20
                        + user.getConnectionProcessor().getClientTick())
                        || user.getMovementProcessor().isBouncedOnSlime()
                        || user.getVehicleTicks() > 0
                        || user.getTick() < 60
                        || user.getBlockData().slimeTimer.hasNotPassed(20)
                        || user.getBlockData().climbableTimer.hasNotPassed(20
                        + user.getConnectionProcessor().getClientTick())
                        || user.getBlockData().pistonTicks > 0
                        || !user.isChunkLoaded()
                        || user.getBlockData().lavaTicks > 0
                        || user.getBlockData().waterTicks > 0
                        || user.getPotionProcessor().getJumpTicks() > 0
                        || user.getBlockData().stairSlabTimer.hasNotPassed(20)
                        || user.getActionProcessor().getVelocityTimer().hasNotPassed(10
                        + user.getConnectionProcessor().getClientTick())
                        && user.getLastFallDamageTimer().passed(20)) {
                    threshold = 0;
                    return;
                }

                double deltaY = user.getMovementProcessor().getDeltaY();
                double maxDeltaYSpeed = user.getMovementProcessor().getAirTicks() > 7 ? 0.0
                        : 0.42f + (user.getPotionProcessor().getJumpAmplifier() * 0.1F);

                if (deltaY > maxDeltaYSpeed && !user.getMovementProcessor().isOnGround()) {
                    if (++threshold > 2) {
                        flag(user, "Flying up abnormally");
                    }
                } else {
                    threshold -= Math.min(threshold, 0.01f);
                }

            }
        }
    }
}