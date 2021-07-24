package me.rhys.anticheat.checks.misc.timer;

import me.rhys.anticheat.Anticheat;
import me.rhys.anticheat.base.check.api.Check;
import me.rhys.anticheat.base.check.api.CheckInformation;
import me.rhys.anticheat.base.event.PacketEvent;
import me.rhys.anticheat.base.user.User;
import me.rhys.anticheat.tinyprotocol.api.Packet;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

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
                        || user.getPlayer().isDead()
                        || user.getTick() < 60
                        || user.getConnectionProcessor().getFlyingTick() > 1
                        || user.getConnectionProcessor().getDropTransTime() > 100L
                        || user.getLastTeleportTimer().hasNotPassed(20)) {
                    threshold = 0;
                    return;
                }


                long currentTime = System.nanoTime();

                long lastTime = this.lastTime != 0 ? this.lastTime : currentTime - 50;

                long balanceRate = currentTime - lastTime;

                balance += TimeUnit.MILLISECONDS.toNanos(50L) - balanceRate;

                if (user.getActionProcessor().getServerPositionTimer().hasNotPassed(
                        (user.getConnectionProcessor().getClientTick() * 2))) {
                    balance = -50.0;
                }

                if (balance < -100.0) {
                    balance = -100.0;
                }

                if (balance > TimeUnit.MILLISECONDS.toNanos(45L)) {

                    if (threshold++ > 6) {
                        flag(user, "Speeding up game", ""
                                +user.getConnectionProcessor().getFlyingTick() + " "
                                +user.getConnectionProcessor().getDropTick());
                    }

                    balance = 0L;
                } else {
                    threshold -= Math.min(threshold, 0.25f);
                }

                this.lastTime = currentTime;

                break;
            }
        }
    }
}