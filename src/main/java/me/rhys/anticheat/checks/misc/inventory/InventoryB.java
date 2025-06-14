package me.rhys.anticheat.checks.misc.inventory;

import me.rhys.anticheat.base.check.api.Check;
import me.rhys.anticheat.base.check.api.CheckInformation;
import me.rhys.anticheat.base.event.PacketEvent;
import me.rhys.anticheat.base.user.User;
import me.rhys.anticheat.tinyprotocol.api.Packet;
import me.rhys.anticheat.tinyprotocol.packet.in.WrappedInWindowClickPacket;

@CheckInformation(checkName = "Inventory", checkType = "B", lagBack = false, canPunish = false)
public class InventoryB extends Check {

    @Override
    public void onPacket(PacketEvent event) {
        if (event.getType().equals(Packet.Client.WINDOW_CLICK)) {
            User user = event.getUser();

            if (user.shouldCancel() || user.getTick() < 60 || !user.isChunkLoaded()) {
                return;
            }

            WrappedInWindowClickPacket clickPacket =
                new WrappedInWindowClickPacket(event.getPacket(), user.getPlayer());

            if (clickPacket.getId() == 0) {
                if (!user.getMovementProcessor().isInInventory()) {
                    flag(user, "Clicking in inventory while its not open");
                    user.getPlayer().closeInventory();
                    user.getMovementProcessor().setInInventory(false);
                }
            }
        }
    }
}
