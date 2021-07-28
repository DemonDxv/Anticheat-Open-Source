package me.rhys.anticheat.checks.combat.autoclicker;

import me.rhys.anticheat.base.check.api.Check;
import me.rhys.anticheat.base.check.api.CheckInformation;
import me.rhys.anticheat.base.event.PacketEvent;
import me.rhys.anticheat.base.user.User;
import me.rhys.anticheat.tinyprotocol.api.Packet;
import me.rhys.anticheat.tinyprotocol.packet.in.WrappedInBlockDigPacket;
import me.rhys.anticheat.tinyprotocol.packet.in.WrappedInBlockPlacePacket;
import me.rhys.anticheat.tinyprotocol.packet.in.WrappedInUseEntityPacket;
import me.rhys.anticheat.util.MathUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

@CheckInformation(checkName = "AutoClicker", lagBack = false, description = "Checks if the player is clicking over 22 clicks per second.")
public class AutoClickerA extends Check {

    private int movements;
    private List<Integer> delays = new ArrayList<>();
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
                    movements = 0;
                    delays.clear();
                    return;
                }

                movements++;

                break;
            }

            case Packet.Client.ARM_ANIMATION: {
                if (movements < 10) {
                    delays.add(movements);
                    double cps = MathUtil.getCPS(delays);

                    if (delays.size() > 30) {

                        if (cps > 20) {
                            flag(user, "Clicking Fast", "CPS: "+cps);
                        }

                        delays.clear();
                    }
                }
                movements = 0;
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
