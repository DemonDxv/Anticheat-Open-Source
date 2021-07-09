package me.rhys.anticheat.checks.combat.killaura;

import me.rhys.anticheat.base.check.api.Check;
import me.rhys.anticheat.base.check.api.CheckInformation;
import me.rhys.anticheat.base.event.PacketEvent;
import me.rhys.anticheat.base.user.User;
import me.rhys.anticheat.tinyprotocol.api.Packet;
import me.rhys.anticheat.tinyprotocol.packet.in.WrappedInBlockDigPacket;
import me.rhys.anticheat.tinyprotocol.packet.in.WrappedInUseEntityPacket;
import me.rhys.anticheat.util.MathUtil;
import org.bukkit.Bukkit;
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

                if (hits++ <= 2 && user.getMovementProcessor().isSprinting()
                        && user.getMovementProcessor().isLastSprinting()) {

                    double difference = user.getMovementProcessor().getDeltaXZ() - user.getMovementProcessor().getLastDeltaXZ();

                    if (user.getMovementProcessor().getDeltaXZ() >= MathUtil.getBaseSpeed(user.getPlayer())) {
                        if (difference <= 0.014) {
                            if (threshold++ > 5) {
                                flag(user, "Keep Sprinting");
                            }
                        } else {
                            threshold -= Math.min(threshold, 0.125);
                        }
                    } else {
                        threshold -= Math.min(threshold, 0.45);
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