package me.rhys.bedrock.tinyprotocol.packet.in;

import lombok.Getter;
import me.rhys.bedrock.tinyprotocol.api.NMSObject;
import me.rhys.bedrock.tinyprotocol.api.ProtocolVersion;
import me.rhys.bedrock.tinyprotocol.reflection.FieldAccessor;
import org.bukkit.entity.Player;

public class WrappedInChatPacket extends NMSObject {
    private static final String packet = Client.CHAT;

    private static final FieldAccessor<String> messageAccessor = fetchField(packet, String.class, 0);

    public WrappedInChatPacket(Object object, Player player) {
        super(object, player);
    }

    @Override
    public void updateObject() {

    }

    @Getter
    private String message;

    @Override
    public void process(Player player, ProtocolVersion version) {
        this.message = fetch(messageAccessor);
    }
}
