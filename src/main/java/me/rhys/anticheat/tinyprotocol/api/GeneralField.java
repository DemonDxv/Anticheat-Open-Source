package me.rhys.anticheat.tinyprotocol.api;

import lombok.RequiredArgsConstructor;
import me.rhys.anticheat.tinyprotocol.api.packets.reflections.types.WrappedField;

@RequiredArgsConstructor
public class GeneralField {
    public final WrappedField field;
    private final Object object;

    public <T> T getObject() {
        return (T) object;
    }
}