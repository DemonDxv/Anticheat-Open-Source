package me.rhys.bedrock.tinyprotocol.packet.out;

import lombok.Getter;
import me.rhys.bedrock.tinyprotocol.api.NMSObject;
import me.rhys.bedrock.tinyprotocol.api.ProtocolVersion;
import me.rhys.bedrock.tinyprotocol.reflection.FieldAccessor;
import org.bukkit.entity.Player;

@Getter
public class WrappedOutResourcePackSend extends NMSObject {
    private static final String packet = Server.RESOURCE_PACK_STATUS;

    private static final FieldAccessor<String> fieldURL = fetchField(packet, String.class, 0);
    private static final FieldAccessor<String> fieldHash = fetchField(packet, String.class, 1);

    private String url;
    private String hash;

    public WrappedOutResourcePackSend(String URL, String hash) {
        setPacket(packet, URL, hash);
    }

    public WrappedOutResourcePackSend(Object object, Player player) {
        super(object, player);
    }

    @Override
    public void updateObject() {

    }

    @Override
    public void process(Player player, ProtocolVersion version) {
        url = fetch(fieldURL);
        hash = fetch(fieldHash);
    }
}
