package me.rhys.anticheat.util;

import me.rhys.anticheat.Anticheat;

import java.io.File;

public class FileManager {

    /** util from nemesis **/

    private final Anticheat plugin = Anticheat.getInstance();

    private final File dataFolder = plugin.getDataFolder();

    public FileManager() {
        if (!dataFolder.exists())
            dataFolder.mkdir();

        loadFile("mongo", "https://repo1.maven.org/maven2/org/mongodb/mongo-java-driver/3.12.6/mongo-java-driver-3.12.6.jar");
    }

    /**
     * Loads a file into memory
     * @param fileName The name of the file to load
     * @param fileUrl The url of the file to load
     */
    private void loadFile(String fileName, String fileUrl) {
        try {
            File lib = new File(dataFolder, String.format("lib/%s.jar", fileName));

            if (!lib.exists()) {
                if (!lib.getParentFile().exists())
                    lib.getParentFile().mkdir();

                plugin.getLogger().info(String.format("Downloading %s...", fileName));
                FileUtil.download(lib, fileUrl);
                plugin.getLogger().info(String.format("Finished downloading %s", fileName));
            }

            FileUtil.injectUrl(lib.toURI().toURL());
        } catch (Exception e) {
            plugin.getLogger().warning(String.format("Failed to download or load %s: %s", fileName, e.getMessage()));
        }
    }
}