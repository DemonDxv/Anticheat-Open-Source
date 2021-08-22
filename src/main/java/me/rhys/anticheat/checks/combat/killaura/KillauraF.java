package me.rhys.anticheat.checks.combat.killaura;

import me.rhys.anticheat.base.check.api.Check;
import me.rhys.anticheat.base.check.api.CheckInformation;
import me.rhys.anticheat.base.event.PacketEvent;
import me.rhys.anticheat.base.user.User;
import me.rhys.anticheat.tinyprotocol.api.Packet;
import me.rhys.anticheat.tinyprotocol.packet.in.WrappedInUseEntityPacket;
import me.rhys.anticheat.util.Verbose;
import org.bukkit.Bukkit;

@CheckInformation(checkName = "Killaura", checkType = "F", lagBack = false, description = "Hiss miss ratio", punishmentVL = 25)
public class KillauraF extends Check {

    private double swings, attacks;
    private Verbose threshold = new Verbose();

    /**
     * Hit miss ratio detection, currently trying to find a better way of detecting this.
     */

    @Override
    public void onPacket(PacketEvent event) {
        User user = event.getUser();

        switch (event.getType()) {
            case Packet.Client.USE_ENTITY: {
                WrappedInUseEntityPacket useEntityPacket = new WrappedInUseEntityPacket(event.getPacket(), user.getPlayer());

                if (useEntityPacket.getAction() == WrappedInUseEntityPacket.EnumEntityUseAction.ATTACK) {

                    if (user.getCombatProcessor().getCancelTicks() > 0) {
                        attacks = 0;
                        return;
                    }

                    double yawDiff = Math.abs(user.getCurrentLocation().getYaw() - user.getLastLocation().getYaw());
                    if (yawDiff > 3.5f && yawDiff < 120 && user.getMovementProcessor().getDeltaXZ() > 0.1
                            && user.getCombatProcessor().isInsideHitbox()) {
                        ++attacks;
                    }

                }
                break;
            }

            case Packet.Client.ARM_ANIMATION: {
                if (user.shouldCancel()
                        || user.getTick() < 60) {
                    threshold.setVerbose(0);
                    attacks = 0;
                    swings = 0;
                    return;
                }

                if (swings > 100) {
                    swings = attacks = 0;
                }

                ++swings;

                double ratio = (attacks / swings) * 100;

                if (ratio < 50) {
                    ratio = 50;
                }

                if (ratio > 75 && attacks > 5 && swings > 5) {
                    if (threshold.flag(4, 3000L)) {
                        flag(user, "Aim is to accurate [H:M]");
                    }
                }

                break;
            }
        }
    }
}