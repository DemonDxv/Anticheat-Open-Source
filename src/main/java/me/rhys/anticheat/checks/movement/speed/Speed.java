package me.rhys.anticheat.checks.movement.speed;

import me.rhys.anticheat.base.check.api.Check;
import me.rhys.anticheat.base.check.api.CheckInformation;
import me.rhys.anticheat.base.event.PacketEvent;
import me.rhys.anticheat.base.user.User;
import me.rhys.anticheat.tinyprotocol.api.Packet;
import org.bukkit.Bukkit;

@CheckInformation(checkName = "Speed", punishmentVL = 8, description = "Detecting if the players MotionXZ matched with the predicted calculated speed.")
public class Speed extends Check {

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
                        || user.getLastTeleportTimer().hasNotPassed(20)
                        || user.getMovementProcessor().isBouncedOnSlime()
                        || user.getActionProcessor().getServerPositionTimer().hasNotPassed(5)
                        || user.getElytraProcessor().isUsingElytra()) {
                    return;
                }

                double motionXZ = user.getPredictionProcessor().getMotionXZ();

                double deltaXZ = user.getMovementProcessor().getDeltaXZ();

                if (motionXZ > 0.001) {
                    if (deltaXZ > 0.2) {
                        flag(user, "MotionXZ -> "+motionXZ);
                    }
                }

                break;
            }
        }
    }
}