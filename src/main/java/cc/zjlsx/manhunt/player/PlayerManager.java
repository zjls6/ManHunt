package cc.zjlsx.manhunt.player;

import cc.zjlsx.manhunt.data.ConfigManager;
import cc.zjlsx.manhunt.games.GameManager;
import cc.zjlsx.manhunt.games.Team;
import cc.zjlsx.manhunt.models.Hunter;
import cc.zjlsx.manhunt.models.Runner;
import cc.zjlsx.manhunt.models.base.GamePlayer;
import cc.zjlsx.manhunt.utils.Color;
import cc.zjlsx.manhunt.utils.ItemBuilder;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.*;

@Getter
public class PlayerManager {

    private final GameManager gameManager;
    private final List<GamePlayer> gamePlayers = new ArrayList<>();
    private ConfigManager configManager;
    private int maxHunter;
    private int maxRunner;
    private int maxPlayer;
    private List<UUID> hunters = new ArrayList<>();
    private List<UUID> runners = new ArrayList<>();
    private List<UUID> spectators = new ArrayList<>();

    private Map<UUID, UUID> playerTrack = new HashMap<>();

    private Map<UUID, Integer> playerKillCount = new HashMap<>();
    private Map<UUID, Integer> playerDeathCount = new HashMap<>();

    public PlayerManager(GameManager gameManager) {
        this.gameManager = gameManager;

        maxPlayer = maxRunner + maxHunter;
    }

    public void setPlaying(Player p) {
        p.getInventory().clear();
        if (getTeam(p).equals(Team.Hunter)) {
            for (String message : gameManager.getMessages().getStringList("gameStart.hunter")) {
//                p.sendMessage(Color.s(message).replace("%time%", String.valueOf(gameManager.getHunterReleaseTime())));
            }
//            p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20 * gameManager.getHunterReleaseTime(), 1, false, false));
        } else if (getTeam(p).equals(Team.Runner)) {
            for (String message : gameManager.getMessages().getStringList("gameStart.runner")) {
                p.sendMessage(Color.s(message));
            }
            World world = Bukkit.getWorld("world");
            p.teleport(world.getSpawnLocation());
        } else {
            p.setGameMode(GameMode.SPECTATOR);
            World world = Bukkit.getWorld("world");
            p.teleport(world.getSpawnLocation());
        }
    }

    public Team getTeam(Player p) {
        if (hunters.contains(p.getUniqueId())) {
            return Team.Hunter;
        }
        if (runners.contains(p.getUniqueId())) {
            return Team.Runner;
        }
        return Team.Spectator;
    }

    public Team getOppositeTeam(Player p) {
        Team team = getTeam(p);
        if (team.equals(Team.Hunter)) {
            return Team.Runner;
        }
        if (team.equals(Team.Runner)) {
            return Team.Hunter;
        }
        return Team.Spectator;
    }

    public void giveTeamTacker(UUID uuid) {
        Player hunter = Bukkit.getPlayer(uuid);
        hunter.getInventory().setItem(8, new ItemBuilder(Material.COMPASS).setName("&a队伍追踪器 &7(右键点击)").toItemStack());
    }

    public boolean isInAnyTeam(Player p) {
        return hunters.contains(p.getUniqueId()) || runners.contains(p.getUniqueId());
    }
    public Optional<GamePlayer> getGamePlayer(Player player){
        return gamePlayers.stream().filter(gamePlayer -> gamePlayer.getUniqueId().equals(player.getUniqueId())).findFirst();
    }

    public List<Hunter> getHunters() {
        return gamePlayers.stream().filter(gamePlayer -> gamePlayer instanceof Hunter).map(gamePlayer -> ((Hunter) gamePlayer)).toList();
    }

    public List<Hunter> getHuntersExcept(Player player) {
        return gamePlayers.stream().filter(gamePlayer -> gamePlayer instanceof Hunter && !gamePlayer.getUniqueId().equals(player.getUniqueId())).map(gamePlayer -> ((Hunter) gamePlayer)).toList();
    }

    public List<Runner> getRunners() {
        return gamePlayers.stream().filter(gamePlayer -> gamePlayer instanceof Runner).map(gamePlayer -> ((Runner) gamePlayer)).toList();
    }

    public List<Runner> getRunnersExcept(Player player) {
        return gamePlayers.stream().filter(gamePlayer -> gamePlayer instanceof Runner && !gamePlayer.getUniqueId().equals(player.getUniqueId())).map(gamePlayer -> ((Runner) gamePlayer)).toList();
    }


}
