package me.rhys.anticheat.checks.misc.scaffold;

import me.rhys.anticheat.base.check.api.Check;
import me.rhys.anticheat.base.check.api.CheckInformation;
import me.rhys.anticheat.base.event.PacketEvent;
import me.rhys.anticheat.base.user.User;
import me.rhys.anticheat.tinyprotocol.api.Packet;
import me.rhys.anticheat.tinyprotocol.packet.in.WrappedInBlockPlacePacket;
import me.rhys.anticheat.util.MathUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;

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

                if (user.shouldCancel() || user.getTick() < 60) {
                    threshold = 0;
                    return;
                }

                int faceInt = blockPlace.getFace().b();

                if (user.getMovementProcessor().getDeltaY() != 0) {
                    threshold -= Math.min(threshold, 1);
                }

                if (user.getBlockPlaced().getLocation().add(0, -1, 0).getBlock().getType() == Material.AIR) {
                    if (faceInt >= 0 && faceInt <= 3 && user.getLastBlockPlaceTimer()
                            .hasNotPassed(user.getConnectionProcessor().getClientTick())
                            && user.getPlayer().getItemInHand() != null
                            && user.getPlayer().getItemInHand().getType().isBlock()) {

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