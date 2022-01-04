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

@CheckInformation(checkName = "Hitbox", lagBack = false, punishmentVL = 30)
public class HitboxA extends Check {

    private double threshold;

    @Override
    public void onPacket(PacketEvent event) {
        User user = event.getUser();

        switch (event.getType()) {

            case Packet.Client.FLYING:
            case Packet.Client.LOOK:
            case Packet.Client.POSITION_LOOK:
            case Packet.Client.POSITION: {

                if (user.getTick() < 120
                        || user.getConnectionProcessor().isLagging()
                        || user.getCombatProcessor().getCancelTicks() > 0
                        || user.shouldCancel()) {
                    threshold = 0;
                    return;
                }

                if (user.getCombatProcessor().getUseEntityTimer().hasNotPassed(1)) {
                    if (!user.getCombatProcessor().isInsideHitbox()) {
                        if (threshold++ > 7) {
                            flag(user, "Expanded Hitbox");
                        }
                    } else {
                        threshold = 0;
                    }
                }

                break;
            }
        }
    }
}
