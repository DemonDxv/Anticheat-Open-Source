package me.rhys.anticheat.tinyprotocol.packet.out;

import lombok.Getter;
import me.rhys.anticheat.tinyprotocol.api.NMSObject;
import me.rhys.anticheat.tinyprotocol.reflection.FieldAccessor;

@Getter
public class WrappedOutEntityDestroy extends NMSObject {
    private static final String packet = Server.ENTITY_DESTROY;


    private static final FieldAccessor<int[]> fieldId = fetchField(packet, int[].class, 0);

    private int[] entities;

    public WrappedOutEntityDestroy(Object packet) {
        super(packet);
    }

    @Override
    public void updateObject() {
        this.entities = fetch(fieldId);
    }
}