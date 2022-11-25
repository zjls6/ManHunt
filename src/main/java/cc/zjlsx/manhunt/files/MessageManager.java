package cc.zjlsx.manhunt.files;

import cc.zjlsx.manhunt.Main;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MessageManager {
    private final String configName = "messages.yml";
    private Main plugin;
    private File messagesFile = null;
    private FileConfiguration messagesConfig = null;

    public MessageManager(Main plugin) {
        this.plugin = plugin;
        saveDefault();
    }

    public void reload() {
        if (messagesFile == null) {
            messagesFile = new File(plugin.getDataFolder(), configName);
        }
        messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);

        InputStream defaultStream = plugin.getResource(configName);
        if (defaultStream != null) {
            YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defaultStream));
            messagesConfig.setDefaults(defaultConfig);
        }
    }

    public FileConfiguration getMessagesConfig() {
        if (messagesConfig == null) {
            reload();
        }
        return messagesConfig;
    }

    public void saveMessagesConfig() {
        if (messagesFile == null || messagesConfig == null) {
            return;
        }
        try {
            getMessagesConfig().save(messagesFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveDefault() {
        if (messagesFile == null) {
            messagesFile = new File(plugin.getDataFolder(), configName);
        }
        if (!messagesFile.exists()) {
            plugin.saveResource(configName, false);
        }
    }
}
