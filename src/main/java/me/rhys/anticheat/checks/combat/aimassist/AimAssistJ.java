package me.rhys.anticheat.checks.combat.aimassist;

import me.rhys.anticheat.base.check.api.Check;
import me.rhys.anticheat.base.check.api.CheckInformation;
import me.rhys.anticheat.base.event.PacketEvent;
import me.rhys.anticheat.base.user.User;
import me.rhys.anticheat.tinyprotocol.api.Packet;
import me.rhys.anticheat.tinyprotocol.packet.in.WrappedInUseEntityPacket;
import me.rhys.anticheat.util.MathUtil;

@CheckInformation(checkName = "AimAssist", checkType = "J", canPunish = false, punishmentVL = 10)
public class AimAssistJ extends Check {

    private double threshold;
    private double lastDeltaYaw;

    @Override
    public void onPacket(PacketEvent event) {
        User user = event.getUser();

        switch (event.getType()) {

            case Packet.Client.USE_ENTITY: {

                WrappedInUseEntityPacket useEntityPacket = new WrappedInUseEntityPacket(event.getPacket(), user.getPlayer());

                if (useEntityPacket.getAction() == WrappedInUseEntityPacket.EnumEntityUseAction.ATTACK) {

                    if (user.getTick() < 60 || user.shouldCancel()) {
                        return;
                    }

                    double yaw = user.getMovementProcessor().getYawDeltaClamped();

                    double difference = Math.abs(yaw - lastDeltaYaw);

                    if (difference == 0 && yaw > 1.0 && lastDeltaYaw > 1.0) {
                        if (threshold++ > 6) {
                            flag(user, "Invalid yaw speed");
                        }
                    } else {
                        threshold -= Math.min(threshold, 0.025);
                    }


                    lastDeltaYaw = yaw;
                }
                break;
            }
        }
    }
}
