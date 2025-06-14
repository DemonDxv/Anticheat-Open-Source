package me.rhys.anticheat.util;
import me.rhys.anticheat.tinyprotocol.api.ProtocolVersion;
import me.rhys.anticheat.tinyprotocol.api.TinyProtocolHandler;
import me.rhys.anticheat.tinyprotocol.api.packets.reflections.types.WrappedClass;
import me.rhys.anticheat.tinyprotocol.packet.out.WrappedPacketPlayOutWorldParticle;
import me.rhys.anticheat.tinyprotocol.packet.types.WrappedEnumParticle;
import me.rhys.anticheat.util.world.types.RayCollision;
import me.rhys.anticheat.util.world.types.SimpleCollisionBox;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Collection;

public class MiscUtils {

    private static final WrappedClass materialClass = new WrappedClass(Material.class);
    public static Material match(String material) {
        if(ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.V1_13)) {
            return materialClass
                    .getMethod("matchMaterial", String.class, boolean.class)
                    .invoke(null, material, material.contains("LEGACY_"));
        } return Material.getMaterial(material.replace("LEGACY_", ""));
    }


    public static void drawRay(RayCollision collision, WrappedEnumParticle particle, Collection<? extends Player> players) {
        for (double i = 0; i < 8; i += 0.2) {
            float fx = (float) (collision.originX + (collision.directionX * i));
            float fy = (float) (collision.originY + (collision.directionY * i));
            float fz = (float) (collision.originZ + (collision.directionZ * i));
            Object packet = new WrappedPacketPlayOutWorldParticle(particle, true, fx, fy, fz,
                    0F, 0F, 0F, 0, 0);
            players.forEach(p -> TinyProtocolHandler.sendPacket(p, packet));
        }
    }

    public static void drawCuboid(SimpleCollisionBox box, WrappedEnumParticle particle, Collection<? extends Player> players) {
        Step.GenericStepper<Float> x = Step.step((float) box.xMin, 0.241f, (float) box.xMax);
        Step.GenericStepper<Float> y = Step.step((float) box.yMin, 0.241f, (float) box.yMax);
        Step.GenericStepper<Float> z = Step.step((float) box.zMin, 0.241f, (float) box.zMax);
        for (float fx : x) {
            for (float fy : y) {
                for (float fz : z) {
                    int check = 0;
                    if (x.first() || x.last()) check++;
                    if (y.first() || y.last()) check++;
                    if (z.first() || z.last()) check++;
                    if (check >= 2) {
                        Object packet = new WrappedPacketPlayOutWorldParticle(particle, true, fx, fy, fz,
                                0F, 0F, 0F, 0, 1);
                        for (Player p : players) {
                            TinyProtocolHandler.sendPacket(p, packet);
                        }
                    }
                }
            }
        }
    }
}
