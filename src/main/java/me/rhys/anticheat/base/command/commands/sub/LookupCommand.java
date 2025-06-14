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

public class LookupCommand {

    public void execute(String[] args, String s, CommandSender commandSender) {
        User user = Anticheat.getInstance().getUserManager().getUser(((Player) commandSender));

        if (user != null) {
            if (args.length >= 2) {
                String targetName = args[1];

                if (!targetName.isEmpty()) {
                    User target = Anticheat.getInstance().getUserManager().getUser(Bukkit.getPlayer(args[1]));
                    if (target != null) {

                        Inventory inventory = Bukkit.getServer().createInventory(null, 27,
                                "Player: " +target.getPlayer().getName());

                        inventory.setItem(13, UiUtil.generateItem(new ItemStack(Material.BOOK, 1),
                                ChatColor.RESET + "Player Information", Arrays.asList(
                                        "",
                                        ChatColor.GREEN + "Player: " + target.getPlayer().getName(),
                                        ChatColor.GREEN + "Brand: " + target.getMovementProcessor().getClientBrand(),
                                        "",
                                        ChatColor.WHITE + "Transaction Ping: " + target.getConnectionProcessor().getTransPing(),
                                        ChatColor.WHITE + "KeepAlive Ping: " + target.getConnectionProcessor().getPing(),
                                        ChatColor.WHITE + "Average Ping: " + target.getConnectionProcessor().getAverageTransactionPing(),
                                        ChatColor.WHITE + "Client Tick: " + target.getConnectionProcessor().getClientTick(),
                                        ChatColor.WHITE + "Lagging: " + target.getConnectionProcessor().isLagging(),
                                        ChatColor.WHITE + "In Combat: " + target.getCombatProcessor().getUseEntityTimer().hasNotPassed(40)
                                        )));


                        for (int slots = 0; slots < 27; slots++) {
                            if (inventory.getItem(slots) == null) inventory.setItem(slots,
                                    UiUtil.createSpacer((byte) 14));
                        }

                        user.getPlayer().openInventory(inventory);
                    }
                }
            }
        }
    }
}