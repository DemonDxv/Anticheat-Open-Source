package me.rhys.anticheat.checks.combat.aimassist;

import me.rhys.anticheat.base.check.api.Check;
import me.rhys.anticheat.base.check.api.CheckInformation;
import me.rhys.anticheat.base.event.PacketEvent;
import me.rhys.anticheat.base.user.User;
import me.rhys.anticheat.tinyprotocol.api.Packet;
import me.rhys.anticheat.tinyprotocol.packet.in.WrappedInUseEntityPacket;

@CheckInformation(checkName = "AimAssist", checkType = "C", lagBack = false, punishmentVL = 10, canPunish = false)
public class AimAssistC extends Check {

    private double threshold;

    @Override
    public void onPacket(PacketEvent event) {
        User user = event.getUser();

        if (event.getType().equalsIgnoreCase(Packet.Client.USE_ENTITY)) {
            WrappedInUseEntityPacket useEntityPacket = new WrappedInUseEntityPacket(event.getPacket(), user.getPlayer());

            if (useEntityPacket.getAction() == WrappedInUseEntityPacket.EnumEntityUseAction.ATTACK) {

                double pitch = Math.abs(user.getCurrentLocation().getPitch() - user.getLastLocation().getPitch());
                double yaw = Math.abs(user.getCurrentLocation().getYaw() - user.getLastLocation().getYaw());

                if (pitch > 4.0 && yaw < 0.0001D) {
                    if (threshold++ > 9) {
                        flag(user, "Low Yaw Change");
                    }
                } else {
                    threshold -= Math.min(threshold, 0.25);
                }
            }
        }
    }
}
