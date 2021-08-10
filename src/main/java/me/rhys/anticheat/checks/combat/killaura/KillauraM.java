package me.rhys.anticheat.checks.combat.killaura;

import me.rhys.anticheat.base.check.api.Check;
import me.rhys.anticheat.base.check.api.CheckInformation;
import me.rhys.anticheat.base.event.PacketEvent;
import me.rhys.anticheat.base.user.User;
import me.rhys.anticheat.tinyprotocol.api.Packet;
import me.rhys.anticheat.tinyprotocol.packet.in.WrappedInBlockDigPacket;
import me.rhys.anticheat.tinyprotocol.packet.in.WrappedInUseEntityPacket;
import me.rhys.anticheat.util.MathUtil;
import me.rhys.anticheat.util.PastLocation;
import me.rhys.anticheat.util.PlayerLocation;
import me.rhys.anticheat.util.block.RayTrace;
import me.rhys.anticheat.util.box.BoundingBox;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@CheckInformation(checkName = "Killlaura", checkType = "M", lagBack = false, punishmentVL = 25, canPunish = false)
public class KillauraM extends Check {

    private double threshold;
    private Block lastBlock;

    @Override
    public void onPacket(PacketEvent event) {
        User user = event.getUser();

        switch (event.getType()) {

            case Packet.Client.USE_ENTITY: {
                WrappedInUseEntityPacket useEntityPacket = new WrappedInUseEntityPacket(event.getPacket(), user.getPlayer());

                if (useEntityPacket.getAction() == WrappedInUseEntityPacket.EnumEntityUseAction.ATTACK) {

                    if (user.shouldCancel()
                            || user.getLastBlockBreakTimer().hasNotPassed(20)
                            || user.getMovementProcessor().getLastBlockDigTimer().hasNotPassed(20)
                            || user.getTick() < 60
                            || user.getLastBlockPlaceTimer().hasNotPassed(20)) {
                        threshold = 0;
                        return;
                    }

                    Block block = getTargetBlock(user.getPlayer(), 1);

                    if (lastBlock != null) {
                        if (!block.getType().isOccluding()
                                && !lastBlock.getType().isOccluding()) {
                            if (block.getType() != Material.AIR && lastBlock.getType() != Material.AIR) {

                                Location location = user.getCurrentLocation().toBukkitLocation(user.getPlayer().getWorld());

                                double distance = location.distance(user.getCombatProcessor()
                                        .getLastAttackedEntity().getLocation());

                                double maxDistance = user.getMovementProcessor().getDeltaY() > 0.0 ? 3.5 : 3.0;


                                if (distance >= maxDistance) {
                                    if (threshold++ > 12) {
                                        flag(user, "Attacking through walls (EXPERIMENTAL)");
                                    }
                                } else {
                                    threshold = 0;
                                }
                            }
                        }
                    }

                    this.lastBlock = block;
                }

                break;
            }
        }
    }

    public final Block getTargetBlock(Player player, int range) {
        BlockIterator iter = new BlockIterator(player, range);
        Block lastBlock = iter.next();
        while (iter.hasNext()) {
            lastBlock = iter.next();
            if (lastBlock.getType() == Material.AIR) {
                continue;
            }
            break;
        }
        return lastBlock;
    }
}
