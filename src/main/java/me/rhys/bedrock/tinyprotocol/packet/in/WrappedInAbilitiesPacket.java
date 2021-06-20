package me.rhys.bedrock.tinyprotocol.packet.in;

import lombok.Getter;
import me.rhys.bedrock.tinyprotocol.api.NMSObject;
import me.rhys.bedrock.tinyprotocol.api.ProtocolVersion;
import me.rhys.bedrock.tinyprotocol.reflection.FieldAccessor;
import org.bukkit.entity.Player;


public class WrappedInAbilitiesPacket extends NMSObject {
    private static final String packet = Client.ABILITIES;
    private static final FieldAccessor<Boolean>
            invulnerableField = fetchField(packet, boolean.class, 0);
    private static final FieldAccessor<Boolean> flyingField = fetchField(packet, boolean.class, 1);
    private static final FieldAccessor<Boolean> allowedFlightField = fetchField(packet, boolean.class, 2);
    private static final FieldAccessor<Boolean> creativeModeField = fetchField(packet, boolean.class, 3);
    private static final FieldAccessor<Float>
            flySpeedField = fetchField(packet, float.class, 0);
    private static final FieldAccessor<Float> walkSpeedField = fetchField(packet, float.class, 1);

    @Getter
    private boolean invulnerable, flying, allowedFlight, creativeMode;
    @Getter
    private float flySpeed, walkSpeed;


    public WrappedInAbilitiesPacket(Object object, Player player) {
        super(object, player);
    }

    @Override
    public void updateObject() {

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