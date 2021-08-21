package me.rhys.anticheat.base.command.commands.sub;

import me.rhys.anticheat.Anticheat;
import me.rhys.anticheat.database.api.InputData;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LogsCommand {

    public void execute(String[] args, String s, CommandSender commandSender) {
        if (args.length > 1) {
            String playerName = args[1];

            if (playerName != null && playerName.length() > 1) {

                if (!Anticheat.getInstance().getDatabaseManager().getDatabase().isSetup()) {
                    commandSender.sendMessage(ChatColor.RED + "Please setup MongoDB in the config.yml!");
                    return;
                }

                commandSender.sendMessage(ChatColor.GRAY + "Contacting database...");
                new Thread(() -> {
                    List<InputData> logs = Anticheat.getInstance().getDatabaseManager().getDatabase()
                            .getLogs(playerName);

                    if (logs.size() > 0) {
                        commandSender.sendMessage(ChatColor.GREEN + "All violations for "
                                + ChatColor.GRAY + playerName + ChatColor.GRAY + " (" +
                                ChatColor.RED + logs.size() + ChatColor.GRAY + ")");

                        Map<String, Integer> integerMap = new HashMap<>();
                        logs.forEach(inputData -> {
                            String name = inputData.getCheckName() + inputData.getCheckType();
                            integerMap.put(name, integerMap.getOrDefault(name, 0) + 1);
                        });

                        integerMap.forEach((s1, integer) -> commandSender.sendMessage(ChatColor.GRAY
                                + " - " + ChatColor.RED
                                + s1 + ChatColor.DARK_GRAY + " x" + ChatColor.GRAY + integer));

                        integerMap.clear();
                        logs.clear();
                    } else {
                        commandSender.sendMessage(ChatColor.GRAY + playerName + " " + ChatColor.RED + "has no logs.");
                    }
                }).start();
            } else {
                commandSender.sendMessage(ChatColor.RED + "Please specify a players name!");
            }
        } else {
            commandSender.sendMessage(ChatColor.RED + "Please specify a players name!");
        }
    }
}