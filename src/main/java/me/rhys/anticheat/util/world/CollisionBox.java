package me.rhys.anticheat.util.world;

import me.rhys.anticheat.tinyprotocol.packet.types.WrappedEnumParticle;
import me.rhys.anticheat.util.world.types.SimpleCollisionBox;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.List;

public interface CollisionBox {
    boolean isCollided(CollisionBox other);
    void draw(WrappedEnumParticle particle, Collection<? extends Player> players);
    CollisionBox copy();
    CollisionBox offset(double x, double y, double z);
    void downCast(List<SimpleCollisionBox> list);
    boolean isNull();
}