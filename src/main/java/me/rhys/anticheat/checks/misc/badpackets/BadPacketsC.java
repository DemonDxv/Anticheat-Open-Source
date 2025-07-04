package me.rhys.anticheat.checks.misc.badpackets;

import me.rhys.anticheat.base.check.api.Check;
import me.rhys.anticheat.base.check.api.CheckInformation;
import me.rhys.anticheat.base.event.PacketEvent;
import me.rhys.anticheat.base.user.User;
import me.rhys.anticheat.tinyprotocol.api.Packet;

@CheckInformation(checkName = "BadPackets", checkType = "C", punishmentVL = 2)
public class BadPacketsC extends Check {

    private double threshold;

    @Override
    public void onPacket(PacketEvent event) {
        switch (event.getType()) {
            case Packet.Client.FLYING:
            case Packet.Client.LOOK:
            case Packet.Client.POSITION_LOOK:
            case Packet.Client.POSITION: {

                User user = event.getUser();

                if (user.shouldCancel()
                        || user.getTick() < 60
                        || user.getLastTeleportTimer().hasNotPassed(20)
                        || user.getActionProcessor().getRespawnTimer().hasNotPassed(20)
                        || user.getBlockData().pistonTicks > 0
                        || user.getActionProcessor().getRespawnTimer().hasNotPassed(20)
                        || user.getPlayer().isDead()
                        || user.getGhostBlockProcessor().getGhostBlockTeleportTimer().hasNotPassed(20)
                        || user.getVehicleTimer().hasNotPassed(20)) {
                    threshold = 0;
                    return;
                }

                double deltaXZ = user.getMovementProcessor().getDeltaXZ();

                double maxSpeed = user.getMovementProcessor().getServerPositionSpeed();

                if (user.getActionProcessor().getServerPositionTimer().hasNotPassed(5)) {

                    double delta = Math.abs(deltaXZ - maxSpeed);

                    if (delta > 0.8) {
                        if (threshold++ > 3) {
                            flag(user, "Invalid teleport");
                        }
                    } else {
                        threshold -= Math.min(threshold, 0.002);
                    }
                } else {
                    threshold -= Math.min(threshold, 0.002);
                }
                break;
            }
        }
    }
}