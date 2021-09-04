package me.rhys.anticheat.checks.movement.sprint;

import me.rhys.anticheat.base.check.api.Check;
import me.rhys.anticheat.base.check.api.CheckInformation;
import me.rhys.anticheat.base.event.PacketEvent;
import me.rhys.anticheat.base.user.User;
import me.rhys.anticheat.tinyprotocol.api.Packet;
import me.rhys.anticheat.util.MathUtil;
import org.bukkit.Bukkit;

@CheckInformation(checkName = "Sprint", checkType = "B", canPunish = false, description = "Sprint cancel", enabled = false)
public class SprintB extends Check {

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
                        || user.getLastTeleportTimer().hasNotPassed(10
                        + user.getConnectionProcessor().getClientTick())
                        || user.getPlayer().isDead()
                        || user.getActionProcessor().getRespawnTimer().hasNotPassed(20
                        + user.getConnectionProcessor().getClientTick())
                        || !user.isChunkLoaded()
                        || user.getActionProcessor().getServerPositionTimer().hasNotPassed(10
                        + user.getConnectionProcessor().getClientTick())) {
                    return;
                }

                double deltaXZ = user.getMovementProcessor().getDeltaXZ();


                if (!user.getMovementProcessor().isSprinting() && !user.getMovementProcessor().isLastSprinting()
                        && deltaXZ > MathUtil.getBaseSpeed_2(user.getPlayer())) {
                    if (++threshold > 12) {
                        flag(user, "Possibly canceling sprint packets");
                    }
                } else {
                    threshold = 0;
                }

                break;
            }
        }
    }
}