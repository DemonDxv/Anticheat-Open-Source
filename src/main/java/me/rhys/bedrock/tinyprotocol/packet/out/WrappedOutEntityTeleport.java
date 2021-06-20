package me.rhys.bedrock.tinyprotocol.packet.out;


import lombok.Getter;
import me.rhys.bedrock.tinyprotocol.api.NMSObject;
import me.rhys.bedrock.tinyprotocol.api.ProtocolVersion;
import me.rhys.bedrock.tinyprotocol.reflection.FieldAccessor;
import org.bukkit.entity.Player;

@Getter
public class WrappedOutEntityTeleport extends NMSObject {
    private static final String packet = Server.ENTITY_TELEPORT;

    // Fields
    private static final FieldAccessor<Integer> fieldId = fetchField(packet, int.class, 0);
    private static final FieldAccessor<Integer> fieldX = fetchField(packet, int.class, 1);
    private static final FieldAccessor<Integer> fieldY = fetchField(packet, int.class, 2);
    private static final FieldAccessor<Integer> fieldZ = fetchField(packet, int.class, 3);
    private static final FieldAccessor<Byte> fieldYaw = fetchField(packet, byte.class, 0);
    private static final FieldAccessor<Byte> fieldPitch = fetchField(packet, byte.class, 1);
 //   private static final FieldAccessor<Boolean> fieldGround = fetchField(packet, Boolean.class, 0);

    // Decoded data
    private int id, x, y, z;
    private byte yaw, pitch;

    public WrappedOutEntityTeleport(Object packet) {
        super(packet);
    }

    @Override
    public void updateObject() {

    }

    @Override
    public void process(Player player, ProtocolVersion version) {
        id = fetch(fieldId);
        x = fetch(fieldX);
        y = fetch(fieldY);
        z = fetch(fieldZ);
        yaw = fetch(fieldYaw);
        pitch = fetch(fieldPitch);
    }

    public void setId(int id) {
        set(fieldId, id);
    }
}