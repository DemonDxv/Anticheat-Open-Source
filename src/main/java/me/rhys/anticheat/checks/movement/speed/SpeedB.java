package me.rhys.anticheat.checks.movement.speed;

import me.rhys.anticheat.base.check.api.Check;
import me.rhys.anticheat.base.check.api.CheckInformation;
import me.rhys.anticheat.base.event.PacketEvent;
import me.rhys.anticheat.base.user.User;
import me.rhys.anticheat.tinyprotocol.api.Packet;
import me.rhys.anticheat.util.MathUtil;
import org.bukkit.Bukkit;

@CheckInformation(checkName = "Speed", checkType = "B", punishmentVL = 8)
public class SpeedB extends Check {

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
                        || !user.isChunkLoaded()
                        || user.getPlayer().getWalkSpeed() != 0.2F
                        || user.getActionProcessor().getServerPositionTimer().hasNotPassed(5)
                        || user.getElytraProcessor().isUsingElytra()) {
                    return;
                }


                double deltaXZ = user.getMovementProcessor().getDeltaXZ();

                double lastDeltaXZ = user.getMovementProcessor().getLastDeltaXZ();

                double prediction = lastDeltaXZ * user.getPredictionProcessor().getBlockFriction();

                double deltaY = user.getMovementProcessor().getDeltaY();

                if (!user.getMovementProcessor().isOnGround()
                        && user.getMovementProcessor().isLastGround()
                        && deltaY > 0.0) {
                    prediction += 0.2F;
                }

                if (user.getActionProcessor().getVelocityTimer().hasNotPassed(10
                        + user.getConnectionProcessor().getClientTick())) {
                    prediction += user.getCombatProcessor().getVelocityHNoTrans();
                }

                if (user.getCombatProcessor().getUseEntityTimer().hasNotPassed(20)) {
                    prediction += 0.0101f;
                }

                if (user.getBlockData().pistonTicks > 0) {
                    prediction += 0.5;
                }

                prediction += MathUtil.movingFlyingV3(user, false);

                double totalSpeed = deltaXZ - prediction;

                if (user.getMovementProcessor().isOnGround() || user.getMovementProcessor().isLastGround()) {

                    if (totalSpeed > 0.005 && deltaXZ > 0.22) {

                        if (++threshold > 3) {
                            flag(user, "Modifying ground speed", "mxz="+totalSpeed);
                        }
                    } else {
                        threshold -= Math.min(threshold, 0.0009);
                    }
                }


                break;
            }
        }
    }
}