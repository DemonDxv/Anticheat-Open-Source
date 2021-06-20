package me.rhys.bedrock.checks.misc.scaffold;

import me.rhys.bedrock.base.check.api.Check;
import me.rhys.bedrock.base.check.api.CheckInformation;
import me.rhys.bedrock.base.event.PacketEvent;
import me.rhys.bedrock.base.user.User;
import me.rhys.bedrock.tinyprotocol.api.Packet;
import me.rhys.bedrock.tinyprotocol.packet.in.WrappedInBlockPlacePacket;

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

                float vecX = blockPlace.getVecX();
                float vecY = blockPlace.getVecY();
                float vecZ = blockPlace.getVecZ();


                int faceInt = blockPlace.getFace().b();

                double yaw = Math.abs(user.getCurrentLocation().getYaw() - user.getLastLocation().getYaw());

                if (yaw > 0) {
                    if (faceInt >= 0 && faceInt <= 2) {
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
                break;
            }
        }
    }
}