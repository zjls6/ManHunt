package cc.zjlsx.manhunt.listener;

import cc.zjlsx.manhunt.games.GameManager;
import cc.zjlsx.manhunt.games.GameState;
import cc.zjlsx.manhunt.games.Team;
import cc.zjlsx.manhunt.player.PlayerManager;
import cc.zjlsx.manhunt.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class EntityDamage implements Listener {

    private final GameManager gameManager;
    private final PlayerManager playerManager;

    public EntityDamage(GameManager gameManager) {
        this.gameManager = gameManager;
        playerManager = gameManager.getPlayerManager();
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if (gameManager.getState().equals(GameState.Waiting) || gameManager.getState().equals(GameState.Starting)) {
            e.setCancelled(true);
            return;
        }
    }

    @EventHandler (priority = EventPriority.MONITOR)
    public void onEntityDamage(EntityDamageByEntityEvent e) {
        if (gameManager.getState().equals(GameState.Active)) {
            if (e.getEntity() instanceof Player && e.getDamager() instanceof Player) {
                Player player = (Player) e.getEntity();
                Player damager = (Player) e.getDamager();
                if (playerManager.getTeam(player).equals(playerManager.getTeam(damager))) {
                    e.setCancelled(true);
                    return;
                }
                int currentSecond = gameManager.getGameTickTask().getCurrentSecond();
                if (currentSecond < gameManager.getPvpOnTime()) {
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        if (gameManager.getState().equals(GameState.Waiting) || gameManager.getState().equals(GameState.Starting)) {
            return;
        }
        Player p = e.getEntity();
        if (playerManager.getTeam(p).equals(Team.Runner)) {
            gameManager.endGame(Team.Hunter);
            return;
        }

        if (playerManager.getTeam(p).equals(Team.Hunter)) {
            playerManager.getPlayerDeathCount().replace(p.getUniqueId(), playerManager.getPlayerDeathCount().getOrDefault(p.getUniqueId(), 0) + 1);
            e.getDrops().remove(new ItemBuilder(Material.COMPASS).setName("&a队伍追踪器 &7(右键点击)").toItemStack());
            Player killer = p.getKiller();
            if (killer == null) {
                return;
            }
            playerManager.getPlayerKillCount().replace(killer.getUniqueId(), playerManager.getPlayerKillCount().getOrDefault(killer.getUniqueId(), 0) + 1);
        }
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent e) {
        Player p = e.getPlayer();
        if (playerManager.getTeam(p).equals(Team.Hunter)) {
            playerManager.giveTeamTacker(p.getUniqueId());
        }
    }
}
