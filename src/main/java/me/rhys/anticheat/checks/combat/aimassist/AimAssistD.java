package me.rhys.anticheat.checks.combat.aimassist;

import me.rhys.anticheat.base.check.api.Check;
import me.rhys.anticheat.base.check.api.CheckInformation;
import me.rhys.anticheat.base.event.PacketEvent;
import me.rhys.anticheat.base.user.User;
import me.rhys.anticheat.tinyprotocol.api.Packet;
import me.rhys.anticheat.tinyprotocol.packet.in.WrappedInUseEntityPacket;
import me.rhys.anticheat.util.MathUtil;

@CheckInformation(checkName = "AimAssist", checkType = "D", lagBack = false, punishmentVL = 45, canPunish = false)
public class AimAssistD extends Check {

    private double threshold;

    private final double offset = Math.pow(2.0, 24.0);
    private double lastPitchDifference;

    @Override
    public void onPacket(PacketEvent event) {
        User user = event.getUser();

        if (event.getType().equalsIgnoreCase(Packet.Client.USE_ENTITY)) {
            WrappedInUseEntityPacket useEntityPacket = new WrappedInUseEntityPacket(event.getPacket(), user.getPlayer());

            if (useEntityPacket.getAction() == WrappedInUseEntityPacket.EnumEntityUseAction.ATTACK) {

                double pitchDifference = Math.abs(user.getCurrentLocation().getPitch() - user.getLastLocation().getPitch());

                if (pitchDifference > 0.0) {

                    /**
                     * TODO: fix bypasses for this as this is just simple quick accounting for OptiFine.
                     */
                    if (pitchDifference < 0.1) {
                        threshold -= Math.min(threshold, 2.2);
                    }

                    long gcd = MathUtil.gcd((long) (pitchDifference * offset), (long) (lastPitchDifference * offset));

                    if (gcd < 131072L) {
                        if (threshold++ > 11) {
                            flag(user, "Not following gcd");
                        }
                    } else {
                        threshold -= Math.min(threshold, 1.2);
                    }

                    lastPitchDifference = pitchDifference;
                }
            }
        }
    }
}
