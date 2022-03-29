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

@CheckInformation(checkName = "AutoClicker", checkType = "E", lagBack = false, description = "Vape Autoclicker Detection")
public class AutoClickerE extends Check {

    private int movements;
    private List<Integer> delays = new ArrayList<>();
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

                    if (delays.size() == 1000) {

                        int outliers = (int) delays.stream()
                                .filter(delay -> delay > 3)
                                .count();

                        if (outliers < 7) {
                            if (++threshold > 1) {
                                flag(user, "Outliers", "t="+threshold, "o="+outliers);
                            }
                        } else {
                            threshold -= Math.min(threshold, 1.5);
                        }

                        delays.clear();
                    }

                }
                movements = 0;
                break;
            }
        }
    }
}
