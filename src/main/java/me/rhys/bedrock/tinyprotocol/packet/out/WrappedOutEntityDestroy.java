package me.rhys.bedrock.tinyprotocol.packet.out;

import lombok.Getter;
import me.rhys.bedrock.tinyprotocol.api.NMSObject;

@Getter
public class WrappedOutEntityDestroy extends NMSObject {
    private static final String packet = Server.ENTITY_DESTROY;

    public WrappedOutEntityDestroy(int[] ids) {
        setPacket(packet, ids);
    }

    @Override
    public void updateObject() {

    }
}