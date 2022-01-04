package me.rhys.anticheat.checks.misc.pingspoof;

import me.rhys.anticheat.Anticheat;
import me.rhys.anticheat.base.check.api.Check;
import me.rhys.anticheat.base.check.api.CheckInformation;
import me.rhys.anticheat.base.event.PacketEvent;
import me.rhys.anticheat.base.user.User;
import me.rhys.anticheat.tinyprotocol.api.Packet;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.TimeUnit;

@CheckInformation(checkName = "PingSpoof", checkType = "D", lagBack = false, description = "Blocks Low Timer")
public class PingSpoofD extends Check {

    private long lastFlying;

    @Override
    public void onPacket(PacketEvent event) {
        switch (event.getType()) {
            case Packet.Client.FLYING:
            case Packet.Client.LOOK:
            case Packet.Client.POSITION_LOOK:
            case Packet.Client.POSITION: {

                User user = event.getUser();

                if (user.shouldCancel()
                        || user.getTick() < 1000
                        || user.getLastTeleportTimer().hasNotPassed(5)
                        || user.getActionProcessor().getServerPositionTimer().hasNotPassed(3)) {
                    return;
                }

                long now = System.currentTimeMillis();

                long delta = (now - this.lastFlying);

                if (delta > 100L && user.getConnectionProcessor().getDropTransTime() < 20) {

                }

                this.lastFlying = now;
                break;
            }
        }
    }
}