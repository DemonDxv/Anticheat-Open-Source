package me.rhys.anticheat.config;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ConfigValues {
    private boolean lagBack, punish, announce, debugMessage, judgementDay, allowOp, logs;
    private String punishCommand, prefix, alertsMessage, announceMessage;
    private int logTime;
}
