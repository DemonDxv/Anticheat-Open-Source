package me.rhys.bedrock.checks.misc.badpackets;

import me.rhys.bedrock.base.check.api.Check;
import me.rhys.bedrock.base.check.api.CheckInformation;
import me.rhys.bedrock.base.event.PacketEvent;
import me.rhys.bedrock.base.user.User;
import me.rhys.bedrock.tinyprotocol.api.Packet;
import me.rhys.bedrock.tinyprotocol.packet.in.WrappedInFlyingPacket;
import me.rhys.bedrock.tinyprotocol.packet.in.WrappedInUseEntityPacket;

@CheckInformation(checkName = "BadPackets", checkType = "B", lagBack = false, punishmentVL = 1)
public class BadPacketsB extends Check {

    private int streaks;

    @Override
    public void onPacket(PacketEvent event) {
        switch (event.getType()) {
            case Packet.Client.FLYING:
            case Packet.Client.LOOK:
            case Packet.Client.POSITION_LOOK:
            case Packet.Client.POSITION: {

                User user = event.getUser();

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