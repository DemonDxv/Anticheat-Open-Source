package me.rhys.anticheat.checks.misc.scaffold;

import me.rhys.anticheat.base.check.api.Check;
import me.rhys.anticheat.base.check.api.CheckInformation;
import me.rhys.anticheat.base.event.PacketEvent;
import me.rhys.anticheat.base.user.User;
import me.rhys.anticheat.tinyprotocol.api.Packet;
import me.rhys.anticheat.tinyprotocol.packet.in.WrappedInBlockPlacePacket;
import me.rhys.anticheat.util.MathUtil;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;

@CheckInformation(checkName = "Scaffold", checkType = "F", lagBack = false, punishmentVL = 50, canPunish = false)
public class ScaffoldF extends Check {

    private double threshold;

    @Override
    public void onPacket(PacketEvent event) {
        User user = event.getUser();

        switch (event.getType()) {
            case Packet.Client.BLOCK_PLACE: {
                WrappedInBlockPlacePacket blockPlace =
                        new WrappedInBlockPlacePacket(event.getPacket(), user.getPlayer());

                int faceInt = blockPlace.getFace().b();

                double blockY = blockPlace.getPosition().getY();
                double deltaY = user.getCurrentLocation().getY();

                double yDelta = Math.abs(blockY - deltaY);
                double pitch = user.getCurrentLocation().getPitch();

                if (yDelta <= 2.02 && pitch > 50) {
                    if (faceInt >= 0 && faceInt <= 3 && user.getLastBlockPlaceTimer()
                            .hasNotPassed(user.getConnectionProcessor().getClientTick())) {

                        if (threshold++ > 10) {
                            flag(user, "Never sneaking while placing blocks");
                        }
                    } else {
                        threshold = 0;
                    }
                }

                break;
            }

            case Packet.Client.FLYING:
            case Packet.Client.POSITION:
            case Packet.Client.POSITION_LOOK:
            case Packet.Client.LOOK: {

                if (user.getMovementProcessor().isSneaking()) {
                    threshold = 0;
                }

                break;
            }
        }
    }
}