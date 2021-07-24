package me.rhys.anticheat.checks.misc.badpackets;

import me.rhys.anticheat.base.check.api.Check;
import me.rhys.anticheat.base.check.api.CheckInformation;
import me.rhys.anticheat.base.event.PacketEvent;
import me.rhys.anticheat.base.user.User;
import me.rhys.anticheat.tinyprotocol.api.Packet;
import me.rhys.anticheat.tinyprotocol.packet.in.WrappedInHeldItemSlotPacket;
import me.rhys.anticheat.tinyprotocol.packet.out.WrappedOutHeldItemSlot;

@CheckInformation(checkName = "BadPackets", checkType = "D", lagBack = false, canPunish = false)
public class BadPacketsD extends Check {

    private int lastSlot;

    @Override
    public void onPacket(PacketEvent event) {
        switch (event.getType()) {

            case Packet.Client.HELD_ITEM_SLOT: {
                User user = event.getUser();

                WrappedInHeldItemSlotPacket heldItemSlot = new WrappedInHeldItemSlotPacket(event.getPacket(), user.getPlayer());

                int slot = heldItemSlot.getSlot();

                if (slot == lastSlot) {
                    flag(user, "Invalid slot packet");
                }

                lastSlot = slot;
                break;
            }
        }
    }
}