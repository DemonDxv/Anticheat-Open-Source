package me.rhys.anticheat.checks.combat.autoclicker;

import me.rhys.anticheat.base.check.api.Check;
import me.rhys.anticheat.base.check.api.CheckInformation;
import me.rhys.anticheat.base.event.PacketEvent;
import me.rhys.anticheat.base.user.User;
import me.rhys.anticheat.tinyprotocol.api.Packet;

@CheckInformation(checkName = "AutoClicker", lagBack = false, description = "Checks if the player is clicking over 22 clicks per second.")
public class AutoClickerA extends Check {

    private int movements, clicks;

    @Override
    public void onPacket(PacketEvent event) {
        User user = event.getUser();

        switch (event.getType()) {
            case Packet.Client.FLYING:
            case Packet.Client.LOOK:
            case Packet.Client.POSITION_LOOK:
            case Packet.Client.POSITION: {


                if (movements++ == 20) {
                    if (clicks > 22) {
                        flag(user, "Clicking abnormally fast: "+clicks);
                    }
                    movements = clicks = 0;
                }

                break;
            }

            case Packet.Client.ARM_ANIMATION: {
                clicks++;
                break;
            }

            case Packet.Client.BLOCK_PLACE:
            case Packet.Client.BLOCK_DIG: {
                clicks = 0;
                break;
            }
        }
    }
}
