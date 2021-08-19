package me.rhys.anticheat.checks.movement.step;

import me.rhys.anticheat.base.check.api.Check;
import me.rhys.anticheat.base.check.api.CheckInformation;
import me.rhys.anticheat.base.event.PacketEvent;
import me.rhys.anticheat.base.user.User;
import me.rhys.anticheat.tinyprotocol.api.Packet;
import org.bukkit.Bukkit;

@CheckInformation(checkName = "Step", checkType = "B", description = "Checks if player goes up blocks non-legitimate", canPunish = false)
public class StepB extends Check {

    private double threshold;

    @Override
    public void onPacket(PacketEvent event) {
        User user = event.getUser();

        switch (event.getType()) {
            case Packet.Client.FLYING:
            case Packet.Client.LOOK:
            case Packet.Client.POSITION_LOOK:
            case Packet.Client.POSITION: {

                double deltaY = user.getMovementProcessor().getDeltaY();
                double lastDeltaY = user.getMovementProcessor().getLastDeltaY();

                double prediction = (lastDeltaY - 0.08D) * 0.98F;

                double difference = Math.abs(deltaY - prediction);

                if (user.getBlockData().collidesHorizontal && deltaY > 0.005) {
                    if (difference > 0.01) {
                        if (threshold++ > 3) {
                           /// flag(user, "Invalid motion y");
                        }
                    }
                } else {
                    threshold -= Math.min(threshold, 0.75);
                }
                break;
            }
        }
    }
}