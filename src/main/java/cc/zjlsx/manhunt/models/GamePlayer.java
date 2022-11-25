package cc.zjlsx.manhunt.models;

import cc.zjlsx.manhunt.Main;
import cc.zjlsx.manhunt.data.ConfigManager;
import cc.zjlsx.manhunt.enums.Messages;
import cc.zjlsx.manhunt.games.GameManager;
import cc.zjlsx.manhunt.player.PlayerManager;
import cc.zjlsx.manhunt.utils.Color;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GamePlayer implements IGamePlayer {
    private final Main plugin;
    private final GameManager gameManager;
    private final PlayerManager playerManager;
    private final ConfigManager configManager;
    private final UUID uuid;
    private final List<String> scoreboardLines = new ArrayList<>();
    private int deathCount;

    public GamePlayer(Main plugin, UUID uuid) {
        this.plugin = plugin;
        this.uuid = uuid;
        gameManager = plugin.getGameManager();
        playerManager = gameManager.getPlayerManager();
        configManager = plugin.getConfigManager();
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

    }

    @Override
    public void updateScoreboardLines() {
        scoreboardLines.clear();
    }


    public List<String> getScoreboardLines() {
        return scoreboardLines;
    }

    public void addPerPlayerLine(UUID uuid) {
        if (getPlayer().getUniqueId().equals(uuid)) {
            return;
        }
        OfflinePlayer offlineRunner = Bukkit.getOfflinePlayer(uuid);
        Player runner = offlineRunner.getPlayer();
        if (!offlineRunner.isOnline() || runner == null) {
            scoreboardLines.add("&6" + offlineRunner.getName() + " &c玩家不在线");
            return;
        }
        if (runner.isDead()) {
            scoreboardLines.add("&6" + runner.getName() + " &c等待复活");
            return;
        }
        if (runner.getWorld().getName().equals(getPlayer().getWorld().getName())) {
            scoreboardLines.add("&6" + runner.getName() + " &b" + (int) Math.round(getPlayer().getLocation().distance(runner.getLocation())) + "m");
        } else {
            scoreboardLines.add("&6" + runner.getName() + " &c不在同一世界");
        }
    }

    private class Hunter extends GamePlayer {
        public Hunter(Main plugin, UUID uuid) {
            super(plugin, uuid);
        }

        @Override
        public void setPlaying() {
            getPlayer().sendMessage(Messages.Game_Start_Hunter.get(configManager.getHunterReleaseTime()));
            getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20 * configManager.getHunterReleaseTime(), 1, false, false));
            super.setPlaying();
        }

        @Override
        public void gameTick(int currentSecond) {
            if (currentSecond == configManager.getHunterReleaseTime()) {
                getPlayer().teleport(Bukkit.getWorld("world").getSpawnLocation());
                getPlayer().setGameMode(GameMode.SURVIVAL);
                gameManager.getPlayerManager().giveTeamTacker(uuid);

                Bukkit.broadcastMessage(Color.str("&c猎人已释放！"));
            }
        }

        @Override
        public void updateScoreboardLines() {
            super.updateScoreboardLines();
            playerManager.getRunners().forEach(this::addPerPlayerLine);

            List<UUID> hunters = playerManager.getHunters();
            if (hunters.size() > 1) {
                scoreboardLines.add("&a追踪&7： &2队友");
                hunters.forEach(this::addPerPlayerLine);
            }
            scoreboardLines.add("");
            scoreboardLines.add("&a死亡： &c" + playerManager.getPlayerDeathCount().getOrDefault(getPlayer().getUniqueId(), 0));
        }
    }

    private class Runner extends GamePlayer {
        public Runner(Main plugin, UUID uuid) {
            super(plugin, uuid);
        }

        @Override
        public void setPlaying() {
            super.setPlaying();
        }

        @Override
        public void gameTick(int currentSecond) {
            super.gameTick(currentSecond);
        }

        @Override
        public void updateScoreboardLines() {
            super.updateScoreboardLines();
            playerManager.getHunters().forEach(this::addPerPlayerLine);
            scoreboardLines.add("");
            scoreboardLines.add("&a击杀： &c" + playerManager.getPlayerKillCount().getOrDefault(getPlayer().getUniqueId(), 0));

            List<UUID> runners = playerManager.getRunners();
            if (runners.size() > 1) {
                scoreboardLines.add("&a追踪&7： &2队友");
                runners.forEach(this::addPerPlayerLine);
            }
            scoreboardLines.add("");
            scoreboardLines.add("&a击杀： &c" + playerManager.getPlayerKillCount().getOrDefault(getPlayer().getUniqueId(), 0));
        }
    }

    private class Spectator extends GamePlayer {
        public Spectator(Main plugin, UUID uuid) {
            super(plugin, uuid);
        }

        @Override
        public void setPlaying() {
            super.setPlaying();
        }
    }


}
