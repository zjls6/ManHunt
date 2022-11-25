package cc.zjlsx.manhunt.listener;

import cc.zjlsx.manhunt.games.GameManager;
import cc.zjlsx.manhunt.games.Team;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.UUID;

public class PlayerMove implements Listener {

    private GameManager gameManager;

    public PlayerMove(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e){
        Player p = e.getPlayer();

        if (gameManager.getPlayerManager().getTeam(p).equals(Team.Runner)) {
            for (UUID uuid : gameManager.getPlayerManager().getHunters()) {
                Player hunter = Bukkit.getPlayer(uuid);
                if (hunter == null) {
                    return;
                }
                hunter.setCompassTarget(p.getLocation());
            }
        }
    }
}
