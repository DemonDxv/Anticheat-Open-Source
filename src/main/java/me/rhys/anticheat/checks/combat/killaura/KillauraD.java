package me.rhys.anticheat.checks.combat.killaura;

import me.rhys.anticheat.base.check.api.Check;
import me.rhys.anticheat.base.check.api.CheckInformation;
import me.rhys.anticheat.base.event.PacketEvent;
import me.rhys.anticheat.base.user.User;
import me.rhys.anticheat.tinyprotocol.api.Packet;
import me.rhys.anticheat.tinyprotocol.packet.in.WrappedInBlockDigPacket;
import me.rhys.anticheat.tinyprotocol.packet.in.WrappedInUseEntityPacket;
import me.rhys.anticheat.util.MathUtil;

@CheckInformation(checkName = "Killaura", checkType = "D", lagBack = false, description = "Keep sprint while attacking")
public class KillauraD extends Check {

    private double threshold;

    @Override
    public void onPacket(PacketEvent event) {
        User user = event.getUser();

        switch (event.getType()) {
            case Packet.Client.USE_ENTITY: {
                WrappedInUseEntityPacket useEntityPacket = new WrappedInUseEntityPacket(event.getPacket(), user.getPlayer());

                if (useEntityPacket.getAction() == WrappedInUseEntityPacket.EnumEntityUseAction.ATTACK) {
                    if (user.getMovementProcessor().getDeltaXZ() >= MathUtil.getBaseSpeed(user.getPlayer())
                            && user.getMovementProcessor().isSprinting()) {
                        if ((user.getMovementProcessor().getDeltaXZ() - user.getMovementProcessor().getLastDeltaXZ()
                                < 0.03)) {
                            if (threshold++ > 3) {

                            }
                        } else {
                            threshold -= Math.min(threshold, 0.098f);
                        }
                    } else {
                        threshold -= Math.min(threshold, 0.125f);
                    }
                }
                break;
            }
        }
    }
}