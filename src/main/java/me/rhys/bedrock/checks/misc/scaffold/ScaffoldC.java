package me.rhys.bedrock.checks.misc.scaffold;

import me.rhys.bedrock.base.check.api.Check;
import me.rhys.bedrock.base.check.api.CheckInformation;
import me.rhys.bedrock.base.event.PacketEvent;
import me.rhys.bedrock.base.user.User;
import me.rhys.bedrock.tinyprotocol.api.Packet;
import me.rhys.bedrock.tinyprotocol.packet.in.WrappedInBlockPlacePacket;
import me.rhys.bedrock.util.MathUtil;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;

@CheckInformation(checkName = "Scaffold", checkType = "C", lagBack = false, punishmentVL = 10)
public class ScaffoldC extends Check {

    private long lastFlying;
    private double threshold;

    @Override
    public void onPacket(PacketEvent event) {
        switch (event.getType()) {
            case Packet.Client.BLOCK_PLACE: {
                User user = event.getUser();

                if ((System.currentTimeMillis() - lastFlying) < 5L) {
                    if (++threshold > 8) {
                        flag(user, "Block Placement Packet Sent Late");
                    }
                } else {
                    threshold -= Math.min(threshold, 0.75);
                }

                break;
            }

            case Packet.Client.FLYING:
            case Packet.Client.LOOK:
            case Packet.Client.POSITION_LOOK:
            case Packet.Client.POSITION: {

                lastFlying = System.currentTimeMillis();

                break;
            }
        }
    }
}