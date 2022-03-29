package me.rhys.anticheat.checks.combat.aimassist;

import me.rhys.anticheat.base.check.api.Check;
import me.rhys.anticheat.base.check.api.CheckInformation;
import me.rhys.anticheat.base.event.PacketEvent;
import me.rhys.anticheat.base.user.User;
import me.rhys.anticheat.tinyprotocol.api.Packet;
import me.rhys.anticheat.util.MathUtil;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;

@CheckInformation(checkName = "AimAssist", checkType = "G", lagBack = false, punishmentVL = 25)
public class AimAssistG extends Check {

    private double threshold;
    private List<Double> deltaPitchList = new ArrayList<>();

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

                    if (deltaPitch > 0.8) {
                        deltaPitchList.add(deltaPitch);

                        if (deltaPitchList.size() > 125) {
                            double std = MathUtil.getStandardDeviation(deltaPitchList);

                            if (std < 0.6) {
                                flag(user, "Pitch consistency, "+std);
                            }

                            deltaPitchList.clear();
                        }
                    }

                }

                break;
            }

        }
    }
}
