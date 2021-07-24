package me.rhys.anticheat.config;

import me.rhys.anticheat.Anticheat;

public class ConfigLoader {

    public void load() {
        Anticheat.getInstance().getConfig().options().copyDefaults(true);
        Anticheat.getInstance().saveConfig();

        Anticheat.getInstance().getConfigValues().setPrefix(this.convertColor(Anticheat.getInstance().getConfig()
                .getString("Alert.Prefix")));
        Anticheat.getInstance().getConfigValues().setAlertsMessage(this.convertColor(Anticheat.getInstance().getConfig()
                .getString("Alert.Alert-Message")));
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

        Anticheat.getInstance().getConfigValues().setMongoEnabled(Anticheat.getInstance().getConfig()
                .getBoolean("Mongo.Enabled"));
        Anticheat.getInstance().getConfigValues().setAnnounceMessage(this.convertColor(Anticheat.getInstance().getConfig()
                .getString("Mongo.Hostname")));
        Anticheat.getInstance().getConfigValues().setDatabase(this.convertColor(Anticheat.getInstance().getConfig()
                .getString("Mongo.Database")));
        Anticheat.getInstance().getConfigValues().setMUsername(this.convertColor(Anticheat.getInstance().getConfig()
                .getString("Mongo.Username")));
        Anticheat.getInstance().getConfigValues().setMPassword(this.convertColor(Anticheat.getInstance().getConfig()
                .getString("Mongo.Password")));
        Anticheat.getInstance().getConfigValues().setAnnounceMessage(this.convertColor(Anticheat.getInstance().getConfig()
                .getString("Mongo.LogsFileName")));
    }

    String convertColor(String in) {
        return in.replace("&", "§");
    }
}
