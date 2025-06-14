package me.rhys.anticheat.checks.combat.autoclicker;

import me.rhys.anticheat.base.check.api.Check;
import me.rhys.anticheat.base.check.api.CheckInformation;
import me.rhys.anticheat.base.event.PacketEvent;
import me.rhys.anticheat.base.user.User;
import me.rhys.anticheat.tinyprotocol.api.Packet;
import me.rhys.anticheat.util.MathUtil;

import java.util.ArrayList;
import java.util.List;

@CheckInformation(checkName = "AutoClicker", checkType = "H", punishmentVL = 3, description = "Mango Clicker Check")
public class AutoClickerH extends Check {

    private int movements;
    private final List<Integer> delays = new ArrayList<>();
    private double threshold, lastAverage, lastStd, lastLastStd, lastStdDiff;

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
                if (movements < 10) {

                    delays.add(movements);

                    double getCps = MathUtil.getCPS(delays);
                    double getSTD = MathUtil.getStandardDeviation(delays);


                    if (delays.size() > 100 && getCps > 8.5f) {
                        double cpsDiff = Math.abs(getCps - lastAverage);
                        double stdDiff = Math.abs(getSTD - lastLastStd);
                        double stdDiffDiff = Math.abs(stdDiff - lastStdDiff);

                        if (stdDiffDiff < 0.001 && cpsDiff > 0.01) {
                            if (++threshold > 3) {
                                flag(user, "Invalid Click Consistency, stdDD="+stdDiffDiff + " cpsD="+cpsDiff);
                            }
                        } else {
                            threshold -= Math.min(threshold, 0.125);
                        }

                        lastStdDiff = stdDiff;
                        delays.clear();
                    }

                    lastAverage = getCps;
                    lastStd = getSTD;
                    lastLastStd = lastStd;
                }
            }
            movements = 0;
            break;
        }
    }
}
