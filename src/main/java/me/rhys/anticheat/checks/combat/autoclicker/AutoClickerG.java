package me.rhys.anticheat.checks.combat.autoclicker;

import me.rhys.anticheat.base.check.api.Check;
import me.rhys.anticheat.base.check.api.CheckInformation;
import me.rhys.anticheat.base.event.PacketEvent;
import me.rhys.anticheat.base.user.User;
import me.rhys.anticheat.tinyprotocol.api.Packet;

@CheckInformation(checkName = "AutoClicker", checkType = "G", canPunish = false, description = "Experimental Vape AutoClicker Check")
public class AutoClickerG extends Check {

    private int flying, clickTicks, outliers, lastOutliers;
    private double threshold, trustFactor;
    private long lastUpdate;

    @Override
    public void onPacket(PacketEvent event) {
        User user = event.getUser();
        switch (event.getType()) {
            case Packet.Client.FLYING:
            case Packet.Client.LOOK:
            case Packet.Client.POSITION_LOOK:
            case Packet.Client.POSITION: {

                flying++;

                double yawDiff = Math.abs(user.getCurrentLocation().getYaw() - user.getLastLocation().getYaw());
                double pitchDiff = Math.abs(user.getCurrentLocation().getPitch() - user.getLastLocation().getPitch());

                if (yawDiff > 0.0 || pitchDiff > 0.0) {
                    clickTicks++;
                } else {
                    clickTicks = 0;
                }

                if (clickTicks > 7) {
                    lastUpdate = System.currentTimeMillis();
                }

                break;
            }

            case Packet.Client.ARM_ANIMATION: {

                if (user.shouldCancel()
                        || user.getMovementProcessor().getLastBlockDigTimer().hasNotPassed(20)
                        || user.getTick() < 60) {
                    return;
                }


                if (flying > 3 && flying < 6) {
                    outliers++;
                }

                int currentOutliers = outliers;
                int diff = Math.abs(currentOutliers - lastOutliers);


                if (currentOutliers == 0 && diff == currentOutliers) {

                    if (clickTicks > 4 &&
                            trustFactor > 28
                            && (System.currentTimeMillis() - lastUpdate) < 1200L) {

                        if (threshold++ >= 45) {
                            flag(user, "Outliers");
                        }
                    }

                    trustFactor += 0.10f;
                } else {
                    trustFactor = 0.0f;
                }

                lastOutliers = currentOutliers;
                flying = outliers = 0;


                break;
            }
        }
    }
}