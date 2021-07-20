package me.rhys.anticheat.util.world.types;

import lombok.Setter;
import me.rhys.anticheat.tinyprotocol.api.ProtocolVersion;
import me.rhys.anticheat.tinyprotocol.packet.types.WrappedEnumParticle;
import me.rhys.anticheat.util.world.CollisionBox;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.List;

public class DynamicCollisionBox implements CollisionBox {

    private CollisionFactory box;
    @Setter
    private Block block;
    @Setter
    private ProtocolVersion version;
    private double x,y,z;

    public DynamicCollisionBox(CollisionFactory box, Block block, ProtocolVersion version) {
        this.box = box;
        this.block = block;
        this.version = version;
    }

    @Override
    public boolean isCollided(CollisionBox other) {
        return box.fetch(version, block).offset(x,y,z).isCollided(other);
    }

    @Override
    public void draw(WrappedEnumParticle particle, Collection<? extends Player> players) {
        box.fetch(version, block).offset(x,y,z).draw(particle,players);
    }

    @Override
    public CollisionBox copy() {
        return new DynamicCollisionBox(box,block,version).offset(x,y,z);
    }

    @Override
    public CollisionBox offset(double x, double y, double z) {
        this.x+=x;
        this.y+=y;
        this.z+=z;
        return this;
    }

    @Override
    public void downCast(List<SimpleCollisionBox> list) {
        box.fetch(version,block).offset(x,y,z).downCast(list);
    }

    @Override
    public boolean isNull() {
        return box.fetch(version,block).isNull();
    }
}