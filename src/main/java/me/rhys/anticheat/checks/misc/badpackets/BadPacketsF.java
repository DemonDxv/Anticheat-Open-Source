package me.rhys.anticheat.checks.misc.badpackets;

import me.rhys.anticheat.base.check.api.Check;
import me.rhys.anticheat.base.check.api.CheckInformation;
import me.rhys.anticheat.base.event.PacketEvent;
import me.rhys.anticheat.base.user.User;
import me.rhys.anticheat.tinyprotocol.api.Packet;
import me.rhys.anticheat.tinyprotocol.packet.in.WrappedInBlockDigPacket;
import me.rhys.anticheat.tinyprotocol.packet.in.WrappedInBlockPlacePacket;
import me.rhys.anticheat.tinyprotocol.packet.in.WrappedInHeldItemSlotPacket;
import org.bukkit.Bukkit;
import org.bukkit.Material;

@CheckInformation(checkName = "BadPackets", checkType = "F", lagBack = false, punishmentVL = 10, canPunish = false)
public class BadPacketsF extends Check {

    private double threshold;

    private int potionTick;

    @Override
    public void onPacket(PacketEvent event) {
        switch (event.getType()) {
            case Packet.Client.BLOCK_PLACE: {
                User user = event.getUser();

                potionTick = 0;

                break;
            }

            case Packet.Client.FLYING:
            case Packet.Client.LOOK:
            case Packet.Client.POSITION_LOOK:
            case Packet.Client.POSITION: {

                User user = event.getUser();

                potionTick++;

                double pitchChange = Math.abs(user.getCurrentLocation().getPitch() - user.getLastLocation().getPitch());

                double yawChange = Math.abs(user.getCurrentLocation().getYaw() - user.getLastLocation().getYaw());

                if (potionTick < 4 && user.getPlayer().getItemInHand().getType() == Material.POTION) {
                    if (pitchChange >= 80) {
                        flag(user, "Potentially using Auto-Pot");
                    }

                    if (yawChange > 5000) {
                        if (threshold++ > 4) {
                      //      flag(user, "Potentially using Auto-Pot (POSSIBLY FALSE)");
                        }
                    } else {
                        threshold -= Math.min(threshold, 0.85);
                    }
                }


                break;
            }
        }
    }
}