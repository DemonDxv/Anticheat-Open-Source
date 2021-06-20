package me.rhys.bedrock.tinyprotocol.packet.types;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.Getter;
import me.rhys.bedrock.tinyprotocol.api.NMSObject;
import me.rhys.bedrock.tinyprotocol.api.packets.reflections.Reflections;
import me.rhys.bedrock.tinyprotocol.api.packets.reflections.types.WrappedClass;
import me.rhys.bedrock.tinyprotocol.api.packets.reflections.types.WrappedConstructor;
import me.rhys.bedrock.tinyprotocol.api.packets.reflections.types.WrappedMethod;

@Getter
public class WrappedPacketDataSerializer extends NMSObject {

    public static WrappedClass vanillaClass = Reflections.getNMSClass("PacketDataSerializer");
    private static final WrappedMethod readBytesMethod = vanillaClass.getMethod("array");
    private static final WrappedMethod hasArray = vanillaClass.getMethod("hasArray");
    private static final WrappedConstructor byteConst = vanillaClass.getConstructor(ByteBuf.class);

    private final byte[] data;

    public WrappedPacketDataSerializer(Object object) {
        super(object);

        boolean hasArray = WrappedPacketDataSerializer.hasArray.invoke(object);

        if(hasArray)
            data = readBytesMethod.invoke(object);
        else data = new byte[0];
    }

    @Override
    public void updateObject() {
        //Empty method
    }

    public WrappedPacketDataSerializer(byte[] data) {
        Object pds = byteConst.newInstance(Unpooled.wrappedBuffer(data));

        this.data = data;
        setObject(pds);
    }
}
