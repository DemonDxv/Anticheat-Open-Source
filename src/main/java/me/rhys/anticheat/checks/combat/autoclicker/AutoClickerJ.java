package me.rhys.anticheat.checks.combat.autoclicker;

import me.rhys.anticheat.base.check.api.Check;
import me.rhys.anticheat.base.check.api.CheckInformation;
import me.rhys.anticheat.base.event.PacketEvent;
import me.rhys.anticheat.base.user.User;
import me.rhys.anticheat.tinyprotocol.api.Packet;
import me.rhys.anticheat.util.MathUtil;

import java.util.ArrayList;
import java.util.List;

@CheckInformation(checkName = "AutoClicker", checkType = "J", punishmentVL = 12, description = "Average Kurtosis")
public class AutoClickerJ extends Check {

    private List<Double> kurtosisList = new ArrayList<>();
    private double threshold;

    @Override
    public void onPacket(PacketEvent event) {
        User user = event.getUser();

        switch (event.getType()) {
            case Packet.Client.ARM_ANIMATION: {

                if (user.getCombatProcessor().getMovements().size() >= 20) {
                    double mean = user.getCombatProcessor().getMedian();
                    double kurtosis = user.getCombatProcessor().getKurtosis();
                    double cps = user.getCombatProcessor().getCurrentCps();

                    if (mean < 2.5 && cps > 8) {
                        if (kurtosisList.size() > 20) {
                            double average = MathUtil.getAverage(kurtosisList);

                            if (average > 10) {
                              if (++threshold > 5) {
                                  flag(user, "avg="+average, " cps="+cps, "mean="+mean);
                              }
                            } else {
                                threshold -= Math.min(threshold, 0.2);
                            }

                            kurtosisList.clear();
                        }

                        kurtosisList.add(kurtosis);
                    }
                }
                break;
            }
        }
    }
}
