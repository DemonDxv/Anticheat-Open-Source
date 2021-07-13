package me.rhys.anticheat.checks.movement.flight;

import me.rhys.anticheat.base.check.api.Check;
import me.rhys.anticheat.base.check.api.CheckInformation;
import me.rhys.anticheat.base.event.PacketEvent;
import me.rhys.anticheat.base.user.User;
import me.rhys.anticheat.tinyprotocol.api.Packet;
import me.rhys.anticheat.util.EntityUtil;
import me.rhys.anticheat.util.MathUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.player.PlayerTeleportEvent;

@CheckInformation(checkName = "Flight", checkType = "B", description = "Spoof Ground Check (ghost block fly)", canPunish = false)
public class FlightB extends Check {

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
                        || user.getActionProcessor().getServerPositionTimer().hasNotPassed(3)
                        || user.getVehicleTicks() > 0
                        || user.getBlockData().snowTicks > 0
                        || user.getBlockData().skullTicks > 0
                        || EntityUtil.isOnBoat(user)
                        || user.getLastBlockPlaceCancelTimer().hasNotPassed(20)
                        || user.getLastBlockPlaceTimer().hasNotPassed(5)
                        || user.getLastTeleportTimer().hasNotPassed(20)) {
                    return;
                }

                Location groundLocation = MathUtil.getGroundLocation(user);

                if (!user.getBlockData().onGround && !user.getBlockData().lastOnGround) {
                    if (user.getMovementProcessor().isOnGround() && user.getMovementProcessor().isServerYGround()) {
                        threshold++;

                        if (threshold > 3) {
                            flag(user, "Ghost Block / Ghost Block Fly");
                        }

                        user.getPlayer().teleport(groundLocation,
                                PlayerTeleportEvent.TeleportCause.PLUGIN);
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
