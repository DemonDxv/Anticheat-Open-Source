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

        switch (event.getType()) {

            case Packet.Client.BLOCK_PLACE: {
                if (user.getMovementProcessor().isInInventory()) {
                    flag(user, "Blocking/Placing blocks while in inventory");
                }
                break;
            }
        }
    }
}