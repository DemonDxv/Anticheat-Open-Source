package me.rhys.anticheat.checks.movement.flight;

import me.rhys.anticheat.base.check.api.Check;
import me.rhys.anticheat.base.check.api.CheckInformation;
import me.rhys.anticheat.base.event.PacketEvent;
import me.rhys.anticheat.base.user.User;
import me.rhys.anticheat.tinyprotocol.api.Packet;

@CheckInformation(checkName = "Flight", checkType = "B", description = "Checks if the player tries to spoof on ground.")
public class FlightB extends Check {

    private double threshold;

    @Override
    public void onPacket(PacketEvent event) {
        switch (event.getType()) {
            case Packet.Client.FLYING:
            case Packet.Client.LOOK:
            case Packet.Client.POSITION_LOOK:
            case Packet.Client.POSITION: {
                User user = event.getUser();

                if (this.checkConditions(user)
                        || user.getActionProcessor().getServerPositionTimer().hasNotPassed(3)
                        || user.getLastTeleportTimer().hasNotPassed(20)) {
                    this.threshold = 0;
                    return;
                }

                if (user.getMovementProcessor().getLastBlockPlacePacketTimer().hasNotPassed(3)) {
                    threshold -= Math.min(threshold, 0.2);
                }
                if (!user.getBlockData().onGround) {
                    if (user.getCurrentLocation().isClientGround()) {
                        if (threshold++ > 3.5) {
                            flag(user, "Spoofing Ground");
                        }
                    } else {
                        threshold -= Math.min(threshold, 0.02);
                    }
                }
            }
        }
    }

    boolean checkConditions(User user) {
        return user.getBlockData().liquidTicks > 0
                || user.getTick() < 60
                || user.shouldCancel()
                || user.getBlockData().climbableTicks > 0;
    }
}
