package me.rhys.anticheat.checks.movement.speed;

import me.rhys.anticheat.base.check.api.Check;
import me.rhys.anticheat.base.check.api.CheckInformation;
import me.rhys.anticheat.base.event.PacketEvent;
import me.rhys.anticheat.base.user.User;
import me.rhys.anticheat.tinyprotocol.api.Packet;
import org.bukkit.Bukkit;

@CheckInformation(checkName = "Speed", punishmentVL = 8, description = "Detecting if the players MotionXZ matched with the predicted calculated speed.")
public class Speed extends Check {

    private double threshold, thresholdg;

    @Override
    public void onPacket(PacketEvent event) {
        User user = event.getUser();

        switch (event.getType()) {
            case Packet.Client.FLYING:
            case Packet.Client.LOOK:
            case Packet.Client.POSITION_LOOK:
            case Packet.Client.POSITION: {

                if (user.getBlockData().liquidTicks > 0
                        || user.getTick() < 60
                        || user.getVehicleTicks() > 0
                        || user.shouldCancel()
                        || user.getLastTeleportTimer().hasNotPassed(5 + user.getConnectionProcessor().getClientTick())
                        || user.getMovementProcessor().isBouncedOnSlime()
                        || user.getMovementProcessor().getRespawnTimer().hasNotPassed(20)
                        || user.getPlayer().isDead()
                        || user.getActionProcessor().getServerPositionTimer().hasNotPassed(5)
                        || user.getElytraProcessor().isUsingElytra()) {
                    return;
                }

                double motionXZ = user.getPredictionProcessor().getMotionXZ();

                double deltaXZ = user.getMovementProcessor().getDeltaXZ();

                double maxMotionXZ = 0.005;

                if (user.getCombatProcessor().getUseEntityTimer().hasNotPassed(20)) {
                    maxMotionXZ = 0.01019F;
                }

                if (deltaXZ > 0.3 && user.getMovementProcessor().getLastDeltaXZ() < 0.3) {
                    maxMotionXZ = 0.01019F;
                }

                if (!user.getMovementProcessor().isLastLastGround()) {
                    maxMotionXZ = 0.01019F;
                }

                if (deltaXZ > 0.25) {
                    if (motionXZ > maxMotionXZ) {
                        flag(user, "Speeding", ""+motionXZ);
                    }
                }

             /*  if (user.getMovementProcessor().isOnGround()
                        || user.getMovementProcessor().isLastGround()
                        || user.getMovementProcessor().isLastLastGround()) {
                    if (deltaXZ > 0.25) {
                        if (motionXZ > 0.005) {
                            if (thresholdg++ > 6) {
                                flag(user, "Speeding on ground", "[A]");
                            }
                        } else {
                            thresholdg -= Math.min(thresholdg, 0.012f);
                        }

                        if (motionXZ > 0.0102) {
                            flag(user, "Speed on ground", "[B]");
                        }
                    }
                }

                if (!user.getMovementProcessor().isOnGround() || !user.getMovementProcessor().isLastGround()
                        || !user.getMovementProcessor().isLastLastGround() ) {
                    if (motionXZ > 0.1 && deltaXZ > 0.01) {
                        flag(user, "Speeding in air", "[A]");
                    }

                    if (motionXZ > 0.005 && deltaXZ > 0.01) {
                        if (threshold++ > 6) {
                            flag(user, "Speeding in air", "[B]");
                        }
                    } else {
                        threshold -= Math.min(threshold, 0.03f);
                    }
                }
*/

                break;
            }
        }
    }
}