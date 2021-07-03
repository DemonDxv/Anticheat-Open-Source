package me.rhys.anticheat.checks.combat.velocity;

import me.rhys.anticheat.base.check.api.Check;
import me.rhys.anticheat.base.check.api.CheckInformation;
import me.rhys.anticheat.base.event.PacketEvent;
import me.rhys.anticheat.base.user.User;
import me.rhys.anticheat.tinyprotocol.api.Packet;
import me.rhys.anticheat.util.MathUtil;
import org.bukkit.Bukkit;

@CheckInformation(checkName = "Velocity",  checkType = "D", lagBack = false, description = "99% Horizontal Velocity")
public class VelocityD extends Check {

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
                        || !user.isChunkLoaded()) {
                    threshold = 0;
                    return;
                }

                /**
                 * TODO: Need to account for if the player is near a wall to fix false flags.
                 **/

                if (user.getCombatProcessor().getVelocityTicks() == 2) {
                    double deltaXZ = user.getMovementProcessor().getDeltaXZ();

                    double velocityH = user.getCombatProcessor().getVelocityH();

                    velocityH -= MathUtil.movingFlyingV3(user);

                    double totalVelocity = deltaXZ / velocityH;

                    if (totalVelocity <= 0.99
                            && !user.getMovementProcessor().isOnGround()
                            && user.getMovementProcessor().isLastGround()) {
                        if (threshold++ > 1) {
                            flag(user, "Horizontal Velocity: "+totalVelocity);
                        }
                    } else {
                        threshold -= Math.min(threshold, 0.09f);
                    }

                }

                break;
            }
        }
    }
}
