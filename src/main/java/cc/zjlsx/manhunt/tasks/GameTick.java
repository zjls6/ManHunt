package cc.zjlsx.manhunt.tasks;

import cc.zjlsx.manhunt.Main;
import cc.zjlsx.manhunt.data.ConfigManager;
import cc.zjlsx.manhunt.games.GameManager;
import cc.zjlsx.manhunt.games.Team;
import cc.zjlsx.manhunt.utils.Color;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

@Getter
public class GameTick extends BukkitRunnable {
    private final Main plugin;
    private final GameManager gameManager;
    private final ConfigManager configManager;
    private int currentSecond = 0;

    public GameTick(Main plugin) {
        this.plugin = plugin;
        gameManager = plugin.getGameManager();
        configManager = plugin.getConfigManager();
    }

    @Override
    public void run() {
        gameManager.getScoreboard().updateScoreboard();

        currentSecond += 1;

        gameManager.getPlayerManager().getInGamePlayers().forEach(player -> player.gameTick(currentSecond));


        if (currentSecond == configManager.getPvpOnTime()) {
            Bukkit.broadcastMessage(Color.str("&cPVP已开启！"));
        }
        if (currentSecond == configManager.getGameEndTime()) {
            gameManager.endGame(Team.Spectator);
        }

    }
}
