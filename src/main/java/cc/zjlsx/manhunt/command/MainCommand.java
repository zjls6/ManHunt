package cc.zjlsx.manhunt.command;

import cc.zjlsx.manhunt.Main;
import cc.zjlsx.manhunt.command.base.BaseCommand;
import cc.zjlsx.manhunt.command.base.CommandInfo;
import cc.zjlsx.manhunt.enums.Permissions;
import cc.zjlsx.manhunt.games.GameManager;
import cc.zjlsx.manhunt.utils.Color;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandInfo (name = "manhunt", permission = Permissions.Admin)
public class MainCommand extends BaseCommand {
    private final Main plugin;
    private final GameManager gameManager;

    public MainCommand(Main plugin) {
        super(plugin);
        this.plugin = plugin;
        gameManager = plugin.getGameManager();
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("/mh reload");
            return;
        }
        if (args[0].equalsIgnoreCase("reload")) {
            gameManager.getPlugin().reloadConfig();
            plugin.getConfigManager().load();
            sender.sendMessage(Color.s("&a重载成功！"));
            return;
        }
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Color.s("&c你不能在后台执行此命令！"));
            return;
        }
        if (args[0].equalsIgnoreCase("setWaitingLobby")) {
            gameManager.setWaitingLobbyLocation(player.getLocation());
            gameManager.getConfig().set("waitingLobby", player.getLocation());
            gameManager.getPlugin().saveConfig();
            gameManager.setConfigurationComplete(true);
            player.sendMessage(Color.s("&a设置等待大厅出生点成功！"));
        }
    }
}
