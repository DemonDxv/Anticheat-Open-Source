package me.rhys.anticheat.checks.combat.aimassist;

import me.rhys.anticheat.base.check.api.Check;
import me.rhys.anticheat.base.check.api.CheckInformation;
import me.rhys.anticheat.base.event.PacketEvent;
import me.rhys.anticheat.base.user.User;
import me.rhys.anticheat.tinyprotocol.api.Packet;
import me.rhys.anticheat.util.MathUtil;

import java.util.ArrayList;
import java.util.List;

@CheckInformation(checkName = "AimAssist", checkType = "L", lagBack = false, punishmentVL = 10)
public class AimAssistL extends Check {

    private double threshold, lastSTD;
    private double lastDeltaYaw;
    private List<Double> deltaYawList = new ArrayList<>();

    @Override
    public void onPacket(PacketEvent event) {
        User user = event.getUser();

        switch (event.getType()) {

            case Packet.Client.FLYING:
            case Packet.Client.LOOK:
            case Packet.Client.POSITION_LOOK:
            case Packet.Client.POSITION: {

                if (user.getTick() < 60 || user.shouldCancel()) {
                    return;
                }

                double yaw = user.getMovementProcessor().getYawDeltaClamped();

                if (yaw > 1.0 && lastDeltaYaw > 1.0) {
                    deltaYawList.add(yaw);

                    if (deltaYawList.size() >= 100) {
                        double std = MathUtil.getStandardDeviation(deltaYawList);

                        if (Math.abs(std - lastSTD) < 0.1) {
                            if (++threshold > 3) {
                                flag(user, "Invalid yaw movements");
                            }
                        } else {
                            threshold -= Math.min(threshold, 0.25);
                        }


                        lastSTD = std;
                        deltaYawList.clear();
                    }
                }


                lastDeltaYaw = yaw;
                break;
            }
        }
    }
}
