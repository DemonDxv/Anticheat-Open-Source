package me.rhys.anticheat.checks.movement.flight;

import me.rhys.anticheat.base.check.api.Check;
import me.rhys.anticheat.base.check.api.CheckInformation;
import me.rhys.anticheat.base.event.PacketEvent;
import me.rhys.anticheat.base.user.User;
import me.rhys.anticheat.tinyprotocol.api.Packet;
import me.rhys.anticheat.util.EntityUtil;
import me.rhys.anticheat.util.MathUtil;
import org.bukkit.Location;
import org.bukkit.event.player.PlayerTeleportEvent;

@CheckInformation(checkName = "Flight", checkType = "B", canPunish = false, description = "Checks if the player is spoofing ground while 1/64")
public class FlightB extends Check {

    private int threshold;

    @Override
    public void onPacket(PacketEvent event) {
        User user = event.getUser();

        switch (event.getType()) {
            case Packet.Client.FLYING:
            case Packet.Client.LOOK:
            case Packet.Client.POSITION_LOOK:
            case Packet.Client.POSITION: {

                if (user.shouldCancel()
                        || user.getActionProcessor().getServerPositionTimer().hasNotPassed(3)
                        || user.getLastTeleportTimer().hasNotPassed(20)
                        || user.getMovementProcessor().isBouncedOnSlime()
                        || user.getVehicleTicks() > 0
                        || EntityUtil.isOnBoat(user)
                        || user.getBlockData().webTicks > 0
                        || user.getBlockData().cakeTicks > 0
                        || user.getBlockData().climbableTicks > 0
                        || user.getCombatProcessor().getVelocityTicks() <= 20
                        || user.getBlockData().liquidTicks > 0
                        || user.getBlockData().lavaTicks > 0
                        || user.getBlockData().lillyPad
                        || user.getBlockData().carpet
                        || user.getBlockData().snow
                        || user.getTick() < 60) {
                    return;
                }


                boolean ground = user.getMovementProcessor().isOnGround();

                boolean serverPositionGround = user.getMovementProcessor().isServerYGround();

                if (ground && serverPositionGround
                        && !user.getBlockData().onGround
                        && !user.getBlockData().lastOnGround) {

                    Location groundLocation = user.getMovementProcessor().getLastGroundLocation();

                    user.getPlayer().teleport(groundLocation,
                            PlayerTeleportEvent.TeleportCause.PLUGIN);


                    if (threshold++ > 3) {
                        flag(user, "Possibly using a Fly/Ground Spoof");
                    }
                } else {
                    threshold -= Math.min(threshold, 0.001f);
                }
            }
        }
    }
}