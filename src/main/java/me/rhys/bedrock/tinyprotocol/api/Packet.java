/*
 * Copyright (c) 2018 NGXDEV.COM. Licensed under MIT.
 */

package me.rhys.bedrock.tinyprotocol.api;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.rhys.bedrock.tinyprotocol.reflection.FieldAccessor;
import me.rhys.bedrock.tinyprotocol.reflection.Reflection;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor
@Getter
@Setter
public abstract class Packet {
    private static Map<String, Class<?>> constructors = new HashMap<>();
    private Object packet;
    private boolean cancelled;

    public Packet(Object packet) {
        this.packet = packet;
    }

    public static Object construct(String packet, Object... args) {
        try {
            Class<?> c = constructors.get(packet);
            if (c == null) {
                c = Reflection.getMinecraftClass(packet);
                constructors.put(packet, c);
            }
            Object p = c.newInstance();
            Field[] fields = c.getDeclaredFields();
            int failed = 0;
            for (int i = 0; i < args.length; i++) {
                Object o = args[i];
                if (o == null) continue;
                fields[i - failed].setAccessible(true);
                try {
                    fields[i - failed].set(p, o);
                } catch (Exception e) {
                    //attempt to continue
                    failed++;
                }
            }
            return p;
        } catch (Exception e) {
            System.out.println("The plugin cannot work as protocol incompatibilities were detected... Disabling...");
            e.printStackTrace();
        }
        return null;
    }

    public static <T> FieldAccessor<T> fetchField(String className, Class<T> fieldType, int index) {
        return Reflection.getFieldSafe(Reflection.NMS_PREFIX + "." + className, fieldType, index);
    }

    public static boolean isPositionLook(String type) {
        switch (type) {
            case Client.POSITION_LOOK:
            case Client.LEGACY_POSITION_LOOK:
            case Server.REL_POSITION_LOOK:
                return true;
            default:
                return false;
        }
    }

    public static boolean isPosition(String type) {
        switch (type) {
            case Client.POSITION:
            case Client.LEGACY_POSITION:
            case Server.REL_POSITION:
                return true;
            default:
                return false;
        }
    }

    public static boolean isLook(String type) {
        switch (type) {
            case Client.LOOK:
            case Client.LEGACY_LOOK:
            case Server.REL_LOOK:
                return true;
            default:
                return false;
        }
    }

    public String getPacketName() {
        String name = packet.getClass().getName();
        return name.substring(name.lastIndexOf(".") + 1);
    }

    public void process(Player player, ProtocolVersion version) {
    }

    public void setPacket(String packet, Object... args) {
        this.packet = construct(packet, args);
    }

    public <T> T fetch(FieldAccessor<T> field) {
        return field.get(packet);
    }

    public static class Type {
        public static final String WATCHABLE_OBJECT = (Reflection.VERSION.startsWith("v1_7") || Reflection.VERSION.startsWith("v1_8_R1")) ? "WatchableObject" : "DataWatcher$WatchableObject";
        public static final String BASEBLOCKPOSITION = "BaseBlockPosition";
        public static final String ITEMSTACK = Reflection.NMS_PREFIX + ".ItemStack";
        public static final String ENTITY = Reflection.NMS_PREFIX + ".Entity";
        public static final String DATAWATCHER = Reflection.NMS_PREFIX + ".DataWatcher";
        public static final String DATAWATCHEROBJECT = Reflection.NMS_PREFIX + ".DataWatcherObject";
        public static final String CRAFTITEMSTACK = Reflection.OBC_PREFIX + ".inventory.CraftItemStack";
        public static final String GAMEPROFILE = "com.mojang.authlib.GameProfile";
        public static final String PROPERTYMAP = "com.mojang.authlib.PropertyMap";
    }

    public static class Client {
        private static final String CLIENT = "PacketPlayIn";

        public static final String KEEP_ALIVE = CLIENT + "KeepAlive";
        public static final String FLYING = CLIENT + "Flying";

        public static final String POSITION = CLIENT + "Position";
        public static final String POSITION_LOOK = CLIENT + "PositionLook";
        public static final String LOOK = CLIENT + "Look";
        @Deprecated
        public static final String LEGACY_POSITION = FLYING + "$" + CLIENT + "Position";
        @Deprecated
        public static final String LEGACY_POSITION_LOOK = FLYING + "$" + CLIENT + "PositionLook";
        @Deprecated
        public static final String LEGACY_LOOK = FLYING + "$" + CLIENT + "Look";

        public static final String TRANSACTION = CLIENT + "Transaction";
        public static final String BLOCK_DIG = CLIENT + "BlockDig";
        public static final String ENTITY_ACTION = CLIENT + "EntityAction";
        public static final String USE_ENTITY = CLIENT + "UseEntity";
        public static final String WINDOW_CLICK = CLIENT + "WindowClick";
        public static final String CUSTOM_PAYLOAD = CLIENT + "CustomPayload";
        public static final String ARM_ANIMATION = CLIENT + "ArmAnimation";
        public static final String BLOCK_PLACE = CLIENT + "BlockPlace";
        public static final String ABILITIES = CLIENT + "Abilities";
        public static final String HELD_ITEM_SLOT = CLIENT + "HeldItemSlot";
        public static final String CLOSE_WINDOW = CLIENT + "CloseWindow";
        public static final String TAB_COMPLETE = CLIENT + "TabComplete";
        public static final String CHAT = CLIENT + "Chat";
        public static final String STEER_VEHICLE = CLIENT + "SteerVehicle";
        public static final String CLIENT_COMMAND = CLIENT + "ClientCommand";
        public static final String RESOURCE_PACK_STATUS = CLIENT + "ResourcePackStatus";
        public static final String SETTINGS = CLIENT + "Settings";
    }

    public static class Server {
        private static final String SERVER = "PacketPlayOut";
        public static final String ENTITY_TELEPORT = SERVER + "EntityTeleport";

        public static final String WORLD_PARTICLE = SERVER + "WorldParticles";

        public static final String ENTITY = SERVER + "Entity";
        public static final String REL_POSITION = ENTITY + "$" + SERVER + "EntityMove";
        public static final String REL_POSITION_LOOK = ENTITY + "$" + SERVER + "EntityMoveLook";
        public static final String REL_LOOK = ENTITY + "$" + SERVER + "EntityLook";
        public static final String LEGACY_REL_POSITION = SERVER + "EntityMove";
        public static final String LEGACY_REL_POSITION_LOOK = SERVER + "EntityMoveLook";
        public static final String LEGACY_REL_LOOK = SERVER + "EntityLook";
        public static final String ENTITY_HEAD_ROTATION = SERVER + "EntityHeadRotation";

        public static final String KEEP_ALIVE = SERVER + "KeepAlive";
        public static final String CHAT = SERVER + "Chat";
        public static final String POSITION = SERVER + "Position";
        public static final String TRANSACTION = SERVER + "Transaction";
        public static final String NAMED_ENTITY_SPAWN = SERVER + "NamedEntitySpawn";
        public static final String SPAWN_ENTITY_LIVING = SERVER + "SpawnEntityLiving";
        public static final String SPAWN_ENTITY = SERVER + "SpawnEntity";
        public static final String CUSTOM_PAYLOAD = SERVER + "CustomPayload";
        public static final String ABILITIES = SERVER + "Abilities";
        public static final String ENTITY_METADATA = SERVER + "EntityMetadata";
        public static final String ENTITY_VELOCITY = SERVER + "EntityVelocity";
        public static final String ENTITY_DESTROY = SERVER + "EntityDestroy";
        public static final String BLOCK_CHANGE = SERVER + "BlockChange";
        public static final String CLOSE_WINDOW = SERVER + "CloseWindow";
        public static final String HELD_ITEM = SERVER + "HeldItemSlot";
        public static final String TAB_COMPLETE = SERVER + "TabComplete";
        public static final String RESPAWN = SERVER + "Respawn";
        public static final String ATTACH = SERVER + "AttachEntity";
        public static final String OPEN_WINDOW = SERVER + "OpenWindow";
    }

    public static class Login {
        public static final String HANDSHAKE = "PacketHandshakingInSetProtocol";
        public static final String PING = "PacketStatusInPing";
        public static final String START = "PacketStatusInStart";
    }
}
