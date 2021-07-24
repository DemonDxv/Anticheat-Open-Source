package me.rhys.anticheat.checks.combat.aimassist;

import me.rhys.anticheat.base.check.api.Check;
import me.rhys.anticheat.base.check.api.CheckInformation;
import me.rhys.anticheat.base.event.PacketEvent;
import me.rhys.anticheat.base.user.User;
import me.rhys.anticheat.tinyprotocol.api.Packet;
import me.rhys.anticheat.tinyprotocol.packet.in.WrappedInUseEntityPacket;
import me.rhys.anticheat.util.MathUtil;
import org.bukkit.Bukkit;

@CheckInformation(checkName = "AimAssist", checkType = "D", lagBack = false, punishmentVL = 25, canPunish = false)
public class AimAssistD extends Check {

    private double threshold;

    private float lastPitchDifference;
    private float lastYawDifference;

    @Override
    public void onPacket(PacketEvent event) {
        User user = event.getUser();

        if (event.getType().equalsIgnoreCase(Packet.Client.USE_ENTITY)) {
            WrappedInUseEntityPacket useEntityPacket = new WrappedInUseEntityPacket(event.getPacket(), user.getPlayer());

            if (useEntityPacket.getAction() == WrappedInUseEntityPacket.EnumEntityUseAction.ATTACK) {

                float pitchDifference = Math.abs(user.getCurrentLocation().getPitch()
                        - user.getLastLocation().getPitch());

                float yawDifference = Math.abs(user.getCurrentLocation().getYaw()
                        - user.getLastLocation().getYaw());

                float yawAccel = Math.abs(pitchDifference - lastPitchDifference);
                float pitchAccel = Math.abs(yawDifference - lastYawDifference);

                long gcd = MathUtil.gcd((long) pitchDifference, (long) lastPitchDifference);

                if (yawDifference > 2.0 && yawAccel > 2.0F && pitchAccel > 2.0F && pitchDifference > 0.009f) {
                    if (gcd < 131072L && gcd > 25) {
                        if (threshold++ > 4) {
                            flag(user, "GCD");
                        }
                    } else {
                        threshold -= Math.min(threshold, 0.6);
                    }
                } else {
                    threshold -= Math.min(threshold, 0.25);
                }

                lastYawDifference = yawDifference;
                lastPitchDifference = pitchDifference;
            }
        }
    }
}
