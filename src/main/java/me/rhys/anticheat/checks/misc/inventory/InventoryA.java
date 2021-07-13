package me.rhys.anticheat.checks.misc.inventory;

import me.rhys.anticheat.base.check.api.Check;
import me.rhys.anticheat.base.check.api.CheckInformation;
import me.rhys.anticheat.base.event.PacketEvent;
import me.rhys.anticheat.base.user.User;
import me.rhys.anticheat.tinyprotocol.api.Packet;
import me.rhys.anticheat.util.MathUtil;

@CheckInformation(checkName = "Inventory", lagBack = false, punishmentVL = 10)
public class InventoryA extends Check {

    private double threshold, invTicks;

    @Override
    public void onPacket(PacketEvent event) {
        switch (event.getType()) {
            case Packet.Client.FLYING:
            case Packet.Client.LOOK:
            case Packet.Client.POSITION_LOOK:
            case Packet.Client.POSITION: {
                User user = event.getUser();

                if (user.shouldCancel()
                        || user.getCombatProcessor().getVelocityTicks() <= 9
                        || user.getLastTeleportTimer().hasNotPassed(20)
                        || user.getActionProcessor().getServerPositionTimer().hasNotPassed(3)
                        || user.getBlockData().iceTimer.hasNotPassed(20)
                        || user.getBlockData().slimeTimer.hasNotPassed(20)
                        || user.getBlockData().pistonTicks > 0) {
                    threshold = 0;
                    return;
                }

                if (user.getMovementProcessor().isInInventory()) {
                    invTicks++;
                    if (invTicks > 12) {
                        if (user.getMovementProcessor().getDeltaXZ() > MathUtil.getBaseSpeed(user.getPlayer())) {
                            if (++threshold > 5) {
                                flag(user, "Moving while in inventory");
                            }
                        } else {
                            threshold -= Math.min(threshold, 1);
                        }
                    } else {
                        threshold -= Math.min(threshold, 1);
                    }
                } else {
                    threshold = invTicks = 0;
                }

                break;
            }
        }
    }
}
