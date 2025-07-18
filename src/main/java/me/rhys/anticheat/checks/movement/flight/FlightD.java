package me.rhys.anticheat.checks.movement.flight;

import me.rhys.anticheat.base.check.api.Check;
import me.rhys.anticheat.base.check.api.CheckInformation;
import me.rhys.anticheat.base.event.PacketEvent;
import me.rhys.anticheat.base.user.User;
import me.rhys.anticheat.tinyprotocol.api.Packet;
import me.rhys.anticheat.util.EntityUtil;

@CheckInformation(checkName = "Flight", checkType = "D", punishmentVL = 15, description = "Invalid deltay movements")
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
                        || EntityUtil.isOnBoat(user)
                        || user.getLastBlockPlaceTimer().hasNotPassed(3)
                        || user.getLastBlockPlaceCancelTimer().hasNotPassed(3)
                        || user.getBlockData().snowTicks > 0
                        || user.getBlockData().skullTicks > 0
                        || user.getBlockData().stairSlabTimer.hasNotPassed(20)
                        || user.getBlockData().webTicks > 0
                        || user.getBlockData().cakeTicks > 0
                        || user.getBlockData().door
                        || !user.isChunkLoaded()
                        || user.getBlockData().climbableTicks > 0
                        || user.getBlockData().underBlockTicks > 0
                        || user.getActionProcessor().getVelocityTimer().hasNotPassed(10)
                        && user.getLastFallDamageTimer().passed(20)
                        || user.getBlockData().lavaTicks > 0
                        || user.getTick() < 60) {
                    return;
                }

                double deltaY = user.getMovementProcessor().getDeltaY();
                double lastDeltaY = user.getMovementProcessor().getLastDeltaY();

                if (deltaY > 0.0 && lastDeltaY <= 0.0) {
                    if (++threshold > 3.5) {
                        flag(user, "Invalid MotionY movements");
                        threshold = 3.5;
                    }
                } else {
                    threshold -= Math.min(threshold, 0.125);
                }

                break;
            }
        }
    }
}