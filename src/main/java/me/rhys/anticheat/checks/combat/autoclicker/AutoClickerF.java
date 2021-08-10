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

@CheckInformation(checkName = "AutoClicker", checkType = "F", canPunish = false, enabled = false, description = "Experimental 1.8 - 1.7 Check")
public class AutoClickerF extends Check {

    private int movements, out;
    private double threshold;

    @Override
    public void onPacket(PacketEvent event) {
        User user = event.getUser();
        switch (event.getType()) {
            case Packet.Client.FLYING:
            case Packet.Client.LOOK:
            case Packet.Client.POSITION_LOOK:
            case Packet.Client.POSITION: {
                movements++;
                break;
            }

            case Packet.Client.ARM_ANIMATION: {

                if (user.shouldCancel()
                        || user.getMovementProcessor().getLastBlockDigTimer().hasNotPassed(20)
                        || user.getCombatProcessor().getUseEntityTimer().hasNotPassed(20)
                        || user.getTick() < 60) {
                    movements = 0;
                    return;
                }

                if (movements < 10) {
                    if (movements < 6 && movements > 2) {
                        out = 1;
                    } else {
                        out = 0;
                    }

                    if (out == 0) {
                        if (threshold++ > 125) {
                            flag(user, "Invalid 1.8 Mouse Delay (HIGHLY EXPERIMENTAL)");
                        }
                    } else {
                        threshold = 0;
                    }
                }
                movements = 0;
                break;
            }
        }
    }
}