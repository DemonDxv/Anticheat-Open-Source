package me.rhys.anticheat.checks.movement.flight;

import me.rhys.anticheat.base.check.api.Check;
import me.rhys.anticheat.base.check.api.CheckInformation;
import me.rhys.anticheat.base.event.PacketEvent;
import me.rhys.anticheat.base.user.User;
import me.rhys.anticheat.tinyprotocol.api.Packet;
import org.bukkit.Bukkit;

@CheckInformation(checkName = "Flight", checkType = "E", punishmentVL = 12, description = "Jump height check")
public class FlightE extends Check {

    private double threshold;

    @Override
    public void onPacket(PacketEvent event) {
        User user = event.getUser();

        switch (event.getType()) {
            case Packet.Client.FLYING:
            case Packet.Client.LOOK:
            case Packet.Client.POSITION_LOOK:
            case Packet.Client.POSITION: {

                double deltaY = user.getMovementProcessor().getDeltaY();

                if (user.shouldCancel()
                        || user.getActionProcessor().getServerPositionTimer().hasNotPassed(3)
                        || user.getLastTeleportTimer().hasNotPassed(20)
                        || user.getLastBlockPlaceTimer().hasNotPassed(20) && deltaY > 0.0
                        || user.getLastBlockPlaceCancelTimer().hasNotPassed(20)
                        || user.getMovementProcessor().isBouncedOnSlime()
                        || user.getVehicleTicks() > 0
                        || user.getBlockData().webTicks > 0
                        || user.getTick() < 60
                        || user.getBlockData().cakeTicks > 0
                        || user.getBlockData().lavaTicks > 0
                        || user.getBlockData().skullTicks > 0
                        || user.getBlockData().waterTicks > 0
                        || user.getBlockData().stairSlabTimer.hasNotPassed(20)
                        || user.getBlockData().snowTicks > 0
                        || user.getBlockData().door
                        || user.getBlockData().underBlockTicks > 0
                        || user.getBlockData().collidesHorizontal
                        || user.getCombatProcessor().getVelocityTicks() <= (10
                        + user.getConnectionProcessor().getClientTick())
                        || user.getBlockData().waterTicks > 0) {
                    threshold = 0;
                    return;
                }

                double maxJumpHeight = 0.42F + (user.getPotionProcessor().getJumpAmplifier() * 0.1F);

                boolean isGround = user.getMovementProcessor().isOnGround(),
                        lastGround = user.getMovementProcessor().isLastGround();

                if (!isGround && lastGround && deltaY > 0.0 && (deltaY < 0.42f || deltaY > maxJumpHeight)) {
                    flag(user, "Invalid Jump Height", "dy: " + deltaY);
                }

            }
        }
    }
}