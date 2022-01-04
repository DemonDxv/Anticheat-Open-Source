package me.rhys.anticheat.checks.movement.noweb;

import me.rhys.anticheat.base.check.api.Check;
import me.rhys.anticheat.base.check.api.CheckInformation;
import me.rhys.anticheat.base.event.PacketEvent;
import me.rhys.anticheat.base.user.User;
import me.rhys.anticheat.tinyprotocol.api.Packet;

@CheckInformation(checkName = "NoWeb", checkType = "B", description = "Checks for invalid state in web")
public class NoWebB extends Check {

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

                    double deltaY = user.getMovementProcessor().getDeltaY();

                    if (deltaY > 0.0 && user.getBlockData().webTicks > 4
                            && user.getMovementProcessor().getAirTicks() > 7) {
                        if (++threshold > 1) {
                            flag(user, "Moving abnormal in a web", ""+deltaY);
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