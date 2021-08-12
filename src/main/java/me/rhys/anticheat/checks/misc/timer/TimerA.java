package me.rhys.anticheat.checks.misc.timer;

import me.rhys.anticheat.base.check.api.Check;
import me.rhys.anticheat.base.check.api.CheckInformation;
import me.rhys.anticheat.base.event.PacketEvent;
import me.rhys.anticheat.base.user.User;
import me.rhys.anticheat.tinyprotocol.api.Packet;
import org.bukkit.Bukkit;

import java.util.concurrent.TimeUnit;

@CheckInformation(checkName = "Timer", lagBack = false, punishmentVL = 15, description = "Detects 1.01% Timer Speed")
public class TimerA extends Check {

    private final long maxDelay = 50000000L;
    private final long maxValue = 45000000L;

    private long lastPacket = -1337L;
    private long balance;

    @Override
    public void onPacket(PacketEvent event) {
        User user = event.getUser();

        //TODO: add server lag checking

        switch (event.getType()) {
            case Packet.Server.POSITION: {

                //We need to deduct the balance on teleport so we don't false flag, and also add the ping-tick

                int pingTick = user.getConnectionProcessor().getClientTick();

                //Prevent ping-spoof exploits
                if (pingTick > 20) pingTick = 10;

                //Add + 10 to be safe on teleport, possibly can change to a lower value other than 250L
                this.balance -= TimeUnit.MILLISECONDS.toNanos(250L + (pingTick + 10));
                break;
            }

            case Packet.Client.FLYING:
            case Packet.Client.LOOK:
            case Packet.Client.POSITION_LOOK:
            case Packet.Client.POSITION: {

                long now = System.nanoTime();
                long delta = (this.maxDelay - (now - this.lastPacket));

                if (!user.shouldCancel() && user.getTick() > 120 && this.lastPacket > -1337L) {
                    this.balance += delta;

                    if (balance <= -500000000) {
                        balance = -500000000;
                    }

                    if (this.balance > this.maxValue) {
                        this.flag(user,
                                "balance=" + this.balance,
                                "packetDelta=" + delta
                        );

                        this.balance = 0;
                    }
                }

                this.lastPacket = now;
                break;
            }
        }
    }
}