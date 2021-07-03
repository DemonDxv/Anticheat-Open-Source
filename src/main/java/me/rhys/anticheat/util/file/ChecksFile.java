package me.rhys.anticheat.util.file;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;

import java.io.File;
import java.io.IOException;

public class ChecksFile {

    private ChecksFile() { }

    static ChecksFile instance = new ChecksFile();

    public static ChecksFile getInstance() {
        return instance;
    }

    Plugin p;

    FileConfiguration config;
    File cfile;

    FileConfiguration data;
    File dfile;

    public void setup(Plugin p) {
        config = p.getConfig();
        if (!p.getDataFolder().exists()) {
            p.getDataFolder().mkdir();
        }
        dfile = new File("plugins/Anticheat/checks.yml");

        if (!dfile.exists()) {
            try {
                dfile.createNewFile();
            }
            catch (IOException ignored) {
            }
        }

        data = YamlConfiguration.loadConfiguration(dfile);
    }

    public FileConfiguration getData() {
        return data;
    }


    public void saveData() {
        try {
            data.save(dfile);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void reloadData() {
        data = YamlConfiguration.loadConfiguration(dfile);
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public void saveConfig() {
        try {
            config.save(cfile);
        }
        catch (IOException e) {
        }
    }

    public void reloadConfig() {
        config = YamlConfiguration.loadConfiguration(cfile);
    }

    public PluginDescriptionFile getDesc() {
        return p.getDescription();
    }
}