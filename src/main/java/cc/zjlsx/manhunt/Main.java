package cc.zjlsx.manhunt;

import cc.zjlsx.manhunt.GUI.TeamPickerGUI;
import cc.zjlsx.manhunt.command.base.BaseCommand;
import cc.zjlsx.manhunt.data.ConfigManager;
import cc.zjlsx.manhunt.data.MessagesManager;
import cc.zjlsx.manhunt.enums.Messages;
import cc.zjlsx.manhunt.games.GameManager;
import cc.zjlsx.manhunt.listener.*;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.util.UUID;

public final class Main extends JavaPlugin {
    public final String packageName = getClass().getPackage().getName();
    private final ConsoleCommandSender logger = Bukkit.getConsoleSender();
    private GameManager gameManager;
    private ConfigManager configManager;
    private BukkitAudiences adventure;

    public @NonNull BukkitAudiences adventure() {
        if (this.adventure == null) {
            throw new IllegalStateException("Tried to access Adventure when the plugin was disabled!");
        }
        return this.adventure;
    }

    public @NonNull Audience audience(CommandSender sender) {
        return adventure().sender(sender);
    }

    public @NonNull Audience audience(UUID playerId) {
        return adventure().player(playerId);
    }

    public @NonNull Audience onlinePlayers() {
        return adventure().players();
    }


    @Override
    public void onEnable() {
        // Initialize an audiences instance for the plugin
        this.adventure = BukkitAudiences.create(this);
        //加载配置文件
        (configManager = new ConfigManager()).init(this, "config");
        new MessagesManager().init(this, "messages");

        this.gameManager = new GameManager(this);

        getServer().getPluginManager().registerEvents(new PlayerMove(this), this);
        getServer().getPluginManager().registerEvents(new PlayerJoinOrQuit(this), this);
        getServer().getPluginManager().registerEvents(new PlayerInteract(this), this);
        getServer().getPluginManager().registerEvents(new EntityDamage(this), this);
        getServer().getPluginManager().registerEvents(new InventoryClick(this), this);
        getServer().getPluginManager().registerEvents(new TeamPickerGUI(this), this);
        getServer().getPluginManager().registerEvents(new WorldChange(this), this);
        getServer().getPluginManager().registerEvents(new BlockUpdate(this), this);
        getServer().getPluginManager().registerEvents(new ItemPickup(this), this);

        //注册监听器
        registerListeners();
        //注册命令
        registerCommands();

        //每个世界的设置边界
        for (World world : Bukkit.getWorlds()) {
            WorldBorder worldBorder = world.getWorldBorder();
            worldBorder.setCenter(0, 0);
            worldBorder.setSize(configManager.getWorldBorderSize());
        }
        audience(logger).sendMessage(Messages.Enable.get());
    }

    private void registerCommands() {
        for (Class<? extends BaseCommand> clazz : new Reflections(packageName + ".command").getSubTypesOf(BaseCommand.class)) {
            try {
                BaseCommand bukkitCommand = clazz.getDeclaredConstructor(Main.class).newInstance(this);
                getCommand(bukkitCommand.getCommandInfo().name()).setExecutor(bukkitCommand);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                     NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void registerListeners() {
        for (Class<? extends Listener> clazz : new Reflections(packageName + ".listener").getSubTypesOf(Listener.class)) {
            try {
                Listener listener = clazz.getDeclaredConstructor(Main.class).newInstance(this);
                getServer().getPluginManager().registerEvents(listener, this);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                     NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDisable() {
        if (this.adventure != null) {
            this.adventure.close();
            this.adventure = null;
        }
        audience(logger).sendMessage(Messages.Disable.get());
    }

    public GameManager getGameManager() {
        return gameManager;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }
}
