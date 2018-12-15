package gameobjects;

import java.io.Serializable;

/**
 * 游戏信息, 主要为队伍的人数、分数和死亡数
 */
public class GameInfo implements Serializable{
    public static final int MAX_POINTS = 10;

    // A 组
    private int player_a = 0;
    private int score_a = 0;
    private int deaths_a = 0;
    // B 组
    private int player_b = 0;
    private int score_b = 0;
    private int deaths_b = 0;

    public int getPlayer_a() {
        return player_a;
    }

    public int getScore_a() {
        return score_a;
    }

    public int getScore_b() {
        return score_b;
    }

    public int getPlayer_b() {
        return player_b;
    }

    public int getDeaths_a() {
        return deaths_a;
    }

    public int getDeaths_b() {
        return deaths_b;
    }

    public GameInfo(int player_a, int score_a, int deaths_a, int player_b, int score_b, int deaths_b) {
        this.player_a = player_a;
        this.score_a = score_a;
        this.deaths_a = deaths_a;
        this.player_b = player_b;
        this.score_b = score_b;
        this.deaths_b = deaths_b;
    }
    public GameInfo(){}

    public void setDeaths_a(int deaths_a) {
        this.deaths_a = deaths_a;
    }

    public void setDeaths_b(int deaths_b) {
        this.deaths_b = deaths_b;
    }

    public void setPlayer_a(int player_a) {
        this.player_a = player_a;
    }

    public void setPlayer_b(int player_b) {
        this.player_b = player_b;
    }

    public void setScore_a(int score_a) {
        this.score_a = score_a;
    }

    public void setScore_b(int score_b) {
        this.score_b = score_b;
    }
}
