package me.rhys.anticheat.checks.misc.scaffold;

import me.rhys.anticheat.base.check.api.Check;
import me.rhys.anticheat.base.check.api.CheckInformation;
import me.rhys.anticheat.base.event.PacketEvent;
import me.rhys.anticheat.base.user.User;
import me.rhys.anticheat.tinyprotocol.api.Packet;
import me.rhys.anticheat.tinyprotocol.packet.in.WrappedInBlockPlacePacket;
import org.bukkit.Material;

@CheckInformation(checkName = "Scaffold", lagBack = false, punishmentVL = 10)
public class ScaffoldA extends Check {

    private double vl;

    @Override
    public void onPacket(PacketEvent event) {
        switch (event.getType()) {
            case Packet.Client.BLOCK_PLACE: {
                User user = event.getUser();

                WrappedInBlockPlacePacket blockPlace =
                        new WrappedInBlockPlacePacket(event.getPacket(), user.getPlayer());

                if (user.shouldCancel() || user.getTick() < 60) {
                    vl = 0;
                    return;
                }

                float vecX = blockPlace.getVecX();
                float vecY = blockPlace.getVecY();
                float vecZ = blockPlace.getVecZ();


                int faceInt = blockPlace.getFace().b();

                double yaw = Math.abs(user.getCurrentLocation().getYaw() - user.getLastLocation().getYaw());

                if (yaw > 0) {
                    if (user.getBlockPlaced().getLocation().add(0, -1, 0).getBlock().getType() == Material.AIR) {
                        if (faceInt >= 0 && faceInt <= 3) {
                            if (vecX == 0.5 && vecY == 0.5 || vecZ == 0.5 && vecY == 0.5) {
                                if (++vl > 4) {
                                    flag(user, "Constant HitVec");
                                }
                            } else {
                                vl -= Math.min(vl, 0.5f);
                            }
                        } else {
                            vl -= Math.min(vl, 0.72f);
                        }
                    }
                }
                break;
            }
        }
    }
}