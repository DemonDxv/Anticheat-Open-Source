package me.rhys.anticheat.base.command.commands.sub;

import me.rhys.anticheat.Anticheat;
import me.rhys.anticheat.base.user.User;
import me.rhys.anticheat.util.ui.UiUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class GUICommand {

    public void execute(String[] args, String s, CommandSender commandSender) {
        User user = Anticheat.getInstance().getUserManager().getUser(((Player) commandSender));

        Inventory inventory = Bukkit.getServer().createInventory(null, 27,
               "Anticheat GUI");

        inventory.setItem(13, UiUtil.generateItem(new ItemStack(Material.BOOK, 1),
                ChatColor.RESET + "Information", Arrays.asList(ChatColor.RED + "Version: "
                                + Anticheat.getInstance().getCurrentVersion(),
                        ChatColor.GREEN + "Latest Version: "+Anticheat.getInstance().getLatestVersion(),
                        ChatColor.DARK_RED + "This GUI is still under development.")));



        for (int slots = 0; slots < 27; slots++) {
            if (inventory.getItem(slots) == null) inventory.setItem(slots,
                    UiUtil.createSpacer((byte) 14));
        }

        user.getPlayer().openInventory(inventory);

    }
}