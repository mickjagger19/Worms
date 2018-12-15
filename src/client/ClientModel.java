package client;

import gameobjects.*;
import gameobjects.Package;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.*;

/**
 * 该类是client注册之后的连接界面，用于等待其他用户的注册
 * 其他用户注册后，该类被替换
 * 使用了 socket
 */
public class ClientModel extends Observable {
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

    int worldRequest = 32;


    private ClientModel() {
        otherPlayers = new LinkedList<>();
        rockets = new LinkedList<>();

        Timer t = new Timer(true);
        t.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Socket csocket = null;
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

                            // 发送 localPlayer 数据
                            if (otherPlayers != null && otherPlayers.size() > 0 && otherPlayers.contains(localPlayer)) {


                                localPlayer = otherPlayers.get(otherPlayers.indexOf(localPlayer));

                                // otherPlayers - localPlayer
                                otherPlayers.remove(localPlayer);

//                                if(localPlayer.equals(currentPlayer)){
//                                    csocket.close();
//                                    sendData();
//                                }
                            } else {
                                csocket.close();
                                sendData();
                            }
                        }
                        //System.out.println("[Client] 接收数据.");
                    }
                } catch (ConnectException e) {
                    e.printStackTrace();
                    System.out.println("[C] 连接失败！");
                } catch (IOException ex) {
                    ex.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                worldRequest--;
                if(worldRequest<0)
                    worldRequest = 32;
            }
        }, 0, 20);

    }

    public Player getLocalPlayer() {
        return localPlayer;
    }

    public void setLocalPlayer(Player player) {
        this.localPlayer = player;
    }

    /**
     * @return otherPlayers - localPlayer
     */
    public synchronized List<Player> getOtherPlayers() {
        ArrayList<Player> pl = new ArrayList<>(otherPlayers);
        pl.remove(localPlayer);
        return pl;
    }

    public String getServerIP() {
        return serverIP;
    }

    public void setServerIP(String serverIP) {
        this.serverIP = serverIP;
    }


    public static ClientModel getInstance() {
        if (singelton == null)
            singelton = new ClientModel();
        return singelton;
    }

    public List<Rocket> getRockets() {
        return rockets;
    }

    public GameWorld getWorld() {
        return world;
    }

    public List<Player> getPlayers() {
        List<Player> players = new ArrayList<>(getOtherPlayers());
        players.add(getLocalPlayer());
        return players;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    /**
     * 发送数据
     */
    public void sendData() {
        Thread t = new Thread(() -> {
            Socket csocket = null;
            try {
                // 新建
                csocket = new Socket();
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