package me.rhys.bedrock.tinyprotocol.packet.out;

import lombok.val;
import me.rhys.bedrock.tinyprotocol.api.NMSObject;
import me.rhys.bedrock.tinyprotocol.api.ProtocolVersion;
import me.rhys.bedrock.tinyprotocol.api.packets.reflections.Reflections;
import me.rhys.bedrock.tinyprotocol.api.packets.reflections.types.WrappedClass;
import me.rhys.bedrock.tinyprotocol.api.packets.reflections.types.WrappedConstructor;
import me.rhys.bedrock.tinyprotocol.api.packets.reflections.types.WrappedMethod;
import me.rhys.bedrock.tinyprotocol.packet.types.WrappedEnumGameMode;
import me.rhys.bedrock.tinyprotocol.packet.types.WrappedEnumPlayerInfoAction;
import me.rhys.bedrock.tinyprotocol.packet.types.WrappedGameProfile;
import me.rhys.bedrock.tinyprotocol.packet.types.WrappedPlayerInfoData;
import me.rhys.bedrock.tinyprotocol.reflection.FieldAccessor;
import me.rhys.bedrock.util.reflection.MinecraftReflection;
import me.rhys.bedrock.util.reflection.ReflectionsUtil;
import org.bukkit.entity.Player;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class WrappedOutPlayerInfo extends NMSObject {
    private static final String packet = Server.PLAYER_INFO;

    private static final WrappedClass playerInfoClass = Reflections.getNMSClass(packet);
    private static WrappedConstructor constructor;
    private static final WrappedClass chatBaseComp = Reflections.getNMSClass("IChatBaseComponent");
    private static final WrappedClass chatSerialClass = Reflections.getNMSClass("IChatBaseComponent$ChatSerializer");

    private static final WrappedMethod stcToComponent = chatSerialClass.getMethod("a", String.class);

    public WrappedOutPlayerInfo(Object object, Player player) {
        super(object, player);
    }

    public WrappedOutPlayerInfo(WrappedEnumPlayerInfoAction action, Player player) {
        if(ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.V1_8)) {
            val construct = playerInfoClass.getConstructor(WrappedEnumPlayerInfoAction.enumPlayerInfoAction.getParent(),
                    Array.newInstance(MinecraftReflection.entityPlayer.getParent(), 0).getClass());

            Object array = Array.newInstance(MinecraftReflection.entityPlayer.getParent(), 1);
            Array.set(array, 0, ReflectionsUtil.getEntityPlayer(player));
            setObject(construct.newInstance(action.toVanilla(), array));
        } else {
            Object packet = playerInfoClass.getConstructor().newInstance();
            playerInfoClass.getMethod(action.legacyMethodName, ReflectionsUtil.EntityPlayer)
                    .invoke(packet, ReflectionsUtil.getEntityPlayer(player));

            setObject(packet);
        }
    }

    //1.8+
    private static FieldAccessor<List> playerInfoListAccessor;
    private static FieldAccessor<Enum> actionAcessorEnum;

    //1.7.10
    private static FieldAccessor<Integer> actionAcessorInteger;
    private static FieldAccessor<Integer> gamemodeAccessor;
    private static FieldAccessor<Object> profileAcessor;
    private static FieldAccessor<Integer> pingAcessor;


    private final List<WrappedPlayerInfoData> playerInfo = new ArrayList<>();
    private WrappedEnumPlayerInfoAction action;

    @Override
    public void process(Player player, ProtocolVersion version) {
        if(ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.V1_8)) {
            playerInfoListAccessor = fetchField(packet, List.class, 0);
            actionAcessorEnum = fetchField(packet, Enum.class, 0);

            List list = fetch(playerInfoListAccessor);

            for (Object object : list) {
                playerInfo.add(new WrappedPlayerInfoData(object));
            }

            action = WrappedEnumPlayerInfoAction.valueOf(fetch(actionAcessorEnum).name());
        } else {
            actionAcessorInteger = fetchField(packet, Integer.class, 5);
            profileAcessor = fetchFieldByName(packet, "player", Object.class);
            gamemodeAccessor = fetchField(packet, Integer.class, 6);
            pingAcessor = fetchField(packet, Integer.class, 7);

            action = WrappedEnumPlayerInfoAction.values()[fetch(actionAcessorInteger)];

            WrappedGameProfile profile = new WrappedGameProfile(fetch(profileAcessor));
            WrappedEnumGameMode gamemode = WrappedEnumGameMode.getById(fetch(gamemodeAccessor));
            int ping = fetch(pingAcessor);
            playerInfo.add(new WrappedPlayerInfoData(profile, gamemode, ping));
        }
    }

    @Override
    public void updateObject() {

    }

    static {
        if(ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.V1_8)) {
            WrappedClass playerInfoDataClass = Reflections.getNMSClass(packet + "$PlayerInfoData");
            //constructor = playerInfoDataClass.getConstructor(Object.class, int.class, Object.class, Object.class);
        }
    }
}
