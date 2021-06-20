package me.rhys.bedrock.tinyprotocol.packet.login;


import me.rhys.bedrock.tinyprotocol.api.NMSObject;
import me.rhys.bedrock.tinyprotocol.api.ProtocolVersion;
import me.rhys.bedrock.tinyprotocol.api.packets.reflections.Reflections;
import me.rhys.bedrock.tinyprotocol.api.packets.reflections.types.WrappedClass;
import me.rhys.bedrock.tinyprotocol.api.packets.reflections.types.WrappedField;
import me.rhys.bedrock.tinyprotocol.packet.types.enums.WrappedEnumProtocol;
import org.bukkit.entity.Player;

public class WrappedHandshakingInSetProtocol extends NMSObject {

    private static final WrappedClass packet = Reflections.getNMSClass(Login.HANDSHAKE);

    public WrappedHandshakingInSetProtocol(Object object) {
        super(object);
        updateObject();
    }

    public int a, port;
    public String hostname;
    public WrappedEnumProtocol enumProtocol;

    private static final WrappedField aField = packet.getFieldByType(int.class, 0);
    private static final WrappedField hostField = packet.getFieldByType(String.class, 0);
    private static final WrappedField portField = packet.getFieldByType(int.class, 1);
    private static final WrappedField protocolField = packet.getFieldByType(WrappedEnumProtocol.enumProtocol.getParent(), 0);

    @Override
    public void process(Player player, ProtocolVersion version) {
        a = aField.get(getObject());
        hostname = hostField.get(getObject());
        port = portField.get(getObject());
        enumProtocol = WrappedEnumProtocol.fromVanilla(protocolField.get(getObject()));
    }

    @Override
    public void updateObject() {
        setObject(NMSObject.construct(getObject(), Login.HANDSHAKE, a, hostname, port, enumProtocol.toVanilla()));
    }
}