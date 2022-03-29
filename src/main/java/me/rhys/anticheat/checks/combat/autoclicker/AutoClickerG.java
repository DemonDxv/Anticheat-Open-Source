package me.rhys.anticheat.checks.combat.autoclicker;

import me.rhys.anticheat.base.check.api.Check;
import me.rhys.anticheat.base.check.api.CheckInformation;
import me.rhys.anticheat.base.event.PacketEvent;
import me.rhys.anticheat.base.user.User;
import me.rhys.anticheat.tinyprotocol.api.Packet;
import me.rhys.anticheat.util.GraphUtil;

import java.util.ArrayList;
import java.util.List;

@CheckInformation(checkName = "AutoClicker", checkType = "G", lagBack = false, description = "Graph Clicker Check")
public class AutoClickerG extends Check {

    private int movements;
    private List<Integer> delays = new ArrayList<>();
    private List<Double> outlierList = new ArrayList<>();
    private double threshold;

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
                    int distinct = (int) delays.stream().distinct().count();

                    int duplicates = delays.size() - distinct;

                    if (duplicates > 40 && user.getCombatProcessor().getAverageCps() > 7.0) {
                        flag(user, "d="+duplicates, "cps="+user.getCombatProcessor().getAverageCps());
                    }

                    delays.clear();
                }
            }
            movements = 0;
            break;
        }
    }
}
