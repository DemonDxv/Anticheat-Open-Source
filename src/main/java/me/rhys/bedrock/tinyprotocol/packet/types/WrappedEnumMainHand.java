package me.rhys.bedrock.tinyprotocol.packet.types;


import me.rhys.bedrock.tinyprotocol.api.ProtocolVersion;
import me.rhys.bedrock.tinyprotocol.api.packets.reflections.Reflections;
import me.rhys.bedrock.tinyprotocol.api.packets.reflections.types.WrappedClass;

public enum WrappedEnumMainHand {
    LEFT(new WrappedChatMessage("options.mainHand.left")),
    RIGHT(new WrappedChatMessage("options.mainHand.right"));

    private final WrappedChatMessage c;
    public static WrappedClass vanillaClass;

    WrappedEnumMainHand(WrappedChatMessage var2) {
        this.c = var2;
    }

    public String toString() {
        return this.c.getChatMessage();
    }

    public <T> T toVanilla() {
        return (T) vanillaClass.getEnum(name());
    }

    public static WrappedEnumMainHand fromVanilla(Object object) {
        if(object instanceof Enum) {
            return valueOf(((Enum)object).name());
        }
        return null;
    }

    static {
        if(ProtocolVersion.getGameVersion().isAbove(ProtocolVersion.V1_8_9))
            vanillaClass = Reflections.getNMSClass("EnumMainHand");
    }
}