package gameobjects;

import java.io.Serializable;
import java.util.List;

/**
 * 把 server 发往 client 的所有数据打包，成为 Package
 * 包括 GameInfo, Player, currentPlayer 和 GameWorld
 */
public class Package implements Serializable {


    private static final long serialVersionUID = 1L;
    private List<Player> players;
    private Player currentPlayer;
    private GameWorld world;

    public Package(List<Player> players, GameWorld world, Player currentPlayer) {
        this.players = players;
        this.currentPlayer = currentPlayer;
        this.world = world;
    }

    public GameWorld getWorld() {
        return world;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public List<Player> getPlayers() {
        return players;
    }
}
