package me.rhys.anticheat.checks.movement.flight;

import me.rhys.anticheat.base.check.api.Check;
import me.rhys.anticheat.base.check.api.CheckInformation;
import me.rhys.anticheat.base.event.PacketEvent;
import me.rhys.anticheat.base.user.User;
import me.rhys.anticheat.tinyprotocol.api.Packet;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

@CheckInformation(checkName = "Flight", checkType = "G", canPunish = false, description = "Checks if player is using yPort", punishmentVL = 75)
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
                        || user.getLastTeleportTimer().hasNotPassed(20)
                        || user.getCombatProcessor().getVelocityTicks() <= 20
                        || user.getVehicleTicks() > 0
                        || user.getLastBlockPlaceTimer().hasNotPassed(20)
                        || user.getLastBlockPlaceCancelTimer().hasNotPassed(20)
                        || user.getMovementProcessor().isBouncedOnSlime()
                        || checkConditions(user)) {
                    return;
                }

                double deltaY = user.getMovementProcessor().getDeltaY();
                double lastDeltaY = user.getMovementProcessor().getLastDeltaY();

                double predicted = (lastDeltaY - 0.08D) * 0.98F;

                double totalChange = Math.abs(predicted - deltaY);

                if (totalChange > 0.005 && !user.getMovementProcessor().isOnGround() && deltaY < 0.0) {
                    if (threshold++ > 2.1) {
                        flag(user, "Using YPort");
                    }
                } else {
                    threshold -= Math.min(threshold, 0.06);
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
