package me.rhys.anticheat.config;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ConfigValues {
    private boolean lagBack, punish, announce, bungeeCord;
    private String punishCommand, prefix, announceMessage, hostName, database, logs;
}
