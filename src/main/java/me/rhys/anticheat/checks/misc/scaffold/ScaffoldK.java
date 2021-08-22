package me.rhys.anticheat.checks.misc.scaffold;

import me.rhys.anticheat.base.check.api.Check;
import me.rhys.anticheat.base.check.api.CheckInformation;
import me.rhys.anticheat.base.event.PacketEvent;
import me.rhys.anticheat.base.user.User;
import me.rhys.anticheat.tinyprotocol.api.Packet;
import me.rhys.anticheat.tinyprotocol.packet.in.WrappedInBlockPlacePacket;
import me.rhys.anticheat.tinyprotocol.packet.in.WrappedInUseEntityPacket;
import org.bukkit.Material;

@CheckInformation(checkName = "Scaffold", checkType = "K", lagBack = false, punishmentVL = 5)
public class ScaffoldK extends Check {

    @Override
    public void onPacket(PacketEvent event) {
        User user = event.getUser();

        switch (event.getType()) {

            case Packet.Client.BLOCK_PLACE: {

                WrappedInBlockPlacePacket blockPlace =
                        new WrappedInBlockPlacePacket(event.getPacket(), user.getPlayer());

                if (user.getTick() < 60 || user.shouldCancel()) {
                    return;
                }

                if (user.getLastBlockPlaceTimer().hasNotPassed(5)) {
                    if (user.getPlayer().getEyeLocation().add(0, -1, 0).getBlock().getType() == Material.AIR) {

                        int faceInt = blockPlace.getFace().b();

                        if (faceInt >= 0 && faceInt <= 3) {
                            double deltaYaw = user.getMovementProcessor().getYawDeltaClamped();

                            double deltaMouse = Math.abs(deltaYaw - user.getMouseDeltaX());

                            if (deltaMouse > 60.0 && deltaMouse != 360 && deltaMouse < 360) {
                                if (deltaYaw >= 100) {
                                    flag(user, "Head Snapping", "dm=" + deltaMouse + ", y=" + deltaYaw);
                                }
                            }
                        }
                    }
                }

                break;
            }
        }
    }
}
