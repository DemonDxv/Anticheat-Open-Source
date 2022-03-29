package me.rhys.anticheat.checks.combat.autoclicker;

import me.rhys.anticheat.base.check.api.Check;
import me.rhys.anticheat.base.check.api.CheckInformation;
import me.rhys.anticheat.base.event.PacketEvent;
import me.rhys.anticheat.base.user.User;
import me.rhys.anticheat.tinyprotocol.api.Packet;
import me.rhys.anticheat.util.MathUtil;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;

@CheckInformation(checkName = "AutoClicker", checkType = "N", punishmentVL = 3, description = "Clicker Flaw")
public class AutoClickerN extends Check {

    private List<Double> delays = new ArrayList<>();
    private double threshold;

    @Override
    public void onPacket(PacketEvent event) {
        User user = event.getUser();

        switch (event.getType()) {
            case Packet.Client.ARM_ANIMATION: {

                if (user.shouldCancel() || user.getTick() < 60) {
                    return;
                }

                double skewness = user.getCombatProcessor().getSkewness();
                double outlier = user.getCombatProcessor().getOutlier();
                double currentCps = user.getCombatProcessor().getCurrentCps();
                double kurtosis = user.getCombatProcessor().getKurtosis();
                double median = user.getCombatProcessor().getMedian();

                if (median < 2.5 && user.getCombatProcessor().getMovements().size() >= 20) {
                    if (currentCps > 8) {
                        delays.add(skewness);

                        if (delays.size() == 25) {

                            double average = MathUtil.getAverage(delays);

                            if (average < -2) {
                                if (++threshold > 3) {
                                    flag(user, "avgS="+average + " s="+skewness + " cps="+currentCps);
                                }
                            } else {
                                threshold -= Math.min(threshold, 0.25);
                            }

                            delays.clear();
                        }
                    }
                }

                break;
            }
        }
    }
}
