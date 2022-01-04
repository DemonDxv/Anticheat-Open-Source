package me.rhys.anticheat.checks.combat.autoclicker;

import me.rhys.anticheat.base.check.api.Check;
import me.rhys.anticheat.base.check.api.CheckInformation;
import me.rhys.anticheat.base.event.PacketEvent;
import me.rhys.anticheat.base.user.User;
import me.rhys.anticheat.tinyprotocol.api.Packet;
import me.rhys.anticheat.util.MathUtil;
import me.rhys.anticheat.util.Tuple;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;

@CheckInformation(checkName = "AutoClicker", checkType = "K", punishmentVL = 12, description = "Vape Lite AutoClicker Check")
public class AutoClickerK extends Check {

    private int movements;
    private List<Integer> delays = new ArrayList<>();
    private List<Double> stdDelays = new ArrayList<>();
    private double threshold, lastAverage, lastDelta;

    @Override
    public void onPacket(PacketEvent event) {
        User user = event.getUser();

        switch (event.getType()) {
            case Packet.Client.FLYING:
            case Packet.Client.LOOK:
            case Packet.Client.POSITION_LOOK:
            case Packet.Client.POSITION: {

                if (user.shouldCancel()
                        || user.getTick() < 60
                        || user.getLastBlockPlaceTimer().hasNotPassed(20)
                        || user.getMovementProcessor().getLastBlockDigTimer().hasNotPassed(20)
                        || user.getLastBlockPlaceCancelTimer().hasNotPassed(20)) {
                    movements = 20;
                    return;
                }

                movements++;

                break;
            }

            case Packet.Client.ARM_ANIMATION: {
                if (movements < 15) {

                    delays.add(movements);

                    double mean = MathUtil.getMedian(delays);
                    double std = MathUtil.getStandardDeviation(delays);
                    double kurtosis = MathUtil.getKurtosis(delays);


                    if (mean < 2.5 && delays.size() >= 20) {
                        if (stdDelays.size() > 30) {
                            double average = MathUtil.getAverage(stdDelays);
                            double delta = Math.abs(average - lastAverage);
                            double outlier = user.getCombatProcessor().getOutlier();

                            if (lastDelta < 0.0855) {

                                double newDelta = Math.abs(delta - lastDelta);

                                if (newDelta < (.43 % 5) && kurtosis < 1.7 && outlier < 15) {
                                    threshold++;

                                    if (threshold > 6) {
                                        flag(user);
                                    }
                                } else {
                                    threshold -= Math.min(threshold, .1);
                                }
                            } else {
                                threshold -= Math.min(threshold, 0.1);
                            }


                            lastDelta = delta;
                            lastAverage = average;
                            stdDelays.clear();
                        }

                        stdDelays.add(std);

                        if (delays.size() >= 100) {
                            delays.clear();
                        }
                    }
                }
                movements = 0;
                break;
            }
        }
    }
}
