package me.rhys.anticheat.checks.movement.speed;

import me.rhys.anticheat.base.check.api.Check;
import me.rhys.anticheat.base.check.api.CheckInformation;
import me.rhys.anticheat.base.event.PacketEvent;
import me.rhys.anticheat.base.user.User;
import me.rhys.anticheat.tinyprotocol.api.Packet;

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
                        || user.getLastTeleportTimer().hasNotPassed(9)
                        || user.getMovementProcessor().isBouncedOnSlime()
                        || user.getActionProcessor().getRespawnTimer().hasNotPassed(20)
                        || user.getPlayer().isDead()
                        || user.getBlockData().waterTicks > 0
                        || user.getPlayer().getWalkSpeed() != 0.2F
                        || user.getActionProcessor().getServerPositionTimer().hasNotPassed(5)
                        || !user.isChunkLoaded()
                        || user.getElytraProcessor().isUsingElytra()) {
                    return;
                }


                double deltaXZ = user.getMovementProcessor().getDeltaXZ();

                double lastDeltaXZ = user.getMovementProcessor().getLastDeltaXZ();

                double prediction = lastDeltaXZ * 0.91F + 0.026F;

                if (user.getActionProcessor().getVelocityTimer().hasNotPassed(40)) {
                    prediction += (user.getCombatProcessor().getVelocityH() + 0.5);
                }

                double motionXZ = deltaXZ - prediction;

                if (!user.getMovementProcessor().isOnGround() && !user.getMovementProcessor().isLastGround()) {
                    if (motionXZ > 0.001 && deltaXZ > 0.22) {
                        if (++threshold > 3) {
                            flag(user, "Modifying air speed", "mxz="+motionXZ);
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