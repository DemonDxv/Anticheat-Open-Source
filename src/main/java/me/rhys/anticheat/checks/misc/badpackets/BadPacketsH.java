package me.rhys.anticheat.checks.misc.badpackets;

import me.rhys.anticheat.base.check.api.Check;
import me.rhys.anticheat.base.check.api.CheckInformation;
import me.rhys.anticheat.base.event.PacketEvent;
import me.rhys.anticheat.base.user.User;
import me.rhys.anticheat.tinyprotocol.api.Packet;
import me.rhys.anticheat.tinyprotocol.packet.in.WrappedInBlockPlacePacket;
import me.rhys.anticheat.tinyprotocol.packet.in.WrappedInCustomPayloadPacket;
import org.bukkit.Bukkit;

@CheckInformation(checkName = "BadPackets", checkType = "H", lagBack = false, punishmentVL = 10, canPunish = false)
public class BadPacketsH extends Check {

    private int armSent;
    private double threshold;

    @Override
    public void onPacket(PacketEvent event) {
        User user = event.getUser();

        switch (event.getType()) {
            case Packet.Client.ARM_ANIMATION: {
                armSent = 5;
                break;
            }

            case Packet.Client.BLOCK_PLACE: {

                WrappedInBlockPlacePacket blockPlace =
                        new WrappedInBlockPlacePacket(event.getPacket(), user.getPlayer());

                int faceInt = blockPlace.getFace().b();

                double yaw = Math.abs(user.getCurrentLocation().getYaw() - user.getLastLocation().getYaw());

                if (yaw > 0) {
                    if (faceInt >= -1 && faceInt <= 3 && user.getLastBlockPlaceTimer().hasNotPassed(3)) {
                        if (armSent == 0) {
                            if (threshold++ > 4.5) {
                                flag(user, "Invalid Block Place");
                            }
                        } else {
                            threshold = 0;
                        }
                    }
                }

                break;
            }

            case Packet.Client.FLYING:
            case Packet.Client.LOOK:
            case Packet.Client.POSITION_LOOK:
            case Packet.Client.POSITION: {
                if (armSent > 0) {
                    armSent--;
                }
                break;
            }
        }
    }
}