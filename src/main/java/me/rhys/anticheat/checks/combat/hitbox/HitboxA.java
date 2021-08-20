package me.rhys.anticheat.checks.combat.hitbox;

import me.rhys.anticheat.base.check.api.Check;
import me.rhys.anticheat.base.check.api.CheckInformation;
import me.rhys.anticheat.base.event.PacketEvent;
import me.rhys.anticheat.base.user.User;
import me.rhys.anticheat.tinyprotocol.api.Packet;
import me.rhys.anticheat.tinyprotocol.packet.in.WrappedInUseEntityPacket;
import me.rhys.anticheat.util.*;
import me.rhys.anticheat.util.block.RayTrace;
import me.rhys.anticheat.util.box.BoundingBox;
import me.rhys.anticheat.util.world.EntityData;
import me.rhys.anticheat.util.world.types.SimpleCollisionBox;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@CheckInformation(checkName = "Hitbox", lagBack = false, punishmentVL = 25, canPunish = false)
public class HitboxA extends Check {

    private double threshold;

    @Override
    public void onPacket(PacketEvent event) {
        User user = event.getUser();

        switch (event.getType()) {

            case Packet.Client.USE_ENTITY: {
                WrappedInUseEntityPacket useEntityPacket = new WrappedInUseEntityPacket(event.getPacket(), user.getPlayer());

                if (useEntityPacket.getAction() == WrappedInUseEntityPacket.EnumEntityUseAction.ATTACK) {

                    if (user.shouldCancel() || user.getTick() < 60 || user.getCombatProcessor().getCancelTicks() > 0) {
                        threshold = 0;
                        return;
                    }

                    Location location = user.getCurrentLocation().clone()
                            .toBukkitLocation(user.getPlayer().getWorld());

                    LivingEntity livingEntity = (LivingEntity) useEntityPacket.getEntity();

                    if (livingEntity != null && location != null && user.getMovementProcessor().getDeltaXZ() > 0.01) {
                        if (!user.getCombatProcessor().isInsideHitbox()) {
                            if (threshold++ > 5) {
                                flag(user, "Expanded Hitbox");
                            }
                        } else {
                            threshold -= Math.min(threshold, 0.25);
                        }
                    }
                }

                break;
            }
        }
    }
}
