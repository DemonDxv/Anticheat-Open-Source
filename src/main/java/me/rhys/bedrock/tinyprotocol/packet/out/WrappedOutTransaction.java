package me.rhys.bedrock.tinyprotocol.packet.out;

import lombok.Getter;
import lombok.Setter;
import me.rhys.bedrock.tinyprotocol.api.NMSObject;
import me.rhys.bedrock.tinyprotocol.api.ProtocolVersion;
import me.rhys.bedrock.tinyprotocol.reflection.FieldAccessor;
import org.bukkit.entity.Player;

@Getter
@Setter
public class WrappedOutTransaction extends NMSObject {
    private static final String packet = Server.TRANSACTION;
    private static final FieldAccessor<Integer> fieldId = fetchField(packet, int.class, 0);
    private static final FieldAccessor<Short> fieldAction = fetchField(packet, short.class, 0);
    private static final FieldAccessor<Boolean> fieldAccepted = fetchField(packet, boolean.class, 0);

    private int id;
    private short action;
    private boolean accept;

    public WrappedOutTransaction(int id, short action, boolean accept) {
        setPacket(packet, id, action, accept);
    }

    public WrappedOutTransaction(Object packet, Player player) {
        super(packet, player);
    }

    public void updateAction(short action) {
        this.action = action;
        setPacket(packet, action);
    }

    @Override
    public void updateObject() {

    }

    @Override
    public void process(Player player, ProtocolVersion version) {
        id = fetch(fieldId);
        action = fetch(fieldAction);
        accept = fetch(fieldAccepted);
    }
}
