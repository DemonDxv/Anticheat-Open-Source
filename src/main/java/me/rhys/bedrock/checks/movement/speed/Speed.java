package me.rhys.bedrock.checks.movement.speed;

import me.rhys.bedrock.base.check.api.Check;
import me.rhys.bedrock.base.check.api.CheckInformation;
import me.rhys.bedrock.base.event.PacketEvent;
import me.rhys.bedrock.base.user.User;
import me.rhys.bedrock.tinyprotocol.api.Packet;
import me.rhys.bedrock.util.MathUtil;
import org.bukkit.Bukkit;

@CheckInformation(checkName = "Speed", lagBack = true, description = "Detecting if the players MotionXZ matched with the predicted calculated speed.")
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
                        || user.shouldCancel()
                        || user.getLastTeleportTimer().hasNotPassed(20)
                        || user.getMovementProcessor().isBouncedOnSlime()
                        || user.getActionProcessor().getServerPositionTimer().hasNotPassed(3)
                        || user.getElytraProcessor().isUsingElytra()) {
                    return;
                }

                double motionXZ = user.getPredictionProcessor().getMotionXZ();

                double deltaXZ = user.getMovementProcessor().getDeltaXZ();

                if (motionXZ > 0.005) {
                    if (deltaXZ > 0.2) {
                        flag(user, "MotionXZ -> "+motionXZ);
                    }
                }

                break;
            }
        }
    }
}