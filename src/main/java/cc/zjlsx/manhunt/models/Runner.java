package cc.zjlsx.manhunt.models;

import cc.zjlsx.manhunt.Main;
import cc.zjlsx.manhunt.enums.Messages;
import cc.zjlsx.manhunt.models.base.GamePlayer;
import net.kyori.adventure.inventory.Book;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class Runner extends GamePlayer {
    public Runner(Main plugin, Player player) {
        super(plugin, player);
    }

    @Override
    public void setPlaying() {
        adventurePlayer.sendMessage(Messages.Game_Start_Runner.get());
        super.setPlaying();
    }

    @Override
    public void gameTick(int currentSecond) {
        super.gameTick(currentSecond);
    }

    @Override
    public void updateScoreboardLines() {
        super.updateScoreboardLines();
        playerManager.getHunters().forEach(this::addPerPlayerLine);

        List<Runner> runners = playerManager.getRunners();
        if (runners.size() > 1) {
            scoreboardLines.add("&a追踪&7： &2队友");
            runners.forEach(this::addPerPlayerLine);
        }
        scoreboardLines.add("");
        addKillDeathLines();
    }
}
