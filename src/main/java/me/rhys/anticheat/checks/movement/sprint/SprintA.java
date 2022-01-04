package me.rhys.anticheat.checks.movement.sprint;

import me.rhys.anticheat.base.check.api.Check;
import me.rhys.anticheat.base.check.api.CheckInformation;
import me.rhys.anticheat.base.event.PacketEvent;
import me.rhys.anticheat.base.user.User;
import me.rhys.anticheat.tinyprotocol.api.Packet;
import me.rhys.anticheat.util.MathUtil;
import org.bukkit.Bukkit;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@CheckInformation(checkName = "Sprint", canPunish = false, description = "OmniSprint detection")
public class SprintA extends Check {

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
                        || user.getTick() < 250
                        || user.getLastTeleportTimer().hasNotPassed(10
                        + user.getConnectionProcessor().getClientTick())
                        || user.getPlayer().isDead()
                        || user.getCombatProcessor().getVelocityTicks() <= 10
                        + user.getConnectionProcessor().getClientTick()
                        || !user.isChunkLoaded()
                        || user.getActionProcessor().getRespawnTimer().hasNotPassed(20
                        + user.getConnectionProcessor().getClientTick())
                        || user.getActionProcessor().getServerPositionTimer().hasNotPassed(10
                        + user.getConnectionProcessor().getClientTick())) {
                    return;
                }

                float moveAngle = MathUtil.getMoveAngle(user.getLastLocation(), user.getCurrentLocation());

                double deltaXZ = user.getMovementProcessor().getDeltaXZ();

                if (user.getCurrentLocation() != null) {
                    if (moveAngle > 90.0F && deltaXZ > MathUtil.getBaseSpeed_2(user.getPlayer())
                            && user.getMovementProcessor().isSprinting()) {
                        if (++threshold > 9) {
                            flag(user, "Omni-Sprint");
                            user.getPlayer().setSprinting(false);
                        }
                    } else {
                        threshold = 0;
                    }
                }

                break;
            }
        }
    }
}