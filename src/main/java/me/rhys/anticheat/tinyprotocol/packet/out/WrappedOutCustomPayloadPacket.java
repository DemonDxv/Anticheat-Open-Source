package me.rhys.anticheat.tinyprotocol.packet.out;

import lombok.Getter;
import me.rhys.anticheat.tinyprotocol.api.NMSObject;
import me.rhys.anticheat.tinyprotocol.api.ProtocolVersion;
import me.rhys.anticheat.tinyprotocol.reflection.FieldAccessor;
import org.bukkit.entity.Player;

public class WrappedOutCustomPayloadPacket extends NMSObject {
    private static final String packet = Server.CUSTOM_PAYLOAD;

    private static FieldAccessor<String> channelAccessor;
    private static FieldAccessor<Object> dataAccessor;

    public WrappedOutCustomPayloadPacket(Object object, Player player) {
        super(object, player);
    }

    @Override
    public void updateObject() {

    }

    @Getter
    private String channel;

    @Getter
    private Object data;

    @Override
    public void process(Player player, ProtocolVersion version) {
        channelAccessor = fetchField(packet, String.class, 0);
        dataAccessor = fetchField(packet, Object.class, 1);
        this.channel = fetch(channelAccessor);
        this.data = fetch(dataAccessor);
    }
}
