package server;

//import com.sun.xml.internal.ws.api.message.Packet;

import client.Rocket;
import gameobjects.*;
import gameobjects.Package;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Inet4Address;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.*;

/**
 *
 */
public class ServerModel {

    private String serverIP;

    private Player currentPlayer;
    private Shoot currentShoot;

    private List<Player> players;
    private List<Rocket> rockets;
    private GameWorld world;

    private GameState state;


    private static volatile boolean flyDone = false;

    public String getServerIP() {
        return serverIP;
    }


    // 画出服务器端的界面
    private void applyPhysics() {

        for (Player p : getPlayers()) {
            p.applyPhysics(getWorld());
        }

        if (!flyDone) return;
        System.out.println("进入爆炸");

        Rocket r = getRockets().get(0);

        Explosion explosion = r.fly(getWorld());

        if (explosion != null) {
            System.out.println("正在计算爆炸效果");
            getRockets().remove(r);
            explosion.calculateDamage(getPlayers());
            getWorld().destroySurface(explosion);
        }

        flyDone = false;
    }

    List<Rocket> getRockets() {
        return rockets;
    }

    List<Player> getPlayers() {
        return new ArrayList<>(players);
    }

    GameWorld getWorld() {
        return world;
    }

    GameState getState() {
        return state;
    }

    Player getCurrentPlayer() {
        return currentPlayer;
    }

    Shoot getCurrentShoot() {
        return currentShoot;
    }

    List<Player> getOtherPlayers() {
        ArrayList<Player> p = new ArrayList<>(getPlayers());
        p.remove(currentPlayer);
        return p;
    }

    private List<Player> changedPlayers() {
        List<Player> pls = new ArrayList<>();
        for (Player p : getPlayers()) {
            if (p != null && p.hasChanged())
                pls.add(p);
        }
        return pls;
    }

    private ServerModel() {
        try {
            serverIP = Inet4Address.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        players = new LinkedList<>();
        state = new GameState(players);
        rockets = new LinkedList<>();
        world = new GameWorld(1024, 576);

        Timer t = new Timer(true);

        // 定时刷新界面
        t.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (state.readyToPlay()) {
                    applyPhysics();
                    if (currentShoot == null || currentShoot.isFired()) {
                        if (currentShoot != null) {
                            double speed = currentShoot.getCurrentSpeed() * 90;
                            getRockets().add(new Rocket(getCurrentPlayer().getPosition(), speed, currentShoot.getAngle()));
                            currentShoot.setFired(false);
                        }
                        //新的一轮, 新的发射
                        currentPlayer = state.nextPlayer();
                        currentShoot = new Shoot(0.5, 50, false);
                    }
                }
            }
        }, 50, 5);

        Thread serverConnection = new Thread(() -> {
            try {
                ServerSocket socket = new ServerSocket(2387);
                while (true) {
                    Socket csocket = socket.accept();

                    Thread clientThread = new Thread(() -> {
                        try {

                            // in: 服务器接收的数据
                            ObjectInputStream in = new ObjectInputStream(csocket.getInputStream());
                            Object receivedP = in.readObject();

                            // out: 要向客户端发送的数据
                            ObjectOutputStream out = new ObjectOutputStream(csocket.getOutputStream());

                            if (receivedP != null) {
                                if (receivedP instanceof UpdateInformation) {
                                    // 如果是游戏信息的数据包
                                    //TODO
                                    // 根据 receivedP 的类型，确定数据包的具体内容
//                                    if (changedPlayers().size()!= 0)
//                                        System.out.println("changedPlayers x: " + changedPlayers().get(0).getPosition().getxCoord());

                                    if (receivedP.equals(UpdateInformation.Player)) {
                                        out.writeObject(new Package(changedPlayers(), null, currentPlayer));
                                    } else if (receivedP.equals(UpdateInformation.World)) {

                                        out.writeObject(new Package(null, world, currentPlayer));
                                    } else if (receivedP.equals(UpdateInformation.World_a_Player)) {

                                        out.writeObject(new Package(changedPlayers(), world, currentPlayer));
                                    }

                                } else if (receivedP instanceof Player) {
//                                    System.out.println("接收 玩家信息");
                                    // 如果是玩家信息的数据包
                                    Player pCL = (Player) receivedP;
                                    if (players.contains(pCL)) {
                                        // 如果要更新的玩家信息属于玩家列表
                                        if (pCL.equals(currentPlayer)) {
                                            currentPlayer = pCL;
                                            currentShoot = pCL.getShoot();
                                            players.set(players.indexOf(pCL), pCL);
                                        }
                                    } else {
                                        System.out.println("'" + pCL.getName() + "' 进入游戏");
                                        state.join(pCL);
                                    }
                                } else if (receivedP instanceof String) {
                                    System.out.println("接收  \"true\" ");
                                    flyDone = true;
                                } else {
                                    System.out.println("来自客户端的无法识别的消息");
                                }
                            }
                        } catch (IOException | ClassNotFoundException ex) {
                            ex.printStackTrace();
                        }
                    });
                    clientThread.setDaemon(true);
                    clientThread.start();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        serverConnection.setDaemon(true);
        serverConnection.setName("ServerConnection-Thread");
        serverConnection.start();

    }


    static ServerModel getInstance() {
        return ourInstance;
    }

    private static ServerModel ourInstance = new ServerModel();
}
