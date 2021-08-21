package me.rhys.anticheat.database.api;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public class InputData {
    private final String UUID, playerName, checkName, checkType;
    private final int violation;
    private final boolean experimental;
}
