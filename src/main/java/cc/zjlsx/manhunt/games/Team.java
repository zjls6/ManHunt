package cc.zjlsx.manhunt.games;

import cc.zjlsx.manhunt.Main;
import cc.zjlsx.manhunt.enums.Messages;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public enum Team {
    Hunter, Runner, Spectator;

    public void setPlaying(Player player, Main plugin) {
        switch (this) {
            case Hunter -> {

                player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20 * plugin.getConfigManager().getHunterReleaseTime(), 1, false, false));
            }
            case Runner -> {

            }
            case Spectator -> {
            }
        }
    }

    public String getName() {
        return switch (this) {
            case Hunter -> Messages.Hunter_Name.getMessage();
            case Runner -> Messages.Runner_Name.getMessage();
            case Spectator -> Messages.Spectator_Name.getMessage();
        };
    }
}
