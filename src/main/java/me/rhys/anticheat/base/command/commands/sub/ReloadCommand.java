package me.rhys.anticheat.base.command.commands.sub;


import me.rhys.anticheat.Anticheat;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;


public class ReloadCommand {

    public void execute(String[] args, String s, CommandSender commandSender) {

        Anticheat.getInstance().getUserManager().getUserMap().forEach((uuid, user) -> {

            user.getPlayer().sendMessage("\n" + Anticheat.getInstance().getConfigValues().getPrefix()
                    + ChatColor.GOLD + " Reloading... \n");

            Anticheat.getInstance().getCheckManager().reloadAnticheat();

            user.getPlayer().sendMessage("\n" + Anticheat.getInstance().getConfigValues().getPrefix()
                    + ChatColor.GREEN + " Reloaded! \n");
        });
    }
}
