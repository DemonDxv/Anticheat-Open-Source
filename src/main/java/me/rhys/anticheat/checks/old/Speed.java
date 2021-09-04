package me.rhys.anticheat.checks.old;

import me.rhys.anticheat.base.check.api.Check;
import me.rhys.anticheat.base.check.api.CheckInformation;
import me.rhys.anticheat.base.event.PacketEvent;
import me.rhys.anticheat.base.user.User;
import me.rhys.anticheat.tinyprotocol.api.Packet;
import org.bukkit.Bukkit;

@CheckInformation(checkName = "Speed", punishmentVL = 8, description = "Detecting if the players MotionXZ matched with the predicted calculated speed.")
public class Speed extends Check {

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
                        || user.getElytraProcessor().isUsingElytra()) {
                    return;
                }

                double motionXZ = user.getPredictionProcessor().getMotionXZ();

                double deltaXZ = user.getMovementProcessor().getDeltaXZ();

                if (motionXZ > 0.005 && deltaXZ > 0.2) {
                    if (threshold++ > 2) {
                        flag(user, "Invalid MotionXZ", ""+motionXZ);
                    }
                } else {
                    threshold -= Math.min(threshold, 0.001);
                }

                break;
            }
        }
    }
}