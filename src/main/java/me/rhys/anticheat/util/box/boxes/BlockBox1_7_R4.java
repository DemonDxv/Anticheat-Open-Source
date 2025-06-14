package me.rhys.anticheat.util.box.boxes;



import me.rhys.anticheat.util.BlockUtil;
import me.rhys.anticheat.util.box.BlockBox;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class BlockBox1_7_R4 implements BlockBox {


    @Override
    public boolean isChunkLoaded(Location loc) {

        return BlockUtil.isChunkLoaded(loc);

  //      net.minecraft.server.v1_7_R4.World world = ((CraftWorld) loc.getWorld()).getHandle();

//        return !world.isStatic && world.isLoaded(loc.getBlockX(), 0, loc.getBlockZ()) && world.getChunkAtWorldCoords(loc.getBlockX(), loc.getBlockZ()).d;
    }

    @Override
    public boolean isRiptiding(LivingEntity entity) {
        return false;
    }



  //  @Override
  //  public float getMovementFactor(Player player) {
      ///  return (float) ((CraftPlayer) player).getHandle().getAttributeInstance(GenericAttributes.d).getValue();
  //  }
    @Override
    public float getMovementFactor(Player player) {
        return (float) ((CraftPlayer) player).getHandle().getWorld()
                .getType(player.getLocation().getBlockX(), player.getLocation().getBlockY() - 1, player.getLocation()
                        .getBlockZ()).frictionFactor;
    }

    @Override
    public float getWidth(Entity entity) {
        return 0;
    }

    @Override
    public float getHeight(Entity entity) {
        return 0;
    }

}
