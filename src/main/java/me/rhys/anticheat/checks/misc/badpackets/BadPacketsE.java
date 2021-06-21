package me.rhys.anticheat.checks.misc.badpackets;

import me.rhys.anticheat.base.check.api.Check;
import me.rhys.anticheat.base.check.api.CheckInformation;
import me.rhys.anticheat.base.event.PacketEvent;
import me.rhys.anticheat.base.user.User;
import me.rhys.anticheat.tinyprotocol.api.Packet;
import me.rhys.anticheat.tinyprotocol.packet.in.WrappedInBlockDigPacket;

@CheckInformation(checkName = "BadPackets", checkType = "E", lagBack = false, punishmentVL = 10, canPunish = false)
public class BadPacketsE extends Check {

    private long lastFlying;
    private double threshold;

    @Override
    public void onPacket(PacketEvent event) {
        switch (event.getType()) {
            case Packet.Client.BLOCK_DIG: {
                User user = event.getUser();

                WrappedInBlockDigPacket digPacket = new WrappedInBlockDigPacket(event.getPacket(), user.getPlayer());

                if (digPacket.getAction() == WrappedInBlockDigPacket.EnumPlayerDigType.RELEASE_USE_ITEM) {
                    if ((System.currentTimeMillis() - lastFlying) < 5L) {
                        if (++threshold > 4) {
                            flag(user, "Digging Packet Sent Late");
                        }
                    } else {
                        threshold -= Math.min(threshold, 0.75);
                    }

                }

                break;
            }

            case Packet.Client.FLYING:
            case Packet.Client.LOOK:
            case Packet.Client.POSITION_LOOK:
            case Packet.Client.POSITION: {

                lastFlying = System.currentTimeMillis();

                break;
            }
        }
    }
}