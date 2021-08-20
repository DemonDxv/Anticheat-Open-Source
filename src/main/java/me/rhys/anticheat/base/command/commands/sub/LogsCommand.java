package me.rhys.anticheat.base.command.commands.sub;

import me.rhys.anticheat.Anticheat;
import me.rhys.anticheat.base.check.api.Check;
import me.rhys.anticheat.base.user.User;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class LogsCommand {

    public void execute(String[] args, String s, CommandSender commandSender) {
        User user = Anticheat.getInstance().getUserManager().getUser(((Player) commandSender));

        try {
            if (user != null) {
                if (args.length >= 2) {
                    String targetName = args[1];

                    if (targetName.length() > 0) {
                        User target = Anticheat.getInstance().getUserManager().getUser(Bukkit.getPlayer(args[1]));
                        if (target != null) {

                            commandSender.sendMessage(ChatColor.GRAY + "Recent Checks flagged: " + ChatColor.GREEN
                                    + 20 +"/"+ target.getFlaggedChecks().size());

                            HashMap<Check, Integer> tmp = new HashMap<>();
                            AtomicInteger total = new AtomicInteger();

                            target.getFlaggedChecks().forEach((c, i) -> {
                                if (total.get() <= 20) {
                                    tmp.put(c, i);
                                }
                                total.getAndIncrement();
                            });

                            tmp.forEach((c, v) -> commandSender.sendMessage(ChatColor.GRAY + " - "
                                    + ChatColor.WHITE + c.getCheckName() + "("+c.checkType+")"
                                    + ChatColor.RED + " x"+v));

                        } else {
                            commandSender.sendMessage("[ERROR] Player your trying to ban is [NULL], try another name.");
                        }
                    } else {
                        commandSender.sendMessage("Please enter a valid username.");
                    }
                } else {
                    commandSender.sendMessage("Usage: /ac logs (player)");
                }
            } else {
                commandSender.sendMessage("How tf are u running this command?");
            }
        } catch (NullPointerException nullP) {

        }
    }
}