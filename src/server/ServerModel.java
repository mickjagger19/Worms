package server;

//import com.sun.xml.internal.ws.api.message.Packet;
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
import java.util.logging.Logger;

/**
 *
 */
public class ServerModel {
    private static ServerModel ourInstance = new ServerModel();

    public static ServerModel getInstance() {
        return ourInstance;
    }

    private String serverIP;

    private Player currentPlayer;
    private Shoot currentShoot;

    private List<Player> players;
    private List<Rocket> rockets;
    private GameWorld world;

    private GameState state;


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
                        if (currentShoot != null && currentShoot.isFired()) {
                            double speed = currentShoot.getCurrentSpeed() * 90;
                            getRockets().add(new Rocket(getCurrentPlayer().getPosition(), speed, currentShoot.getAngle()));
                            currentShoot.setFired(false);
                        }
                        //新的一轮
                        currentPlayer = state.nextPlayer();
                        currentShoot = new Shoot(0.5, 50, false);
                    }
                }
            }
        }, 50, 5);
        Thread serverConnection = new Thread(() -> {
            /*state.join(new Player("Sepp"));
            state.join(new Player("Mehmet"));
            state.join(new Player("Franz"));
            state.join(new Player("Gustav"));
            state.join(new Player("Hans"));
            state.join(new Player("Günther"));*/

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
                                    if (receivedP.equals(UpdateInformation.Player)) {
                                        out.writeObject(new Package(state.getInfo(), changedPlayers(), null, currentPlayer));
                                    } else if (receivedP.equals(UpdateInformation.World)) {
                                        out.writeObject(new Package(state.getInfo(), null, world, currentPlayer));
                                    } else if (receivedP.equals(UpdateInformation.World_a_Player)) {
                                        out.writeObject(new Package(state.getInfo(), changedPlayers(), world, currentPlayer));
                                    }
                                } else if (receivedP instanceof Player) {
                                    // 如果是玩家信息的数据包
                                    Player pCL = (Player) receivedP;
                                    if (players.contains(pCL)) {
                                        // 如果要更新的玩家信息属于玩家列表
                                        if (pCL.equals(currentPlayer)) {
                                            currentPlayer = pCL;
                                            currentShoot = pCL.getShoot();
                                            players.set(players.indexOf(pCL), pCL);
                                            //System.out.println(players.get(players.indexOf(pCL))); //Gesendeter Spieler
                                        }
                                    } else {
                                        System.out.println("'"+pCL.getName() + "' 进入游戏");
                                        state.join(pCL);
                                    }
                                } else {
                                    System.out.println("来自客户端的无法识别的消息");
                                }
                            }
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
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

        /*Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                players.get(new Random().nextInt(players.size())).removeHealth(100);
            }
        },100,3000);*/
    }

    public String getServerIP() {
        return serverIP;
    }

    public void applyPhysics() {
        for (Player p : getPlayers()) {
            p.applyPhysics(getWorld());
        }
        for (Rocket r : rockets) {
            Explosion explosion = r.fly(getWorld());
            if (explosion != null) {
                getRockets().remove(r);
                explosion.calculateDamage(getPlayers());
                getWorld().destroySurface(explosion);
            }
        }
    }

    public List<Rocket> getRockets() {
        return rockets;
    }

    public List<Player> getPlayers() {
        return new ArrayList<>(players);
    }

    public GameWorld getWorld() {
        return world;
    }

    public GameState getState() {
        return state;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public Shoot getCurrentShoot() {
        return currentShoot;
    }

    public List<Player> getOtherPlayers() {
        ArrayList<Player> p = new ArrayList<>(getPlayers());
        p.remove(currentPlayer);
        return p;
    }

    public List<Player> changedPlayers() {
        return getPlayers();/*
        List<Player> pls = new ArrayList<>();
        for (Player p : getPlayers()) {
            if (p != null && p.hasChanged())
                pls.add(p);
        }
        return pls;*/
    }
}