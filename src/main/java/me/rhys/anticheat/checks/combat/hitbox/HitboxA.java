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

    private PastLocation hitBoxPastLocations = new PastLocation();
    private List<BoundingBox> boundingBoxList = new ArrayList<>();
    private double threshold;

    @Override
    public void onPacket(PacketEvent event) {
        User user = event.getUser();

        switch (event.getType()) {

            case Packet.Client.FLYING:
            case Packet.Client.LOOK:
            case Packet.Client.POSITION_LOOK:
            case Packet.Client.POSITION: {
                if (user.getCombatProcessor().getLastAttackedEntity() != null) {
                    hitBoxPastLocations.addLocation(user.getCombatProcessor().getLastAttackedEntity().getLocation());
                }

                break;
            }

            case Packet.Client.USE_ENTITY: {
                WrappedInUseEntityPacket useEntityPacket = new WrappedInUseEntityPacket(event.getPacket(), user.getPlayer());

                if (useEntityPacket.getAction() == WrappedInUseEntityPacket.EnumEntityUseAction.ATTACK) {

                    Location location = user.getCurrentLocation().clone()
                            .toBukkitLocation(user.getPlayer().getWorld());

                    LivingEntity livingEntity = (LivingEntity) useEntityPacket.getEntity();

                    List<PlayerLocation> pastLocation = hitBoxPastLocations.getEstimatedLocation(event.getTimestamp(),
                            user.getConnectionProcessor().getTransPing(), 200L);

                    if (pastLocation.size() > 0) {

                        if (livingEntity != null && location != null) {

                            pastLocation.forEach(loc1 -> boundingBoxList.add(MathUtil.getHitbox(livingEntity, loc1, user)));

                            location.setY(location.getY() + (user.getPlayer().isSneaking() ? 1.53 : user.getPlayer().getEyeHeight()));

                            RayTrace trace = new RayTrace(location.toVector(), user.getPlayer().getEyeLocation().getDirection());


                            boolean outsideHitbox = boundingBoxList.stream().noneMatch(box ->
                                    trace.intersects(box, box.getMinimum().distance(location.toVector()) + 1.0,
                                            .4));

                            if (outsideHitbox) {
                                if (threshold++ > 5) {
                                    flag(user, "Expanded Hitbox");
                                }
                            } else {
                                threshold -= Math.min(threshold, 0.25);
                            }

                            boundingBoxList.clear();
                            pastLocation.clear();
                        }
                    }
                }

                break;
            }
        }
    }
}
