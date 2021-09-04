package me.rhys.anticheat.checks.movement.flight;

import me.rhys.anticheat.base.check.api.Check;
import me.rhys.anticheat.base.check.api.CheckInformation;
import me.rhys.anticheat.base.event.PacketEvent;
import me.rhys.anticheat.base.user.User;
import me.rhys.anticheat.tinyprotocol.api.Packet;
import org.bukkit.Bukkit;

@CheckInformation(checkName = "Flight", checkType = "G", canPunish = false, description = "FastLadder")
public class FlightG extends Check {

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
                double maxYSpeed = 0.11760000228882461;

                if (!user.getBlockData().onGround && !user.getBlockData().lastOnGround) {
                    if (user.getBlockData().climbable && user.getBlockData().climbableTicks >= 20) {
                        if (deltaY > maxYSpeed && user.getCurrentLocation().getY() > user.getLastLocation().getY()) {
                            if (++threshold > 2) {
                                flag(user, "Moving abnormally fast up a ladder");
                            }
                        } else {
                            threshold -= Math.min(threshold, 0.025);
                        }
                    }
                }
            }
        }
    }
}