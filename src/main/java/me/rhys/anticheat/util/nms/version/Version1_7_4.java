package me.rhys.anticheat.util.nms.version;

import me.rhys.anticheat.util.nms.NmsInterface;
import net.minecraft.server.v1_7_R4.EntityPlayer;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class Version1_7_4 implements NmsInterface {
    public int getVersion(Player player) {
        EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
        return entityPlayer.playerConnection.networkManager.getVersion();
    }
}
