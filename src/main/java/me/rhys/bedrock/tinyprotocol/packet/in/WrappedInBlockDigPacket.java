package me.rhys.bedrock.tinyprotocol.packet.in;

import lombok.Getter;
import me.rhys.bedrock.tinyprotocol.api.NMSObject;
import me.rhys.bedrock.tinyprotocol.api.ProtocolVersion;
import me.rhys.bedrock.tinyprotocol.api.packets.reflections.Reflections;
import me.rhys.bedrock.tinyprotocol.api.packets.reflections.types.WrappedClass;
import me.rhys.bedrock.tinyprotocol.api.packets.reflections.types.WrappedField;
import me.rhys.bedrock.tinyprotocol.packet.types.BaseBlockPosition;
import me.rhys.bedrock.tinyprotocol.packet.types.enums.WrappedEnumDirection;
import me.rhys.bedrock.util.reflection.MinecraftReflection;
import org.bukkit.entity.Player;

//TODO Fix for 1.7.10.
@Getter
public class WrappedInBlockDigPacket extends NMSObject {
    private static final WrappedClass packet = Reflections.getNMSClass(Client.BLOCK_DIG);

    // 1.8+ Fields
    private static WrappedField fieldBlockPosition, fieldDirection, fieldDigType;

    // 1.7.10 and below fields
    private static WrappedField fieldPosX, fieldPosY, fieldPosZ, fieldFace, fieldIntAction;

    // Decoded data
    private BaseBlockPosition position;
    private WrappedEnumDirection direction;
    private EnumPlayerDigType action;


    public WrappedInBlockDigPacket(Object packet, Player player) {
        super(packet, player);
    }

    @Override
    public void updateObject() {
        if(ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_8)) {
            set(fieldPosX, position.getX());
            set(fieldPosY, position.getY());
            set(fieldPosZ, position.getZ());
            set(fieldFace, direction.ordinal()); //TODO Test if this causes errors.
            set(fieldIntAction, action.ordinal()); //TODO Test if this causes errors.
        } else {
            set(fieldBlockPosition, position.getObject());
            set(fieldDirection, direction.toVanilla());
            set(fieldDigType, action.toVanilla());
        }
    }

    @Override
    public void process(Player player, ProtocolVersion version) {
        if (ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_8)) {
            position = new BaseBlockPosition(fetch(fieldPosX), fetch(fieldPosY), fetch(fieldPosZ));
            direction = WrappedEnumDirection.values()[Math.min(fetch(fieldFace), 5)];
            action = EnumPlayerDigType.values()[(int)fetch(fieldIntAction)];
        } else if(ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.v1_15)){
            position = new BaseBlockPosition(fetch(fieldBlockPosition));
            direction = WrappedEnumDirection.fromVanilla(fetch(fieldDirection));
            action = EnumPlayerDigType.fromVanilla(fetch(fieldDigType));
        }
    }

    public enum EnumPlayerDigType {
        START_DESTROY_BLOCK,
        ABORT_DESTROY_BLOCK,
        STOP_DESTROY_BLOCK,
        DROP_ALL_ITEMS,
        DROP_ITEM,
        RELEASE_USE_ITEM,
        SWAP_HELD_ITEMS;

        public static WrappedClass classDigType;

        public static EnumPlayerDigType fromVanilla(Enum obj) {
            return valueOf(obj.name());
        }

        public <T> T toVanilla() {
            return (T) classDigType.getEnum(name());
        }
    }

    static {
        if(ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_8)) {
            fieldPosX = packet.getFieldByType(int.class, 0);
            fieldPosY = packet.getFieldByType(int.class, 1);
            fieldPosZ =  packet.getFieldByType(int.class, 2);
            fieldFace =  packet.getFieldByType(int.class, 3);
            fieldIntAction =  packet.getFieldByType(int.class, 4);
        } else {
            fieldBlockPosition = packet.getFieldByType(MinecraftReflection.blockPos.getParent(), 0);
            fieldDirection = packet.getFieldByType(WrappedEnumDirection.enumDirection.getParent(), 0);
            EnumPlayerDigType.classDigType = Reflections.getNMSClass("PacketPlayInBlockDig$EnumPlayerDigType");
            fieldDigType = packet.getFieldByType(EnumPlayerDigType.classDigType.getParent(), 0);
        }
    }
}
