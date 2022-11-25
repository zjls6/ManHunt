package cc.zjlsx.manhunt.listener;

import cc.zjlsx.manhunt.games.GameManager;
import cc.zjlsx.manhunt.games.GameState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;

public class InventoryClick implements Listener {

    private GameManager gameManager;

    public InventoryClick(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @EventHandler
    public void onClick(InventoryClickEvent e){
        if (e.getWhoClicked().isOp()) {
            return;
        }
        if (gameManager.getState().equals(GameState.Waiting) || gameManager.getState().equals(GameState.Starting)) {
            if (e.getAction().equals(InventoryAction.MOVE_TO_OTHER_INVENTORY)) {
                e.setCancelled(true);
            }
            e.setCancelled(true);
            return;
        }
    }
}