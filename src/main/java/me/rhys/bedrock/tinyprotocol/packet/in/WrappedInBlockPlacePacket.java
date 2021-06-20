package me.rhys.bedrock.tinyprotocol.packet.in;

import lombok.Getter;
import me.rhys.bedrock.tinyprotocol.api.NMSObject;
import me.rhys.bedrock.tinyprotocol.api.ProtocolVersion;
import me.rhys.bedrock.tinyprotocol.api.packets.reflections.Reflections;
import me.rhys.bedrock.tinyprotocol.api.packets.reflections.types.WrappedClass;
import me.rhys.bedrock.tinyprotocol.api.packets.reflections.types.WrappedField;
import me.rhys.bedrock.tinyprotocol.packet.types.BaseBlockPosition;
import me.rhys.bedrock.tinyprotocol.packet.types.enums.WrappedEnumDirection;
import me.rhys.bedrock.util.reflection.CraftReflection;
import me.rhys.bedrock.util.reflection.MinecraftReflection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@Getter
public class WrappedInBlockPlacePacket extends NMSObject {
    private static final String packet = Client.BLOCK_PLACE;

    // Fields
    private static WrappedField fieldFace;
    private static WrappedField fieldFace1_9;
    private static WrappedField fieldBlockPosition;
    private static WrappedField fieldItemStack;
    private static WrappedField fieldPosX;
    private static WrappedField fieldPosY;
    private static WrappedField fieldPosZ;
    private static WrappedField fieldVecX;
    private static WrappedField fieldVecY;
    private static WrappedField fieldVecZ;
    private static WrappedField enumHand;

    private static final BaseBlockPosition a = new BaseBlockPosition(-1,-1,-1);

    private static WrappedClass movingObjectBSObject;
    private static final WrappedClass blockPlacePacket;
    private static WrappedClass enumHandClass;

    // Decoded data
    private WrappedEnumDirection face;
    private ItemStack itemStack;
    private BaseBlockPosition position;
    private boolean mainHand;
    private float vecX, vecY, vecZ;

    public WrappedInBlockPlacePacket(Object packet, Player player) {
        super(packet, player);
        updateObject();
    }

    @Override
    public void process(Player player, ProtocolVersion version) {
        if (ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_8)) {
            position = new BaseBlockPosition(fieldPosX.get(getObject()),
                    fieldPosY.get(getObject()),
                    fieldPosZ.get(getObject()));
            face = WrappedEnumDirection.values()[Math.min(fieldFace.get(getObject()), 5)];
            itemStack = toBukkitStack(fieldItemStack.get(getObject()));
            vecX = fieldVecX.get(getObject());
            vecY = fieldVecY.get(getObject());
            vecZ = fieldVecZ.get(getObject());
            mainHand = true;
        } else if (ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_9)) {
            position = new BaseBlockPosition(fieldBlockPosition.get(getObject()));
            face = WrappedEnumDirection.values()[Math.min(fieldFace.get(getObject()), 5)];
            itemStack = toBukkitStack(fieldItemStack.get(getObject()));
            vecX = fieldVecX.get(getObject());
            vecY = fieldVecY.get(getObject());
            vecZ = fieldVecZ.get(getObject());
            mainHand = true;
        } else {
            position = new BaseBlockPosition(fieldBlockPosition.get(getObject()));
            face = WrappedEnumDirection.values()[((Enum)fieldFace1_9.get(getObject())).ordinal()];
            if(ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_14)) {
                vecX = fieldVecX.get(getObject());
                vecY = fieldVecY.get(getObject());
                vecZ = fieldVecZ.get(getObject());
            }
            mainHand = ((Enum)enumHand.get(getObject())).name().toLowerCase().contains("main");
        }
    }

    @Override
    public void updateObject() {
        if(ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.V1_14)) {
            setObject(NMSObject.construct(getObject(), packet, position.getAsBlockPosition(), face.toVanilla(),
                    mainHand ? enumHandClass.getEnum("MAIN_HAND") : enumHandClass.getEnum("OFF_HAND")));
        } else if(ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.V1_9)) {
            setObject(NMSObject.construct(getObject(), packet, position.getAsBlockPosition(), face.toVanilla(),
                    mainHand ? enumHandClass.getEnum("MAIN_HAND") : enumHandClass.getEnum("OFF_HAND"),
                    vecX, vecY, vecZ));
        } else if(ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.V1_8)) {
            setObject(NMSObject.construct(getObject(), packet, position.getAsBlockPosition(), face.ordinal(),
                    CraftReflection.getVanillaItemStack(itemStack), vecX, vecY, vecZ));
        } else {
            setObject(NMSObject.construct(getObject(), packet, position.getX(), position.getY(), position.getZ(),
                    CraftReflection.getVanillaItemStack(itemStack), vecX, vecY, vecZ));
        }
    }

    static {
        if(ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.V1_9)) {
            blockPlacePacket = Reflections.getNMSClass("PacketPlayInUseItem");
            fieldBlockPosition = blockPlacePacket.getFieldByType(Object.class, 0);
            fieldFace1_9 = blockPlacePacket.getFieldByType(Enum.class, 1);
            if(ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_14)) {
                fieldVecX = blockPlacePacket.getFieldByType(float.class, 0);
                fieldVecY = blockPlacePacket.getFieldByType(float.class, 1);
                fieldVecZ = blockPlacePacket.getFieldByType(float.class, 2);
            }
            enumHand = blockPlacePacket.getFieldByType(Enum.class, 0);
            enumHandClass = Reflections.getNMSClass("EnumHand");
        } else if(ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.V1_8)) {
            blockPlacePacket = Reflections.getNMSClass("PacketPlayInBlockPlace");
            fieldBlockPosition = blockPlacePacket.getFieldByType(MinecraftReflection.blockPos.getParent(), 1);
            fieldFace = blockPlacePacket.getFieldByType(int.class, 0);
            fieldItemStack = blockPlacePacket.getFieldByType(MinecraftReflection.itemStack.getParent(), 0);
            fieldVecX = blockPlacePacket.getFieldByType(float.class, 0);
            fieldVecY = blockPlacePacket.getFieldByType(float.class, 1);
            fieldVecZ = blockPlacePacket.getFieldByType(float.class, 2);
        } else {
            blockPlacePacket = Reflections.getNMSClass("PacketPlayInBlockPlace");
            fieldPosX = blockPlacePacket.getFieldByType(int.class, 0);
            fieldPosY = blockPlacePacket.getFieldByType(int.class, 1);
            fieldPosZ = blockPlacePacket.getFieldByType(int.class, 2);
            fieldFace = blockPlacePacket.getFieldByType(int.class, 3);
            fieldItemStack = blockPlacePacket.getFieldByType(MinecraftReflection.itemStack.getParent(), 0);
            fieldVecX = blockPlacePacket.getFieldByType(float.class, 0);
            fieldVecY = blockPlacePacket.getFieldByType(float.class, 1);
            fieldVecZ = blockPlacePacket.getFieldByType(float.class, 2);
        }
    }
}