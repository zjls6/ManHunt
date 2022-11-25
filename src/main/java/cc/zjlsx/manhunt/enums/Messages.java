package cc.zjlsx.manhunt.enums;

import net.md_5.bungee.api.ChatColor;

public enum Messages {

    Enable("&a插件已开启"),
    Disable("&c插件已关闭"),
    Run_In_Console("&c你不能在控制台执行此命令"),
    No_Permission("&c你没有执行此命令的权限"),
    Player_Not_Found("&c该玩家不在线"),
    Hunter_Name("&c猎人", "names.hunter"),
    Runner_Name("&c猎人", "names.hunter"),
    Spectator_Name("&c猎人", "names.hunter"),
    Player_Join("&f&l%playerName% &e加入了游戏！ &7(&6%onlinePlayers%&7/&a%maxPlayers%&7)", "join.message"),
    Already_In_Hunter_Team("&4你已经加入了 &f&l[&c&l猎人&f&l] &4队伍", "join.hunter.alreadyIn"),
    Join_Hunter_Team("&a成功加入 &f&l[&c&l猎人&f&l] &a队伍", "join.hunter.success"),
    Join_Hunter_Full("&f&l[&c&l猎人&f&l] &4队伍已满", "join.hunter.full"),
    Already_In_Runner_Team("&4你已经加入了 &f&l[&e&l逃生者&f&l] &4队伍", "join.runner.alreadyIn"),
    Join_Runner_Team("&a成功加入 &f&l[&e&l逃生者&f&l] &a队伍", "join.runner.success"),
    Join_Runner_Full("&f&l[&e&l逃生者&f&l] &4队伍已满", "join.runner.full"),
    Menu_Choose_Team("&7选择你的队伍", "menu.chooseTeam"),
    Menu_Team_Player_Top("&7队伍玩家：", "menu.teamPlayers.top"),
    Menu_Team_Per_Player("&6%playerName%", "menu.teamPlayers.perPlayer"),
    Menu_Player_Track("&6%playerName%", "menu.playerTrack"),
    Game_Starting("&a游戏将在 &6&l %time% &a秒内开始"),
    Game_Start_Hunter("""

            &c你是一名猎人！&a你需要等待 &6&l%time% &a秒后才能被释放！

            """, "gameStart.hunter"),
    Game_Start_Runner("""

            &a游戏开始！赶紧跑吧！

            """, "gameStart.runner"),
    Reload_Plugin("%prefix%&a插件配置重载成功");

    private final String configPath;
    private String message;

    Messages(String message) {
        this.message = format(message);
        this.configPath = "messages." + name().toLowerCase().replace("_", "-");
    }

    Messages(String message, String configPath) {
        this.message = format(message);
        this.configPath = "messages." + configPath;
    }

    public static String format(String msg) {
        return ChatColor.translateAlternateColorCodes('&', msg);
    }

    public String getPath() {
        return configPath;
    }

    public String get() {
        return message;
    }

    public String get(int time) {
        return message.replace("%time%", String.valueOf(time));
    }

    public void setMessage(String message) {
        this.message = format(message);
    }


}
