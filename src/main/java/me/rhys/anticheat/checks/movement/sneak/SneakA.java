package me.rhys.anticheat.checks.movement.sneak;

import me.rhys.anticheat.base.check.api.Check;
import me.rhys.anticheat.base.check.api.CheckInformation;
import me.rhys.anticheat.base.event.PacketEvent;
import me.rhys.anticheat.base.user.User;
import me.rhys.anticheat.tinyprotocol.api.Packet;
import me.rhys.anticheat.util.TimeUtils;
import org.bukkit.Bukkit;

@CheckInformation(checkName = "Sneak", punishmentVL = 15, description = "Checks if sneaking is sent rapidly")
public class SneakA extends Check {

    private long lastSneaking;
    private double threshold;

    @Override
    public void onPacket(PacketEvent event) {

        User user = event.getUser();

        switch (event.getType()) {
            case Packet.Client.FLYING:
            case Packet.Client.POSITION:
            case Packet.Client.POSITION_LOOK:
            case Packet.Client.LOOK: {

                if (user.shouldCancel()
                        || user.getTick() < 100
                        || user.getLastTeleportTimer().hasNotPassed(20)
                        || !user.isChunkLoaded()
                        || user.getActionProcessor().getServerPositionTimer().hasNotPassed(3)) {
                    threshold = 0;
                    return;
                }

                if (user.getPlayer().isSneaking()) {
                    long diff = Math.abs(System.currentTimeMillis() - lastSneaking);

                    if (diff <= 0L) {
                        if (threshold++ > 12) {
                            flag(user, "Sneaking rapidly");
                        }
                    } else {
                        threshold -= Math.min(threshold, 0.25);
                    }

                    lastSneaking = System.currentTimeMillis();
                }


                break;
            }
        }
    }
}
