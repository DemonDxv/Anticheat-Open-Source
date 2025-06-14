package me.rhys.anticheat.checks.misc.badpackets;

import me.rhys.anticheat.base.check.api.Check;
import me.rhys.anticheat.base.check.api.CheckInformation;
import me.rhys.anticheat.base.event.PacketEvent;
import me.rhys.anticheat.base.user.User;
import me.rhys.anticheat.tinyprotocol.api.Packet;
import me.rhys.anticheat.tinyprotocol.packet.in.WrappedInCustomPayloadPacket;

@CheckInformation(checkName = "BadPackets", checkType = "F", lagBack = false, punishmentVL = 10, canPunish = false)
public class BadPacketsF extends Check {

    @Override
    public void onPacket(PacketEvent event) {
        if (event.getType().equals(Packet.Client.CUSTOM_PAYLOAD)) {
            User user = event.getUser();

            WrappedInCustomPayloadPacket packet
                = new WrappedInCustomPayloadPacket(event.getPacket());

            if (packet.getDecodedData().equals("Vanilla")) {
                flag(user, "Modded Client");
            }
        }
    }
}
