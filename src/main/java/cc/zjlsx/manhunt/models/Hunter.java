package cc.zjlsx.manhunt.models;

import cc.zjlsx.manhunt.Main;
import cc.zjlsx.manhunt.enums.Messages;
import cc.zjlsx.manhunt.models.base.GamePlayer;
import cc.zjlsx.manhunt.utils.Color;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;
import java.util.UUID;

public class Hunter extends GamePlayer {
    public Hunter(Main plugin, Player player) {
        super(plugin, player);
    }

    @Override
    public void setPlaying() {
        getPlayer().sendMessage(Messages.Game_Start_Hunter.getMessage(configManager.getHunterReleaseTime()));
        getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20 * configManager.getHunterReleaseTime(), 1, false, false));
//            super.setPlaying();
    }

    @Override
    public void gameTick(int currentSecond) {
        super.gameTick(currentSecond);
        if (currentSecond == configManager.getHunterReleaseTime()) {
            getPlayer().teleport(Bukkit.getWorld("world").getSpawnLocation());
            getPlayer().setGameMode(GameMode.SURVIVAL);
            gameManager.getPlayerManager().giveTeamTacker(uuid);

            Bukkit.broadcastMessage(Color.s("&c猎人已释放！"));
        }
    }

    @Override
    public void updateScoreboardLines() {
        super.updateScoreboardLines();
        playerManager.getRunners().forEach(this::addPerPlayerLine);

        List<Hunter> hunters = playerManager.getHunters();
        if (hunters.size() > 1) {
            scoreboardLines.add("&a追踪&7： &2队友");
            hunters.forEach(this::addPerPlayerLine);
        }
        scoreboardLines.add("");
        addKillDeathLines();
    }
}
