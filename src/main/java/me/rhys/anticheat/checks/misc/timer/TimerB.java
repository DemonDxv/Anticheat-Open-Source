package me.rhys.anticheat.checks.misc.timer;

import me.rhys.anticheat.base.check.api.Check;
import me.rhys.anticheat.base.check.api.CheckInformation;
import me.rhys.anticheat.base.event.PacketEvent;
import me.rhys.anticheat.base.user.User;
import me.rhys.anticheat.tinyprotocol.api.Packet;
import org.bukkit.Bukkit;

import java.util.concurrent.TimeUnit;

@CheckInformation(checkName = "Timer", checkType = "B", lagBack = false, punishmentVL = 4, description = "Detects Balance Timer Abuse")
public class TimerB extends Check {

    private long lastPacket = -1337L;

    private double threshold;

    @Override
    public void onPacket(PacketEvent event) {
        User user = event.getUser();

        switch (event.getType()) {
            case Packet.Client.FLYING:
            case Packet.Client.LOOK:
            case Packet.Client.POSITION_LOOK:
            case Packet.Client.POSITION: {

                long now = System.currentTimeMillis();
                long delta = now - this.lastPacket;

                if (!user.shouldCancel() && user.getTick() > 120 && this.lastPacket > -1337L && user.isChunkLoaded()) {
                    if (delta > 100L) {
                        if (++threshold > 6) {
                            flag(user);
                            threshold = 0;
                        }
                    } else {
                        threshold -= Math.min(threshold, 0.7f);
                    }
                }

                this.lastPacket = now;
                break;
            }
        }
    }
}