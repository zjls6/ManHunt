package cc.zjlsx.manhunt.data;


import cc.zjlsx.manhunt.data.base.BaseYamlConfigProvider;
import cc.zjlsx.manhunt.enums.Messages;
import cc.zjlsx.manhunt.models.base.GamePlayer;

import java.util.ArrayList;
import java.util.List;

public class MessagesManager extends BaseYamlConfigProvider {
    private final List<GamePlayer> gamePlayers = new ArrayList<>();

    @Override
    public void load() {
        reload();
        // 生成默认的消息
        config.addDefault("messages.prefix", "&7[&a&l!&7] ");
        for (Messages message : Messages.values()) {
            config.addDefault(message.getPath(), message.get());
        }
        config.options().copyDefaults(true);
        save();
        //加载消息
        loadMessages();
    }

    private void loadMessages() {
        for (Messages message : Messages.values()) {
            String prefix = config.getString("messages.prefix", "&7[&a&l!&7] ");
            String configPath = message.getPath();
            if (configPath == null) {
                getPlugin().getLogger().severe("无法获取消息 " + message + " 请尝试删除配置文件后重启重新生成");
                continue;
            }
            String string = config.getString(configPath);
            if (string == null) {
                getPlugin().getLogger().severe("无法获取消息 " + message.getPath() + " 请尝试删除配置文件后重启重新生成");
                continue;
            }
            message.setMessage(string.replace("%prefix%", prefix));
        }
    }

}
