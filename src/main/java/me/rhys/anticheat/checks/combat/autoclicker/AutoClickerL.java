package me.rhys.anticheat.checks.combat.autoclicker;

import me.rhys.anticheat.base.check.api.Check;
import me.rhys.anticheat.base.check.api.CheckInformation;
import me.rhys.anticheat.base.event.PacketEvent;
import me.rhys.anticheat.base.user.User;
import me.rhys.anticheat.tinyprotocol.api.Packet;

import java.util.ArrayList;
import java.util.List;

@CheckInformation(checkName = "AutoClicker", checkType = "L", punishmentVL = 3, description = "Clicker Flaw")
public class AutoClickerL extends Check {

    private int movements;
    private final List<Integer> delays = new ArrayList<>();
    private double threshold;

    @Override
    public void onPacket(PacketEvent event) {
        User user = event.getUser();

        if (event.getType().equals(Packet.Client.ARM_ANIMATION)) {
            if (user.shouldCancel() || user.getTick() < 60) {
                return;
            }

            double skewness = user.getCombatProcessor().getSkewness();
            double outlier = user.getCombatProcessor().getOutlier();
            double currentCps = user.getCombatProcessor().getCurrentCps();
            double kurtosis = user.getCombatProcessor().getKurtosis();
            double median = user.getCombatProcessor().getMedian();

            if (median < 2.5 && skewness < 0.1 && outlier < 3 && currentCps > 7.6 && kurtosis < 0) {

                if (++threshold > 4 && threshold < 50) {
                    flag(user, "s=" + skewness, "median=" + median, "o=" + outlier, "cps=" + currentCps, "k=" + kurtosis);
                }

            } else {
                threshold -= Math.min(threshold, 1.25);
            }
        }
    }
}
