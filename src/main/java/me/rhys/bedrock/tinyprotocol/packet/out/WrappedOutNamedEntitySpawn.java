package me.rhys.bedrock.tinyprotocol.packet.out;

import me.rhys.bedrock.tinyprotocol.api.NMSObject;
import me.rhys.bedrock.tinyprotocol.api.ProtocolVersion;
import me.rhys.bedrock.tinyprotocol.api.packets.reflections.Reflections;
import me.rhys.bedrock.tinyprotocol.api.packets.reflections.types.WrappedClass;
import me.rhys.bedrock.tinyprotocol.api.packets.reflections.types.WrappedField;
import me.rhys.bedrock.tinyprotocol.packet.types.MathHelper;
import me.rhys.bedrock.tinyprotocol.packet.types.WrappedGameProfile;
import me.rhys.bedrock.util.reflection.MinecraftReflection;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class WrappedOutNamedEntitySpawn extends NMSObject {

    public WrappedOutNamedEntitySpawn(Object object, Player player) {
        super(object, player);
    }

    private static final WrappedClass packet = Reflections.getNMSClass(Server.NAMED_ENTITY_SPAWN);

    public int entityId;
    public UUID uuid;
    public double x, y, z;
    public byte yaw, pitch;
    public int currentItem;

    private static final WrappedField fieldEntityId, fieldUuid, fieldX, fieldY, fieldZ, fieldYaw, fieldPitch,
            fieldCurrentItem;


    @Override
    public void process(Player player, ProtocolVersion version) {
        entityId = fetch(fieldEntityId);
        yaw = fetch(fieldYaw);
        pitch = fetch(fieldPitch);

        if (ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_8)) {
            uuid = new WrappedGameProfile(fetch(fieldUuid)).id;
        } else uuid = fetch(fieldUuid);

        if (ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_9)) {
            currentItem = fetch(fieldCurrentItem);
            x = (int)fetch(fieldX) / 32.;
            y = (int)fetch(fieldY) / 32.;
            z = (int)fetch(fieldZ) / 32.;
        } else {
            x = fetch(fieldX);
            y = fetch(fieldY);
            z = fetch(fieldZ);
        }
    }

    @Override
    public void updateObject() {
        if (ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_8)) {
            Player player = Bukkit.getPlayer(uuid);
            set(fieldUuid, player != null ? new WrappedGameProfile(player) : null);
        } else set(fieldUuid, uuid);

        if (ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_9)) {
            set(fieldCurrentItem, currentItem);
            set(fieldX, MathHelper.floor(x * 32.));
            set(fieldY, MathHelper.floor(y * 32.));
            set(fieldZ, MathHelper.floor(z * 32.));
        } else {
            set(fieldX, x);
            set(fieldY, y);
            set(fieldZ, z);
        }
    }

    static {
        fieldEntityId = fetchField(packet, int.class, 0);
        fieldYaw = fetchField(packet, byte.class, 0);
        fieldPitch = fetchField(packet, byte.class, 1);

        if (ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_8)) {
            fieldUuid = fetchField(packet, MinecraftReflection.gameProfile.getParent(), 0);
        } else fieldUuid = fetchField(packet, UUID.class, 0);

        if (ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_9)) {
            fieldX = fetchField(packet, int.class, 1);
            fieldY = fetchField(packet, int.class, 2);
            fieldZ = fetchField(packet, int.class, 3);
            fieldCurrentItem = fetchField(packet, int.class, 4);
        } else {
            fieldX = fetchField(packet, double.class, 0);
            fieldY = fetchField(packet, double.class, 1);
            fieldZ = fetchField(packet, double.class, 2);
            fieldCurrentItem = null;
        }
    }
}