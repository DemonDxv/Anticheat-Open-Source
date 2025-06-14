package me.rhys.anticheat.checks.movement.flight;

import me.rhys.anticheat.base.check.api.Check;
import me.rhys.anticheat.base.check.api.CheckInformation;
import me.rhys.anticheat.base.event.PacketEvent;
import me.rhys.anticheat.base.user.User;
import me.rhys.anticheat.tinyprotocol.api.Packet;
import me.rhys.anticheat.util.EntityUtil;
import org.bukkit.Bukkit;

@CheckInformation(checkName = "Flight", punishmentVL = 12, description = "Checks if the players predicted y delta")
public class FlightA extends Check {

    private double threshold;
    private boolean lastZeroThree = false;

    @Override
    public void onPacket(PacketEvent event) {
        User user = event.getUser();

        switch (event.getType()) {
            case Packet.Client.POSITION_LOOK:
            case Packet.Client.POSITION: {
                double deltaY = user.getMovementProcessor().getDeltaY();

                if (user.shouldCancel()
                        || user.getActionProcessor().getServerPositionTimer().hasNotPassed(40)
                        || user.getLastTeleportTimer().hasNotPassed(20)
                        || user.getMovementProcessor().isBouncedOnSlime()
                        || user.getVehicleTicks() > 0
                        || EntityUtil.isOnBoat(user)
                        || user.getLastBlockPlaceCancelTimer().hasNotPassed(3)
                        || user.getBlockData().webTicks > 0
                        || user.getBlockData().cakeTicks > 0
                        || user.getBlockData().climbableTicks > 0
                        || user.getBlockData().stairTicks > 0
                        || user.getBlockData().slabTicks > 0
                        || user.getLastBlockPlaceTimer().hasNotPassed(3)
                        || user.getBlockData().underBlockTicks > 0
                        || user.getBlockData().waterTicks > 0
                        || user.getBlockData().lavaTicks > 0
                        || user.getBlockData().door
                        || !user.isChunkLoaded()
                        || user.getMovementProcessor().getDeltaXZ() < 0.2
                        && user.getPotionProcessor().getJumpTicks() > 0
                        || user.getActionProcessor().getVelocityTimer().hasNotPassed(20)
                        || user.getLastFallDamageTimer().hasNotPassedNoPing(20)
                        || user.getTick() < 60) {
                    this.threshold -= Math.min(this.threshold, 0.025);
                    return;
                }

                double jumpHeight = 0.42F + (user.getPotionProcessor().getJumpAmplifier() * 0.1F);

                double lastDeltaY = user.getMovementProcessor().getLastDeltaY();
                double prediction = ((this.lastZeroThree ? jumpHeight : lastDeltaY) - 0.08D) * 0.9800000190734863D;

                if (user.getMovementProcessor().getAirTicks() == 1 && deltaY > 0.402 && deltaY < 0.407) {
                    this.lastZeroThree = true;
                } else {
                    this.lastZeroThree = false;
                }

                if (Math.abs(prediction) < 0.005D) {
                    prediction = 0.0D;
                }

                double offset = Math.abs(deltaY - prediction);

                if (!user.getMovementProcessor().isOnGround() && !user.getMovementProcessor().isLastGround()) {
                    if (offset > 1E-9) {
                        if (++this.threshold > 6) {
                            flag(user, "Invalid motion prediction", "t="+offset + " y="+deltaY + " p="+prediction);
                        }
                    } else {
                        this.threshold -= Math.min(this.threshold, 0.025);
                    }
                }
            }
        }
    }
}