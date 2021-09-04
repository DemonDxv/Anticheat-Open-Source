package me.rhys.anticheat.checks.combat.velocity;

import me.rhys.anticheat.Anticheat;
import me.rhys.anticheat.base.check.api.Check;
import me.rhys.anticheat.base.check.api.CheckInformation;
import me.rhys.anticheat.base.event.PacketEvent;
import me.rhys.anticheat.base.user.User;
import me.rhys.anticheat.tinyprotocol.api.Packet;
import me.rhys.anticheat.util.MathUtil;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

@CheckInformation(checkName = "Velocity", checkType = "D", canPunish = false, enabled = false, description = "Checks for canceled velocity transactions")
public class VelocityD extends Check {

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
                        || user.getVehicleTicks() > 0
                        || user.getActionProcessor().getServerPositionTimer().hasNotPassed(20)
                        || user.getLastTeleportTimer().hasNotPassed(20
                        + user.getConnectionProcessor().getClientTick())
                        || !user.isChunkLoaded()) {
                    return;
                }

                int velocityTicks = user.getCombatProcessor().getVelocityTicks(),
                        velocityNoTransTicks = user.getCombatProcessor().getVelocityNoTransTicks();

                int tickChange = Math.abs(velocityTicks - velocityNoTransTicks);

                if (velocityTicks <= 20) {
                    if (tickChange > 20) {
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                user.getPlayer().kickPlayer("Disconnected.");
                            }
                        }.runTask(Anticheat.getInstance());
                    }
                }

                break;
            }
        }
    }
}
