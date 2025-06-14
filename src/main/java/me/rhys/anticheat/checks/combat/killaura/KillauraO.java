package me.rhys.anticheat.checks.combat.killaura;

import me.rhys.anticheat.base.check.api.Check;
import me.rhys.anticheat.base.check.api.CheckInformation;
import me.rhys.anticheat.base.event.PacketEvent;
import me.rhys.anticheat.base.user.User;
import me.rhys.anticheat.tinyprotocol.api.Packet;
import me.rhys.anticheat.tinyprotocol.packet.in.WrappedInUseEntityPacket;

@CheckInformation(checkName = "Killaura", checkType = "O", canPunish = false, description = "Switch Aura Check")
public class KillauraO extends Check {

    private int threshold;

    @Override
    public void onPacket(PacketEvent event) {
        User user = event.getUser();

        switch (event.getType()) {

            case Packet.Client.USE_ENTITY: {
                WrappedInUseEntityPacket packet = new WrappedInUseEntityPacket(event.getPacket(), user.getPlayer());

                if (packet.getAction() == WrappedInUseEntityPacket.EnumEntityUseAction.ATTACK) {
                    if (user.getCombatProcessor().getLastAttackedEntity() != null
                            && user.getCombatProcessor().getLastLastAttackedEntity() != null) {
                        if (user.getCombatProcessor().getLastAttackedEntity().getEntityId() !=
                                user.getCombatProcessor().getLastLastAttackedEntity().getEntityId()) {

                            if (++threshold > 2) {
                                flag(user, "Multi-Aura/Switching between entities rapidly");
                            }
                        } else {
                            threshold = 0;
                        }
                    } else {
                        threshold = 0;
                    }
                }

                break;
            }


            case Packet.Client.FLYING:
            case Packet.Client.LOOK:
            case Packet.Client.POSITION_LOOK:
            case Packet.Client.POSITION: {
                if (user.getCombatProcessor().getUseEntityTimer().passed(0)) {
                    threshold = 0;
                }
                break;
            }
        }
    }
}