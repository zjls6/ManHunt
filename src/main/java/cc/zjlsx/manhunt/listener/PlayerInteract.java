package cc.zjlsx.manhunt.listener;

import cc.zjlsx.manhunt.GUI.PlayerTrackGUI;
import cc.zjlsx.manhunt.GUI.TeamPickerGUI;
import cc.zjlsx.manhunt.Main;
import cc.zjlsx.manhunt.games.GameManager;
import cc.zjlsx.manhunt.games.GameState;
import cc.zjlsx.manhunt.utils.Color;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class PlayerInteract implements Listener {

    private final GameManager gameManager;

    public PlayerInteract(Main plugin) {
        this.gameManager = plugin.getGameManager();
    }

    @EventHandler
    public void onClick(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if (!e.hasItem()) return;
        ItemStack item = e.getItem();
        if (item == null) return;

        if (!item.hasItemMeta()) return;
        if (e.getAction().equals(Action.LEFT_CLICK_AIR) || e.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
            return;
        }

        String itemName = item.getItemMeta().getDisplayName();
        itemName = ChatColor.stripColor(itemName);

        if (gameManager.getState().equals(GameState.Waiting) || gameManager.getState().equals(GameState.Starting)) {
            if (itemName.contains("选择队伍")) {
                new TeamPickerGUI(gameManager).open(p);
            }
        }
        if (gameManager.getState().equals(GameState.Active)) {
            if (itemName.contains("队伍追踪器")) {
//                Map<UUID, UUID> playerTracker = gameManager.getPlayerManager().getPlayerTracker();
//
//
//                Optional<? extends Player> player = Bukkit.getOnlinePlayers().stream().findFirst();
//                if (!player.isPresent()) {
//                    return;
//                }
//
//                Player player1 = player.getMessage();
//
//                UUID uuid = playerTracker.getOrDefault(p.getUniqueId(), player1.getUniqueId());
//                Player trackPlayer = Bukkit.getPlayer(uuid);
//                if (trackPlayer == null) {
//                    return;
//                }
//                Optional<? extends Player> optionalPlayer = Bukkit.getOnlinePlayers().stream().filter(player2 -> player2 != trackPlayer && player2 != p).findFirst();
//                if (!optionalPlayer.isPresent()) {
//                    return;
//                }
//                Player player2 = optionalPlayer.getMessage();
                new PlayerTrackGUI(gameManager).open(p);
                UUID uuid = gameManager.getPlayerManager().getRunners().get(0);
                TextComponent textComponent = new TextComponent(Color.s("&a追踪&7： &e" + Bukkit.getPlayer(uuid).getName()+"  &b"+(int) Math.round(p.getLocation().distance(Bukkit.getPlayer(uuid).getLocation())) + "m"));
                p.spigot().sendMessage(ChatMessageType.ACTION_BAR,textComponent);
            }
        }
    }
}
