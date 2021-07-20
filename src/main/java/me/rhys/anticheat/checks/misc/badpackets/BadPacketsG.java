package me.rhys.anticheat.checks.misc.badpackets;

import me.rhys.anticheat.base.check.api.Check;
import me.rhys.anticheat.base.check.api.CheckInformation;
import me.rhys.anticheat.base.event.PacketEvent;
import me.rhys.anticheat.base.user.User;
import me.rhys.anticheat.tinyprotocol.api.Packet;
import me.rhys.anticheat.tinyprotocol.packet.in.WrappedInSettingsPacket;
import org.bukkit.Bukkit;
import org.bukkit.Material;

@CheckInformation(checkName = "BadPackets", checkType = "G", lagBack = false, punishmentVL = 1, description = "LOL MOON IS SHIT")
public class BadPacketsG extends Check {

    private double threshold;

    @Override
    public void onPacket(PacketEvent event) {
        switch (event.getType()) {
            case Packet.Client.SETTINGS: {


                break;
            }
        }
    }
}