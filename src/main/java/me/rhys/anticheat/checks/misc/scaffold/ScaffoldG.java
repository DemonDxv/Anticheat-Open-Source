package me.rhys.anticheat.checks.misc.scaffold;

import me.rhys.anticheat.base.check.api.Check;
import me.rhys.anticheat.base.check.api.CheckInformation;
import me.rhys.anticheat.base.event.PacketEvent;
import me.rhys.anticheat.base.user.User;
import me.rhys.anticheat.tinyprotocol.api.Packet;
import me.rhys.anticheat.tinyprotocol.packet.in.WrappedInBlockPlacePacket;
import me.rhys.anticheat.util.MathUtil;

import java.util.ArrayList;
import java.util.List;

@CheckInformation(checkName = "Scaffold", checkType = "G", lagBack = false, punishmentVL = 10)
public class ScaffoldG extends Check {

    private List<Float> placeList = new ArrayList<>();
    private double lastSTD, threshold;

    @Override
    public void onPacket(PacketEvent event) {
        switch (event.getType()) {
            case Packet.Client.BLOCK_PLACE: {
                User user = event.getUser();

                WrappedInBlockPlacePacket blockPlace =
                        new WrappedInBlockPlacePacket(event.getPacket(), user.getPlayer());

                float vecY = blockPlace.getVecY();

                int faceInt = blockPlace.getFace().b();

                double yaw = Math.abs(user.getCurrentLocation().getYaw() - user.getLastLocation().getYaw());

                if (yaw > 0) {
                    if (faceInt >= 0 && faceInt <= 2) {
                        placeList.add(vecY);

                        if (user.getPlayer().isSneaking()) {
                            placeList.clear();
                        }

                        if (placeList.size() == 4) {

                            if (++threshold > 1) {
                                flag(user, "No Sneak?");
                            }

                            placeList.clear();
                        }
                    }
                }
                break;
            }
        }
    }
}