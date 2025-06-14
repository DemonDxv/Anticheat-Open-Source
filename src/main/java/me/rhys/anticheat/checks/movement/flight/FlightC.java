package me.rhys.anticheat.checks.movement.flight;

import me.rhys.anticheat.base.check.api.Check;
import me.rhys.anticheat.base.check.api.CheckInformation;
import me.rhys.anticheat.base.event.PacketEvent;
import me.rhys.anticheat.base.user.User;
import me.rhys.anticheat.tinyprotocol.api.Packet;
import me.rhys.anticheat.util.EntityUtil;

@CheckInformation(checkName = "Flight", checkType = "C", punishmentVL = 120, description = "Checks if the player is on ground when its not possible")
public class FlightC extends Check {

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
                        || user.getLastTeleportTimer().hasNotPassed(3)
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
                        || user.getActionProcessor().getVelocityTimer().hasNotPassed(10)
                        && user.getLastFallDamageTimer().passed(20)
                        || user.getBlockData().lavaTicks > 0
                        || user.getTick() < 60) {
                     return;
                }

                double deltaY = user.getMovementProcessor().getDeltaY();

                boolean isGround = user.getMovementProcessor().isLastGround();

                if (!user.getMovementProcessor().isServerYGround()) {
                    if (isGround && (deltaY < 0 || deltaY >= 0.0)
                            && !user.getBlockData().onGround) {
                        if (threshold++ > 3) {
                            flag(user, "Spoofing ground");
                            user.getLastFlaggedFlightCTimer().reset();
                        }
                    } else {
                        threshold -= Math.min(threshold, 0.00001);
                    }
                }
            }
        }
    }
}