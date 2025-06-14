package me.rhys.anticheat.checks.misc.pingspoof;

import me.rhys.anticheat.Anticheat;
import me.rhys.anticheat.base.check.api.Check;
import me.rhys.anticheat.base.check.api.CheckInformation;
import me.rhys.anticheat.base.event.PacketEvent;
import me.rhys.anticheat.base.user.User;
import me.rhys.anticheat.tinyprotocol.api.Packet;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

@CheckInformation(checkName = "PingSpoof", checkType = "C", lagBack = false, description = "Detects Ping Spoofing")
public class PingSpoofC extends Check {

    private double threshold;

    @Override
    public void onPacket(PacketEvent event) {
        switch (event.getType()) {
            case Packet.Client.FLYING:
            case Packet.Client.LOOK:
            case Packet.Client.POSITION_LOOK:
            case Packet.Client.POSITION: {

                User user = event.getUser();

                if (user.shouldCancel()
                        || user.getTick() < 60
                         || user.getPlayer().isDead()) {
                    threshold = 0;
                    return;
                }

                int keepSize = user.getConnectionMap2().size();
                int transSize = user.getConnectionMap().size();

                if (keepSize > transSize + 5) {
                    devFlag(user, "Got disconnected due to game slowing down.");

                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            user.getPlayer().kickPlayer("Disconnected.");
                        }
                    }.runTask(Anticheat.getInstance());
                }

                break;
            }
        }
    }
}