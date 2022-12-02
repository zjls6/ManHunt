package cc.zjlsx.manhunt.listener;

import cc.zjlsx.manhunt.Main;
import cc.zjlsx.manhunt.games.GameManager;
import cc.zjlsx.manhunt.games.Team;
import cc.zjlsx.manhunt.player.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.UUID;

public class PlayerMove implements Listener {

    private final GameManager gameManager;
    private final PlayerManager playerManager;

    public PlayerMove(Main plugin) {
        this.gameManager = plugin.getGameManager();
        playerManager = gameManager.getPlayerManager();
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();

        if (playerManager.getTeam(p).equals(Team.Runner)) {
            for (UUID uuid : playerManager.getHunters()) {
                Player hunter = Bukkit.getPlayer(uuid);
                if (hunter == null) {
                    return;
                }
                hunter.setCompassTarget(p.getLocation());
            }
        }
    }
}
