package me.rhys.bedrock.tinyprotocol.packet.out;

import lombok.Getter;
import me.rhys.bedrock.tinyprotocol.api.NMSObject;
import me.rhys.bedrock.tinyprotocol.api.ProtocolVersion;
import me.rhys.bedrock.tinyprotocol.packet.types.WrappedChatMessage;
import me.rhys.bedrock.tinyprotocol.reflection.FieldAccessor;
import org.bukkit.entity.Player;

@Getter
public class WrappedOutOpenWindow extends NMSObject {

    private static final String packet = Server.OPEN_WINDOW;

    public WrappedOutOpenWindow(Object object, Player player) {
        super(object, player);
    }

    @Override
    public void updateObject() {

    }

    public WrappedOutOpenWindow(int id, String name, WrappedChatMessage msg, int size) {
        setPacket(packet, id, name, msg.getObject(), size);
    }

    private static final FieldAccessor<Integer> idField = fetchField(packet, int.class, 0);
    private static final FieldAccessor<String> nameField = fetchField(packet, String.class, 0);
    private static final FieldAccessor<Object> chatCompField = fetchField(packet, Object.class, 2);
    private static final FieldAccessor<Integer> inventorySize = fetchField(packet, int.class, 1);

    private int id;
    private String name;
    private WrappedChatMessage chatComponent;
    private int size;

    @Override
    public void process(Player player, ProtocolVersion version) {
        id = fetch(idField);
        name = fetch(nameField);
        chatComponent = new WrappedChatMessage(fetch(chatCompField));
        size = fetch(inventorySize);
    }
}
