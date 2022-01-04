package me.rhys.anticheat.checks.misc.scaffold;

import me.rhys.anticheat.base.check.api.Check;
import me.rhys.anticheat.base.check.api.CheckInformation;
import me.rhys.anticheat.base.event.PacketEvent;
import me.rhys.anticheat.base.user.User;
import me.rhys.anticheat.tinyprotocol.api.Packet;
import me.rhys.anticheat.tinyprotocol.packet.in.WrappedInBlockPlacePacket;
import me.rhys.anticheat.util.MathUtil;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

@CheckInformation(checkName = "Scaffold", checkType = "E", lagBack = false, punishmentVL = 10)
public class ScaffoldE extends Check {

    private List<Float> placeList = new ArrayList<>();
    private double lastSTD, threshold;

    @Override
    public void onPacket(PacketEvent event) {
        switch (event.getType()) {
            case Packet.Client.BLOCK_PLACE: {
                User user = event.getUser();

                WrappedInBlockPlacePacket blockPlace =
                        new WrappedInBlockPlacePacket(event.getPacket(), user.getPlayer());

                if (user.shouldCancel() || user.getTick() < 60) {
                    threshold = 0;
                    placeList.clear();
                    return;
                }

                float vecY = blockPlace.getVecY();

                int faceInt = blockPlace.getFace().b();

                double yaw = Math.abs(user.getCurrentLocation().getYaw() - user.getLastLocation().getYaw());

                if (user.getLastBlockPlaceTimer().hasNotPassed(2)) {
                    if (user.getBlockPlaced().getType().isBlock()) {
                        if (user.getBlockPlaced().getLocation().add(0, -1, 0).getBlock().getType() == Material.AIR) {
                            if (yaw > 0 && user.getPlayer().getItemInHand() != null
                                    && user.getPlayer().getItemInHand().getType().isBlock()) {
                                if (faceInt >= 0 && faceInt <= 3) {
                                    placeList.add(vecY);

                                    if (placeList.size() == 100) {
                                        double std = MathUtil.getStandardDeviation(placeList);

                                        if (std < 0.05 || std == lastSTD) {
                                            if (++threshold > 3) {
                                                flag(user, "HitVec Consistency");
                                            }
                                        } else {
                                            threshold -= Math.min(threshold, 0.5);
                                        }

                                        lastSTD = std;
                                        placeList.clear();
                                    }
                                } else {
                                    threshold = 0;
                                    placeList.clear();
                                }
                            }
                        }
                    }
                }
                break;
            }
        }
    }
}