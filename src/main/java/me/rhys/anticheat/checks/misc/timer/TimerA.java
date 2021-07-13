package me.rhys.anticheat.checks.misc.timer;

import me.rhys.anticheat.base.check.api.Check;
import me.rhys.anticheat.base.check.api.CheckInformation;
import me.rhys.anticheat.base.event.PacketEvent;
import me.rhys.anticheat.base.user.User;
import me.rhys.anticheat.tinyprotocol.api.Packet;

import java.util.concurrent.TimeUnit;

@CheckInformation(checkName = "Timer", lagBack = false, punishmentVL = 15, description = "Detects 1.01% Timer Speed")
public class TimerA extends Check {

    private double balance = -100L, threshold;

    private long lastTime;

    @Override
    public void onPacket(PacketEvent event) {
        switch (event.getType()) {
            case Packet.Client.FLYING:
            case Packet.Client.LOOK:
            case Packet.Client.POSITION_LOOK:
            case Packet.Client.POSITION: {

                User user = event.getUser();

                if (user.shouldCancel()
                        || user.getActionProcessor().getServerPositionTimer().hasNotPassed(3)
                        || user.getPlayer().isDead()
                        || user.getTick() < 60
                        || user.getLastTeleportTimer().hasNotPassed(20)) {
                    threshold = 0;
                    return;
                }


                long currentTime = System.nanoTime();

                long lastTime = this.lastTime != 0 ? this.lastTime : currentTime - 50;

                long balanceRate = currentTime - lastTime;

                balance += TimeUnit.MILLISECONDS.toNanos(50L) - balanceRate;

                if (balance > TimeUnit.MILLISECONDS.toNanos(46L)) {
                    if (threshold++ > 2) {
                        flag(user, "Speeding up game");
                    }

                    balance = 0L;
                } else {
                    threshold -= Math.min(threshold, 0.001f);
                }

                this.lastTime = currentTime;

                break;
            }
        }
    }
}