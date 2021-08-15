package me.rhys.anticheat.checks.combat.killaura;

import me.rhys.anticheat.base.check.api.Check;
import me.rhys.anticheat.base.check.api.CheckInformation;
import me.rhys.anticheat.base.event.PacketEvent;
import me.rhys.anticheat.base.user.User;
import me.rhys.anticheat.tinyprotocol.api.Packet;
import me.rhys.anticheat.tinyprotocol.packet.in.WrappedInUseEntityPacket;

@CheckInformation(checkName = "Killaura", lagBack = false, description = "Post Attack Check (unstable)",  punishmentVL = 15)
public class KillauraA extends Check {

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

            case Packet.Client.USE_ENTITY: {
                WrappedInUseEntityPacket attack = new WrappedInUseEntityPacket(event.getPacket(), user.getPlayer());

                if (attack.getAction() == WrappedInUseEntityPacket.EnumEntityUseAction.ATTACK) {

                    if (user.shouldCancel()
                            || user.getConnectionProcessor().getClientTick() > 5
                            || user.getConnectionProcessor().getFlyingTick() > 1
                            || user.getConnectionProcessor().getDropTransTime() > 50L
                            || user.getTick() < 60) {

                        threshold = 0;
                        return;
                    }

                    if ((System.currentTimeMillis() - lastFlyingPacket) <= 5L) {
                        if (threshold++ > 10) {
                            flag(user, "Sent attack packet late");
                        }
                    } else {
                        threshold = 0;
                    }

                }
                break;
            }
        }
    }
}