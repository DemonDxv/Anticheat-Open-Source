package me.rhys.anticheat.util.file;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;

import java.io.File;
import java.io.IOException;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class ChecksFile {

    private ChecksFile() {
    }

    static ChecksFile instance = new ChecksFile();

    public static ChecksFile getInstance() {
        return instance;
    }

    private FileConfiguration data;
    private File dataFile;

    public void setup(Plugin p) {

        if (!p.getDataFolder().exists()) {
            p.getDataFolder().mkdir();
        }

        dataFile = new File("plugins/Anticheat/checks.yml");

        if (!dataFile.exists()) {
            try {
                dataFile.createNewFile();
            } catch (IOException ignored) {
            }
        }

        data = YamlConfiguration.loadConfiguration(dataFile);
    }


    public FileConfiguration getData() {
        return data;
    }


    public void saveData() {
        try {
            data.save(dataFile);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void reloadData() {
        data = YamlConfiguration.loadConfiguration(dataFile);
    }
}