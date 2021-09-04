package me.rhys.anticheat.checks.movement.noweb;

import me.rhys.anticheat.base.check.api.Check;
import me.rhys.anticheat.base.check.api.CheckInformation;
import me.rhys.anticheat.base.event.PacketEvent;
import me.rhys.anticheat.base.user.User;
import me.rhys.anticheat.tinyprotocol.api.Packet;
import org.bukkit.Bukkit;

@CheckInformation(checkName = "NoWeb", canPunish = false, description = "Checks for invalid state in web")
public class NoWebA extends Check {

    private double threshold;

    @Override
    public void onPacket(PacketEvent event) {
        User user = event.getUser();

        switch (event.getType()) {
            case Packet.Client.FLYING:
            case Packet.Client.LOOK:
            case Packet.Client.POSITION_LOOK:
            case Packet.Client.POSITION: {

                if (user.getBlockData().web) {

                    double deltaXZ = user.getMovementProcessor().getDeltaXZ();

                    if (deltaXZ > 0.1 && user.getBlockData().webTicks >= 20) {
                        if (++threshold > 1) {
                            flag(user, "Moving to fast in a web");
                        }
                    } else {
                        threshold -= Math.min(threshold, 0.001);
                    }

                }

                break;
            }
        }
    }
}