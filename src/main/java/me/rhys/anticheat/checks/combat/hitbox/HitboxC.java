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
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@CheckInformation(checkName = "Hitbox", checkType = "C", punishmentVL = 2)
public class HitboxC extends Check {


    @Override
    public void onPacket(PacketEvent event) {
        User user = event.getUser();

        switch (event.getType()) {

            case Packet.Client.USE_ENTITY: {
                WrappedInUseEntityPacket useEntityPacket = new WrappedInUseEntityPacket(event.getPacket(), user.getPlayer());

                if (useEntityPacket.getAction() == WrappedInUseEntityPacket.EnumEntityUseAction.INTERACT_AT) {
                    if (useEntityPacket.getEntity() instanceof Player) {

                        if (user.shouldCancel()) {
                            return;
                        }

                        double x = Math.abs(useEntityPacket.getVec().a);
                        double y = useEntityPacket.getVec().b;
                        double z = Math.abs(useEntityPacket.getVec().c);

                        double maxXZ = 0.4 + 6E-9;
                        if (x > maxXZ || z > maxXZ || y > 1.9001 || y < -.1001) {
                            this.flag(user, "x=" + x, "y=" + y, "z=" + z);
                        }
                    }
                }
                break;
            }
        }
    }
}
