package me.rhys.anticheat.config;

import me.rhys.anticheat.Anticheat;
import me.rhys.anticheat.util.file.ConfigFile;

public class ConfigLoader {

    public void load() {
        ConfigFile.getInstance().setup(Anticheat.getInstance());
        ConfigFile.getInstance().writeDefaults();

        Anticheat.getInstance().getConfigValues().setConsoleAlerts(ConfigFile.getInstance().getData()
                .getBoolean("Alert.Console-Alerts"));
        Anticheat.getInstance().getConfigValues().setPrefix(this.convertColor(ConfigFile.getInstance().getData()
                .getString("Alert.Prefix")));
        Anticheat.getInstance().getConfigValues().setAlertsMessage(this.convertColor(ConfigFile.getInstance().getData()
                .getString("Alert.Alert-Message")));
        Anticheat.getInstance().getConfigValues().setDiscord(ConfigFile.getInstance().getData()
                .getBoolean("Alert.Discord"));
        Anticheat.getInstance().getConfigValues().setDiscordWebURL(ConfigFile.getInstance().getData()
                .getString("Alert.Discord-WebhookURL"));
        Anticheat.getInstance().getConfigValues().setDiscordAlerts(ConfigFile.getInstance().getData()
                .getString("Alert.Discord-Alert-Message"));
        Anticheat.getInstance().getConfigValues().setLagBack(ConfigFile.getInstance().getData()
                .getBoolean("Punishment.LagBack"));
        Anticheat.getInstance().getConfigValues().setPunish(ConfigFile.getInstance().getData()
                .getBoolean("Punishment.Command.Enabled"));
        Anticheat.getInstance().getConfigValues().setPunishCommand(this.convertColor(ConfigFile.getInstance().getData()
                .getString("Punishment.Command.Execute")));
        Anticheat.getInstance().getConfigValues().setAnnounce(ConfigFile.getInstance().getData()
                .getBoolean("Punishment.Announce.Enabled"));
        Anticheat.getInstance().getConfigValues().setAnnounceMessage(this.convertColor(ConfigFile.getInstance().getData()
                .getString("Punishment.Announce.Message")));

        Anticheat.getInstance().getConfigValues().setAllowOp(ConfigFile.getInstance().getData()
                .getBoolean("Bypass.Op-Bypass"));

        Anticheat.getInstance().getConfigValues().setLogs(ConfigFile.getInstance().getData()
                .getBoolean("Logs.Enabled"));

        Anticheat.getInstance().getConfigValues().setMongoDBURI(ConfigFile.getInstance().getData()
                .getString("Logs.MongoDBURI"));
    }

    String convertColor(String in) {
        return in.replace("&", "§");
    }


}
