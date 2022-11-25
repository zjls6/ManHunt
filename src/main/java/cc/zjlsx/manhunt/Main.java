package cc.zjlsx.manhunt;

import cc.zjlsx.manhunt.GUI.TeamPickerGUI;
import cc.zjlsx.manhunt.command.MainCommand;
import cc.zjlsx.manhunt.command.base.BaseCommand;
import cc.zjlsx.manhunt.data.ConfigManager;
import cc.zjlsx.manhunt.enums.Messages;
import cc.zjlsx.manhunt.games.GameManager;
import cc.zjlsx.manhunt.listener.*;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;

public final class Main extends JavaPlugin {
    public final String packageName = getClass().getPackage().getName();
    private final ConsoleCommandSender logger = Bukkit.getConsoleSender();
    private GameManager gameManager;
    private ConfigManager configManager;

    @Override
    public void onEnable() {
        //加载配置文件
        (configManager = new ConfigManager()).init(this, "config");

        this.gameManager = new GameManager(this);

        getServer().getPluginManager().registerEvents(new PlayerMove(gameManager), this);
        getServer().getPluginManager().registerEvents(new PlayerJoinOrQuit(this), this);
        getServer().getPluginManager().registerEvents(new PlayerInteract(gameManager), this);
        getServer().getPluginManager().registerEvents(new EntityDamage(gameManager), this);
        getServer().getPluginManager().registerEvents(new InventoryClick(gameManager), this);
        getServer().getPluginManager().registerEvents(new TeamPickerGUI(gameManager), this);
        getServer().getPluginManager().registerEvents(new WorldChange(gameManager), this);
        getServer().getPluginManager().registerEvents(new BlockUpdate(gameManager), this);
        getServer().getPluginManager().registerEvents(new ItemPickup(gameManager), this);

        getCommand("manhunt").setExecutor(new MainCommand(this));
        //注册监听器
        registerListeners();
        //注册命令
        registerCommands();


        for (World world : Bukkit.getWorlds()) {
            WorldBorder worldBorder = world.getWorldBorder();
            worldBorder.setCenter(0, 0);
            worldBorder.setSize(2000);
        }

        logger.sendMessage(Messages.Enable.get());
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
        logger.sendMessage(Messages.Disable.get());
    }

    public GameManager getGameManager() {
        return gameManager;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }
}
