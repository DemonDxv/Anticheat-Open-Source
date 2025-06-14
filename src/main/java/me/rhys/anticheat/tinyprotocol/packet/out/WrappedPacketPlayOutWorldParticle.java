package me.rhys.anticheat.tinyprotocol.packet.out;

import lombok.Getter;
import me.rhys.anticheat.tinyprotocol.api.NMSObject;
import me.rhys.anticheat.tinyprotocol.api.Packet;
import me.rhys.anticheat.tinyprotocol.api.ProtocolVersion;
import me.rhys.anticheat.tinyprotocol.api.packets.reflections.types.WrappedClass;
import me.rhys.anticheat.tinyprotocol.api.packets.reflections.types.WrappedMethod;
import me.rhys.anticheat.tinyprotocol.packet.types.WrappedEnumParticle;

@Getter
public class WrappedPacketPlayOutWorldParticle extends NMSObject {

    private final WrappedEnumParticle type;
    private static WrappedClass craftParticle, particle;
    private static WrappedMethod toNMS;
    private static final String packetPlayOutWorldParticle = Packet.Server.WORLD_PARTICLE;
    private final boolean j;
    private final float x;
    private final float y;
    private final float z;
    private final float xOffset;
    private final float yOffset;
    private final float zOffset;
    private final float speed;
    private final int amount;
    private final int[] data;

    public WrappedPacketPlayOutWorldParticle(WrappedEnumParticle type, boolean var2, float x, float y, float z, float xOffset, float yOffset, float zOffset, float speed, int amount, int... data) {
        this.type = type;
        this.j = var2;
        this.x = x;
        this.y = y;
        this.z = z;
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.zOffset = zOffset;
        this.speed = speed;
        this.amount = amount;
        this.data = data;

        if (ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_8)) {
            setPacket(packetPlayOutWorldParticle, type.getName().toLowerCase(), x, y, z, xOffset, yOffset, zOffset, speed, amount);
        } else if(ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_13)) {
            setPacket(packetPlayOutWorldParticle, type.toNMS(), var2, x, y, z, xOffset, yOffset, zOffset, speed, amount, data);
        } else {
            setPacket(packetPlayOutWorldParticle, x, y, z, xOffset, yOffset, zOffset, speed, amount,
                    var2, type.toNMS());
        }
    }

    @Override
    public void updateObject() {

    }
}