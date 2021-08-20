package me.rhys.anticheat.base.command;


import lombok.Getter;
import me.rhys.anticheat.base.command.commands.MainCommand;
import me.rhys.anticheat.util.command.CommandUtils;

import java.util.ArrayList;
import java.util.List;

@Getter
public class CommandManager {
    private final List<Command> commandList = new ArrayList<>();

    public CommandManager() {
        addCommand(new Command(new MainCommand("ac"), "ac", null, "Main command.",
                true));

        addCommand(new Command(new MainCommand("ac"), "ac alerts", "/ac alerts",
                "Toggle on, and off alerts.", true));

        addCommand(new Command(new MainCommand("ac"), "ac check", "/ac check [check&type]",
                "Toggle on, and off detections.", true));

        addCommand(new Command(new MainCommand("ac"), "ac forceban", "/ac forceban [player]",
                "Forceban a player with the anticheat.", true));

        addCommand(new Command(new MainCommand("ac"), "ac ping", "/ac ping [player]",
                "Gets the ping of the target player.", true));

     //   addCommand(new Command(new MainCommand("ac"), "ac banwave", "/ac banwave",
       //         "Banwave Settings", true));

        addCommand(new Command(new MainCommand("ac"), "ac gui", "/ac gui",
                "GUI for the anticheat.", true));

        addCommand(new Command(new MainCommand("ac"), "ac logs", "/ac logs (player)",
                "Shows recent flags of the desired player that are stored", true));
    }

    private void addCommand(Command... commands) {
        for (Command command : commands) {
            commandList.add(command);
            if (command.isEnabled()) CommandUtils.registerCommand(command);
        }
    }

    public void removeCommand() {
        commandList.forEach(CommandUtils::unRegisterBukkitCommand);
    }
}

