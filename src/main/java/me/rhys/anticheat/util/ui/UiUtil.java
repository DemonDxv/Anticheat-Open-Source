package me.rhys.anticheat.util.ui;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class UiUtil {
    public static ItemStack generateItem(ItemStack itemStack, String itemName, List<String> meta) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setLore(meta);
        itemMeta.setDisplayName(itemName);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public static ItemStack createSpacer() {
        ItemStack i = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 15);
        ItemMeta im = i.getItemMeta();
        im.setDisplayName(" ");
        i.setItemMeta(im);
        return i;
    }

    public static ItemStack createSpacer(byte color) {
        ItemStack i = new ItemStack(Material.STAINED_GLASS_PANE, 1, color);
        ItemMeta im = i.getItemMeta();
        im.setDisplayName(" ");
        i.setItemMeta(im);
        return i;
    }
}
