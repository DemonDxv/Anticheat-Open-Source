package me.rhys.anticheat.checks.misc.timer;

import me.rhys.anticheat.base.check.api.Check;
import me.rhys.anticheat.base.check.api.CheckInformation;
import me.rhys.anticheat.base.event.PacketEvent;
import me.rhys.anticheat.base.user.User;
import me.rhys.anticheat.tinyprotocol.api.Packet;
import me.rhys.anticheat.util.RollingAverageDouble;

@CheckInformation(checkName = "Timer", checkType = "B", lagBack = false, description = "Timer 1.01")
public class TimerB extends Check {

    private Long lastMove;
    private long lastTime;
    private double threshold;
    private final RollingAverageDouble timerRate = new RollingAverageDouble(50, 50.0);

    @Override
    public void onPacket(PacketEvent event) {
        User user = event.getUser();

        switch (event.getType()) {
            case Packet.Client.FLYING:
            case Packet.Client.LOOK:
            case Packet.Client.POSITION_LOOK:
            case Packet.Client.POSITION: {

                if (user.getTick() < 60
                        || user.getConnectionProcessor().isLagging()
                        || user.shouldCancel()
                        || user.getVehicleTicks() > 0
                        || user.getLastTeleportTimer().hasNotPassed(20)
                        || user.getActionProcessor().getServerPositionTimer().hasNotPassed(3)) {
                    return;
                }


                long now = System.currentTimeMillis();

                if (lastMove != null) {
                    long diff = now - lastMove;

                    timerRate.add(diff);

                    if (now - lastTime >= 1000L) {
                        lastTime = now;

                        double timerSpeed = 50.0 / timerRate.getAverage();

                        if (timerSpeed >= 1.01) {
                            if (threshold++ > 8) {
                                flag(user, "Speeding Up Game: "+timerSpeed);
                            }
                        } else {
                            threshold -= Math.min(threshold, 0.18f);
                        }
                    }
                }

                lastMove = now;


                break;
            }
        }
    }
}