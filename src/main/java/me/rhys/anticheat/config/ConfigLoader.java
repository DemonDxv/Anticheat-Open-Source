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

        Anticheat.getInstance().getConfigValues().setAllowOp(Anticheat.getInstance().getConfig()
                .getBoolean("Bypass.Op-Bypass"));

        Anticheat.getInstance().getConfigValues().setLogs(Anticheat.getInstance().getConfig()
                .getBoolean("Logs.Enabled"));

        Anticheat.getInstance().getConfigValues().setLogTime(Anticheat.getInstance().getConfig()
                .getInt("Logs.Time"));

        //       Anticheat.getInstance().getConfigValues().setJudgementDay(Anticheat.getInstance().getConfig()
//                .getBoolean("Banwave.Enabled"));
    }

    String convertColor(String in) {
        return in.replace("&", "§");
    }
}
