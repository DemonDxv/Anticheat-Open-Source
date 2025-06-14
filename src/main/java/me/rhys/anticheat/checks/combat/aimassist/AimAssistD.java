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

    private final double offset = Math.pow(2.0, 24.0);
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

                long gcd = MathUtil.gcd((long) (pitchDifference * offset), (long) (lastPitchDifference * offset));


                if (user.getCombatProcessor().getCancelTicks() > 0) {
                    threshold = 0;
                    return;
                }

                if (yawDifference > 2.0F && yawAccel > 1.0F && pitchAccel > 0.0F && pitchDifference > 0.009f) {

                    if (gcd < 131072L && pitchAccel < 6.5) {
                        threshold += 0.89;

                        if (threshold > 12.5) {
                            flag(user, "gcd=" + gcd, "accel=" + pitchAccel);
                        }
                    } else {
                        threshold -= Math.min(threshold, 0.25);
                    }
                }

                lastYawDifference = yawDifference;
                lastPitchDifference = pitchDifference;
            }
        }
    }
}
