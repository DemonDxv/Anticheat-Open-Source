package me.rhys.anticheat.checks.misc.pingspoof;

import me.rhys.anticheat.Anticheat;
import me.rhys.anticheat.base.check.api.Check;
import me.rhys.anticheat.base.check.api.CheckInformation;
import me.rhys.anticheat.base.event.PacketEvent;
import me.rhys.anticheat.base.user.User;
import me.rhys.anticheat.tinyprotocol.api.Packet;
import org.bukkit.scheduler.BukkitRunnable;

@CheckInformation(checkName = "PingSpoof", checkType = "B", lagBack = false, description = "Detects Ping Spoofing")
public class PingSpoofB extends Check {

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
                        || user.getLastTeleportTimer().hasNotPassed(5)
                        || user.getActionProcessor().getServerPositionTimer().hasNotPassed(3)) {
                    return;
                }

                boolean canKick = user.getTick() > 1000 && user.getConnectionProcessor().getLastFlyingReceived() > 175;

                if (canKick) {
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            devFlag(user, "Got disconnected due to abnormal spikes in flying packets being sent");
                            user.getPlayer().kickPlayer("Disconnected. (packets timed out?)");
                        }
                    }.runTask(Anticheat.getInstance());
                }

                break;
            }
        }
    }
}