package me.rhys.anticheat.checks.misc.badpackets;

import me.rhys.anticheat.base.check.api.Check;
import me.rhys.anticheat.base.check.api.CheckInformation;
import me.rhys.anticheat.base.event.PacketEvent;
import me.rhys.anticheat.base.user.User;
import me.rhys.anticheat.tinyprotocol.api.Packet;
import me.rhys.anticheat.tinyprotocol.packet.in.WrappedInBlockDigPacket;
import me.rhys.anticheat.tinyprotocol.packet.in.WrappedInBlockPlacePacket;
import org.bukkit.Bukkit;

@CheckInformation(checkName = "BadPackets", checkType = "H", lagBack = false, punishmentVL = 10, canPunish = false)
public class BadPacketsI extends Check {

    private long lastDig;
    private double threshold;

    @Override
    public void onPacket(PacketEvent event) {
        User user = event.getUser();

        switch (event.getType()) {
            case Packet.Client.BLOCK_DIG: {
                WrappedInBlockDigPacket digPacket = new WrappedInBlockDigPacket(event.getPacket(), user.getPlayer());

                if (digPacket.getAction() == WrappedInBlockDigPacket.EnumPlayerDigType.START_DESTROY_BLOCK) {
                    lastDig = System.currentTimeMillis();
                } else if (digPacket.getAction() == WrappedInBlockDigPacket.EnumPlayerDigType.ABORT_DESTROY_BLOCK) {
                    lastDig = System.currentTimeMillis();
                } else if (digPacket.getAction() == WrappedInBlockDigPacket.EnumPlayerDigType.STOP_DESTROY_BLOCK) {
                    lastDig = System.currentTimeMillis();
                }
                break;
            }

            case Packet.Client.BLOCK_PLACE: {


                break;
            }

            case Packet.Client.FLYING:
            case Packet.Client.LOOK:
            case Packet.Client.POSITION_LOOK:
            case Packet.Client.POSITION: {
                if (user.getLastBlockBreakTimer().hasNotPassed(5)) {
                  //  Bukkit.broadcastMessage(""+(System.currentTimeMillis() - lastDig));
                }

                break;
            }
        }
    }
}