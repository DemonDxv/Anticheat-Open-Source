package me.rhys.anticheat.base.command.commands.sub;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.rhys.anticheat.Anticheat;
import me.rhys.anticheat.base.check.api.Check;
import me.rhys.anticheat.base.check.impl.CheckManager;
import me.rhys.anticheat.base.user.User;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ChecksCommand {

    private List<String> disabledChecks = new ArrayList<>();

    public void execute(String[] args, String s, CommandSender commandSender) {
        if (args.length > 2) {
            User user = Anticheat.getInstance().getUserManager().getUser((Player) commandSender);
            if (user != null) {

                Check foundCHeck = user.getCheckManager().getCheckList().parallelStream().filter(check ->
                        check.getCheckName().equalsIgnoreCase(args[1])
                                && check.getCheckType().equalsIgnoreCase(args[2])).findAny().orElse(null);

                if (foundCHeck != null) {

                    String combined = args[1] + args[2];

                    String combined2 = args[1] + " " + args[2];

                    if (!disabledChecks.contains(combined)) {
                        disabledChecks.add(combined);
                        commandSender.sendMessage(  "Disabled check: " + ChatColor.RED + combined2);
                        this.update(false, args[1], args[2]);
                    } else {
                        disabledChecks.remove(combined);
                        commandSender.sendMessage("Enabled check: " + ChatColor.GREEN + combined2);
                        this.update(true, args[1], args[2]);
                    }
                } else {
                    commandSender.sendMessage(ChatColor.RED+"[ERROR] No check found.");
                }
            }
        }
    }

    private void update(boolean enabled, String name, String type) {
        Anticheat.getInstance().getUserManager().getUserMap().forEach((uuid, user) -> {
            CheckManager.updateCheckState(user, name, type, enabled);
        });
    }

    @Getter
    @AllArgsConstructor
    public static class Data {
        private String name, type;
        private boolean bool;
    }
}
