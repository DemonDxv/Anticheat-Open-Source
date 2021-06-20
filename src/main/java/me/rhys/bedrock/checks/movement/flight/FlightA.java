package me.rhys.bedrock.checks.movement.flight;

import me.rhys.bedrock.base.check.api.Check;
import me.rhys.bedrock.base.check.api.CheckInformation;
import me.rhys.bedrock.base.event.PacketEvent;
import me.rhys.bedrock.base.user.User;
import me.rhys.bedrock.tinyprotocol.api.Packet;
import org.bukkit.Bukkit;

@CheckInformation(checkName = "Flight", lagBack = true, description = "Checks if the players predicted y delta")
public class FlightA extends Check {

    private double motionChange, motionMultiply;

    @Override
    public void onPacket(PacketEvent event) {
        User user = event.getUser();

        switch (event.getType()) {
            case Packet.Client.FLYING:
            case Packet.Client.LOOK:
            case Packet.Client.POSITION_LOOK:
            case Packet.Client.POSITION: {

                if (user.shouldCancel()
                        || user.getBlockData().insideBlock
                        || user.getActionProcessor().getServerPositionTimer().hasNotPassed(3)
                        || user.getLastTeleportTimer().hasNotPassed(20)
                        || user.getCombatProcessor().getVelocityTicks() <= 20
                        || checkConditions(user)) {
                    return;
                }

                double deltaY = user.getMovementProcessor().getDeltaY();

                double lastDeltaY = user.getMovementProcessor().getLastDeltaY();

                if (user.getBlockData().nearLiquid) {
                    motionChange = 0.02D;
                    motionMultiply = 0.800000011920929D;

                } else {
                    motionChange = 0.08D;
                    motionMultiply = 0.9800000190734863D;
                }

                double prediction = (lastDeltaY - motionChange) * motionMultiply;

                if (deltaY >= 0.42f) {
                    prediction += 0.7F;
                }


                double difference = deltaY - prediction;

                if (!user.getCurrentLocation().isClientGround()
                        && !user.getLastLocation().isClientGround()
                        && !user.getLastLastLocation().isClientGround()) {

                    if (difference > 0.005) {
                        flag(user, ""+difference + " "+deltaY + " "+lastDeltaY + " "+prediction);
                    }
                }

            }
        }
    }
    boolean checkConditions(User user) {
        return user.getBlockData().liquidTicks > 0
                || user.getTick() < 60
                || user.shouldCancel()
                || user.getBlockData().climbableTicks > 0
                || user.getBlockData().climbableTimer.hasNotPassed();
    }
}
