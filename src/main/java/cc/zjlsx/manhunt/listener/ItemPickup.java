package cc.zjlsx.manhunt.listener;

import cc.zjlsx.manhunt.games.GameManager;
import cc.zjlsx.manhunt.games.Team;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;

public class ItemPickup implements Listener {

    private GameManager gameManager;

    public ItemPickup(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @EventHandler
    public void onPick(EntityPickupItemEvent e) {
        if (e.getItem().getItemStack().getType().equals(Material.BLAZE_ROD)) {
            if (e.getEntity() instanceof Player) {
                Player p = (Player) e.getEntity();
                if (gameManager.getPlayerManager().getTeam(p).equals(Team.Runner)) {
                    gameManager.endGame(Team.Runner);
                }
            }
        }
    }
}
