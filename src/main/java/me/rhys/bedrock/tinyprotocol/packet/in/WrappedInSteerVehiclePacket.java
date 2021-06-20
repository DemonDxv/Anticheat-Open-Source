package me.rhys.bedrock.tinyprotocol.packet.in;

import lombok.Getter;
import me.rhys.bedrock.tinyprotocol.api.NMSObject;
import me.rhys.bedrock.tinyprotocol.api.ProtocolVersion;
import org.bukkit.entity.Player;

@Getter
public class WrappedInSteerVehiclePacket extends NMSObject {
    private static final String packet = Client.STEER_VEHICLE;

    // Fields

    // Decoded data


    public WrappedInSteerVehiclePacket(Object packet, Player player) {
        super(packet, player);
    }

    @Override
    public void updateObject() {

    }

    @Override
    public void process(Player player, ProtocolVersion version) {

    }
}
