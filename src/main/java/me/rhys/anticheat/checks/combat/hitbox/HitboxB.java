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
import me.rhys.anticheat.util.block.RayTrace;
import me.rhys.anticheat.util.box.BoundingBox;
import me.rhys.anticheat.util.world.CollisionBox;
import me.rhys.anticheat.util.world.EntityData;
import me.rhys.anticheat.util.world.types.RayCollision;
import me.rhys.anticheat.util.world.types.SimpleCollisionBox;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@CheckInformation(checkName = "Hitbox", checkType = "B", canPunish = false, punishmentVL = 15)
public class HitboxB extends Check {

    private double threshold;
    private PastLocation hitboxLocations = new PastLocation();
    private List<BoundingBox> boundingBoxList = new ArrayList<>();
    private boolean outsideHitbox;

    @Override
    public void onPacket(PacketEvent event) {
        User user = event.getUser();

        switch (event.getType()) {

            case Packet.Client.USE_ENTITY: {
                WrappedInUseEntityPacket useEntityPacket = new WrappedInUseEntityPacket(event.getPacket(), user.getPlayer());

                if (useEntityPacket.getAction() == WrappedInUseEntityPacket.EnumEntityUseAction.ATTACK) {

                    if (user.shouldCancel()
                            || user.getTick() < 60
                            || user.getConnectionProcessor().getClientTick() > 18
                            || user.getCombatProcessor().getCancelTicks() > 0) {
                        threshold = 0;
                        return;
                    }

                    boolean canFlag = user.getMovementProcessor().getYawDeltaClamped() < 20
                            && user.getMovementProcessor().getDeltaXZ() > 0.1;

                    if (outsideHitbox && canFlag) {
                        if (++threshold > 15) {
                            flag(user, "Attacking outside hitbox");
                        }
                    } else {
                        threshold -= Math.min(threshold, 1.75);
                    }

                }
                break;
            }

            case Packet.Client.FLYING:
            case Packet.Client.LOOK:
            case Packet.Client.POSITION_LOOK:
            case Packet.Client.POSITION: {

                if (user.getCombatProcessor().getLastAttackedEntity() != null) {
                    hitboxLocations.addLocation(user.getCombatProcessor()
                            .getLastAttackedEntity().getLocation());
                }

                Location location = user.getCurrentLocation().clone()
                        .toBukkitLocation(user.getPlayer().getWorld());

                LivingEntity livingEntity = (LivingEntity) user.getCombatProcessor().getLastAttackedEntity();

                List<PlayerLocation> pastLocation = hitboxLocations.getEstimatedLocation(event.getTimestamp(),
                        user.getConnectionProcessor().getTransPing(),
                        user.getConnectionProcessor().getDropTransTime() + 250L);

                if (user.getCombatProcessor().getCancelTicks() > 0) {
                    threshold = 0;
                    return;
                }

                if (pastLocation.size() > 1) {

                    if (livingEntity != null && location != null) {

                        if (user.getCombatProcessor().getUseEntityTimer().hasNotPassed(1)) {

                            pastLocation.forEach(loc1 ->
                                    boundingBoxList.add(MathUtil.getHitboxV2(livingEntity, loc1, user)));

                            location.setY(location.getY() + (user.getPlayer().isSneaking() ? 1.53
                                    : user.getPlayer().getEyeHeight()));

                            RayTrace trace = new RayTrace(location.toVector(),
                                    user.getPlayer().getEyeLocation().getDirection());

                            boolean outsideHitbox = boundingBoxList.stream().noneMatch(box ->
                                    trace.intersects(box, box.getMinimum().distance(location.toVector())
                                            + 1.0, 0.2f));

                            this.outsideHitbox = outsideHitbox;

                            boundingBoxList.clear();
                            pastLocation.clear();
                        }
                    }
                }

                break;
            }
        }
    }

    private static CollisionBox getHitbox(Entity entity, PlayerLocation loc) {
        return EntityData.getEntityBox(loc, entity);
    }
}
