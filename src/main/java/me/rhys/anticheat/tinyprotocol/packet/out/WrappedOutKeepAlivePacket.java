package me.rhys.anticheat.tinyprotocol.packet.out;

import lombok.Getter;
import me.rhys.anticheat.tinyprotocol.api.NMSObject;
import me.rhys.anticheat.tinyprotocol.api.ProtocolVersion;
import me.rhys.anticheat.tinyprotocol.reflection.FieldAccessor;
import org.bukkit.entity.Player;

@Getter
public class WrappedOutKeepAlivePacket extends NMSObject {
    private static final String packet = Server.KEEP_ALIVE;

    private static FieldAccessor<Integer> fieldLegacy;
    private static FieldAccessor<Long> field;

    private long time;

    public WrappedOutKeepAlivePacket(long time) {
        if (ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_12)) setPacket(packet, (int) time);
        else setPacket(packet, time);
    }

    public WrappedOutKeepAlivePacket(Object packet, Player player) {
        super(packet, player);
    }

    @Override
    public void updateObject() {

    }

    @Override
    public void process(Player player, ProtocolVersion version) {
        if (ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_12)) {
            fieldLegacy = fetchField(packet, int.class, 0);
            time = fetch(fieldLegacy);
        }
        else {
            field = fetchField(packet, long.class, 0);
            time = fetch(field);
        }
    }
}