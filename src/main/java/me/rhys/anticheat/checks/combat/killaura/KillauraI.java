package me.rhys.anticheat.checks.combat.killaura;

import me.rhys.anticheat.base.check.api.Check;
import me.rhys.anticheat.base.check.api.CheckInformation;
import me.rhys.anticheat.base.event.PacketEvent;
import me.rhys.anticheat.base.user.User;
import me.rhys.anticheat.tinyprotocol.api.Packet;
import me.rhys.anticheat.tinyprotocol.packet.in.WrappedInUseEntityPacket;

@CheckInformation(checkName = "Killaura", checkType = "I", lagBack = false, description = "Inventory Attack/Interact", punishmentVL = 3)
public class KillauraI extends Check {

    @Override
    public void onPacket(PacketEvent event) {
        User user = event.getUser();

        if (event.getType().equals(Packet.Client.USE_ENTITY)) {
            WrappedInUseEntityPacket useEntityPacket =
                new WrappedInUseEntityPacket(event.getPacket(), user.getPlayer());

            switch (useEntityPacket.getAction()) {
                case ATTACK:
                case INTERACT:
                case INTERACT_AT: {
                    if (user.getMovementProcessor().isInInventory()) {
                        flag(user, "Attacking / Interacting while in inventory");
                    }
                    break;
                }
            }
        }
    }
}