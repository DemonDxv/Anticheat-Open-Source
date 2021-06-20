package me.rhys.bedrock.tinyprotocol.packet.out;

import lombok.Getter;
import lombok.val;
import me.rhys.bedrock.tinyprotocol.api.NMSObject;
import me.rhys.bedrock.tinyprotocol.api.ProtocolVersion;
import me.rhys.bedrock.tinyprotocol.reflection.FieldAccessor;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public class WrappedOutTabComplete extends NMSObject {

    private static final String packet = Server.TAB_COMPLETE;

    @Getter
    private String[] result;

    //Below 1.13
    private static FieldAccessor<String[]> arrayAccessor;

    //1.13 and newer
    private static FieldAccessor<Object> suggestsAccessor;
    private static FieldAccessor<List> suggestionListAccessor;
    private static FieldAccessor<String> suggestionStringAccessor;

    public WrappedOutTabComplete(Object object) {
        super(object);
    }

    public WrappedOutTabComplete(Object object, Player player) {
        super(object, player);
    }

    @Override
    public void updateObject() {

    }

    //For everything below 1.13. There will be 1.13+ support for this soon.
    public WrappedOutTabComplete(String[] result) {
        setPacketArg(packet, result);
    }

    @Override
    public void process(Player player, ProtocolVersion version) {
        if(ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_13)) {
            result = fetch(arrayAccessor);
        } else {
            Object suggestions = fetch(suggestsAccessor);
            List<Object> suggestsList = (List<Object>) suggestionListAccessor.get(suggestions);


            val strings = suggestsList.stream().map(object -> suggestionStringAccessor.get(object)).collect(Collectors.toList());

            result = new String[strings.size()];
            for (int i = 0; i < strings.size(); i++) {
                result[i] = strings.get(i);
            }
        }
    }

    static {
        if(ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_13)) {
            arrayAccessor = fetchField(packet, String[].class, 0);
        } else {
            suggestsAccessor = fetchField(packet, Object.class, 1);
            suggestionListAccessor = fetchField(packet, List.class, 0);
            suggestionStringAccessor = fetchField(packet, String.class, 0);
        }
    }
}
