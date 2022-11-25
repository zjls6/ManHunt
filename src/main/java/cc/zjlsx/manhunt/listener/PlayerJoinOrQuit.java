package cc.zjlsx.manhunt.listener;

import cc.zjlsx.manhunt.Main;
import cc.zjlsx.manhunt.games.GameManager;
import cc.zjlsx.manhunt.games.GameState;
import cc.zjlsx.manhunt.player.PlayerManager;
import cc.zjlsx.manhunt.utils.Color;
import cc.zjlsx.manhunt.utils.ItemBuilder;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerJoinOrQuit implements Listener {

    private final GameManager gameManager;
    private final Main plugin;

    public PlayerJoinOrQuit(Main plugin) {
        this.plugin = plugin;
        gameManager = plugin.getGameManager();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        if (!gameManager.isConfigurationComplete()) {
            player.sendMessage(Color.str("&e游戏似乎未配置好，您可以选择进行以下操作："));
            TextComponent setWaitingLobbyLocation = new TextComponent(Color.str("&a设置等待大厅出生点"));
            setWaitingLobbyLocation.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(Color.str("&7点击将当前位置设置为等待大厅出生点"))));
            setWaitingLobbyLocation.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/hunter setWaitingLobby"));

            player.spigot().sendMessage(setWaitingLobbyLocation);
            return;
        }
        gameManager.getScoreboard().addPlayer(player);
        gameManager.getScoreboard().updateScoreboard();
        if (gameManager.getState().equals(GameState.Waiting) || gameManager.getState().equals(GameState.Starting)) {
            player.teleport(gameManager.getWaitingLobbyLocation());
//        e.setJoinMessage(Color.str("&f&l" + player.getDisplayName() + " &e加入了游戏！ &7(&6"
//                + Bukkit.getOnlinePlayers().size() + "&7/&a2&7)"));
            PlayerManager playerManager = gameManager.getPlayerManager();

            e.setJoinMessage(Color.str(gameManager.getMessages().getString("join.message"))
                    .replace("%playerName%", player.getName())
                    .replace("%onlinePlayers%", String.valueOf(Bukkit.getOnlinePlayers().size()))
                    .replace("%maxPlayers%", String.valueOf(playerManager.getMaxPlayer())));

            player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
            player.setFoodLevel(20);
            player.getInventory().clear();
            player.getEnderChest().clear();
            player.setGameMode(GameMode.ADVENTURE);
            player.setLevel(0);
            player.setExp(0);

            player.getInventory().addItem(new ItemBuilder(Material.COMPASS).setName(Color.str("&r选择队伍 &7(右键点击)")).toItemStack());

            if (gameManager.getState().equals(GameState.Waiting)) {
                if (Bukkit.getOnlinePlayers().size() == playerManager.getMaxPlayer()) {
                    gameManager.setState(GameState.Starting);
                }
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        if (gameManager.getState().equals(GameState.Waiting) || gameManager.getState().equals(GameState.Starting)) {
            gameManager.setState(GameState.Waiting);
            gameManager.getPlayerManager().getRunners().remove(p.getUniqueId());
            gameManager.getPlayerManager().getHunters().remove(p.getUniqueId());
        }
        Bukkit.getScheduler().runTaskLater(plugin, () -> gameManager.getScoreboard().updateScoreboard(), 5);

        gameManager.getScoreboard().removePlayer(p);
    }
}
