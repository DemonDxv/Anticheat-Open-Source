package me.rhys.anticheat.util.world.types;


import me.rhys.anticheat.tinyprotocol.api.ProtocolVersion;
import me.rhys.anticheat.util.world.CollisionBox;
import org.bukkit.block.Block;

public interface CollisionFactory {
    CollisionBox fetch(ProtocolVersion version, Block block);
}