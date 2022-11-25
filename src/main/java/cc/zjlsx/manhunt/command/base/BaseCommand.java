package cc.zjlsx.manhunt.command.base;

import cc.zjlsx.manhunt.enums.Messages;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public abstract class BaseCommand implements CommandExecutor {
    public final JavaPlugin plugin;
    private final CommandInfo commandInfo;

    public BaseCommand(JavaPlugin plugin) {
        this.plugin = plugin;
        this.commandInfo = getClass().getDeclaredAnnotation(CommandInfo.class);
        Objects.requireNonNull(commandInfo, "未找到该命令的信息");
    }

    public CommandInfo getCommandInfo() {
        return commandInfo;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!commandInfo.permission().getPermission().isEmpty()) {
            if (!sender.hasPermission(commandInfo.permission().getPermission())) {
                sender.sendMessage(Messages.No_Permission.getMessage());
                return true;
            }
        }
        if (commandInfo.requiresPlayer()) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(Messages.Run_In_Console.getMessage());
                return true;
            }
            execute((Player) sender, args);
            return true;
        }
        execute(sender, args);
        return true;
    }

    public void execute(CommandSender sender, String[] args) {
    }

    public void execute(Player player, String[] args) {
    }

}
