package me.rhys.anticheat.checks.movement.phase;

import me.rhys.anticheat.base.check.api.Check;
import me.rhys.anticheat.base.check.api.CheckInformation;
import me.rhys.anticheat.base.event.PacketEvent;
import me.rhys.anticheat.base.user.User;
import me.rhys.anticheat.tinyprotocol.api.Packet;
import org.bukkit.Bukkit;

@CheckInformation(checkName = "Phase", canPunish = false)
public class PhaseA extends Check {

    private double threshold;

    @Override
    public void onPacket(PacketEvent event) {
        User user = event.getUser();

        switch (event.getType()) {
            case Packet.Client.FLYING:
            case Packet.Client.LOOK:
            case Packet.Client.POSITION_LOOK:
            case Packet.Client.POSITION: {

                if (user.shouldCancel()
                        || user.getTick() < 60
                        || user.getBlockData().bedTicks > 0
                        || user.getBlockData().cakeTicks > 0
                        || user.getBlockData().pistonTicks > 0
                        || user.getBlockData().waterTicks > 0
                        || user.getBlockData().lavaTicks > 0
                        || user.getBlockData().skullTicks > 0
                        || user.getBlockData().door
                        || user.getBlockData().stairSlabTimer.hasNotPassed(20)
                        || user.getBlockData().underBlockTicks > 0) {
                    threshold = 0;
                    return;
                }

                double deltaXZ = user.getMovementProcessor().getDeltaXZ();

                if (user.getLastTeleportTimer().passed(20) && user.getBlockData().insideBlock) {

                    double max = 0.4;

                    if (deltaXZ > max) {
                        if (++threshold > 5) {
                            flag(user, "Possibly phasing");
                        }

                        user.getPlayer().teleport(user.getMovementProcessor().getLastOutOfBlockLocation());

                    } else {
                        threshold -= Math.min(threshold, 0.1);
                    }

                //    Bukkit.broadcastMessage(""+deltaXZ + " "+deltaY);
                }

                break;
            }
        }
    }
}