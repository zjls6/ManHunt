package cc.zjlsx.manhunt.data;

import cc.zjlsx.manhunt.data.base.BaseYamlConfigProvider;

public class ConfigManager extends BaseYamlConfigProvider {
    private int maxHunter;
    private int maxRunner;
    private int maxSpectator;
    private int hunterReleaseTime;
    private int pvpOnTime;
    private int gameEndTime;
    private int worldBorderSize;

    @Override
    public void load() {
        reload();

        maxHunter = config.getInt("hunters");
        maxRunner = config.getInt("runners");
        maxSpectator = config.getInt("spectators");
        hunterReleaseTime = config.getInt("hunterRelease");
        pvpOnTime = config.getInt("pvpOn");
        gameEndTime = config.getInt("gameEnd");
        worldBorderSize = config.getInt("worldBorderSize");
    }


    public int getHunterReleaseTime() {
        return hunterReleaseTime;
    }

    public int getPvpOnTime() {
        return pvpOnTime;
    }

    public int getGameEndTime() {
        return gameEndTime;
    }

    public int getWorldBorderSize() {
        return worldBorderSize;
    }

    public int getMaxHunter() {
        return maxHunter;
    }

    public int getMaxRunner() {
        return maxRunner;
    }

    public int getMaxSpectator() {
        return maxSpectator;
    }

    public boolean isAllowSpectator() {
        return maxSpectator > 0;
    }

}
