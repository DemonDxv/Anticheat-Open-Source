package me.rhys.anticheat.tinyprotocol.packet.out;

import lombok.Getter;
import me.rhys.anticheat.tinyprotocol.api.NMSObject;
import me.rhys.anticheat.tinyprotocol.api.ProtocolVersion;
import me.rhys.anticheat.tinyprotocol.api.packets.reflections.Reflections;
import me.rhys.anticheat.tinyprotocol.api.packets.reflections.types.WrappedClass;
import me.rhys.anticheat.tinyprotocol.api.packets.reflections.types.WrappedField;
import org.bukkit.entity.Player;

import java.util.List;

@Getter
public class WrappedOutRelativePosition extends NMSObject {
    private static final String packet = Server.ENTITY;

    private static WrappedClass packetClass = Reflections.getNMSClass(packet);
    private static WrappedField fieldId, fieldX, fieldY, fieldZ, fieldYaw, fieldPitch, fieldGround;

    // Decoded data
    private int id;
    private boolean look, pos, ground;

    public WrappedOutRelativePosition(Object packet, Player player) {
        super(packet, player);
    }

    @Override
    public void process(Player player, ProtocolVersion version) {
        String name = getPacketName();

        if (ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_8)) {
            pos = name.equals(Server.LEGACY_REL_POSITION) || name.equals(Server.LEGACY_REL_POSITION_LOOK);
            look = name.equals(Server.LEGACY_REL_LOOK) || name.equals(Server.LEGACY_REL_POSITION_LOOK);
        } else {
            pos = name.equals(Server.REL_POSITION) || name.equals(Server.REL_POSITION_LOOK);
            look = name.equals(Server.REL_LOOK) || name.equals(Server.REL_POSITION_LOOK);
        }
        id = fetch(fieldId);
        ground = fetch(fieldGround);
    }

    @Override
    public void updateObject() {

    }

    public double getX() {
        return fetch(fieldX);
    }


    public double getY() {
        return fetch(fieldY);
    }

    public double getZ() {
        return fetch(fieldZ);
    }

    public double getYaw() {
        return fetch(fieldYaw);
    }

    public double getPitch() {
        return fetch(fieldPitch);
    }


    static {
        List<WrappedField> fields = packetClass.getFields(true);

        fieldId = fields.get(0);
        fieldX = fields.get(1);
        fieldY = fields.get(2);
        fieldZ = fields.get(3);
        fieldYaw = fields.get(4);
        fieldPitch = fields.get(5);
        fieldGround = fields.get(6);
    }
}