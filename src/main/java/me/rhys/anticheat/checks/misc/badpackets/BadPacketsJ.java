package me.rhys.anticheat.checks.misc.badpackets;

import me.rhys.anticheat.base.check.api.Check;
import me.rhys.anticheat.base.check.api.CheckInformation;
import me.rhys.anticheat.base.event.PacketEvent;
import me.rhys.anticheat.base.user.User;
import me.rhys.anticheat.tinyprotocol.api.Packet;

@CheckInformation(checkName = "BadPackets", checkType = "J", description = "Enderpearl/Teleport movement fix")
public class BadPacketsJ extends Check {

    private double threshold;

    @Override
    public void onPacket(PacketEvent event) {
        User user = event.getUser();

        switch (event.getType()) {
            case Packet.Client.FLYING:
            case Packet.Client.LOOK:
            case Packet.Client.POSITION_LOOK:
            case Packet.Client.POSITION: {

                if (user.getLastEnderPearlTimer().hasNotPassed(5)) {

                    double deltaXZ = user.getMovementProcessor().getDeltaXZ();

                    double maxXZ = 0.6;

                    if (deltaXZ > maxXZ && user.getEnderPearlDistance() < 8) {
                        if (threshold++ > 2) {
                            flag(user, "Invalid teleport");
                        }
                    } else {
                        threshold = 0;
                    }
                }
                break;
            }
        }
    }
}