package me.rhys.anticheat.checks.misc.pingspoof;

import me.rhys.anticheat.Anticheat;
import me.rhys.anticheat.base.check.api.Check;
import me.rhys.anticheat.base.check.api.CheckInformation;
import me.rhys.anticheat.base.event.PacketEvent;
import me.rhys.anticheat.base.user.User;
import me.rhys.anticheat.tinyprotocol.api.Packet;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

@CheckInformation(checkName = "PingSpoof", lagBack = false, canPunish = false, description = "Blocks Ping Spoofing")
public class PingSpoofA extends Check {

    private double threshold;

    @Override
    public void onPacket(PacketEvent event) {
        switch (event.getType()) {
            case Packet.Client.FLYING:
            case Packet.Client.LOOK:
            case Packet.Client.POSITION_LOOK:
            case Packet.Client.POSITION: {

                User user = event.getUser();

                if (user.shouldCancel() && user.getTick() < 60) {
                    return;
                }

                if (user.getConnectionProcessor().isLagging()) {
                    if (threshold++ > 70) {
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                user.getPlayer().kickPlayer("Timed out.");
                            }
                        }.runTask(Anticheat.getInstance());
                    }
                } else {
                    threshold -= Math.min(threshold, 0.110);
                }

                break;
            }
        }
    }
}