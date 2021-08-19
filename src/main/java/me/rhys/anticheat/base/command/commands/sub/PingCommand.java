package me.rhys.anticheat.base.command.commands.sub;

import me.rhys.anticheat.Anticheat;
import me.rhys.anticheat.base.check.api.Check;
import me.rhys.anticheat.base.user.User;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PingCommand {

    public void execute(String[] args, String s, CommandSender commandSender) {
        User user = Anticheat.getInstance().getUserManager().getUser(((Player) commandSender));

        try {
            if (user != null) {
                if (args.length >= 2) {
                    String targetName = args[1];

                    if (targetName.length() > 0) {
                        User target = Anticheat.getInstance().getUserManager().getUser(Bukkit.getPlayer(args[1]));
                        if (target != null) {
                            commandSender.sendMessage(target.getPlayer().getName() + "'s ping is " + ChatColor.GREEN
                                    + target.getConnectionProcessor().getTransPing() + "." + ChatColor.RESET +

                                    "\nKeepAlive Ping: " + ChatColor.GREEN
                                            + user.getConnectionProcessor().getPing() + "." +

                                    ChatColor.RESET + "\nAvg Ping: " + ChatColor.GREEN
                                            + user.getConnectionProcessor().getAverageTransactionPing() + ".");
                        } else {
                            commandSender.sendMessage("[ERROR] Player your trying to ban is NULL, try another name.");
                        }
                    } else {
                        commandSender.sendMessage("Please enter a valid username.");
                    }
                } else {
                    commandSender.sendMessage("Usage: /ac ping (player)");
                }
            } else {
                commandSender.sendMessage("How tf are u running this command?");
            }
        } catch (NullPointerException nullP) {

        }
    }
}