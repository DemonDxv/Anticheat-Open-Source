package me.rhys.anticheat.checks.combat.killaura;

import me.rhys.anticheat.base.check.api.Check;
import me.rhys.anticheat.base.check.api.CheckInformation;
import me.rhys.anticheat.base.event.PacketEvent;
import me.rhys.anticheat.base.user.User;
import me.rhys.anticheat.tinyprotocol.api.Packet;
import me.rhys.anticheat.tinyprotocol.packet.in.WrappedInBlockDigPacket;
import me.rhys.anticheat.tinyprotocol.packet.in.WrappedInUseEntityPacket;
import org.bukkit.Bukkit;

@CheckInformation(checkName = "Killaura", checkType = "N", lagBack = false, description = "No Interact Check")
public class KillauraN extends Check {

    private int interactTick, attackTick;

    @Override
    public void onPacket(PacketEvent event) {
        User user = event.getUser();

        switch (event.getType()) {

            case Packet.Client.USE_ENTITY: {
                WrappedInUseEntityPacket packet = new WrappedInUseEntityPacket(event.getPacket(), user.getPlayer());

                if (packet.getAction() == WrappedInUseEntityPacket.EnumEntityUseAction.INTERACT) {
                    interactTick = user.getTick();
                }

                if (packet.getAction() == WrappedInUseEntityPacket.EnumEntityUseAction.ATTACK) {
                    attackTick = user.getTick();
                }
                break;
            }

            case Packet.Client.BLOCK_DIG: {
                WrappedInBlockDigPacket digPacket = new WrappedInBlockDigPacket(event.getPacket(), user.getPlayer());

                if (digPacket.getAction() == WrappedInBlockDigPacket.EnumPlayerDigType.RELEASE_USE_ITEM) {
                    if (user.getCombatProcessor().getUseEntityTimer().hasNotPassed(1)) {
                        double changeTick = Math.abs(attackTick - interactTick);

                        if (changeTick > 100) {
                            flag(user, "No interact while blocking");
                        }
                    }
                }
                break;
            }
        }
    }
}