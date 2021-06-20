package me.rhys.bedrock.tinyprotocol.packet.types.enums;

import me.rhys.bedrock.tinyprotocol.api.packets.reflections.Reflections;
import me.rhys.bedrock.tinyprotocol.api.packets.reflections.types.WrappedClass;

import java.util.Arrays;

public enum WrappedEnumProtocol {

    HANDSHAKING(-1),
    PLAY(0),
    STATUS(1),
    LOGIN(2),
    UNKNOWN(-69); //Not an actual vanilla object.

    public static WrappedClass enumProtocol = Reflections.getNMSClass("EnumProtocol");
    int id;

    WrappedEnumProtocol(int id) {
        this.id = id;
    }

    public <T> T toVanilla() {
        return (T) enumProtocol.getEnum(name());
    }

    public static WrappedEnumProtocol fromVanilla(Enum object) {
        return Arrays.stream(values()).filter(val -> val.name().equals(object.name())).findFirst()
                .orElse(WrappedEnumProtocol.UNKNOWN);
    }
}