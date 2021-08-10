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
    private double threshold;

    @Override
    public void onPacket(PacketEvent event) {
        switch (event.getType()) {

            case Packet.Client.HELD_ITEM_SLOT: {
                User user = event.getUser();

                if (user.shouldCancel() || user.getLastTeleportTimer().hasNotPassed(20)) {
                    threshold = 0;
                    return;
                }

                WrappedInHeldItemSlotPacket heldItemSlot = new WrappedInHeldItemSlotPacket(event.getPacket(), user.getPlayer());

                int slot = heldItemSlot.getSlot();

                if (slot == lastSlot) {
                    if (threshold++ > 1) {
                        flag(user, "Invalid slot packet");
                    }
                } else {
                    threshold -= Math.min(threshold, 0.25);
                }

                lastSlot = slot;
                break;
            }
        }
    }
}