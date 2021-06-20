package me.rhys.bedrock.tinyprotocol.packet.out;

import lombok.Getter;
import lombok.Setter;
import me.rhys.bedrock.tinyprotocol.api.NMSObject;
import me.rhys.bedrock.tinyprotocol.api.Packet;
import me.rhys.bedrock.tinyprotocol.api.ProtocolVersion;
import me.rhys.bedrock.tinyprotocol.reflection.FieldAccessor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class WrappedOutEntityMetadata extends NMSObject {
    private static final String packet = Packet.Server.ENTITY_METADATA;

    private static FieldAccessor<Integer> entityidField;
    private static FieldAccessor<List> watchableObjectsField;

    private List<Object> watchableObjects;
    private int entityId;

    public WrappedOutEntityMetadata(Object object, Player player) {
        super(object, player);
    }

    @Override
    public void updateObject() {

    }

    public WrappedOutEntityMetadata(int entityId, List<Object> objects) {
        setPacket(packet, entityId, objects);
    }

    @Override
    public void process(Player player, ProtocolVersion version) {
        watchableObjectsField = fetchField(packet, List.class, 0);
        entityidField = fetchField(packet, int.class, 0);

        watchableObjects = new ArrayList<>();
        entityId = fetch(entityidField);

        List list = fetch(watchableObjectsField);

        if(list != null) {
            list.forEach(object -> watchableObjects.add(object));
        }
    }
}
