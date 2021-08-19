package me.rhys.anticheat.util;

import me.rhys.anticheat.Anticheat;

import java.util.LinkedList;
import java.util.List;

public class LogUtil
{
    private LinkedList<String> logs;
    
    public LogUtil() {
        this.logs = new LinkedList<String>();
    }
    
    public List<String> getLogs() {
        return this.logs;
    }
    
    public void clear() {
        this.logs.clear();
    }
    
    public void setLogs(final LinkedList<String> logs) {
        this.logs = logs;
    }
    
    public int size() {
        return this.logs.size();
    }
    
    public void addLog(String playerName, String check, String type, int violation, int maxVL, boolean exp, int ping,
                       boolean banned) {
        this.logs.add(Anticheat.getInstance().currentDate + " " + playerName
                + " flagged: " + check + " " + type + " (x" + violation + "/" + maxVL + ")"
                + (exp ? " (experimental)" : "") + " Ping: "+ping + " Banned: "+banned);
    }
    
    public void addLogString(String info) {
        this.logs.add(info);
    }
}
