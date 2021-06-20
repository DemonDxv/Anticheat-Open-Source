package me.rhys.bedrock.tinyprotocol.packet.in;

import lombok.Getter;
import me.rhys.bedrock.tinyprotocol.api.NMSObject;
import me.rhys.bedrock.tinyprotocol.api.ProtocolVersion;
import me.rhys.bedrock.tinyprotocol.api.packets.reflections.Reflections;
import me.rhys.bedrock.tinyprotocol.api.packets.reflections.types.WrappedClass;
import me.rhys.bedrock.tinyprotocol.api.packets.reflections.types.WrappedField;
import me.rhys.bedrock.tinyprotocol.packet.types.Vec3D;
import me.rhys.bedrock.tinyprotocol.packet.types.enums.WrappedEnumHand;
import me.rhys.bedrock.tinyprotocol.reflection.FieldAccessor;
import me.rhys.bedrock.util.reflection.MinecraftReflection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.Objects;

@Getter
public class WrappedInUseEntityPacket extends NMSObject {
    private static final String packet = Client.USE_ENTITY;

    private static final FieldAccessor<Integer> fieldId = fetchField(packet, int.class, 0);
    private static final FieldAccessor<Enum> fieldAction = fetchField(packet, Enum.class, 0);
    private static final WrappedClass packetClass = Reflections.getNMSClass(packet);
    private static final WrappedClass enumEntityUseAction = Reflections.getNMSClass(
                    (ProtocolVersion.getGameVersion().isAbove(ProtocolVersion.V1_8)
                            ? "PacketPlayInUseEntity$" : "") + "EnumEntityUseAction");
    private static WrappedField vecField, handField;

    private int id;
    private EnumEntityUseAction action;
    private Entity entity;
    private Vec3D vec;
    private WrappedEnumHand enumHand;

    public WrappedInUseEntityPacket(Object packet, Player player) {
        super(packet, player);
    }

    @Override
    public void process(Player player, ProtocolVersion version) {
        id = Objects.requireNonNull(fetch(fieldId));
        Enum fieldAct = Objects.nonNull(fetch(fieldAction)) ? fetch(fieldAction) : null;
        action = fieldAct == null ? EnumEntityUseAction.INTERACT_AT : EnumEntityUseAction.valueOf(fieldAct.name());

      //  this.entity = Sparky.getInstance().getEntities()
        //        .getOrDefault(player.getWorld().getUID(), new ArrayList<>()).stream()
          //      .filter(entity1 -> id == entity1.getEntityId()).findAny().orElse(null);

        //fixes strange entity bug?
        this.entity = player.getWorld().getEntities().stream()
                .filter(entity1 -> id == entity1.getEntityId()).findAny().orElse(null);

        if (ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.V1_8)) {
            Object vec = fetch(vecField);
            if (vec != null)
                this.vec = new Vec3D(vec);
        }
        if (ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.V1_9)) {
            enumHand = WrappedEnumHand.getFromVanilla(fetch(handField));
        } else enumHand = WrappedEnumHand.MAIN_HAND;
    }

    @Override
    public void updateObject() {
        if (ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.V1_9)) {
            vec.updateObject();
            setPacket(packet, id, enumEntityUseAction.getEnum(action.toString()),
                    vec.getObject(), enumHand.toEnumHand());
        } else if (ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.V1_8)) {
            setPacket(packet, id, enumEntityUseAction.getEnum(action.toString()), vec.getObject());
        } else setPacket(packet, id, enumEntityUseAction.getEnum(action.toString()));
    }

    public enum EnumEntityUseAction {
        INTERACT("INTERACT"),
        ATTACK("ATTACK"),
        INTERACT_AT("INTERACT_AT");

        @Getter
        private final String name;

        EnumEntityUseAction(String name) {
            this.name = name;
        }
    }

    static {
        if (ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.V1_8)) {
            vecField = packetClass.getFieldByType(MinecraftReflection.vec3D.getParent(), 0);
        } else if (ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.V1_9)) {
            handField = packetClass.getFieldByType(WrappedEnumHand.enumHandClass.getParent(), 0);
        }
    }
}