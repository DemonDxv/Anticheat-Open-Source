package me.rhys.anticheat.checks.combat.aimassist;

import me.rhys.anticheat.base.check.api.Check;
import me.rhys.anticheat.base.check.api.CheckInformation;
import me.rhys.anticheat.base.event.PacketEvent;
import me.rhys.anticheat.base.user.User;
import me.rhys.anticheat.tinyprotocol.api.Packet;
import me.rhys.anticheat.tinyprotocol.packet.in.WrappedInUseEntityPacket;

@CheckInformation(checkName = "AimAssist", lagBack = false, punishmentVL = 15)
public class AimAssistA extends Check {

    private double threshold;

    @Override
    public void onPacket(PacketEvent event) {
        User user = event.getUser();

        if (event.getType().equalsIgnoreCase(Packet.Client.USE_ENTITY)) {
            WrappedInUseEntityPacket useEntityPacket = new WrappedInUseEntityPacket(event.getPacket(), user.getPlayer());

            if (useEntityPacket.getAction() == WrappedInUseEntityPacket.EnumEntityUseAction.ATTACK) {

                double pitch = Math.abs(user.getCurrentLocation().getPitch() - user.getLastLocation().getPitch());

                if (pitch % 0.5 == 0.0 && pitch % 1.5f != 0.0) {
                    if (threshold++ > 7) {
                        flag(user, "Rounding Rotations");
                    }
                } else {
                    threshold -= Math.min(threshold, 0.125);
                }
            }
        }
    }
}
