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

    @Override
    public void onPacket(PacketEvent event) {
        User user = event.getUser();

        switch (event.getType()) {
            case Packet.Client.FLYING:
            case Packet.Client.LOOK:
            case Packet.Client.POSITION_LOOK:
            case Packet.Client.POSITION: {
                //    Bukkit.broadcastMessage(""+user.getBlockData().nearWater);

                if (user.shouldCancel()
                        || user.getActionProcessor().getServerPositionTimer().hasNotPassed(5)
                        || user.getLastTeleportTimer().hasNotPassed(20)
                        || user.getMovementProcessor().isBouncedOnSlime()
                        || user.getVehicleTicks() > 0
                        || EntityUtil.isOnBoat(user)
                        || user.getBlockData().webTicks > 0
                        || user.getBlockData().cakeTicks > 0
                        || user.getBlockData().climbableTicks > 0
                        || user.getBlockData().stairTicks > 0
                        || user.getBlockData().slabTicks > 0
                        || user.getBlockData().underBlockTicks > 0
                        || user.getBlockData().waterTicks > 0
                        || user.getBlockData().lavaTicks > 0
                        || user.getCombatProcessor().getVelocityTicks() <= 20
                        || user.getTick() < 60) {
                    threshold = 0;
                    return;
                }

                double deltaY = user.getMovementProcessor().getDeltaY();

                double lastDeltaY = user.getMovementProcessor().getLastDeltaY();


                double gravity = 0.9800000190734863D;
                double fallMotion = 0.08D;

                double prediction = (lastDeltaY - fallMotion) * gravity;

                if (!user.getMovementProcessor().isOnGround()
                        && user.getMovementProcessor().isLastGround() && deltaY > 0.0) {
                    prediction = 0.42F;

                }

                double totalUp = Math.abs(deltaY - prediction);

                double max = 0.005;

                if (!user.getMovementProcessor().isOnGround() && !user.getMovementProcessor().isLastGround()) {
                    if (totalUp > max && Math.abs(prediction) > max) {
                        if (threshold++ > 1) {
                            flag(user, "Invalid motion prediction");
                        }
                    } else {
                        threshold -= Math.min(threshold, 0.001f);
                    }
                }
            }
        }
    }
}