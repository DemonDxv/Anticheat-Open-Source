package me.rhys.anticheat.checks.combat.killaura;

import me.rhys.anticheat.base.check.api.Check;
import me.rhys.anticheat.base.check.api.CheckInformation;
import me.rhys.anticheat.base.event.PacketEvent;
import me.rhys.anticheat.base.user.User;
import me.rhys.anticheat.tinyprotocol.api.Packet;
import me.rhys.anticheat.tinyprotocol.packet.in.WrappedInBlockDigPacket;
import me.rhys.anticheat.tinyprotocol.packet.in.WrappedInUseEntityPacket;
import org.bukkit.Bukkit;

@CheckInformation(checkName = "Killaura", checkType = "H", lagBack = false, description = "No Interact Autoblock", punishmentVL = 3)
public class KillauraH extends Check {

    private boolean interact;

    @Override
    public void onPacket(PacketEvent event) {
        User user = event.getUser();

        switch (event.getType()) {
            case Packet.Client.FLYING:
            case Packet.Client.LOOK:
            case Packet.Client.POSITION_LOOK:
            case Packet.Client.POSITION: {
                interact = false;
                break;
            }

            case Packet.Client.USE_ENTITY: {
                WrappedInUseEntityPacket useEntityPacket = new WrappedInUseEntityPacket(event.getPacket(), user.getPlayer());

                if (useEntityPacket.getAction() == WrappedInUseEntityPacket.EnumEntityUseAction.INTERACT
                        || useEntityPacket.getAction() == WrappedInUseEntityPacket.EnumEntityUseAction.INTERACT_AT) {
                    if (user.isSword(user.getPlayer().getItemInHand())) {
                        interact = true;
                    }
                }

                if (useEntityPacket.getAction() == WrappedInUseEntityPacket.EnumEntityUseAction.ATTACK) {
                    if (interact) {
                        flag(user, "Interacting while attacking");
                    }
                }

                break;
            }
        }
    }
}