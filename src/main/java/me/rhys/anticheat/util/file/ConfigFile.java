package me.rhys.anticheat.util.file;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;

public class ConfigFile {

    private ConfigFile() {}

    static ConfigFile instance = new ConfigFile();

    public static ConfigFile getInstance() {
        return instance;
    }

    private FileConfiguration config;
    private FileConfiguration data;
    private File dfile;

    public void setup(Plugin p) {
        config = p.getConfig();
        if (!p.getDataFolder().exists()) {
            p.getDataFolder().mkdir();
        }
        dfile = new File("plugins/Anticheat/config.yml");

        if (!dfile.exists()) {
            try {
                dfile.createNewFile();
            } catch (IOException ignored) {
            }
        }

        data = YamlConfiguration.loadConfiguration(dfile);

    }

    public FileConfiguration getData() {
        return data;
    }


    public void writeDefaults() {
        data.options().header("%PLAYER% = the player cheating. %PREFIX% = the prefix of the anticheat you set below. %DEBUG% - dev/check info " +
                "\nOp-Bypass: if set to true, a player that is op will automatically bypass else if false they will need the permission \"anticheat.bypass\" to bypass");

        if (!data.contains("Alert.Console-Alerts")) data.set("Alert.Console-Alerts", true);
        if (!data.contains("Alert.Prefix")) data.set("Alert.Prefix", "&8[&cAnticheat&8]&r");
        if (!data.contains("Alert.Alert-Message")) data.set("Alert.Alert-Message",
                "%PREFIX% &f%PLAYER% &7flagged &c%CHECK% &8(&c%CHECKTYPE%&8) &8[&c%VL%/%MAX-VL%&8]");
        if (!data.contains("Alert.Discord")) data.set("Alert.Discord", false);
        if (!data.contains("Alert.Discord-WebhookURL")) data.set("Alert.Discord-WebhookURL",
                "https://discord.com/api/webhooks/");
        if (!data.contains("Alert.Discord-Alert-Message")) data.set("Alert.Discord-Alert-Message",
                "[Anticheat] %PLAYER% flagged %CHECK% (%CHECKTYPE%) [%VL%/%MAX-VL%]");
        if (!data.contains("Punishment.LagBack")) data.set("Punishment.LagBack", false);
        if (!data.contains("Punishment.Command.Enabled")) data.set("Punishment.Command.Enabled", true);
        if (!data.contains("Punishment.Command.Execute")) data.set("Punishment.Command.Execute",
                "/ban %PLAYER% %PREFIX% &cUnfair Advantage.");
        if (!data.contains("Punishment.Announce.Enabled")) data.set("Punishment.Announce.Enabled", true);
        if (!data.contains("Punishment.Announce.Message")) data.set("Punishment.Announce.Message",
                "%PREFIX% &7has removed &f%PLAYER% &7for using &cUnfair Advantages.");

        if (!data.contains("Bypass.Op-Bypass")) data.set("Bypass.Op-Bypass", false);
        if (!data.contains("Logs.Enabled")) data.set("Logs.Enabled", false);
        if (!data.contains("Logs.MongoDBURI")) data.set("Logs.MongoDBURI",
                "mongodb+srv://user:password@node.mongodb.net/myFirstDatabase?retryWrites=true&w=majority");

        saveData();
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
        this.data = YamlConfiguration.loadConfiguration(dfile);
    }

    public FileConfiguration getConfig() {
        return config;
    }
}
