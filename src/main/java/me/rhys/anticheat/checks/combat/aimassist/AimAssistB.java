package me.rhys.anticheat.checks.combat.aimassist;

import me.rhys.anticheat.base.check.api.Check;
import me.rhys.anticheat.base.check.api.CheckInformation;
import me.rhys.anticheat.base.event.PacketEvent;
import me.rhys.anticheat.base.user.User;
import me.rhys.anticheat.tinyprotocol.api.Packet;
import me.rhys.anticheat.tinyprotocol.packet.in.WrappedInUseEntityPacket;

@CheckInformation(checkName = "AimAssist", checkType = "B", lagBack = false, punishmentVL = 10)
public class AimAssistB extends Check {

    private double threshold;

    @Override
    public void onPacket(PacketEvent event) {
        User user = event.getUser();

        if (event.getType().equalsIgnoreCase(Packet.Client.USE_ENTITY)) {
            WrappedInUseEntityPacket useEntityPacket = new WrappedInUseEntityPacket(event.getPacket(), user.getPlayer());

            if (useEntityPacket.getAction() == WrappedInUseEntityPacket.EnumEntityUseAction.ATTACK) {

                double pitch = Math.abs(user.getCurrentLocation().getPitch() - user.getLastLocation().getPitch());
                double yaw = Math.abs(user.getCurrentLocation().getYaw() - user.getLastLocation().getYaw());

                if (pitch < 0.0001D && yaw > 3.0F) {
                    if (threshold++ > 3) {
                        flag(user, "Low Pitch Change");
                    }
                } else {
                    threshold -= Math.min(threshold, 0.25);
                }
            }
        }
    }
}
