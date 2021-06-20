package me.rhys.bedrock.tinyprotocol.packet.in;

import lombok.Getter;
import me.rhys.bedrock.tinyprotocol.api.NMSObject;
import me.rhys.bedrock.tinyprotocol.api.ProtocolVersion;
import me.rhys.bedrock.tinyprotocol.reflection.FieldAccessor;
import org.bukkit.entity.Player;

@Getter
public class WrappedInResourcePackStatus extends NMSObject {
    private static final String packet = Client.RESOURCE_PACK_STATUS;

    private static FieldAccessor<String> hash;

    private static FieldAccessor<Enum> fieldStaus;

    private Action status;
    private String hashString;

    public WrappedInResourcePackStatus(Object object, Player player) {
        super(object, player);
    }

    @Override
    public void updateObject() {

    }

    @Override
    public void process(Player player, ProtocolVersion version) {
        hash = fetchField(packet, String.class, 0);

        fieldStaus = fetchField(packet, Enum.class, 1);

        status = Action.values()[Math.min(8, fetch(fieldStaus).ordinal())];
        hashString = fetch(hash);
    }

    public enum Action
    {
        SUCCESSFULLY_LOADED,
        DECLINED,
        FAILED_DOWNLOAD,
        ACCEPTED
    }
}
