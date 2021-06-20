package me.rhys.bedrock.config;

import me.rhys.bedrock.Anticheat;

public class ConfigLoader {

    public void load() {
        Anticheat.getInstance().getConfig().options().copyDefaults(true);
        Anticheat.getInstance().saveConfig();

        Anticheat.getInstance().getConfigValues().setPrefix(this.convertColor(Anticheat.getInstance().getConfig()
                .getString("Prefix")));
        Anticheat.getInstance().getConfigValues().setLagBack(Anticheat.getInstance().getConfig()
                .getBoolean("Punishment.LagBack"));
        Anticheat.getInstance().getConfigValues().setPunish(Anticheat.getInstance().getConfig()
                .getBoolean("Punishment.Command.Enabled"));
        Anticheat.getInstance().getConfigValues().setPunishCommand(this.convertColor(Anticheat.getInstance().getConfig()
                .getString("Punishment.Command.Execute")));
        Anticheat.getInstance().getConfigValues().setAnnounce(Anticheat.getInstance().getConfig()
                .getBoolean("Punishment.Announce.Enabled"));
        Anticheat.getInstance().getConfigValues().setAnnounceMessage(this.convertColor(Anticheat.getInstance().getConfig()
                .getString("Punishment.Announce.Message")));
    }

    String convertColor(String in) {
        return in.replace("&", "§");
    }
}
