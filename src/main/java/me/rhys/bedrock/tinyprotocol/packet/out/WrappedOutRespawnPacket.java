package me.rhys.bedrock.tinyprotocol.packet.out;

import lombok.Getter;
import me.rhys.bedrock.tinyprotocol.api.NMSObject;
import me.rhys.bedrock.tinyprotocol.api.ProtocolVersion;
import me.rhys.bedrock.tinyprotocol.api.packets.reflections.Reflections;
import me.rhys.bedrock.tinyprotocol.api.packets.reflections.types.WrappedClass;
import me.rhys.bedrock.tinyprotocol.packet.types.WrappedEnumDifficulty;
import me.rhys.bedrock.tinyprotocol.packet.types.WrappedEnumGameMode;
import me.rhys.bedrock.tinyprotocol.reflection.FieldAccessor;
import org.bukkit.WorldType;
import org.bukkit.entity.Player;

@Getter
public class WrappedOutRespawnPacket extends NMSObject {

    public WrappedOutRespawnPacket(Object object, Player player) {
        super(object, player);
    }

    @Override
    public void updateObject() {

    }

    private static final String packet = Server.RESPAWN;

    private static final FieldAccessor<Enum> difficultyAcessor;
    private static final FieldAccessor<Enum> gamemodeAccessor;
    private static final WrappedClass worldTypeClass;

    //Before 1.13
    private static FieldAccessor<Integer> dimensionAccesor;

    //1.13 and newer version of World ID
    private static FieldAccessor<Object> dimensionManagerAcceessor;
    private static WrappedClass dimensionManagerClass;

    private int dimension;
    private WrappedEnumGameMode gamemode;
    private WrappedEnumDifficulty difficulty;
    private WorldType worldType;

    @Override
    public void process(Player player, ProtocolVersion version) {
        if(ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.V1_13)) {
            Object dimensionManager = fetch(dimensionManagerAcceessor);
            dimension = dimensionManagerClass.getFirstFieldByType(int.class).get(dimensionManager);
        } else {
            dimension = fetch(dimensionAccesor);
        }
        gamemode = WrappedEnumGameMode.fromObject(fetch(gamemodeAccessor));
        difficulty = WrappedEnumDifficulty.fromObject(fetch(difficultyAcessor));
        worldType = WorldType.getByName(worldTypeClass.getFirstFieldByType(String.class).get(getObject()));
    }

    static {
        if(ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.V1_13)) {
            dimensionManagerAcceessor = fetchField(packet, Object.class, 0);
            dimensionManagerClass = Reflections.getNMSClass("DimensionManager");
        } else dimensionAccesor = fetchField(packet, int.class, 0);

        difficultyAcessor = fetchField(packet, Enum.class, 0);
        gamemodeAccessor = fetchField(packet, Enum.class, 1);
        worldTypeClass = Reflections.getNMSClass("WorldType");
    }
}
