package me.rhys.anticheat.checks.movement.strafe;

import me.rhys.anticheat.base.check.api.Check;
import me.rhys.anticheat.base.check.api.CheckInformation;
import me.rhys.anticheat.base.event.PacketEvent;
import me.rhys.anticheat.base.user.User;
import me.rhys.anticheat.tinyprotocol.api.Packet;
import me.rhys.anticheat.util.MathUtil;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@CheckInformation(checkName = "Strafe", punishmentVL = 25, description = "Detects if player is strafing in the air")
public class Strafe extends Check {

    private static final Set<Integer> DIRECTIONS = new HashSet<>(Arrays.asList(45, 90, 135, 180));
    private double threshold;

    @Override
    public void onPacket(PacketEvent event) {
        User user = event.getUser();

        switch (event.getType()) {
            case Packet.Client.FLYING:
            case Packet.Client.LOOK:
            case Packet.Client.POSITION_LOOK:
            case Packet.Client.POSITION: {

                if (user.shouldCancel() || user.getTick() < 60) {
                    return;
                }

                if (user.getLastLocation() != null && user.getLastLastLocation() != null) {
                    float moveAngle = MathUtil.getMoveAngle(user.getLastLastLocation(), user.getLastLocation());

                    double deltaXZ = user.getMovementProcessor().getDeltaXZ(),
                            lastDeltaXZ = user.getMovementProcessor().getLastDeltaXZ();

                    if (deltaXZ > 0.01 && lastDeltaXZ > 0.01 && !user.getMovementProcessor().isOnGround()) {
                        if (DIRECTIONS.stream().anyMatch(direction -> Math.abs(direction - moveAngle) < 0.0001F)) {
                            if (threshold++ > 5) {
                                flag(user, "Strafing in the air");
                            }
                        } else {
                            threshold -= Math.min(threshold, 0.005f);
                        }
                    }
                }
            }
        }
    }
}