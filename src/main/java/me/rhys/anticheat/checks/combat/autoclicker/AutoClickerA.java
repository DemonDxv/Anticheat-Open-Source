package me.rhys.anticheat.checks.combat.autoclicker;

import me.rhys.anticheat.base.check.api.Check;
import me.rhys.anticheat.base.check.api.CheckInformation;
import me.rhys.anticheat.base.event.PacketEvent;
import me.rhys.anticheat.base.user.User;
import me.rhys.anticheat.tinyprotocol.api.Packet;
import me.rhys.anticheat.tinyprotocol.packet.in.WrappedInBlockDigPacket;
import me.rhys.anticheat.tinyprotocol.packet.in.WrappedInBlockPlacePacket;
import me.rhys.anticheat.tinyprotocol.packet.in.WrappedInUseEntityPacket;
import org.bukkit.Material;

@CheckInformation(checkName = "AutoClicker", lagBack = false, description = "Checks if the player is clicking over 22 clicks per second.")
public class AutoClickerA extends Check {

    private int movements, clicks;
    private int blockTicks;

    @Override
    public void onPacket(PacketEvent event) {
        User user = event.getUser();

        switch (event.getType()) {
            case Packet.Client.FLYING:
            case Packet.Client.LOOK:
            case Packet.Client.POSITION_LOOK:
            case Packet.Client.POSITION: {

                if (user.shouldCancel()
                        || user.getMovementProcessor().getLastBlockDigTimer().hasNotPassed(20)
                        || blockTicks > 0
                        || user.getTick() < 60) {
                    clicks = 0;
                    return;
                }

                if (movements++ == 20) {
                    if (clicks > 22) {
                        flag(user, "Clicking abnormally fast", "C: "+clicks);
                    }
                    movements = clicks = 0;
                }

                break;
            }

            case Packet.Client.ARM_ANIMATION: {
                clicks++;
                break;
            }

            case Packet.Client.BLOCK_PLACE: {
                WrappedInBlockPlacePacket blockP = new WrappedInBlockPlacePacket(event.getPacket(), user.getPlayer());

                if (blockP.getItemStack().getType().isBlock()
                        || blockP.getItemStack().getType() == Material.DIAMOND_SWORD
                        || blockP.getItemStack().getType() == Material.GOLD_SWORD
                        || blockP.getItemStack().getType() == Material.IRON_SWORD
                        || blockP.getItemStack().getType() == Material.STONE_SWORD
                        || blockP.getItemStack().getType() == Material.WOOD_SWORD) {
                    blockTicks++;
                } else {
                    blockTicks = 0;
                }

                break;
            }
        }
    }
}
