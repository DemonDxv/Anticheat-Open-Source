package me.rhys.anticheat.checks.combat.aimassist;

import me.rhys.anticheat.base.check.api.Check;
import me.rhys.anticheat.base.check.api.CheckInformation;
import me.rhys.anticheat.base.event.PacketEvent;
import me.rhys.anticheat.base.user.User;
import me.rhys.anticheat.tinyprotocol.api.Packet;
import org.bukkit.Bukkit;

@CheckInformation(checkName = "AimAssist", checkType = "N", canPunish = false, punishmentVL = 10)
public class AimAssistN extends Check {

    private double lastDeltaYaw, lastLastDeltaYaw, lastDeltaPitch, lastLastDeltaPitch;
    private double threshold;

    private double TRUST_FACTOR = 1.7;

    @Override
    public void onPacket(PacketEvent event) {
        User user = event.getUser();

        switch (event.getType()) {

            case Packet.Client.FLYING:
            case Packet.Client.LOOK:
            case Packet.Client.POSITION_LOOK:
            case Packet.Client.POSITION: {

                if (user.getCombatProcessor().getUseEntityTimer().hasNotPassed(2)) {

                    double deltaYaw = user.getMovementProcessor().getYawDeltaClamped();
                    double deltaPitch = user.getMovementProcessor().getPitchDelta();

                    if (deltaPitch < 0.05f && deltaPitch > 0.009f && lastDeltaPitch < 0.05f && lastDeltaPitch > 0.009f
                            && deltaYaw > 6.2f && lastDeltaYaw > 0.4f) {
                        devFlag(user, "Smooth Aim");
                    }


                    lastLastDeltaPitch = lastDeltaPitch;
                    lastLastDeltaYaw = lastDeltaPitch;
                    lastDeltaPitch = deltaPitch;
                    lastDeltaYaw = deltaYaw;


                }

                break;
            }
        }
    }
}