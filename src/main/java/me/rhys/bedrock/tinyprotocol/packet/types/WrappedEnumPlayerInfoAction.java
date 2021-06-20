package me.rhys.bedrock.tinyprotocol.packet.types;

import me.rhys.bedrock.tinyprotocol.api.packets.reflections.Reflections;
import me.rhys.bedrock.tinyprotocol.api.packets.reflections.types.WrappedClass;

public enum WrappedEnumPlayerInfoAction {
    ADD_PLAYER("addPlayer"),
    UPDATE_GAME_MODE("updateGamemode"),
    UPDATE_LATENCY("updatePing"),
    UPDATE_DISPLAY_NAME("updateDisplayName"),
    REMOVE_PLAYER("removePlayer");

    public String legacyMethodName;
    public static WrappedClass enumPlayerInfoAction =
            Reflections.getNMSClass("PacketPlayOutPlayerInfo$EnumPlayerInfoAction");

    WrappedEnumPlayerInfoAction(String legacyMethodName) {
        this.legacyMethodName = legacyMethodName;
    }

    public <T> T toVanilla() {
        return (T) enumPlayerInfoAction.getEnum(name());
    }
}
