package me.rhys.anticheat.checks.combat.velocity;

import me.rhys.anticheat.base.check.api.Check;
import me.rhys.anticheat.base.check.api.CheckInformation;
import me.rhys.anticheat.base.event.PacketEvent;
import me.rhys.anticheat.base.user.User;
import me.rhys.anticheat.tinyprotocol.api.Packet;
import me.rhys.anticheat.util.MathUtil;
import org.bukkit.Bukkit;

@CheckInformation(checkName = "Velocity",  checkType = "J", canPunish = false, description = "More Horizontal Velocity")
public class VelocityJ extends Check {

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
                        || user.getVehicleTicks() > 0
                        || user.getBlockData().collidesHorizontal
                        || user.getBlockData().underBlockTicks > 0
                        || !user.isChunkLoaded()) {
                    threshold = 0;
                    return;
                }

                double deltaXZ = user.getMovementProcessor().getDeltaXZ();

                double velocityH = user.getCombatProcessor().getVelocityH();

                velocityH -= MathUtil.movingFlyingV3(user);

                double totalVelocity = deltaXZ / velocityH;

                if (user.getCombatProcessor().getVelocityTicks() <= 5) {
                    if (totalVelocity > 1.4) {
                        if ((threshold += 0.95) > 5) {
                            flag(user, "More KB", "v-"+totalVelocity);
                        }
                    } else {
                        threshold -= Math.min(threshold, 0.75);
                    }
                }

                break;
            }
        }
    }
}
