package cc.zjlsx.manhunt.models;

import cc.zjlsx.manhunt.Main;
import cc.zjlsx.manhunt.models.base.GamePlayer;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

public class Spectator extends GamePlayer {
    public Spectator(Main plugin, Player player) {
        super(plugin, player);
    }

    @Override
    public void setPlaying() {
        getPlayer().setGameMode(GameMode.SPECTATOR);
        super.setPlaying();
    }
}
