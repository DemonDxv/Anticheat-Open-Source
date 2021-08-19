package me.rhys.anticheat.base.command.commands;

import me.rhys.anticheat.Anticheat;
import me.rhys.anticheat.base.command.commands.sub.*;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class MainCommand extends BukkitCommand {
    private final String line = ChatColor.GRAY + "§m------------------------------------------";
    private final AlertsCommand alertsCommand = new AlertsCommand();
    private final ChecksCommand checksCommand = new ChecksCommand();
    private final ForceBanCommand forceBanCommand = new ForceBanCommand();
    private final GUICommand guiCommand = new GUICommand();
    private final PingCommand pingCommand = new PingCommand();
    private final BanWaveCommand banWaveCommand = new BanWaveCommand();


    public MainCommand(String name) {
        super(name);
        this.description = "Anticheat command.";
        this.usageMessage = "/" + name;
        this.setAliases(new ArrayList<>());
    }

    @Override
    public boolean execute(CommandSender commandSender, String commandLabel, String[] args) {
        if (commandLabel.equalsIgnoreCase("ac")
                || commandLabel.equalsIgnoreCase("anticheat")) {
            if (commandSender.isOp() || commandSender.hasPermission("anticheat.command")) {

                if (args.length < 1) {
                    commandSender.sendMessage(ChatColor.RED + "Anticheat" + ChatColor.GRAY + " - "
                            + ChatColor.RED + Anticheat.getInstance().getDescription().getVersion());
                    commandSender.sendMessage(line);

                    Player player = (Player) commandSender;

                    Anticheat.getInstance().getCommandManager().getCommandList().forEach(command -> {
                        TextComponent textComponent = new TextComponent(ChatColor.GRAY + "» " + ChatColor.WHITE
                                + "/" + command.getCommand() + ChatColor.GRAY + " - " + ChatColor.RED
                                + command.getDescription());
                        textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                new ComponentBuilder((command.getUsage() != null ? ChatColor.RED + command.getUsage()
                                        : ChatColor.WHITE + "No usage found.")).create()));
                        player.spigot().sendMessage(textComponent);
                    });

                    commandSender.sendMessage(line);
                } else {
                    String s = args[0];
                    boolean found = false;

                    if (s.equalsIgnoreCase("alerts")) {
                        found = true;
                        alertsCommand.execute(args, s, commandSender);

                    } else if (s.equalsIgnoreCase("check")) {
                        found = true;
                        checksCommand.execute(args, s, commandSender);
                    } else if (s.equalsIgnoreCase("forceban")) {
                        found = true;
                        forceBanCommand.execute(args, s, commandSender);
                    } else if (s.equalsIgnoreCase("banwave")) {
                      //  found = true;
                      //  banWaveCommand.execute(args, s, commandSender);
                    } else if (s.equalsIgnoreCase("gui")) {
                        found = true;
                        guiCommand.execute(args, s, commandSender);
                    } else if (s.equalsIgnoreCase("ping")) {
                        found = true;
                        pingCommand.execute(args, s, commandSender);
                    }

                    if (!found) commandSender.sendMessage(ChatColor.RED + "Sub command doesn't exist!");
                }
            } else {
                commandSender.sendMessage(ChatColor.RED + "You don't have permission to execute this command.");
            }
        }
        return false;
    }
}
