package me.rhys.anticheat.checks.movement.speed;

import me.rhys.anticheat.base.check.api.Check;
import me.rhys.anticheat.base.check.api.CheckInformation;
import me.rhys.anticheat.base.event.PacketEvent;
import me.rhys.anticheat.base.user.User;
import me.rhys.anticheat.tinyprotocol.api.Packet;
import me.rhys.anticheat.util.MathUtil;
import org.bukkit.Bukkit;

@CheckInformation(checkName = "Speed", punishmentVL = 8)
public class SpeedA extends Check {

    private double threshold;

    @Override
    public void onPacket(PacketEvent event) {
        User user = event.getUser();

        switch (event.getType()) {
            case Packet.Client.FLYING:
            case Packet.Client.LOOK:
            case Packet.Client.POSITION_LOOK:
            case Packet.Client.POSITION: {

                if (user.getTick() < 60
                        || user.getVehicleTicks() > 0
                        || user.shouldCancel()
                        || user.getLastTeleportTimer().hasNotPassed(5 + user.getConnectionProcessor().getClientTick())
                        || user.getMovementProcessor().isBouncedOnSlime()
                        || user.getActionProcessor().getRespawnTimer().hasNotPassed(20)
                        || user.getPlayer().isDead()
                        || user.getPlayer().getWalkSpeed() != 0.2F
                        || user.getActionProcessor().getServerPositionTimer().hasNotPassed(5)
                        || !user.isChunkLoaded()
                        || user.getElytraProcessor().isUsingElytra()) {
                    return;
                }


                double deltaXZ = user.getMovementProcessor().getDeltaXZ();

                double lastDeltaXZ = user.getMovementProcessor().getLastDeltaXZ();

                double prediction = lastDeltaXZ * 0.91F;

                prediction += MathUtil.movingFlyingV3(user);

                if (user.getActionProcessor().getVelocityTimer().hasNotPassed(10
                        + user.getConnectionProcessor().getClientTick())) {
                    prediction += user.getCombatProcessor().getVelocityH();
                }

                if (user.getLastBlockPlaceTimer().hasNotPassed(10 + user.getConnectionProcessor().getClientTick())) {
                    prediction += 0.1f;
                }

                if (user.getBlockData().waterTicks > 0 && user.getBlockData().nearWater) {
                    prediction += 0.1F;
                }

                double thresholdRemoval = 0.001;

                if (user.getBlockData().collidesHorizontal) {
                    thresholdRemoval = 0.02f;
                }


                double totalSpeed = deltaXZ - prediction;

                if (!user.getMovementProcessor().isOnGround() && !user.getMovementProcessor().isLastGround()) {

                    if (totalSpeed > 0.001 && deltaXZ > 0.2) {

                        if (threshold++ > 1) {
                            flag(user, "Modifying air speed");
                        }
                    } else {
                        threshold -= Math.min(threshold, thresholdRemoval);
                    }
                }


                break;
            }
        }
    }
}