package me.rhys.anticheat.checks.movement.jesus;

import me.rhys.anticheat.base.check.api.Check;
import me.rhys.anticheat.base.check.api.CheckInformation;
import me.rhys.anticheat.base.event.PacketEvent;
import me.rhys.anticheat.base.user.User;
import me.rhys.anticheat.tinyprotocol.api.Packet;

@CheckInformation(checkName = "Jesus", checkType = "B", canPunish = false, description = "Checks if the player is speeding in water")
public class JesusB extends Check {

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
                        || user.getLastTeleportTimer().hasNotPassed(20)
                        || user.getVehicleTicks() > 0
                        || !user.isChunkLoaded()
                        || user.getActionProcessor().getServerPositionTimer().hasNotPassed(3)) {
                    threshold = 0;
                    return;
                }

                if (user.getBlockData().nearWater || user.getBlockData().nearLava) {
                    double deltaXZ = user.getMovementProcessor().getDeltaXZ();

                    double maxXZ = 0.36;

                    if (user.getActionProcessor().getVelocityTimer().hasNotPassed(20)) {
                        maxXZ += user.getCombatProcessor().getVelocityH();
                    }

                    if (deltaXZ > maxXZ) {
                        if (threshold++ > 4) {
                            flag(user, "Speeding in water");
                        }
                    } else {
                        threshold -= Math.min(threshold, 0.04f);
                    }
                }

                break;
            }
        }
    }
}