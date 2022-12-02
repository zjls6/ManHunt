package cc.zjlsx.manhunt.games;

import cc.zjlsx.manhunt.Main;
import cc.zjlsx.manhunt.data.ConfigManager;
import cc.zjlsx.manhunt.files.MessageManager;
import cc.zjlsx.manhunt.models.base.GamePlayer;
import cc.zjlsx.manhunt.player.PlayerManager;
import cc.zjlsx.manhunt.tasks.GameStarting;
import cc.zjlsx.manhunt.tasks.GameTick;
import cc.zjlsx.manhunt.utils.Color;
import dev.jcsoftware.jscoreboards.JPerPlayerScoreboard;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.*;

@Getter
@Setter
public class GameManager {

    private Main plugin;

    private GameState state;

    private boolean configurationComplete = true;

    private PlayerManager playerManager;

    private Location waitingLobbyLocation;

    private JPerPlayerScoreboard scoreboard;

    private GameStarting gameStartingTask;
    private GameTick gameTickTask;
    private Team winnerTeam;
    private ConfigManager configManager;

    public GameManager(Main plugin) {
        this.plugin = plugin;
        configManager = plugin.getConfigManager();

        if (getConfig().get("waitingLobby") == null) {
            configurationComplete = false;
        }
        Location waitingLobbyLocation = (Location) getConfig().get("waitingLobby");
        if (waitingLobbyLocation == null) {
            configurationComplete = false;
        } else {
            if (waitingLobbyLocation.getWorld() == null) {
                configurationComplete = false;
            }
        }
        this.waitingLobbyLocation = waitingLobbyLocation;
        this.playerManager = new PlayerManager(this);

        this.scoreboard = new JPerPlayerScoreboard(
                (player) -> "&e&l猎人游戏",
                (player) -> {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    String sd = sdf.format(new Date());

                    List<String> lines = new ArrayList<>();
                    switch (state) {
                        case Waiting -> {
                            lines.add("&7" + sd);
                            lines.add("");
                            lines.add("&f玩家： &6" + Bukkit.getOnlinePlayers().size() + "&7/&a" + playerManager.getMaxPlayer());
                            lines.add("");
                            lines.add("&f等待中...");
                            lines.add("");
                            lines.add("&f模式： &a" + playerManager.getMaxRunner() + "v" + playerManager.getMaxHunter());
                        }
                        case Starting -> {
                            lines.add("&7" + sd);
                            lines.add("");
                            lines.add("&f玩家： &6" + Bukkit.getOnlinePlayers().size() + "&7/&a" + playerManager.getMaxPlayer());
                            lines.add("");
                            lines.add("&f即将开始：&a" + gameStartingTask.getTime() + " 秒");
                            lines.add("");
                            lines.add("&f模式： &a" + playerManager.getMaxRunner() + "v" + playerManager.getMaxHunter());
                        }
                        case Active -> {
                            int currentSecond = gameTickTask.getCurrentSecond();
                            lines.add("&7" + sd);
                            lines.add("");
                            lines.add("&a下个事件：");
                            if (currentSecond < configManager.getHunterReleaseTime()) {
                                lines.add("&c猎人释放 &7- &a" + getDate(configManager.getHunterReleaseTime() - currentSecond));
                            } else if (currentSecond < configManager.getPvpOnTime()) {
                                lines.add("&cPVP开启 &7- &a" + getDate(configManager.getPvpOnTime() - currentSecond));
                            } else {
                                lines.add("&e游戏结束 &7- &a" + getDate(configManager.getGameEndTime() - currentSecond));
                            }
                            lines.add("");
                            if (currentSecond > configManager.getHunterReleaseTime()) {
                                playerManager.getGamePlayer(player).ifPresent(gamePlayer -> lines.addAll(gamePlayer.getScoreboardLines()));
                            }
                            lines.add("");
                            lines.add("&a时间： &f" + getDate(currentSecond));
                        }
                        default -> {
                            lines.add("&7" + sd);
                            lines.add("");
                            lines.add("&6游戏结束！");
                            lines.add("");
                            lines.add(winnerTeam.getName() + " &a胜利！");
                        }
                    }
                    lines.add("");
                    lines.add("&ezjlsx.cc");
                    return lines;
                }
        );
        setState(GameState.Waiting);
    }

    public void setState(GameState state) {
        this.state = state;
        switch (state) {
            case Waiting -> {
                if (this.gameStartingTask != null) {
                    this.gameStartingTask.cancel();
                }
                this.scoreboard.updateScoreboard();
            }
            case Starting -> {
                this.gameStartingTask = new GameStarting(this);
                this.gameStartingTask.runTaskTimer(plugin, 0, 20);
            }
            case Active -> {
                while (playerManager.getHunters().size() < playerManager.getMaxHunter()) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        if (playerManager.isInAnyTeam(player)) {
                            continue;
                        }
                        playerManager.getHunters().add(player.getUniqueId());
                    }
                }
                while (playerManager.getRunners().size() < playerManager.getMaxRunner()) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        if (playerManager.isInAnyTeam(player)) {
                            continue;
                        }
                        playerManager.getRunners().add(player.getUniqueId());
                    }
                }
//                for (int i = 0; i < playerManager.getHunters().size(); i++) {
//                    playerManager.getInGamePlayers().put(playerManager.getHunters().getMessage(i), i);
//                }
//                for (int i = 0; i < playerManager.getRunners().size(); i++) {
//                    playerManager.getInGamePlayers().put(playerManager.getRunners().getMessage(i), i + playerManager.getHunters().size());
//                }
                for (UUID uuid : playerManager.getRunners()) {
                    Player runner = Bukkit.getPlayer(uuid);
                    if (runner == null) {
                        return;
                    }
                    playerManager.getPlayerKillCount().put(uuid, 0);
                }
                for (UUID uuid : playerManager.getHunters()) {
                    Player hunter = Bukkit.getPlayer(uuid);
                    if (hunter == null) {
                        return;
                    }
                    playerManager.getPlayerDeathCount().put(uuid, 0);
                }
                if (this.gameStartingTask != null) {
                    this.gameStartingTask.cancel();
                }
                this.gameStartingTask = null;
                this.gameTickTask = new GameTick(plugin);
                gameTickTask.runTaskTimer(plugin, 0, 20);
                for (Player p : Bukkit.getOnlinePlayers()) {
                    Random random = new Random();
                    List<UUID> hunters = playerManager.getHunters();
                    int size = hunters.size();
                    int i;
                    if (size <= 1) {
                        i = 0;
                    } else {
                        i = random.nextInt(size - 1);
                    }
                    playerManager.getPlayerTrack().put(p.getUniqueId(), hunters.get(i));
                    playerManager.setPlaying(p);
                }
                this.scoreboard.updateScoreboard();
            }
            case End -> {
                if (this.gameTickTask != null) {
//                    Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> {
                    this.gameTickTask.cancel();
                    this.gameTickTask = null;
//                    }, 20);
                }
                Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> setState(GameState.Reset), 20 * 15);
                Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> this.scoreboard.updateScoreboard(), 10);
            }
            case Reset -> {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    p.kickPlayer("游戏正在重置");
                }
                //回档
                Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> Bukkit.getServer().shutdown(), 20);
            }
        }
    }
    public boolean isAtAnyState(GameState... gameStates) {
        return Arrays.stream(gameStates).anyMatch(gameState -> state.equals(gameState));
    }

    public void endGame(Player winner) {
        this.state = GameState.End;
        setState(GameState.End);

        Team team = playerManager.getTeam(winner);
        if (team == null) {
            return;
        }
        this.winnerTeam = team;
        Bukkit.broadcastMessage(Color.s("&6游戏结束，" + team.getName() + " &a获得胜利！"));
    }

    public void endGame(Team team) {
        this.state = GameState.End;
        setState(GameState.End);
        this.winnerTeam = team;
        Bukkit.broadcastMessage(Color.s("&6游戏结束，" + team.getName() + " &a获得胜利！"));
    }

    public FileConfiguration getMessages() {
        return messageManager.getMessagesConfig();
    }

    public FileConfiguration getConfig() {
        return plugin.getConfig();
    }

    public String getDate(int time) {
        if (time < 60) {
            return "0:" + time;
        } else if (time < 3600) {
            int m = time / 60;
            int s = time % 60;
            return m + ":" + s;
        } else {
            int h = time / 3600;
            int m = (time % 3600) / 60;
            int s = (time % 3600) % 60;
            return h + ":" + m + ":" + s;
        }
    }


}
