package cc.zjlsx.manhunt.models;

import java.util.List;

public interface IGamePlayer {
    void setPlaying();

    void gameTick(int currentSecond);

    void updateScoreboardLines();
}
