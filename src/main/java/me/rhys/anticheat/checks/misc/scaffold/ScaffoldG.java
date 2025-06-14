package me.rhys.anticheat.checks.misc.scaffold;

import me.rhys.anticheat.base.check.api.Check;
import me.rhys.anticheat.base.check.api.CheckInformation;
import me.rhys.anticheat.base.event.PacketEvent;
import me.rhys.anticheat.base.user.User;
import me.rhys.anticheat.tinyprotocol.api.Packet;

@CheckInformation(checkName = "Scaffold", checkType = "G", lagBack = false, description = "Inventory Blocking", punishmentVL = 3)
public class ScaffoldG extends Check {

    @Override
    public void onPacket(PacketEvent event) {
        User user = event.getUser();

        if (event.getType().equals(Packet.Client.BLOCK_PLACE)) {
            if (user.shouldCancel() || user.getTick() < 60) {
                return;
            }

            if (user.getMovementProcessor().isInInventory()) {
                devFlag(user, "Blocking/Placing blocks while in inventory");
            }
        }
    }
}