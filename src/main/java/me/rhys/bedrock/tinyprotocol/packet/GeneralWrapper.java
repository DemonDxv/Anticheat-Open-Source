package me.rhys.bedrock.tinyprotocol.packet;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.val;
import me.rhys.bedrock.tinyprotocol.api.GeneralField;
import me.rhys.bedrock.tinyprotocol.api.packets.reflections.types.WrappedClass;
import me.rhys.bedrock.tinyprotocol.api.packets.reflections.types.WrappedMethod;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/* This class can be used for packets that do not have dedicated wrappers or change constantly between versions. */
@Getter
@RequiredArgsConstructor
public class GeneralWrapper {
    private final List<GeneralField> fields = new ArrayList<>();
    private List<WrappedMethod> methods;
    private final WrappedClass objectClass;

    public GeneralWrapper(Object object) {
        objectClass = new WrappedClass(object.getClass());
        methods = objectClass.getMethods();

        objectClass.getFields().forEach(field -> fields.add(new GeneralField(field, field.get(object))));
    }

    public <T> T build(Object... args) {
        val classes = Arrays.stream(args).map(Object::getClass).toArray(Class<?>[]::new);

        return objectClass.getConstructor(classes).newInstance(args);
    }

}
