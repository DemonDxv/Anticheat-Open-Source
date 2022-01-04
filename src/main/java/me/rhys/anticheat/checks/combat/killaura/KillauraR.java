package me.rhys.anticheat.checks.combat.killaura;

import me.rhys.anticheat.base.check.api.Check;
import me.rhys.anticheat.base.check.api.CheckInformation;
import me.rhys.anticheat.base.event.PacketEvent;
import me.rhys.anticheat.base.user.User;
import me.rhys.anticheat.tinyprotocol.api.Packet;
import me.rhys.anticheat.tinyprotocol.packet.in.WrappedInBlockDigPacket;
import me.rhys.anticheat.tinyprotocol.packet.in.WrappedInBlockPlacePacket;
import me.rhys.anticheat.tinyprotocol.packet.in.WrappedInUseEntityPacket;
import org.bukkit.Bukkit;

@CheckInformation(checkName = "Killaura", checkType = "R", lagBack = false, description = "Invalid Block Values")
public class KillauraR extends Check {

    private int blockX, blockY, blockZ;
    private double threshold;

    @Override
    public void onPacket(PacketEvent event) {
        User user = event.getUser();

        switch (event.getType()) {

            case Packet.Client.BLOCK_PLACE: {
                WrappedInBlockPlacePacket placePacket = new WrappedInBlockPlacePacket(event.getPacket(), user.getPlayer());

                if (user.isSword(placePacket.getItemStack())) {
                    blockX = placePacket.getPosition().getX();
                    blockY = placePacket.getPosition().getY();
                    blockZ = placePacket.getPosition().getZ();
                }

                break;
            }

            case Packet.Client.FLYING:
            case Packet.Client.LOOK:
            case Packet.Client.POSITION_LOOK:
            case Packet.Client.POSITION: {


                if (blockZ != 0 && blockY != 0 && blockX != 0) {
                    if (user.getCombatProcessor().getUseEntityTimer().hasNotPassed(1)) {
                        if (blockX != -1 || blockY != -1 || blockZ != -1) {
                            flag(user, "Invalid blocking state");
                        }
                    }
                }

                blockZ = blockX = blockY = 0;
                break;
            }
        }
    }
}