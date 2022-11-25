package cc.zjlsx.manhunt.GUI;

import cc.zjlsx.manhunt.games.GameManager;
import cc.zjlsx.manhunt.games.GameState;
import cc.zjlsx.manhunt.player.PlayerManager;
import cc.zjlsx.manhunt.utils.Color;
import cc.zjlsx.manhunt.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TeamPickerGUI implements Listener {

    private GameManager gameManager;
    private Inventory inventory;

    public TeamPickerGUI(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    public void open(Player p) {

        inventory = Bukkit.createInventory(null, 9, Color.str(gameManager.getMessages().getString("menu.chooseTeam")));

        List<UUID> hunters = gameManager.getPlayerManager().getHunters();
        List<UUID> runners = gameManager.getPlayerManager().getRunners();

        if (hunters.isEmpty()) {
            inventory.setItem(3, new ItemBuilder(Material.GOLDEN_SWORD).setName(gameManager.getMessages().getString("names.hunter")).hideAttributes().toItemStack());
        } else {
            List<String> playerNames = new ArrayList<>();
            playerNames.add(gameManager.getMessages().getString("menu.teamPlayers.top"));
            for (UUID uuid : hunters) {
                Player hunter = Bukkit.getPlayer(uuid);
                playerNames.add(gameManager.getMessages().getString("menu.teamPlayers.perPlayer").replace("%playerName%", hunter.getName()));
            }
            if (hunters.contains(p.getUniqueId())) {
                inventory.setItem(3, new ItemBuilder(Material.GOLDEN_SWORD).setName(gameManager.getMessages().getString("names.hunter")).setLore(playerNames)
                        .addEnchant(Enchantment.LUCK, 1).hideEnchants().hideAttributes().toItemStack());
            } else {
                inventory.setItem(3, new ItemBuilder(Material.GOLDEN_SWORD).setName(gameManager.getMessages().getString("names.hunter")).setLore(playerNames).hideAttributes().toItemStack());
            }
        }
        inventory.setItem(4, new ItemBuilder(Material.LEGACY_STAINED_GLASS_PANE, 1, (byte) 7).setName(" ").toItemStack());
        if (runners.isEmpty()) {
            inventory.setItem(5, new ItemBuilder(Material.FEATHER).setName(gameManager.getMessages().getString("names.runner")).toItemStack());
        } else {
            List<String> playerNames = new ArrayList<>();
            playerNames.add(gameManager.getMessages().getString("menu.teamPlayers.top"));
            for (UUID uuid : runners) {
                Player runner = Bukkit.getPlayer(uuid);
                playerNames.add(gameManager.getMessages().getString("menu.teamPlayers.perPlayer").replace("%playerName%", runner.getName()));
            }
            if (runners.contains(p.getUniqueId())) {
                inventory.setItem(5, new ItemBuilder(Material.FEATHER).setName(gameManager.getMessages().getString("names.runner")).setLore(playerNames)
                        .addEnchant(Enchantment.LUCK, 1).hideEnchants().toItemStack());
            } else {
                inventory.setItem(5, new ItemBuilder(Material.FEATHER).setName(gameManager.getMessages().getString("names.runner")).setLore(playerNames).toItemStack());
            }
        }
        p.openInventory(inventory);
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        if (!e.getView().getTitle().contains(Color.str(gameManager.getMessages().getString("menu.chooseTeam")))) {
            return;
        }
        if (e.getCurrentItem() == null) {
            return;
        }
        if (e.isShiftClick()) {
            e.setCancelled(true);
        }
        int slot = e.getSlot();
        if (gameManager.getState().equals(GameState.Waiting) || gameManager.getState().equals(GameState.Starting)) {
            PlayerManager playerManager = gameManager.getPlayerManager();
            List<UUID> hunters = playerManager.getHunters();
            List<UUID> runners = playerManager.getRunners();
            switch (slot) {
                case 3:
                    if (hunters.contains(p.getUniqueId())) {
                        p.sendMessage(Color.str(gameManager.getMessages().getString("join.hunter.alreadyIn")));
                        p.closeInventory();
                        return;
                    }
                    if (hunters.size() == playerManager.getMaxHunter()) {
                        p.sendMessage(Color.str(gameManager.getMessages().getString("join.hunter.full")));
                        p.closeInventory();
                        return;
                    }
                    runners.remove(p.getUniqueId());
                    hunters.add(p.getUniqueId());
                    p.sendMessage(Color.str(gameManager.getMessages().getString("join.hunter.success")));
                    p.closeInventory();
                    break;
                case 5:
                    if (runners.contains(p.getUniqueId())) {
                        p.sendMessage(Color.str(gameManager.getMessages().getString("join.runner.alreadyIn")));
                        p.closeInventory();
                        return;
                    }
                    if (runners.size() == playerManager.getMaxRunner()) {
                        p.sendMessage(Color.str(gameManager.getMessages().getString("join.runner.full")));
                        p.closeInventory();
                        return;
                    }
                    hunters.remove(p.getUniqueId());
                    runners.add(p.getUniqueId());
                    p.sendMessage(Color.str(gameManager.getMessages().getString("join.runner.success")));
                    p.closeInventory();
                    break;
                default:
                    e.setCancelled(true);
                    break;
            }
        }
    }
}
