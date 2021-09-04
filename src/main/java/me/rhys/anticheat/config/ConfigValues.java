package me.rhys.anticheat.config;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ConfigValues {
    private boolean discord, consoleAlerts, lagBack, punish, announce, debugMessage, judgementDay, allowOp, logs;
    private String discordWebURL, discordAlerts, punishCommand, prefix, alertsMessage, announceMessage, mongoDBURI;
}
