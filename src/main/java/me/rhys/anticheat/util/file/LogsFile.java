package me.rhys.anticheat.util.file;


import java.io.*;
import org.bukkit.configuration.file.*;
import org.bukkit.plugin.*;

public class LogsFile
{
    static LogsFile instance;
    Plugin p;
    FileConfiguration config;
    File cfile;
    FileConfiguration data;
    File dfile;

    private LogsFile() {
    }

    public static LogsFile getInstance() {
        return LogsFile.instance;
    }

    public void setup(final Plugin p, final String player) {
        this.config = p.getConfig();
        if (!p.getDataFolder().exists()) {
            p.getDataFolder().mkdir();
        }
        this.dfile = new File(p.getDataFolder(), "Logs/LOG-" + player + ".yml");
        if (!this.dfile.exists()) {
            try {
                this.dfile.createNewFile();
            }
            catch (IOException ex) {}
        }
        this.data = (FileConfiguration)YamlConfiguration.loadConfiguration(this.dfile);
    }

    public FileConfiguration getData() {
        return this.data;
    }

    public void saveData() {
        try {
            this.data.save(this.dfile);
        }
        catch (IOException ex) {}
    }

    public void reloadData() {
        this.data = (FileConfiguration)YamlConfiguration.loadConfiguration(this.dfile);
    }

    public FileConfiguration getConfig() {
        return this.config;
    }

    public void saveConfig() {
        try {
            this.config.save(this.cfile);
        }
        catch (IOException ex) {}
    }

    public void reloadConfig() {
        this.config = (FileConfiguration)YamlConfiguration.loadConfiguration(this.cfile);
    }

    public PluginDescriptionFile getDesc() {
        return this.p.getDescription();
    }

    static {
        LogsFile.instance = new LogsFile();
    }
}
