package me.rhys.bedrock.tinyprotocol.packet.out;

import lombok.Getter;
import me.rhys.bedrock.tinyprotocol.api.NMSObject;
import me.rhys.bedrock.tinyprotocol.api.ProtocolVersion;
import me.rhys.bedrock.tinyprotocol.api.packets.reflections.Reflections;
import me.rhys.bedrock.tinyprotocol.api.packets.reflections.types.WrappedClass;
import me.rhys.bedrock.tinyprotocol.api.packets.reflections.types.WrappedField;
import me.rhys.bedrock.tinyprotocol.reflection.FieldAccessor;
import org.bukkit.entity.Player;

public class WrappedOutAbilitiesPacket extends NMSObject {
    private static final String packet = Server.ABILITIES;

    private static final FieldAccessor<Boolean>
            invulnerableField = fetchField(packet, boolean.class, 0);
    private static final FieldAccessor<Boolean> flyingField = fetchField(packet, boolean.class, 1);
    private static final FieldAccessor<Boolean> allowedFlightField = fetchField(packet, boolean.class, 2);
    private static final FieldAccessor<Boolean> creativeModeField = fetchField(packet, boolean.class, 3);
    private static final FieldAccessor<Float>
            flySpeedField = fetchField(packet, float.class, 0);
    private static final FieldAccessor<Float> walkSpeedField = fetchField(packet, float.class, 1);

    private static final WrappedClass abilitiesClass = Reflections.getNMSClass("PlayerAbilities");
    private static final WrappedField invulnerableAcc = abilitiesClass.getFieldByType(boolean.class, 0);
    private static final WrappedField flyingAcc = abilitiesClass.getFieldByType(boolean.class, 1);
    private static final WrappedField allowedFlightAcc = abilitiesClass.getFieldByType(boolean.class, 2);
    private static final WrappedField creativeModeAcc = abilitiesClass.getFieldByType(boolean.class, 3);
    private static final WrappedField flySpeedAcc = abilitiesClass.getFieldByType(float.class, 0);
    private static final WrappedField walkSpeedAcc = abilitiesClass.getFieldByType(float.class, 1);
    @Getter
    private boolean invulnerable, flying, allowedFlight, creativeMode;
    @Getter
    private float flySpeed, walkSpeed;


    public WrappedOutAbilitiesPacket(Object object, Player player) {
        super(object, player);
    }

    @Override
    public void updateObject() {

    }

    public WrappedOutAbilitiesPacket(boolean invulernable, boolean flying, boolean allowedFlight, boolean creativeMode, float flySpeed, float walkSpeed) {
        Object abilities = abilitiesClass.getConstructorAtIndex(0).newInstance();
        invulnerableAcc.set(abilities, invulernable);
        flyingAcc.set(abilities, flying);
        allowedFlightAcc.set(abilities, allowedFlight);
        creativeModeAcc.set(abilities, creativeMode);
        flySpeedAcc.set(abilities, flySpeed);
        walkSpeedAcc.set(abilities, walkSpeed);

       setObject(Reflections.getNMSClass(packet).getConstructor(abilitiesClass.getParent()).newInstance(abilities));
    }

    @Override
    public void process(Player player, ProtocolVersion version) {
        invulnerable = fetch(invulnerableField);
        flying = fetch(flyingField);
        allowedFlight = fetch(allowedFlightField);
        creativeMode = fetch(creativeModeField);
        flySpeed = fetch(flySpeedField);
        walkSpeed = fetch(walkSpeedField);
    }
}