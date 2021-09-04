package me.rhys.anticheat.checks.misc.inventory;

import me.rhys.anticheat.base.check.api.Check;
import me.rhys.anticheat.base.check.api.CheckInformation;
import me.rhys.anticheat.base.event.PacketEvent;
import me.rhys.anticheat.base.user.User;
import me.rhys.anticheat.tinyprotocol.api.Packet;
import me.rhys.anticheat.util.MathUtil;
import org.bukkit.Location;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.util.Vector;

@CheckInformation(checkName = "Inventory", canPunish = false, punishmentVL = 45)
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
                        || !user.isChunkLoaded()
                        || user.getBlockData().pistonTicks > 0) {
                    threshold = invTicks = 0;
                    return;
                }

                double max = invTicks <= 30 ? MathUtil.getBaseSpeed(user.getPlayer()) : 0.01;

                Vector vector = user.getMovementProcessor().getInventoryVector();

                if (user.getMovementProcessor().isInInventory()) {
                    invTicks++;
                    if (invTicks > 12) {
                        if (user.getMovementProcessor().getDeltaXZ() > max) {
                            if (++threshold > 15) {
                                flag(user, "Moving while in inventory");
                                user.getPlayer().closeInventory();
                                user.getMovementProcessor().setInInventory(false);
                                user.getPlayer().teleport(
                                        new Location(user.getPlayer().getWorld(),
                                                vector.getX(), vector.getY(), vector.getZ(),
                                                user.getCurrentLocation().getYaw(),
                                                user.getCurrentLocation().getPitch()),
                                        PlayerTeleportEvent.TeleportCause.UNKNOWN);
                                user.getGhostBlockProcessor().getGhostBlockTeleportTimer().reset();
                            }
                        } else {
                            threshold -= Math.min(threshold, 2);
                        }
                    } else {
                        threshold -= Math.min(threshold, 2);
                    }
                } else {
                    threshold = invTicks = 0;
                }

                break;
            }
        }
    }
}
