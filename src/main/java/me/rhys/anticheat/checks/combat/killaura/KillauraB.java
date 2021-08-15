package me.rhys.anticheat.checks.combat.killaura;

import me.rhys.anticheat.base.check.api.Check;
import me.rhys.anticheat.base.check.api.CheckInformation;
import me.rhys.anticheat.base.event.PacketEvent;
import me.rhys.anticheat.base.user.User;
import me.rhys.anticheat.tinyprotocol.api.Packet;
import me.rhys.anticheat.tinyprotocol.packet.in.WrappedInUseEntityPacket;

@CheckInformation(checkName = "Killaura", checkType = "B", lagBack = false, description = "Post Swing Check", punishmentVL = 50)
public class KillauraB extends Check {

    private long lastFlyingPacket;
    private double threshold;

    @Override
    public void onPacket(PacketEvent event) {
        User user = event.getUser();

        switch (event.getType()) {
            case Packet.Client.FLYING:
            case Packet.Client.LOOK:
            case Packet.Client.POSITION_LOOK:
            case Packet.Client.POSITION: {

                lastFlyingPacket = System.currentTimeMillis();

                break;
            }

            case Packet.Client.ARM_ANIMATION: {

                if (user.shouldCancel()
                        || user.getConnectionProcessor().getClientTick() > 5
                        || user.getConnectionProcessor().getFlyingTick() > 1
                        || user.getConnectionProcessor().getDropTransTime() > 50L
                        || user.getTick() < 60) {

                    threshold = 0;
                    return;
                }

                if (user.getConnectionProcessor().getFlyingTick() > 3) {
                    threshold = 0;
                }

                if ((System.currentTimeMillis() - lastFlyingPacket) <= 5L) {
                    if (threshold++ > 10) {
                        flag(user, "Sent swing packet late");
                    }
                } else {
                    threshold -= Math.min(threshold, 0.25);

                }

                break;
            }
        }
    }
}