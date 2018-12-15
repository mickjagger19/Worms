package gameobjects;

import java.io.Serializable;
import java.util.*;

/**
 * 游戏状态类
 */
public class GameState implements Serializable {

    private HashMap<String, Player> teamA;
    private HashMap<String, Player> teamB;

    private List<Player> left_TeamA;
    private List<Player> left_TeamB;

    private List<Player> players;
    private GameInfo info;

    int round = -1;

    public GameState(List<Player> players) {
        this.players = players;
        teamA = new HashMap<>();
        teamB = new HashMap<>();
        info = new GameInfo();
    }

    public boolean readyToPlay() {
        return teamA.size() == teamB.size() && teamA.size() > 0;
    }

    // 将新玩家加入人较少的队伍，并赋予随机位置
    public synchronized void join(Player newPlayer) {
        if (players.size() == 0 || !players.contains(newPlayer)) {

            Random random = new Random();
            int x = random.nextInt(1000) + 20;
            newPlayer.setPosition(new Point(x, 20));

            if (teamA.size() <= teamB.size()) {
                newPlayer.setTeam("A");
                teamA.put(newPlayer.getName(), newPlayer);
            } else {
                newPlayer.setTeam("B");
                teamB.put(newPlayer.getName(), newPlayer);
            }
            players.add(newPlayer);
        }
    }

    public void newRound() {
        round++;
        left_TeamA = new LinkedList<>(teamA.values());
        left_TeamB = new LinkedList<>(teamB.values());
        String team = "B";

        if (getDeadPlayer(teamA.values()).size() == teamA.size()) {
            int rec = getDeadPlayer(teamB.values()).size() == 0 ? 3 : getDeadPlayer(teamB.values()).size() < teamB.size() ? 2 : 1;
            info.setScore_b(info.getScore_b() + rec);
            healPlayer();
            replacePlayers();

            System.out.println("[S] B队得分.");
        } else if (getDeadPlayer(teamB.values()).size() == teamB.size()) {
            int rec = info.getScore_a() + getDeadPlayer(teamA.values()).size() == 0 ? 3 : getDeadPlayer(teamA.values()).size() < teamA.size() ? 2 : 1;
            info.setScore_a(info.getScore_a() + rec);
            healPlayer();
            replacePlayers();
            System.out.println("[S] A队得分.");
            team = "A";
        }
        System.out.println("[S] 新的一轮开始了. 现在轮到 " + team);
    }

    public Player nextPlayer() {
        Player next = null;
        if (left_TeamA == null || left_TeamB == null)
            newRound();

        left_TeamA.removeAll(getDeadPlayer(teamA.values()));
        left_TeamB.removeAll(getDeadPlayer(teamB.values()));

        if ((left_TeamB.size() == 0 && left_TeamA.size() == 0)) {
            newRound();
            left_TeamA.removeAll(getDeadPlayer(teamA.values()));
            left_TeamB.removeAll(getDeadPlayer(teamB.values()));
        }


        if (round <= 0 && left_TeamA.size()>0&&left_TeamB.size()>0) {
            if (new Random().nextInt(2) == 0) {
                next = left_TeamB.get(0);
                left_TeamB.remove(0);
            } else {
                next = left_TeamA.get(0);
                left_TeamA.remove(0);
            }
        } else if (left_TeamA.size() == left_TeamB.size()) {
            if (info.getScore_a() > info.getScore_b()) {
                next = left_TeamB.get(0);
                left_TeamB.remove(0);
            } else if (info.getScore_a() < info.getScore_b()) {
                next = left_TeamA.get(0);
                left_TeamA.remove(0);
            } else {
                if (new Random().nextInt(2) == 0) {
                    next = left_TeamB.get(0);
                    left_TeamB.remove(0);
                } else {
                    next = left_TeamA.get(0);
                    left_TeamA.remove(0);
                }
            }
        } else if (left_TeamA.size() > left_TeamB.size()) {
            next = left_TeamA.get(0);
            left_TeamA.remove(0);
        } else if (left_TeamA.size() < left_TeamB.size()) {
            next = left_TeamB.get(0);
            left_TeamB.remove(0);
        }

        next.setCurrent(true);
        return next;
    }

    public void printTeams() {
        System.out.println("TeamA:");
        for (Player p : teamA.values()) {
            System.out.println("\t" + p.getName());
        }
        System.out.println("TeamB:");
        for (Player p : teamB.values()) {
            System.out.println("\t" + p.getName());
        }
    }

    private List<Player> getDeadPlayer(Collection<Player> plyrs) {
        List<Player> pl = new ArrayList<>();
        for (Player p : plyrs) {
            if (p.isDead()) {
                pl.add(p);
            }
        }
        return pl;
    }

    public GameInfo getInfo() {
        info.setPlayer_a(teamA.size());
        info.setPlayer_b(teamB.size());
        info.setDeaths_a(getDeadPlayer(teamA.values()).size());
        info.setDeaths_b(getDeadPlayer(teamB.values()).size());
        return info;
    }

    public void healPlayer(){
        for (Player p : players) {
            p.heal(100);
        }
    }

    public void replacePlayers(){
        for (Player p : players) {
            Random random = new Random();
            int x = random.nextInt(1000) + 20;
            p.setPosition(new Point(x, 20));
        }
    }
}
