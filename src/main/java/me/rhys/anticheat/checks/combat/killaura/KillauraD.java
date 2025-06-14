package me.rhys.anticheat.checks.combat.killaura;

import me.rhys.anticheat.base.check.api.Check;
import me.rhys.anticheat.base.check.api.CheckInformation;
import me.rhys.anticheat.base.event.PacketEvent;
import me.rhys.anticheat.base.user.User;
import me.rhys.anticheat.tinyprotocol.api.Packet;
import me.rhys.anticheat.tinyprotocol.packet.in.WrappedInUseEntityPacket;
import me.rhys.anticheat.util.MathUtil;
import org.bukkit.entity.Player;

@CheckInformation(checkName = "Killaura", checkType = "D", lagBack = false, description = "Keep sprint while attacking")
public class KillauraD extends Check {

    private double threshold;
    private int hits;

    @Override
    public void onPacket(PacketEvent event) {
        User user = event.getUser();

        switch (event.getType()) {
            case Packet.Client.FLYING:
            case Packet.Client.LOOK:
            case Packet.Client.POSITION_LOOK:
            case Packet.Client.POSITION: {

                if (user.shouldCancel()
                        || user.getTick() < 60) {
                    threshold = 0;
                    return;
                }

                if (hits++ <= 2 && user.getMovementProcessor().isSprinting()) {

                    double difference = Math.abs(user.getMovementProcessor().getDeltaXZ()
                            - user.getMovementProcessor().getLastDeltaXZ());

                    if (user.getMovementProcessor().getDeltaXZ() > MathUtil.getBaseSpeed(user.getPlayer())) {
                        if (difference <= 0.01) {
                            if (threshold++ > 9.5) {
                                flag(user, "Keep Sprint");
                            }
                        } else {
                            threshold -= Math.min(threshold, 0.75);
                        }
                    }

                }
                break;
            }

            case Packet.Client.USE_ENTITY: {
                WrappedInUseEntityPacket useEntityPacket = new WrappedInUseEntityPacket(event.getPacket(), user.getPlayer());

                if (useEntityPacket.getAction() == WrappedInUseEntityPacket.EnumEntityUseAction.ATTACK) {
                    if (useEntityPacket.getEntity() instanceof Player) {
                        hits = 0;
                    }
                }
                break;
            }
        }
    }
}