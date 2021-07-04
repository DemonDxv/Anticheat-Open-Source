package me.rhys.anticheat.checks.misc.badpackets;

import me.rhys.anticheat.base.check.api.Check;
import me.rhys.anticheat.base.check.api.CheckInformation;
import me.rhys.anticheat.base.event.PacketEvent;
import me.rhys.anticheat.base.user.User;
import me.rhys.anticheat.tinyprotocol.api.Packet;

@CheckInformation(checkName = "BadPackets", checkType = "C", punishmentVL = 2)
public class BadPacketsC extends Check {

    @Override
    public void onPacket(PacketEvent event) {
        switch (event.getType()) {
            case Packet.Client.FLYING:
            case Packet.Client.LOOK:
            case Packet.Client.POSITION_LOOK:
            case Packet.Client.POSITION: {

                User user = event.getUser();

                if (user.shouldCancel() || user.getTick() < 60 || user.getVehicleTicks() > 0) {
                    return;
                }

                double deltaXZ = user.getMovementProcessor().getDeltaXZ();

                double maxSpeed = user.getMovementProcessor().getServerPositionSpeed();

                if (user.getActionProcessor().getServerPositionTimer().hasNotPassed(3)) {
                    if (deltaXZ > maxSpeed && deltaXZ > 1.0) {
                        flag(user, "Invalid Teleport "+deltaXZ + " "+maxSpeed);
                    }
                }
                break;
            }
        }
    }
}