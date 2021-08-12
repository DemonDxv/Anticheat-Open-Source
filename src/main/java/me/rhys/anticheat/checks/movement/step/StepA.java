package me.rhys.anticheat.checks.movement.step;

import me.rhys.anticheat.base.check.api.Check;
import me.rhys.anticheat.base.check.api.CheckInformation;
import me.rhys.anticheat.base.event.PacketEvent;
import me.rhys.anticheat.base.user.User;
import me.rhys.anticheat.tinyprotocol.api.Packet;
import me.rhys.anticheat.util.EntityUtil;
import org.bukkit.Bukkit;

@CheckInformation(checkName = "Step", description = "Checks if player goes up blocks higher than legit", canPunish = false)
public class StepA extends Check {

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
                        || user.getBlockData().bedTicks > 0
                        || user.getBlockData().slabTicks > 0
                        || user.getBlockData().stairTicks > 0
                        || user.getBlockData().fenceTicks > 0
                        || user.getBlockData().skullTicks > 0
                        || user.getBlockData().snowTicks > 0
                        || user.getBlockData().cakeTicks > 0
                        || user.getBlockData().piston
                        || user.getBlockData().underBlock
                        || EntityUtil.isOnBoat(user)
                        || user.getVehicleTicks() > 0
                        || user.getLastFallDamageTimer().hasNotPassed(20)
                        || user.getCombatProcessor().getVelocityTicks() <= 20
                        || user.getLastTeleportTimer().hasNotPassed(20)
                        || user.getMovementProcessor().isBouncedOnSlime()
                        || user.getActionProcessor().getServerPositionTimer().hasNotPassed(3)
                        || user.getElytraProcessor().isUsingElytra()) {
                    return;
                }

                double deltaY = user.getMovementProcessor().getDeltaY();
                double lastDeltaY = user.getMovementProcessor().getLastDeltaY();

                boolean ground = user.getMovementProcessor().isOnGround();

                if (deltaY > 0.0 && lastDeltaY > 0.0 && ground) {
                    flag(user, "Going up blocks abnormal (1)");
                } else if (deltaY > 0.42f && lastDeltaY >= 0.0 && ground) {
                    flag(user, "Going up blocks abnormal (2)");
                }
            }
        }
    }
}
