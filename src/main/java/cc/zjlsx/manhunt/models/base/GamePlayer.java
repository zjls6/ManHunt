package cc.zjlsx.manhunt.models.base;

import cc.zjlsx.manhunt.Main;
import cc.zjlsx.manhunt.data.ConfigManager;
import cc.zjlsx.manhunt.games.GameManager;
import cc.zjlsx.manhunt.player.PlayerManager;
import cc.zjlsx.manhunt.utils.Color;
import cc.zjlsx.manhunt.utils.ItemBuilder;
import net.kyori.adventure.audience.Audience;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GamePlayer implements IGamePlayer {
    public final Main plugin;
    public final Audience adventurePlayer;
    public final GameManager gameManager;
    public final PlayerManager playerManager;
    public final ConfigManager configManager;
    public final List<String> scoreboardLines = new ArrayList<>();
    public final UUID uuid;
    private int killCount = 0;
    private int deathCount = 0;

    public GamePlayer(Main plugin, UUID uuid) {
        this.plugin = plugin;
        adventurePlayer = plugin.audience(uuid);
        this.uuid = uuid;
        gameManager = plugin.getGameManager();
        playerManager = gameManager.getPlayerManager();
        configManager = plugin.getConfigManager();
    }

    public GamePlayer(Main plugin, Player player) {
        this(plugin, player.getUniqueId());
        player.getInventory().clear();
        player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
        player.setFoodLevel(20);
        player.getInventory().clear();
        player.getEnderChest().clear();
        player.setGameMode(GameMode.ADVENTURE);
        player.setLevel(0);
        player.setExp(0);
        player.getInventory().addItem(new ItemBuilder(Material.COMPASS).setName(Color.s("&r选择队伍 &7(右键点击)")).toItemStack());
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    @Override
    public void setPlaying() {
        if (getPlayer() == null) {
            return;
        }
        World world = Bukkit.getWorld("world");
        if (world != null) {
            getPlayer().teleport(world.getSpawnLocation());
        }
    }

    @Override
    public void gameTick(int currentSecond) {
        updateScoreboardLines();
    }

    @Override
    public void updateScoreboardLines() {
        scoreboardLines.clear();
        scoreboardLines.add("&a追踪&7： " + playerManager.getOppositeTeam(getPlayer()).getName());
    }


    public List<String> getScoreboardLines() {
        return scoreboardLines;
    }

    public void addPerPlayerLine(GamePlayer gamePlayer) {
        UUID uuid = gamePlayer.getUniqueId();
        if (getPlayer().getUniqueId().equals(uuid)) {
            return;
        }
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
        Player player = offlinePlayer.getPlayer();
        if (!offlinePlayer.isOnline() || player == null) {
            scoreboardLines.add("&6" + offlinePlayer.getName() + " &c玩家不在线");
            return;
        }
        if (player.isDead()) {
            scoreboardLines.add("&6" + player.getName() + " &c等待复活");
            return;
        }
        if (player.getWorld().getName().equals(getPlayer().getWorld().getName())) {
            scoreboardLines.add("&6" + player.getName() + " &b" + (int) Math.round(getPlayer().getLocation().distance(player.getLocation())) + "m");
        } else {
            scoreboardLines.add("&6" + player.getName() + " &c不在同一世界");
        }
    }

    public void addKillDeathLines() {
        scoreboardLines.add("&a击杀： &c" + getKillCount());
        scoreboardLines.add("&a死亡： &c" + getDeathCount());
    }

    public void addKill() {
        killCount++;
    }

    public void addDeath() {
        deathCount++;
    }

    public UUID getUniqueId() {
        return uuid;
    }

    public int getKillCount() {
        return killCount;
    }

    public int getDeathCount() {
        return deathCount;
    }

}
