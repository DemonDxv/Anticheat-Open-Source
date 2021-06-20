package me.rhys.bedrock.tinyprotocol.packet.out;

import me.rhys.bedrock.tinyprotocol.api.NMSObject;
import me.rhys.bedrock.tinyprotocol.api.ProtocolVersion;
import me.rhys.bedrock.tinyprotocol.packet.GeneralWrapper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.UUID;

public class WrappedOutSpawnEntityPacket extends NMSObject {

    public Entity entity;
    public UUID uuid;
    public int entityId;
    public Vector location;
    public int yaw, pitch, var2, var3;
    public int motX, motY, motZ;
    private GeneralWrapper wrapper;

    public WrappedOutSpawnEntityPacket(Object object, Player player) {
        super(object, player);
        wrapper = new GeneralWrapper(object);
        setPacket(Server.SPAWN_ENTITY, object);
        process(player, ProtocolVersion.getGameVersion());
    }

    @Override
    public void updateObject() {

    }

    public WrappedOutSpawnEntityPacket(Player player, Object... objects) {
        wrapper = new GeneralWrapper(wrapper);
        setPacket(Server.SPAWN_ENTITY, objects);
        process(player, ProtocolVersion.getGameVersion());
    }

    @SuppressWarnings("Duplicates")
    @Override
    public void process(Player player, ProtocolVersion version) {
        if(ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_9)) {
            if(wrapper.getFields().size() == 11) {
                entityId = wrapper.getFields().get(0).getObject();
                entity = player.getWorld().getEntities().stream().filter(entity -> entity.getEntityId() == entityId)
                        .findFirst().orElse(null);

                if (entity != null) uuid = entity.getUniqueId();
                int x = wrapper.getFields().get(1).getObject(),
                        y = wrapper.getFields().get(2).getObject(),
                        z = wrapper.getFields().get(3).getObject();
                location = new Vector(x, y, z);
                motX = wrapper.getFields().get(4).getObject();
                motY = wrapper.getFields().get(5).getObject();
                motZ = wrapper.getFields().get(6).getObject();
                pitch = wrapper.getFields().get(7).getObject();
                yaw = wrapper.getFields().get(8).getObject();
                var2 = wrapper.getFields().get(9).getObject();
                var3 = wrapper.getFields().get(10).getObject();
            } else System.out.println("&cError on WrappedOutSpawnEntityPacket, size=" + wrapper.getFields().size());
        } else {
            if(wrapper.getFields().size() == 12) {
                entityId = wrapper.getFields().get(0).getObject();
                uuid = wrapper.getFields().get(1).getObject();
                entity = player.getWorld().getEntities()
                        .stream()
                        .filter(ent -> ent.getUniqueId().equals(uuid))
                        .findFirst().orElse(null);
                location = new Vector(
                        wrapper.getFields().get(2).getObject(),
                        wrapper.getFields().get(3).getObject(),
                        wrapper.getFields().get(4).getObject());
                motX = wrapper.getFields().get(5).getObject();
                motY = wrapper.getFields().get(6).getObject();
                motZ = wrapper.getFields().get(7).getObject();
                pitch = wrapper.getFields().get(8).getObject();
                yaw = wrapper.getFields().get(9).getObject();
                var2 = wrapper.getFields().get(10).getObject();
                var3 = wrapper.getFields().get(11).getObject();
            } else System.out.println("&cError on WrappedOutSpawnEntityPacket, size=" + wrapper.getFields().size());
        }
    }
}