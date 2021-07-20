package me.rhys.anticheat.checks.combat.hitbox;

import me.rhys.anticheat.base.check.api.Check;
import me.rhys.anticheat.base.check.api.CheckInformation;
import me.rhys.anticheat.base.event.PacketEvent;
import me.rhys.anticheat.base.user.User;
import me.rhys.anticheat.tinyprotocol.api.Packet;
import me.rhys.anticheat.tinyprotocol.packet.in.WrappedInUseEntityPacket;
import me.rhys.anticheat.util.MathUtil;
import me.rhys.anticheat.util.PastLocation;
import me.rhys.anticheat.util.PlayerLocation;
import me.rhys.anticheat.util.Verbose;
import me.rhys.anticheat.util.block.RayTrace;
import me.rhys.anticheat.util.box.BoundingBox;
import me.rhys.anticheat.util.world.EntityData;
import me.rhys.anticheat.util.world.types.SimpleCollisionBox;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@CheckInformation(checkName = "Hitbox", lagBack = false, punishmentVL = 25, canPunish = false)
public class HitboxA extends Check {

    private PastLocation hitBoxPastLocations = new PastLocation();
    private double threshold;

    @Override
    public void onPacket(PacketEvent event) {
        User user = event.getUser();

        switch (event.getType()) {

            case Packet.Client.FLYING:
            case Packet.Client.LOOK:
            case Packet.Client.POSITION_LOOK:
            case Packet.Client.POSITION: {
                hitBoxPastLocations.addLocation(user.getCombatProcessor().getLastAttackedEntity().getLocation());
                break;
            }

            case Packet.Client.USE_ENTITY: {
                WrappedInUseEntityPacket attack = new WrappedInUseEntityPacket(event.getPacket(), user.getPlayer());

                if (attack.getAction() == WrappedInUseEntityPacket.EnumEntityUseAction.ATTACK) {

                    List<BoundingBox> boundingBoxList = new ArrayList<>();

                    List<PlayerLocation> pastLocation = hitBoxPastLocations.getEstimatedLocation(
                            user.getConnectionProcessor().getTransPing(),
                            Math.abs(user.getConnectionProcessor().getDropTransTime()) + 200);

                    if (pastLocation.size() > 0) {

                        PlayerLocation location = user.getCurrentLocation().clone();
                        LivingEntity livingEntity = (LivingEntity) attack.getEntity();

                        pastLocation.forEach(loc1 -> {
                            boundingBoxList.add(MathUtil.getHitbox(livingEntity, loc1, user));
                        });

                        location.setY(location.getY() +
                                (user.getPlayer().isSneaking() ? 1.63f : user.getPlayer().getEyeHeight()));

                        RayTrace trace = new RayTrace(location.toVector(),
                                user.getPlayer().getEyeLocation().getDirection());

                        boolean intersect = boundingBoxList.stream().noneMatch(box ->
                                trace.intersects(box, box.getMinimum()
                                        .distance(location.toVector()) + 1.0, 0.2));

                        if (!intersect) {
                            Bukkit.broadcastMessage("flag" + threshold);

                            if (threshold++ > 8) {
                                flag(user, "Hitboxing");
                            }
                        } else {
                            threshold -= Math.min(threshold, 0.79f);
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
