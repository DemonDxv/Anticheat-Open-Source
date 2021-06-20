/*
 * Copyright (c) 2018 NGXDEV.COM. Licensed under MIT.
 */

package me.rhys.bedrock.tinyprotocol.packet.in;

import lombok.Getter;
import me.rhys.bedrock.tinyprotocol.api.NMSObject;
import me.rhys.bedrock.tinyprotocol.api.ProtocolVersion;
import me.rhys.bedrock.tinyprotocol.api.packets.reflections.Reflections;
import me.rhys.bedrock.tinyprotocol.api.packets.reflections.types.WrappedClass;
import me.rhys.bedrock.tinyprotocol.api.packets.reflections.types.WrappedField;
import org.bukkit.entity.Player;

@Getter
public class WrappedInFlyingPacket extends NMSObject {
    private static final WrappedClass packet = Reflections.getNMSClass(Client.FLYING);

    // Fields
    private static final WrappedField fieldX = fetchField(packet, double.class, 0);
    private static final WrappedField fieldY = fetchField(packet, double.class, 1);
    private static final WrappedField fieldZ = fetchField(packet, double.class, 2);
    private static final WrappedField fieldYaw = fetchField(packet, float.class, 0);
    private static final WrappedField fieldPitch = fetchField(packet, float.class, 1);
    private static final WrappedField fieldGround = fetchField(packet, boolean.class, 0);
    private static final WrappedField hasPos = fetchField(packet, boolean.class, 1);
    private static final WrappedField hasLook = fetchField(packet, boolean.class, 2);

    // Decoded data
    private double x, y, z;
    private float yaw, pitch;
    private boolean look, pos, ground;

    public WrappedInFlyingPacket(Object packet, Player player) {
        super(packet, player);
    }

    @Override
    public void process(Player player, ProtocolVersion version) {
        x = fetch(fieldX);
        y = fetch(fieldY);
        z = fetch(fieldZ);
        yaw = fetch(fieldYaw);
        pitch = fetch(fieldPitch);
        ground = fetch(fieldGround);
        pos = fetch(hasPos);
        look = fetch(hasLook);
    }

    @Override
    public void updateObject() {
        set(fieldX, x);
        set(fieldY, y);
        set(fieldZ, z);
        set(fieldYaw, yaw);
        set(fieldPitch, pitch);
        set(fieldGround, ground);
        set(hasPos, pos);
        set(hasLook, look);
    }
}
