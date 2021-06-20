package me.rhys.bedrock.tinyprotocol.packet.in;

import lombok.Getter;
import me.rhys.bedrock.tinyprotocol.api.NMSObject;
import me.rhys.bedrock.tinyprotocol.api.ProtocolVersion;
import me.rhys.bedrock.tinyprotocol.packet.types.BaseBlockPosition;
import me.rhys.bedrock.tinyprotocol.reflection.FieldAccessor;
import org.bukkit.entity.Player;

@Getter
public class WrappedInTabComplete extends NMSObject {

    private static final String packet = Client.TAB_COMPLETE;

    public WrappedInTabComplete(Object object, Player player) {
        super(object, player);
    }

    @Override
    public void updateObject() {

    }

    private static final FieldAccessor<String> messageAccessor = fetchField(packet, String.class, 0);
    private static FieldAccessor<Boolean> hasToolTipAccessor;

    private String message;
    private BaseBlockPosition blockPosition; //1.8 and up only.
    private boolean hasToolTip; //1.9 and up only.

    @Override
    public void process(Player player, ProtocolVersion version) {
        message = fetch(messageAccessor);

        if(ProtocolVersion.getGameVersion().isAbove(ProtocolVersion.V1_8_9)) {
            hasToolTipAccessor = fetchField(packet, boolean.class, 0);
            hasToolTip = fetch(hasToolTipAccessor);
        }
    }
}
