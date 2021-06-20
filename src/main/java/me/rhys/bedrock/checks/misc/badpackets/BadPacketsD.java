package me.rhys.bedrock.checks.misc.badpackets;

import me.rhys.bedrock.base.check.api.Check;
import me.rhys.bedrock.base.check.api.CheckInformation;
import me.rhys.bedrock.base.event.PacketEvent;
import me.rhys.bedrock.base.user.User;
import me.rhys.bedrock.tinyprotocol.api.Packet;
import me.rhys.bedrock.tinyprotocol.packet.in.WrappedInHeldItemSlotPacket;
import me.rhys.bedrock.tinyprotocol.packet.out.WrappedOutHeldItemSlot;
import me.rhys.bedrock.tinyprotocol.packet.out.WrappedOutTransaction;

@CheckInformation(checkName = "BadPackets", checkType = "D", lagBack = false, punishmentVL = 2)
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
                 ///   flag(user);
                }

                lastSlot = slot;
                break;
            }

            case Packet.Server.HELD_ITEM: {
                User user = event.getUser();

                WrappedOutHeldItemSlot heldItemSlot = new WrappedOutHeldItemSlot(event.getPacket(), user.getPlayer());

                int slot = heldItemSlot.getSlot();

                if (slot == lastSlot) {
             //       flag(user);
                }

                lastSlot = slot;
                break;
            }
        }
    }
}