package me.rhys.anticheat.checks.misc.badpackets;

import me.rhys.anticheat.base.check.api.Check;
import me.rhys.anticheat.base.check.api.CheckInformation;
import me.rhys.anticheat.base.event.PacketEvent;
import me.rhys.anticheat.base.user.User;
import me.rhys.anticheat.tinyprotocol.api.Packet;
import me.rhys.anticheat.tinyprotocol.packet.in.WrappedInFlyingPacket;

@CheckInformation(checkName = "BadPackets", checkType = "B", lagBack = false, punishmentVL = 1)
public class BadPacketsB extends Check {

    private int streaks = 0;

    @Override
    public void onPacket(PacketEvent event) {
        switch (event.getType()) {
            case Packet.Client.FLYING:
            case Packet.Client.LOOK:
            case Packet.Client.POSITION_LOOK:
            case Packet.Client.POSITION: {

                User user = event.getUser();

                if (user.shouldCancel()
                        || user.getTick() < 60
                        || !user.isChunkLoaded()) {
                    streaks = 0;
                    return;
                }

                WrappedInFlyingPacket flyingPacket = new WrappedInFlyingPacket(event.getPacket(), user.getPlayer());

                if (flyingPacket.isPos() || user.getPlayer().isInsideVehicle()) {
                    streaks = 0;
                } else if (streaks++ > 20) {
                    flag(user, "Invalid Game Tick");
                }
                break;
            }
        }
    }
}