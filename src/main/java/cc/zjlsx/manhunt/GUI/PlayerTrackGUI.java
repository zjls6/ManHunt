package cc.zjlsx.manhunt.GUI;

import cc.zjlsx.manhunt.games.GameManager;
import cc.zjlsx.manhunt.games.GameState;
import cc.zjlsx.manhunt.games.Team;
import cc.zjlsx.manhunt.utils.Color;
import cc.zjlsx.manhunt.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.List;
import java.util.UUID;

public class PlayerTrackGUI implements Listener {

    private GameManager gameManager;
    private Inventory inventory;


    public PlayerTrackGUI(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    public void open(Player p) {

        inventory = Bukkit.createInventory(null, 9, Color.s(gameManager.getMessages().getString("menu.playerTrack")));

        List<UUID> hunters = gameManager.getPlayerManager().getHunters();
        List<UUID> runners = gameManager.getPlayerManager().getRunners();

        Team team = gameManager.getPlayerManager().getTeam(p);

        if (team.equals(Team.Hunter)) {

            for (UUID uuid : hunters) {
                if (uuid.equals(p.getUniqueId())) {
                    return;
                }
                inventory.addItem(new ItemBuilder(Material.PLAYER_HEAD).setSkullOwner(Bukkit.getOfflinePlayer(uuid)).toItemStack());
            }
            for (UUID uuid : runners) {
                if (uuid.equals(p.getUniqueId())) {
                    return;
                }
                inventory.addItem(new ItemBuilder(Material.PLAYER_HEAD).setSkullOwner(Bukkit.getOfflinePlayer(uuid)).toItemStack());
            }
        }

        p.openInventory(inventory);
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        if (!e.getView().getTitle().contains(Color.s(gameManager.getMessages().getString("menu.playerTrack")))) {
            return;
        }
        if (e.getCurrentItem() == null) {
            return;
        }
        ItemStack item = e.getCurrentItem();
        if (e.isShiftClick()) {
            e.setCancelled(true);
        }
        int slot = e.getSlot();
        if (!gameManager.getState().equals(GameState.Active)) {
            return;
        }

        SkullMeta skull = (SkullMeta) item.getItemMeta();
        if (!skull.hasOwner()) {
            return;
        }
        OfflinePlayer offlinePlayer = skull.getOwningPlayer();
        if (!offlinePlayer.isOnline()) {
            p.sendMessage(Color.s(gameManager.getMessages().getString("playerNotOnline")));
            return;
        }
        Player player = offlinePlayer.getPlayer();
        gameManager.getPlayerManager().getPlayerTrack().replace(p.getUniqueId(), player.getUniqueId());
    }

}
