package cc.zjlsx.manhunt.games;

import cc.zjlsx.manhunt.Main;
import cc.zjlsx.manhunt.tasks.GameStarting;
import cc.zjlsx.manhunt.tasks.GameTick;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Random;
import java.util.UUID;

public enum GameState {
    Waiting, Starting, Active, End, Reset;

    public void set(GameManager gameManager) {
        gameManager.setState(this);
        Main plugin = gameManager.getPlugin();
        switch (this) {
            case Waiting -> {
                if (gameManager.getGameStartingTask() != null) {
                    gameManager.getGameStartingTask().cancel();
                }
                gameManager.getScoreboard().updateScoreboard();
            }
            case Starting -> {
                gameManager.setGameStartingTask(new GameStarting(gameManager));
                gameManager.getGameStartingTask().runTaskTimer(plugin, 0, 20);
            }
            case Active -> {
                while (gameManager.getPlayerManager().getHunters().size() < gameManager.getPlayerManager().getMaxHunter()) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        if (gameManager.getPlayerManager().isInAnyTeam(player)) {
                            continue;
                        }
                        gameManager.getPlayerManager().getHunters().add(player.getUniqueId());
                    }
                }
                while (gameManager.getPlayerManager().getRunners().size() < gameManager.getPlayerManager().getMaxRunner()) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        if (gameManager.getPlayerManager().isInAnyTeam(player)) {
                            continue;
                        }
                        gameManager.getPlayerManager().getRunners().add(player.getUniqueId());
                    }
                }
                for (int i = 0; i < gameManager.getPlayerManager().getHunters().size(); i++) {
                    gameManager.getPlayerManager().getInGamePlayers().put(gameManager.getPlayerManager().getHunters().get(i), i);
                }
                for (int i = 0; i < gameManager.getPlayerManager().getRunners().size(); i++) {
                    gameManager.getPlayerManager().getInGamePlayers().put(gameManager.getPlayerManager().getRunners().get(i), i + gameManager.getPlayerManager().getHunters().size());
                }
                for (UUID uuid : gameManager.getPlayerManager().getRunners()) {
                    Player runner = Bukkit.getPlayer(uuid);
                    if (runner == null) {
                        return;
                    }
                    gameManager.getPlayerManager().getPlayerKillCount().put(uuid, 0);
                }
                for (UUID uuid : gameManager.getPlayerManager().getHunters()) {
                    Player hunter = Bukkit.getPlayer(uuid);
                    if (hunter == null) {
                        return;
                    }
                    gameManager.getPlayerManager().getPlayerDeathCount().put(uuid, 0);
                }
                if (gameManager.getGameStartingTask() != null) {
                    gameManager.getGameStartingTask().cancel();
                }
                gameManager.setGameStartingTask(null);
                gameManager.setGameTickTask(new GameTick(gameManager.getPlugin()));
                gameManager.getGameTickTask().runTaskTimer(plugin, 0, 20);

                for (Player p : Bukkit.getOnlinePlayers()) {
                    Random random = new Random();
                    List<UUID> hunters = gameManager.getPlayerManager().getHunters();
                    int size = hunters.size();
                    int i;
                    if (size <= 1) {
                        i = 0;
                    } else {
                        i = random.nextInt(size - 1);
                    }
                    gameManager.getPlayerManager().getPlayerTrack().put(p.getUniqueId(), hunters.get(i));
                    gameManager.getPlayerManager().setPlaying(p);
                }
                gameManager.getScoreboard().updateScoreboard();
            }
            case End -> {
                if (gameManager.getGameTickTask() != null) {
//                    Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> {
                    gameManager.getGameTickTask().cancel();
                    gameManager.setGameTickTask(null);
//                    }, 20);
                }
                Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> gameManager.setState(GameState.Reset), 20 * 15);
                Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> gameManager.getScoreboard().updateScoreboard(), 10);
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
}
