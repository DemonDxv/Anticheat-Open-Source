package me.rhys.anticheat.config;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ConfigValues {
    private boolean lagBack, punish, announce, debugMessage, mongoEnabled;
    private String punishCommand, prefix, alertsMessage, announceMessage, hostName, database, logs = "logs", mUsername, mPassword;
    private int mongoPort;
}
