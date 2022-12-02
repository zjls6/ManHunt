package cc.zjlsx.manhunt.models.base;

public interface IGamePlayer {
    void setPlaying();

    void gameTick(int currentSecond);

    void updateScoreboardLines();
}
