package me.rhys.bedrock.checks.misc.timer;

import me.rhys.bedrock.base.check.api.Check;
import me.rhys.bedrock.base.check.api.CheckInformation;
import me.rhys.bedrock.base.event.PacketEvent;
import me.rhys.bedrock.base.user.User;
import me.rhys.bedrock.tinyprotocol.api.Packet;
import me.rhys.bedrock.util.MathUtil;
import me.rhys.bedrock.util.RollingAverageDouble;
import org.bukkit.Bukkit;

@CheckInformation(checkName = "Timer", lagBack = false, description = "Timer Speeds of 0.9")
public class TimerA extends Check {

    private Long lastMove;
    private long lastTime;
    private double threshold;
    private final RollingAverageDouble timerRate = new RollingAverageDouble(20, 50.0);

    @Override
    public void onPacket(PacketEvent event) {
        User user = event.getUser();

        switch (event.getType()) {
            case Packet.Client.FLYING:
            case Packet.Client.LOOK:
            case Packet.Client.POSITION_LOOK:
            case Packet.Client.POSITION: {

                if (user.getTick() < 60
                        || user.shouldCancel()
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

                        if (timerSpeed < 0.9) {
                            if (threshold++ > 12) {
                                flag(user, "Slowing Game Speed: "+timerSpeed);
                            }
                        } else {
                            threshold -= Math.min(threshold, 0.5);
                        }
                    }
                }

                lastMove = now;


                break;
            }
        }
    }
}