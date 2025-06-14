package me.rhys.anticheat.checks.combat.aimassist;

import me.rhys.anticheat.base.check.api.Check;
import me.rhys.anticheat.base.check.api.CheckInformation;
import me.rhys.anticheat.base.event.PacketEvent;
import me.rhys.anticheat.base.user.User;
import me.rhys.anticheat.tinyprotocol.api.Packet;

@CheckInformation(checkName = "AimAssist", checkType = "F", lagBack = false, punishmentVL = 25, canPunish = false)
public class AimAssistF extends Check {

    /**
     * Patch for tick auras (pitch).
     */

    private double threshold;
    private int pitchChangeTick, lastPitchDifferenceTick;

    @Override
    public void onPacket(PacketEvent event) {
        User user = event.getUser();

        switch (event.getType()) {

            case Packet.Client.FLYING:
            case Packet.Client.LOOK:
            case Packet.Client.POSITION_LOOK:
            case Packet.Client.POSITION: {

                if (user.getCombatProcessor().getUseEntityTimer().hasNotPassed(2)) {
                    double deltaPitch = Math.abs(user.getCurrentLocation().getPitch()
                            - user.getLastLocation().getPitch());

                    if (deltaPitch > 0.0) {
                        pitchChangeTick = user.getTick();
                    }

                    int pitchDeltaTick = user.getTick() - pitchChangeTick;

                    if (pitchDeltaTick <= 2 && lastPitchDifferenceTick <= 2) {
                        if (pitchDeltaTick > 0 && lastPitchDifferenceTick > 0) {
                            if (++threshold > 3) {
                                flag(user, "Pitch Snapping");
                            }
                        } else {
                            threshold -= Math.min(threshold, 0.125);
                        }
                    } else {
                        threshold -= Math.min(threshold, 0.125);
                    }

                    lastPitchDifferenceTick = pitchDeltaTick;
                }

                break;
            }

        }
    }
}
