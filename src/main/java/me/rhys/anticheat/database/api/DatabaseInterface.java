package me.rhys.anticheat.database.api;

import java.util.List;

public interface DatabaseInterface {
    void addViolation(InputData inputData);
    void initManager();
    void shutdown();
    List<InputData> getLogs(String playerName);
    boolean isSetup();
}
