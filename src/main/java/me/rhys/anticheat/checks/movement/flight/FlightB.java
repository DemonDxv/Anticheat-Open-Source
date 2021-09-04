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
                        || !user.isChunkLoaded()
                        || user.getLastBlockPlaceTimer().hasNotPassed(20 + user.getConnectionProcessor().getClientTick())
                        || user.getLastBlockPlaceCancelTimer().hasNotPassed(20 + user.getConnectionProcessor().getClientTick())
                        || user.getTick() < 60) {
                    return;
                }

                if (user.getGhostBlockProcessor().getGhostBlockTeleportTimer().hasNotPassed(1)) {
                    if (++threshold > 6) {
                        flag(user, "Possibly using Fly/Nofall");
                    }
                } else {
                    threshold = 0;
                }
            }
        }
    }
}