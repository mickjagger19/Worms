package client;

import gameobjects.*;
import gameobjects.Package;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.*;

/**
 * 该类是client注册之后的连接界面，用于等待其他用户的注册。
 * 只有一个静态实例： singelton
 * 其他用户注册后，该类被替换
 * 使用了 socket
 */
public class ClientModel {

    private static ClientModel singelton;

    private String serverIP;

    // 当前被focus的窗口对应的 Player
    private Player localPlayer;

    // 当前轮到哪个 Player 进行操作
    private Player currentPlayer;

    // 除了当前 localPlayer 以外的所有 Player
    private List<Player> otherPlayers;

    private List<Rocket> rockets;
    private GameWorld world;

    private int worldRequest = 32;


    private ClientModel() {
        otherPlayers = new LinkedList<>();

        rockets = new LinkedList<>();

        Timer t = new Timer(true);
        t.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Socket csocket;
                try {
                    if (getServerIP() != null) {
                        // 新建 socket
                        csocket = new Socket();
                        // 尝试连接到 2387 端口
                        csocket.connect(new InetSocketAddress(getServerIP(), 2387), 10000);

                        ObjectOutputStream out = new ObjectOutputStream(csocket.getOutputStream());

                        synchronized (otherPlayers) {

                            if (world == null && otherPlayers.size() == 0 || worldRequest == 0) {
                                // 同时更新 world 和 player
                                out.writeObject(UpdateInformation.World_a_Player);
                            } else if (world == null) {
                                // 只更新 world
                                out.writeObject(UpdateInformation.World);
                            } else {
                                // 只更新 player
                                out.writeObject(UpdateInformation.Player);
                            }
                            ObjectInputStream in = new ObjectInputStream(csocket.getInputStream());

                            // 读取 server 发往 client 的数据
                            Package p = (Package) in.readObject();

                            if (p.getWorld() != null)
                                world = p.getWorld();
                            if (p.getCurrentPlayer() != null)
                                currentPlayer = p.getCurrentPlayer();

                            otherPlayers = p.getPlayers();

                            // 根据接收的数据，进行相应处理
                            if (otherPlayers != null && otherPlayers.size() > 0 && otherPlayers.contains(localPlayer)) {
                                localPlayer = otherPlayers.get(otherPlayers.indexOf(localPlayer));
                                otherPlayers.remove(localPlayer);
                            } else {
                                csocket.close();
                                sendData();
                            }



                        }

                    }
                } catch (ConnectException e) {
                    e.printStackTrace();
                    System.out.println("[C] 连接失败！");
                } catch (IOException | ClassNotFoundException ex) {
                    ex.printStackTrace();
                }
                worldRequest--;
                if(worldRequest<0)
                    worldRequest = 32;
            }
        }, 0, 20);

    }

    Player getLocalPlayer() {
        return localPlayer;
    }

    void setLocalPlayer(Player player) {
        this.localPlayer = player;
    }

    /**
     * @return otherPlayers - localPlayer
     */
    synchronized List<Player> getOtherPlayers() {
        ArrayList<Player> pl = new ArrayList<>(otherPlayers);
        pl.remove(localPlayer);
        return pl;
    }

    String getServerIP() {
        return serverIP;
    }

    void setServerIP(String serverIP) {
        this.serverIP = serverIP;
    }


    // 只在第一次调用时新建 clientModel
    // 之后调用， 都返回该静态变量
    static ClientModel getInstance() {
        if (singelton == null)
            singelton = new ClientModel();
        return singelton;
    }

    List<Rocket> getRockets() {
        return rockets;
    }

    GameWorld getWorld() {
        return world;
    }

    List<Player> getPlayers() {
        List<Player> players = new ArrayList<>(getOtherPlayers());
        players.add(getLocalPlayer());
        return players;
    }

    Player getCurrentPlayer() {
        return currentPlayer;
    }

    /**
     * 发送数据
     */
    void sendData() {
        Thread t = new Thread(() -> {

            try {
                // 新建
                Socket csocket = new Socket();
                // 连接
                csocket.connect(new InetSocketAddress(getServerIP(), 2387), 10000);
                ObjectOutputStream out = new ObjectOutputStream(csocket.getOutputStream());

                // 发送 localPlayer 信息
                out.writeObject(getLocalPlayer());
                csocket.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        t.setDaemon(true);
        t.start();
    }
}
