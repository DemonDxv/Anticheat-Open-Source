package me.rhys.anticheat.checks.movement.phase;

import me.rhys.anticheat.base.check.api.Check;
import me.rhys.anticheat.base.check.api.CheckInformation;
import me.rhys.anticheat.base.event.PacketEvent;
import me.rhys.anticheat.base.user.User;
import me.rhys.anticheat.tinyprotocol.api.Packet;
import org.bukkit.Bukkit;

@CheckInformation(checkName = "Phase", checkType = "B", punishmentVL = 3)
public class PhaseB extends Check {

    @Override
    public void onPacket(PacketEvent event) {
        User user = event.getUser();

        switch (event.getType()) {
            case Packet.Client.FLYING:
            case Packet.Client.LOOK:
            case Packet.Client.POSITION_LOOK:
            case Packet.Client.POSITION: {

                if (user.shouldCancel()
                        || user.getLastTeleportTimer().hasNotPassed(20)
                        || user.getMovementProcessor().isBouncedOnSlime()
                        || user.getActionProcessor().getServerPositionTimer().hasNotPassed(5)
                        || user.getBlockData().slimeTimer.hasNotPassed(20)
                        || !user.isChunkLoaded()
                        || user.getTick() < 60) {
                    return;
                }

                double deltaY = user.getMovementProcessor().getDeltaY();
                double lastDeltaY = user.getMovementProcessor().getLastDeltaY();

                if (user.getMovementProcessor().getAirTicks() <= 6 && !user.getMovementProcessor().isOnGround()) {
                    if (deltaY < -1 && lastDeltaY < 0.42f || deltaY > 1 && lastDeltaY < 0.42f) {
                        flag(user, "Possibly VClipping");
                    }
                }

                break;
            }
        }
    }
}