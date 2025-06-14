package me.rhys.anticheat.tinyprotocol.packet.in;

import lombok.Getter;
import me.rhys.anticheat.tinyprotocol.api.NMSObject;
import me.rhys.anticheat.tinyprotocol.api.ProtocolVersion;
import me.rhys.anticheat.tinyprotocol.api.packets.reflections.Reflections;
import me.rhys.anticheat.tinyprotocol.api.packets.reflections.types.WrappedClass;
import me.rhys.anticheat.tinyprotocol.api.packets.reflections.types.WrappedField;
import org.bukkit.entity.Player;

@Getter
public class WrappedInSteerVehiclePacket extends NMSObject {
    private static final WrappedClass packetClass = Reflections.getNMSClass(Client.STEER_VEHICLE);

    // Fields
    private float sideways, forward;
    private boolean jump, unmount;

    // Decoded data
    private static final WrappedField sidewaysField = packetClass.getFieldByType(float.class, 0);
    private static final WrappedField forwardField = packetClass.getFieldByType(float.class, 0);
    private static final WrappedField jumpField = packetClass.getFieldByType(boolean.class, 0);
    private static final WrappedField unmountField = packetClass.getFieldByType(boolean.class, 1);


    public WrappedInSteerVehiclePacket(Object packet, Player player) {
        super(packet, player);
    }

    @Override
    public void process(Player player, ProtocolVersion version) {
        sideways = sidewaysField.get(getObject());
        forward = forwardField.get(getObject());

        jump = jumpField.get(getObject());
        unmount = unmountField.get(getObject());
    }

    @Override
    public void updateObject() {
        setObject(NMSObject.construct(getObject(), Client.STEER_VEHICLE, sideways, forward, jump, unmount));
    }
}
