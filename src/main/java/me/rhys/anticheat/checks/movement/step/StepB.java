package me.rhys.anticheat.checks.movement.step;

import me.rhys.anticheat.base.check.api.Check;
import me.rhys.anticheat.base.check.api.CheckInformation;
import me.rhys.anticheat.base.event.PacketEvent;
import me.rhys.anticheat.base.user.User;
import me.rhys.anticheat.tinyprotocol.api.Packet;
import org.bukkit.Bukkit;

@CheckInformation(checkName = "Step", description = "Checks if player goes up blocks higher than legit", canPunish = false)
public class StepB extends Check {

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
                        || user.getBlockData().snowTicks > 0
                        || user.getBlockData().piston
                        || user.getCombatProcessor().getVelocityTicks() <= 20
                        || user.getLastTeleportTimer().hasNotPassed(20)
                        || user.getMovementProcessor().isBouncedOnSlime()
                        || user.getActionProcessor().getServerPositionTimer().hasNotPassed(3)
                        || user.getElytraProcessor().isUsingElytra()) {
                    return;
                }


                boolean ground = user.getMovementProcessor().isOnGround(),
                        lGround = user.getMovementProcessor().isLastGround();

                double maxPrediction = 0.0;
                double deltaY = user.getMovementProcessor().getDeltaY();

                if (!ground && lGround) {
                    maxPrediction = 0.42F;
                }

            }
        }
    }
}