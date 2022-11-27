package cc.zjlsx.manhunt.listener;

import cc.zjlsx.manhunt.games.GameManager;
import cc.zjlsx.manhunt.games.GameState;
import cc.zjlsx.manhunt.games.Team;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class BlockUpdate implements Listener {

    private final GameManager gameManager;

    public BlockUpdate(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        Player p = e.getPlayer();
        if (p.isOp() && p.getGameMode().equals(GameMode.CREATIVE)) {
            return;
        }
        if (gameManager.getState().equals(GameState.Waiting) || gameManager.getState().equals(GameState.Starting)) {
            e.setCancelled(true);
        }
        if (gameManager.getState().equals(GameState.Active)) {
            if (gameManager.getPlayerManager().getTeam(p).equals(Team.Hunter)) {
                if (gameManager.getGameTickTask().getCurrentSecond() <= gameManager.getPlugin().getConfigManager().getHunterReleaseTime()  ) {
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent e) {
        Player p = e.getPlayer();
        if (p.isOp() && p.getGameMode().equals(GameMode.CREATIVE)) {
            return;
        }
        if (gameManager.getState().equals(GameState.Waiting) || gameManager.getState().equals(GameState.Starting)) {
            e.setCancelled(true);
        }
    }
}