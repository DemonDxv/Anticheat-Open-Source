package me.rhys.anticheat.checks.movement.sneak;

import me.rhys.anticheat.base.check.api.Check;
import me.rhys.anticheat.base.check.api.CheckInformation;
import me.rhys.anticheat.base.event.PacketEvent;
import me.rhys.anticheat.base.user.User;
import me.rhys.anticheat.tinyprotocol.api.Packet;
import me.rhys.anticheat.tinyprotocol.packet.in.WrappedInEntityActionPacket;
import me.rhys.anticheat.util.MathUtil;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;

@CheckInformation(checkName = "Sneak", checkType = "D", description = "Checks if lots of sneak packets are sent at once")
public class SneakD extends Check {

    private double sneakTicks, threshold;

    @Override
    public void onPacket(PacketEvent event) {

        User user = event.getUser();

        switch (event.getType()) {
            case Packet.Client.FLYING:
            case Packet.Client.POSITION:
            case Packet.Client.POSITION_LOOK:
            case Packet.Client.LOOK: {

                if (this.sneakTicks > 1) {
                    if (threshold++ > 2) {
                        this.flag(user, "Large amounts of sneak packets");
                    }
                } else {
                    threshold -= Math.min(threshold, 0.04f);
                }

                this.sneakTicks = 0;
                break;
            }

            case Packet.Client.ENTITY_ACTION: {
                WrappedInEntityActionPacket actionPacket =
                        new WrappedInEntityActionPacket(event.getPacket(), user.getPlayer());

                if (actionPacket.getAction() == WrappedInEntityActionPacket.EnumPlayerAction.START_SNEAKING) {
                    this.sneakTicks++;
                } else if (actionPacket.getAction() == WrappedInEntityActionPacket.EnumPlayerAction.STOP_SNEAKING) {
                    this.sneakTicks++;
                }
                break;
            }
        }
    }
}
