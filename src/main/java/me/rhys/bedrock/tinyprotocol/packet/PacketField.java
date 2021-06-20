package me.rhys.bedrock.tinyprotocol.packet;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.rhys.bedrock.tinyprotocol.api.packets.reflections.types.WrappedField;

@Getter
@AllArgsConstructor
public class PacketField<T> {
    private final WrappedField field;
    private final T value;
}
