package me.rhys.anticheat.checks.combat.aimassist;

import me.rhys.anticheat.base.check.api.Check;
import me.rhys.anticheat.base.check.api.CheckInformation;
import me.rhys.anticheat.base.event.PacketEvent;
import me.rhys.anticheat.base.user.User;
import me.rhys.anticheat.tinyprotocol.api.Packet;

@CheckInformation(checkName = "AimAssist", checkType = "G", lagBack = false, punishmentVL = 25, canPunish = false)
public class AimAssistG extends Check {

    /**
     * Patch for Stitch's Aura that snaps every couple ticks.
     */

    private double threshold;
    private int yawChangeTick, lastYawDifferenceTick;

    @Override
    public void onPacket(PacketEvent event) {
        User user = event.getUser();

        switch (event.getType()) {

            case Packet.Client.FLYING:
            case Packet.Client.LOOK:
            case Packet.Client.POSITION_LOOK:
            case Packet.Client.POSITION: {

                if (user.getCombatProcessor().getUseEntityTimer().hasNotPassed(2)) {
                    double deltaPitch = Math.abs(user.getCurrentLocation().getYaw()
                            - user.getLastLocation().getYaw());

                    if (deltaPitch > 0.0) {
                        yawChangeTick = user.getTick();
                    }

                    int pitchDeltaTick = user.getTick() - yawChangeTick;

                    if (pitchDeltaTick <= 2 && lastYawDifferenceTick <= 2) {
                        if (pitchDeltaTick > 0 && lastYawDifferenceTick > 0) {
                            if (++threshold > 5) {
                                flag(user, "Yaw Snapping");
                            }
                        } else {
                            threshold -= Math.min(threshold, 0.125);
                        }
                    } else {
                        threshold -= Math.min(threshold, 0.125);
                    }

                    lastYawDifferenceTick = pitchDeltaTick;
                }

                break;
            }

        }
    }
}
