package me.rhys.bedrock.tinyprotocol.packet.types;

import lombok.NoArgsConstructor;
import me.rhys.bedrock.tinyprotocol.api.NMSObject;
import me.rhys.bedrock.tinyprotocol.api.ProtocolVersion;
import me.rhys.bedrock.tinyprotocol.reflection.FieldAccessor;
import org.bukkit.entity.Player;

@NoArgsConstructor
public class WrappedPlayerInfoData extends NMSObject {
    private static final String type = Type.PLAYERINFODATA;

    private static final FieldAccessor<Enum> enumGamemodeAccessor = fetchField(type, Enum.class, 0);
    private static final FieldAccessor<Object> profileAcessor = fetchFieldByName(type, "d", Object.class);
    private static final FieldAccessor<Integer> pingAcessor = fetchField(type, Integer.class, 0);

    private int ping;
    private WrappedEnumGameMode gameMode;
    private WrappedGameProfile gameProfile;
    private String username = "";

    public WrappedPlayerInfoData(Object object, Player player) {
        super(object, player);
    }

    @Override
    public void updateObject() {

    }

    public WrappedPlayerInfoData(Object object) {
        super(object);
        ping = fetch(pingAcessor);
        gameProfile = new WrappedGameProfile(fetch(profileAcessor));
        gameMode = WrappedEnumGameMode.fromObject(fetch(enumGamemodeAccessor));
    }

    public WrappedPlayerInfoData(WrappedGameProfile gameProfile, WrappedEnumGameMode gameMode, int ping) {
        this.ping = ping;
        this.gameProfile = gameProfile;
        this.gameMode = gameMode;
    }

    @Override
    public void process(Player player, ProtocolVersion version) {
        super.process(player, version);

        ping = fetch(pingAcessor);
        gameProfile = new WrappedGameProfile(fetch(profileAcessor));
        gameMode = WrappedEnumGameMode.fromObject(fetch(enumGamemodeAccessor));
        username = player.getName();
    }
}
