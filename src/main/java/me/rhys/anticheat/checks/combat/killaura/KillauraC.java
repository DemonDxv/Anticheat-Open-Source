package me.rhys.anticheat.checks.combat.killaura;

import me.rhys.anticheat.base.check.api.Check;
import me.rhys.anticheat.base.check.api.CheckInformation;
import me.rhys.anticheat.base.event.PacketEvent;
import me.rhys.anticheat.base.user.User;
import me.rhys.anticheat.tinyprotocol.api.Packet;
import me.rhys.anticheat.tinyprotocol.packet.in.WrappedInBlockDigPacket;
import me.rhys.anticheat.tinyprotocol.packet.in.WrappedInUseEntityPacket;

@CheckInformation(checkName = "Killaura", checkType = "C", lagBack = false, description = "AutoBlock Check")
public class KillauraC extends Check {

    private boolean block, dig;

    @Override
    public void onPacket(PacketEvent event) {
        User user = event.getUser();

        switch (event.getType()) {
            case Packet.Client.FLYING:
            case Packet.Client.LOOK:
            case Packet.Client.POSITION_LOOK:
            case Packet.Client.POSITION: {
                block = dig = false;

                break;
            }

            case Packet.Client.USE_ENTITY: {
                WrappedInUseEntityPacket useEntityPacket = new WrappedInUseEntityPacket(event.getPacket(), user.getPlayer());

                if (useEntityPacket.getAction() == WrappedInUseEntityPacket.EnumEntityUseAction.ATTACK) {
                    if (block || dig) {
                        flag(user, "AutoBlock");
                    }
                }

                break;
            }

            case Packet.Client.BLOCK_DIG: {
                WrappedInBlockDigPacket digPacket = new WrappedInBlockDigPacket(event.getPacket(), user.getPlayer());

                if (digPacket.getAction() == WrappedInBlockDigPacket.EnumPlayerDigType.RELEASE_USE_ITEM) {
                    dig = true;
                }

                break;
            }

            case Packet.Client.BLOCK_PLACE: {
                block = true;
                break;
            }
        }
    }
}