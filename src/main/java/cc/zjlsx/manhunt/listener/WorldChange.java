package cc.zjlsx.manhunt.listener;

import cc.zjlsx.manhunt.games.GameManager;
import org.bukkit.event.Listener;

public class WorldChange implements Listener {

    private GameManager gameManager;

    public WorldChange(GameManager gameManager) {
        this.gameManager = gameManager;
    }

//    @EventHandler
//    public void onChange(PlayerChangedWorldEvent e) {
//        Player p = e.getPlayer();
//        if (gameManager.getPlayerManager().getTeam(p).equals(Team.MainCommand)) {
//            return;
//        }
//        if (p.getWorld().getEnvironment().equals(World.Environment.NETHER)) {
//            gameManager.endGame(Team.Runner);
//        }
//    }
}
