package me.rhys.anticheat.checks.misc.badpackets;

import me.rhys.anticheat.base.check.api.Check;
import me.rhys.anticheat.base.check.api.CheckInformation;
import me.rhys.anticheat.base.event.PacketEvent;
import me.rhys.anticheat.base.user.User;
import me.rhys.anticheat.tinyprotocol.api.Packet;
import me.rhys.anticheat.tinyprotocol.packet.in.WrappedInCustomPayloadPacket;

@CheckInformation(checkName = "BadPackets", checkType = "G", lagBack = false, punishmentVL = 10, canPunish = false)
public class BadPacketsG extends Check {

    private int threshold;

    @Override
    public void onPacket(PacketEvent event) {
        switch (event.getType()) {
            case Packet.Client.CUSTOM_PAYLOAD: {
                User user = event.getUser();

                WrappedInCustomPayloadPacket packet
                        = new WrappedInCustomPayloadPacket(event.getPacket());

                String tag = packet.getTag();

                if (tag.equals("MC|Brand") || tag.equals("REGISTER")) {
                    threshold++;
                    if (threshold > 2) {
                        flag(user, "Invalid CustomPayloads");
                    }
                }

                break;
            }
        }
    }
}