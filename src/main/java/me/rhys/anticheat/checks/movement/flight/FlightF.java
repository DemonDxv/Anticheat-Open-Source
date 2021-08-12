package me.rhys.anticheat.checks.movement.flight;

import me.rhys.anticheat.base.check.api.Check;
import me.rhys.anticheat.base.check.api.CheckInformation;
import me.rhys.anticheat.base.event.PacketEvent;
import me.rhys.anticheat.base.user.User;
import me.rhys.anticheat.tinyprotocol.api.Packet;
import org.bukkit.Bukkit;

@CheckInformation(checkName = "Flight", checkType = "F", punishmentVL = 9, description = "Jump height check")
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
                        || user.getBlockData().lavaTicks > 0
                        || user.getBlockData().waterTicks > 0
                        || user.getBlockData().stairSlabTimer.hasNotPassed(20)
                        || user.getCombatProcessor().getVelocityTicks() <= (5
                        + user.getConnectionProcessor().getClientTick() + 5)) {
                    threshold = 0;
                    return;
                }


                double deltaY = user.getMovementProcessor().getDeltaY();

                if (deltaY > 0.0 && user.getMovementProcessor().getAirTicks() > 7) {
                    if (threshold++ > 5) {
                        flag(user, "Moving upwards abnormal");
                    }
                } else {
                    threshold -= Math.min(threshold, 0.025);
                }
            }
        }
    }
}