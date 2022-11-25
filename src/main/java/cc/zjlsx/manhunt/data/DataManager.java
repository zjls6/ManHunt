package cc.zjlsx.manhunt.data;



import cc.zjlsx.manhunt.data.base.BaseYamlConfigProvider;
import cc.zjlsx.manhunt.models.GamePlayer;

import java.util.ArrayList;
import java.util.List;

public class DataManager extends BaseYamlConfigProvider {
    private final List<GamePlayer> gamePlayers = new ArrayList<>();

    @Override
    public void load() {
        reload();
        gamePlayers.clear();
//        for (final String name : this.config.getKeys(false)) {
//            final ConfigurationSection poisonAreaSection = config.getConfigurationSection(name);
//
//            gamePlayerList.add(new GamePlayer());
//        }
    }


//    public Optional<GamePlayer> getPoisonArea(final Player player) {
//        return getPoisonArea();
//    }


}
