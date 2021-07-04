package me.rhys.anticheat.base.command.commands.sub;

import me.rhys.anticheat.Anticheat;
import me.rhys.anticheat.base.check.api.Check;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class ChecksCommand {

    public void execute(String[] args, String s, CommandSender commandSender) {
        if (args.length >= 2) {
            String checkName = args[1];

            if (checkName.length() > 0) {
                Check foundCheck = Anticheat.getInstance().getCheckManager().getCheckList().stream().filter(check ->
                        checkName.equalsIgnoreCase(check.getFriendlyName())).findAny().orElse(null);

                if (foundCheck != null) {

                    if (foundCheck.isEnabled()) {
                        commandSender.sendMessage(ChatColor.RED + "Disabled the check " + ChatColor.GREEN
                                + foundCheck.getFriendlyName());
                    } else {
                        commandSender.sendMessage(ChatColor.GREEN + "Enabled the check " + ChatColor.GREEN
                                + foundCheck.getFriendlyName());
                    }

                    foundCheck.setEnabled(!foundCheck.isEnabled());

                    Anticheat.getInstance().getExecutorService().execute(() -> {
                        //Disable the current check for everyone else
                        Anticheat.getInstance().getUserManager().getUserMap().forEach((uuid, user) ->
                                user.getCheckManager().getCheckList().forEach(check ->
                                        check.setEnabled(foundCheck.isEnabled())));

                        Anticheat.getInstance().getCheckManager().saveChecks();
                    });
                } else {
                    commandSender.sendMessage(ChatColor.RED + "Unable to find the check "
                            + ChatColor.GRAY + checkName);
                }
            }
        }
    }
}
