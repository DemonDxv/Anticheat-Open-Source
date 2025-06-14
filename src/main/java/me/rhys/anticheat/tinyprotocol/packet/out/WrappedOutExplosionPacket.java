package me.rhys.anticheat.tinyprotocol.packet.out;


import lombok.Getter;
import lombok.Setter;
import me.rhys.anticheat.tinyprotocol.api.NMSObject;
import me.rhys.anticheat.tinyprotocol.api.ProtocolVersion;
import me.rhys.anticheat.tinyprotocol.packet.types.BaseBlockPosition;
import me.rhys.anticheat.tinyprotocol.reflection.FieldAccessor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class WrappedOutExplosionPacket extends NMSObject {
    private static final String packet = Server.EXPLOSION;
    private final FieldAccessor<Double> fieldX = fetchField(packet, double.class, 0);
    private final FieldAccessor<Double> fieldY = fetchField(packet, double.class, 1);
    private final FieldAccessor<Double> fieldZ = fetchField(packet, double.class, 2);
    private final FieldAccessor<Float> fieldRadius = fetchField(packet, float.class, 0);
    private final FieldAccessor<List> fieldBlockRecords = fetchField(packet, List.class, 0);
    private final FieldAccessor<Float> fieldMotionX = fetchField(packet, float.class, 1);
    private final FieldAccessor<Float> fieldMotionY = fetchField(packet, float.class, 2);
    private final FieldAccessor<Float> fieldMotionZ = fetchField(packet, float.class, 3);

    private double x, y, z;
    private final List<BaseBlockPosition> blockRecords = new ArrayList<>();
    private float radius, motionX, motionY, motionZ;

    public WrappedOutExplosionPacket(Object object, Player player) {
        super(object, player);
    }


    @Override
    public void updateObject() {
        set(fieldX, x);
        set(fieldY, y);
        set(fieldZ, z);
        set(fieldRadius, radius);
        set(fieldMotionX, fieldMotionX);
        set(fieldMotionY, fieldMotionY);
        set(fieldMotionZ, fieldMotionZ);

        final List<Object> objects = new ArrayList<>();

        for (BaseBlockPosition blockRecord : blockRecords) {
            objects.add(blockRecord.getAsBlockPosition());
        }

        set(fieldBlockRecords, objects);
    }

    @Override
    public void process(Player player, ProtocolVersion version) {
        y = fetch(fieldY);
        z = fetch(fieldZ);
        radius = fetch(fieldRadius);
        motionX = fetch(fieldMotionX);
        motionY = fetch(fieldMotionY);
        motionZ = fetch(fieldMotionZ);

        final List<Object> blockRecordObjects = fetch(fieldBlockRecords);

        for (Object blockRecordObject : blockRecordObjects) {
            blockRecords.add(new BaseBlockPosition(blockRecordObject));
        }
    }
}