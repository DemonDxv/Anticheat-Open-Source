package me.rhys.anticheat.checks.movement.flight;

import me.rhys.anticheat.base.check.api.Check;
import me.rhys.anticheat.base.check.api.CheckInformation;
import me.rhys.anticheat.base.event.PacketEvent;
import me.rhys.anticheat.base.user.User;
import me.rhys.anticheat.tinyprotocol.api.Packet;
import me.rhys.anticheat.util.MathUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.player.PlayerTeleportEvent;

@CheckInformation(checkName = "Flight", checkType = "F", description = "Checks if the player tries to spoof on ground.")
public class FlightF extends Check {

    private double threshold;

    @Override
    public void onPacket(PacketEvent event) {
        switch (event.getType()) {
            case Packet.Client.FLYING:
            case Packet.Client.LOOK:
            case Packet.Client.POSITION_LOOK:
            case Packet.Client.POSITION: {
                User user = event.getUser();

                if (this.checkConditions(user)
                        || user.getLastBlockPlaceTimer().hasNotPassed(20)
                        || user.getLastBlockPlaceCancelTimer().hasNotPassed(5)
                        || user.getLastTeleportTimer().hasNotPassed(20)) {
                    this.threshold = 0;
                    return;
                }

                if (user.getActionProcessor().getServerPositionTimer().hasNotPassed(3)
                        && user.getMovementProcessor().getDeltaXZ() < 0.39) {
                    threshold -= Math.min(threshold, 0.90);
                }

                if (!user.getBlockData().onGround && !user.getBlockData().lastOnGround) {
                    if (user.getMovementProcessor().isOnGround()) {

                        if ((threshold += 1.25) >= 4.3) {
                            flag(user, "Spoofing Ground");
                        }
                    } else {
                        threshold -= Math.min(threshold, 0.001f);
                    }
                }
            }
        }
    }

    boolean checkConditions(User user) {
        return user.getBlockData().liquidTicks > 0
                || user.getTick() < 60
                || user.shouldCancel()
                || user.getBlockData().climbableTicks > 0;
    }
}
