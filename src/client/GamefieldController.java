package client;

import gameobjects.*;
import gameobjects.Point;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.text.Font;
import javafx.scene.text.Text;


import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;
import java.util.*;

import static java.lang.Thread.sleep;

/**
 * 该类控制游戏中用户的动作
 * Gamefield.fxml
 */
public class GamefieldController implements Initializable {

    @FXML
    public Canvas canvas_gamefield;
    public Canvas canvas_player;

    public ImageView backgroundImage;
    @FXML
    private Canvas canvas;
    @FXML
    public Canvas cv_hud;

    @FXML
    private Canvas Canvas_parabola;

    @FXML
    public AnchorPane pane;

    public static Point currentPoint = new Point(0,0);

    public static List<Point> tracks;

    public static int background_num = 1;

    private static Explosion explosion;
    private GraphicsContext gc;
    private GraphicsContext hudgc;
    private GraphicsContext paragc;

    private ClientModel model;
    private boolean speedUp = true;

    public static int  drawTimes = 0;

    private boolean rocketNeedFly = false;


    @Override
    public void initialize(URL location, ResourceBundle resources) {


        backgroundImage.setImage(new Image("/images/background" + background_num + ".png"));

        model = ClientModel.getInstance();

        gc = canvas.getGraphicsContext2D();

        paragc = Canvas_parabola.getGraphicsContext2D();

        hudgc = cv_hud.getGraphicsContext2D();


        pane.setOnKeyPressed(event -> {

            if (model.getCurrentPlayer() != null && model.getCurrentPlayer().equals(model.getLocalPlayer())
            ) {
                    if (event.getCode() == KeyCode.SPACE) {
                        double newSpeed = model.getCurrentPlayer().getShoot().getCurrentSpeed();
                        if ( speedUp ) newSpeed += 0.03;
                        else newSpeed -= 0.03;
                        if ( newSpeed > 1 ) { newSpeed  = 2 - newSpeed; speedUp = false;}
                        else if ( newSpeed < 0 ) { newSpeed  = -newSpeed; speedUp = true;}
                        model.getCurrentPlayer().getShoot().setCurrentSpeed(newSpeed);
                    }
                    if (event.getCode() == KeyCode.UP) {
                        double currentAngle = model.getLocalPlayer().getShoot().getAngle();
                        if ( model.getLocalPlayer().dir == Player.direction.left ) {
                            if ( currentAngle >= 0 )
                            model.getLocalPlayer().getShoot().setAngle( currentAngle < 92 ? 90 : currentAngle - 2 );
                            else if ( currentAngle > -178 )
                                model.getLocalPlayer().getShoot().setAngle( currentAngle - 2 );
                            else model.getLocalPlayer().getShoot().setAngle( 180 );
                        }else
                            model.getLocalPlayer().getShoot().setAngle( currentAngle < 88 ? currentAngle + 2 : 90);

                    }
                    if (event.getCode() == KeyCode.DOWN) {

                        double currentAngle = model.getLocalPlayer().getShoot().getAngle();
                        if ( model.getLocalPlayer().dir == Player.direction.left ) {
                            if ( currentAngle < 0 )
                            model.getLocalPlayer().getShoot().setAngle( currentAngle < -92 ? currentAngle + 2 : -90  );
                            else if ( currentAngle <  178 )
                                model.getLocalPlayer().getShoot().setAngle( currentAngle + 2 );
                            else model.getLocalPlayer().getShoot().setAngle( -180 );
                        }else
                            model.getLocalPlayer().getShoot().setAngle( currentAngle > -88 ? currentAngle - 2 : -90);

                    }
                    if (event.getCode() == KeyCode.LEFT) {
//                        System.out.println("检测到向左");
                        model.getLocalPlayer().movePlayer(-2);
                        model.getLocalPlayer().previousDir =  model.getLocalPlayer().dir;
                        model.getLocalPlayer().dir = Player.direction.left;
                        if ( model.getLocalPlayer().previousDir == Player.direction.right ) {
                            if (model.getLocalPlayer().getShoot().getAngle() > 0)
                                model.getLocalPlayer().getShoot().setAngle(180 - model.getLocalPlayer().getShoot().getAngle());
                            else model.getLocalPlayer().getShoot().setAngle(-180 - model.getLocalPlayer().getShoot().getAngle());
                        }
                    }
                    if (event.getCode() == KeyCode.RIGHT) {
//                        System.out.println("检测到向右");
                        model.getLocalPlayer().movePlayer(2);
                        model.getLocalPlayer().previousDir =  model.getLocalPlayer().dir;
                        model.getLocalPlayer().dir = Player.direction.right;
                        if ( model.getLocalPlayer().previousDir == Player.direction.left ) {
                            if (model.getLocalPlayer().getShoot().getAngle() > 0)
                                model.getLocalPlayer().getShoot().setAngle(180 - model.getLocalPlayer().getShoot().getAngle());
                            else model.getLocalPlayer().getShoot().setAngle(-180 - model.getLocalPlayer().getShoot().getAngle());
                        }
                    }
                    model.sendData();
            }
        });

        pane.setOnKeyReleased(event -> {
            if (event.getCode() == KeyCode.SPACE) {
                System.out.println("检测到空格释放");
                if (model.getCurrentPlayer() != null && model.getLocalPlayer() != null) {
                    if (model.getCurrentPlayer().equals(model.getLocalPlayer())) {
                        double speed = model.getCurrentPlayer().getShoot().getCurrentSpeed() * 90;
                        model.getRockets().add(new Rocket(model.getCurrentPlayer().getPosition(), speed, model.getCurrentPlayer().getShoot().getAngle()));
                        model.getLocalPlayer().getShoot().setFired(true);
                        rocketNeedFly = true;
                        model.sendData();
                        speedUp = true;
                    }
                }
            }
        });

        Timer timer = new Timer(true);

        timer.scheduleAtFixedRate(new TimerTask() {
                                      @Override
                                      public void run() {
                                          // 底部状态栏
                                          Platform.runLater(() -> {
                                                      hudgc.setFont(new Font("System", 14));
                                                      hudgc.drawImage(new Image("/images/hud_background.png"), 0, 0, 1024, 50);

                                                      hudgc.setFill(Color.WHITE);

                                                      hudgc.fillRoundRect(10, 10, 104, 24, 5, 5);

                                                      hudgc.setStroke(Color.PINK);
                                                      hudgc.strokeRoundRect(10, 10, 104, 24, 5, 5);


                                                      if (model.getCurrentPlayer() != null && model.getLocalPlayer() != null) {
                                                          // 速度条的填充颜色
                                                          hudgc.setFill(Color.color(0.937,0.294,0.227,1));
                                                          hudgc.fillRoundRect(12, 12, model.getLocalPlayer().getShoot().getCurrentSpeed() * 100, 20, 5, 5);
                                                          // 速度条填充字
                                                          hudgc.setStroke(Color.BLACK);
                                                          hudgc.strokeText(String.format("%d%%", (int) (model.getLocalPlayer().getShoot().getCurrentSpeed() * 100)), 49, 27);

                                                          // 玩家位置信息
                                                          if (model.getLocalPlayer().getPosition() != null) {
                                                              hudgc.setStroke(Color.ORANGE);
                                                              hudgc.strokeText(String.format("%s: X: %f Y: %f",model.getLocalPlayer().getName(), model.getLocalPlayer().getPosition().getxCoord(),
                                                                      model.getLocalPlayer().getPosition().getyCoord()), 420, 33);
                                                          }

                                                          // 现在操作的玩家信息
                                                          if (model.getLocalPlayer().getPosition() != null) {
                                                              hudgc.setStroke(Color.ORANGE);
                                                              hudgc.strokeText(String.format("现在轮到：%s",model.getCurrentPlayer().getName()), 730, 33);
                                                          }


                                                          // 角度信息
                                                          hudgc.setStroke(Color.ORANGE);
                                                          hudgc.strokeText(String.format("角度: %.2f", model.getLocalPlayer().getShoot().getAngle()), 230, 33);

                                                          // 生命值
                                                          if (model.getLocalPlayer() != null && !model.getLocalPlayer().isDead()) {
                                                              hudgc.setFill(Color.RED);
                                                              hudgc.setFont(new Font("System", 24));
                                                              hudgc.fillText(String.format("♥ %d", model.getLocalPlayer().getHealth()), 930, 35);
                                                          } else {
                                                              hudgc.drawImage(new Image("/images/dead.png"), 960, 5, 40, 40);
                                                          }
                                                      }
                                                  }

                                          );
                                          Thread background = new Thread(() -> {
                                              Platform.runLater(() -> {
                                                  drawBackground();
                                                  drawPlayers();
                                                  if (model.getRockets().size() > 0 && rocketNeedFly ) {
                                                      try {
                                                          drawRockets();
                                                      } catch (InterruptedException e) {
                                                          e.printStackTrace();
                                                      }
                                                  }
                                                  drawForeground();
                                              });
                                          });
                                          background.setDaemon(true);
                                          background.start();

                                      }
                                  }
                , 100, 35);

        Timer parabola = new Timer(true);

        parabola.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if ( Rocket.isFLying ) {
                    drawParabola();
                }
            }
        } ,0,15);

    }

    synchronized public void drawParabola()  {

        long startTime = System.currentTimeMillis();

        long previous = -1;

        long index = 0;

        long endTime;

        while ( index < tracks.size() ) {

            if ( previous != index) {
//                System.out.println("时间过得真快。又过去了 100 ms");
                previous = index;
                paragc.clearRect(0,0,1024,576);

                currentPoint = tracks.get( (int)index );

//                System.out.println("我在画： " + currentPoint.getxCoord() + "  " + currentPoint.getyCoord());

                paragc.drawImage(new Image("/images/parabola.png"), currentPoint.getxCoord(), currentPoint.getyCoord());

            }
            endTime = System.currentTimeMillis();
            index = (endTime - startTime)/3;
        }

        paragc.clearRect(0,0,1024,576);

        Rocket.isFLying = false;

        Rocket r = model.getRockets().get(0);


        Socket csocket = null;
        try {
                ClientModel client = ClientModel.getInstance();
                // 新建
                csocket = new Socket();
                // 连接
                csocket.connect(new InetSocketAddress(client.getServerIP(), 2387), 10000);
                ObjectOutputStream out = new ObjectOutputStream(csocket.getOutputStream());

                out.writeObject("true");
                System.out.println("发送完毕");
                csocket.close();
        } catch (IOException ex) {
                ex.printStackTrace();
        }



        // 如果没有爆炸发生，在原地画圆
        if (explosion == null) {
//            gc.setFill(Color.RED);
//            gc.fillOval(r.getPosition().getxCoord() - 3, r.getPosition().getyCoord() - 3, 6, 6);
        } else {
            System.out.println("发生爆炸");
            // 如果有爆炸发生
            model.getRockets().remove(r);
            explosion.calculateDamage(model.getPlayers());


            double[] xCoord = new double[explosion.getBorder().length];
            double[] grasYCoord = new double[explosion.getBorder().length];

            for (int i = 0; i < explosion.getBorder().length; i++) {
                xCoord[i] = explosion.getBorder()[i].getxCoord();
                grasYCoord[i] = explosion.getBorder()[i].getyCoord();
            }

            gc.setFill(Color.RED);
            gc.fillPolygon(xCoord, grasYCoord, explosion.getBorder().length);
            model.getWorld().destroySurface(explosion);
        }

    }


    // 画出火箭
    synchronized private void drawRockets() throws InterruptedException {

        if ( model.getRockets().size() == 0 ) return;

        Rocket r  =  model.getRockets().get(0);

        explosion = r.fly(model.getWorld());

        rocketNeedFly = false;

//            // 如果没有爆炸发生，在原地画圆
//            if (explosion == null) {
//                gc.setFill(Color.RED);
//                gc.fillOval(r.getPosition().getxCoord() - 3, r.getPosition().getyCoord() - 3, 6, 6);
//            } else {
//                // 如果有爆炸发生
//                model.getRockets().remove(r);
//                explosion.calculateDamage(model.getPlayers());
//
//                model.getWorld().destroySurface(explosion);
//
//                double[] xCoord = new double[explosion.getBorder().length];
//                double[] grasYCoord = new double[explosion.getBorder().length];
//
//                for (int i = 0; i < explosion.getBorder().length; i++) {
//                    xCoord[i] = explosion.getBorder()[i].getxCoord();
//                    grasYCoord[i] = explosion.getBorder()[i].getyCoord();
//                }
//
//                gc.setFill(Color.RED);
//                gc.fillPolygon(xCoord, grasYCoord, explosion.getBorder().length);
//            }
            //Point destination = new Rocket(model.getLocalPlayer().getPosition(), speed, angle).calculateFlightPath(gc, model.getWorld());



    }

    // 画出玩家 worms
    private void drawPlayers() {
        GraphicsContext gcPl = canvas_player.getGraphicsContext2D();

        gcPl.clearRect(0, 0, canvas.getWidth(), canvas.getWidth());

        if (model != null && model.getPlayers() != null) {
            for (Player p : model.getPlayers()) {
                if (p != null && p.getPosition() != null) {
                    double x = p.getPosition().getxCoord();
                    double y = p.getPosition().getyCoord();

                    if (!p.isDead()) {
                        if (p.getShoot().getAngle() < 90 && p.getShoot().getAngle() > -90) {
                            gcPl.drawImage(new Image(String.format("/images/worms/%cworm%d.png",p.dir == Player.direction.left ? 'L' : 'R' , p.getWormSkin())), x - 8, y - 24, 16, 24);
                        } else {
                            gcPl.drawImage(new Image(String.format("/images/worms/%cworm%d.png", p.dir == Player.direction.left ? 'L' : 'R' ,p.getWormSkin())), x - 8, y - 24, 16, 24);
                        }
                    } else {
                        gcPl.drawImage(new Image("/images/grave.png"), x - 8, y - 24, 16, 24);
                    }
                }
            }
        }


//        if ( Rocket.isFLying) {
//            for (Point currentPoint : tracks) {
//
//                System.out.println("我在画： " + currentPoint.getxCoord() + "  " + currentPoint.getyCoord());
//
//                gc.drawImage(new Image("/images/Canvas_parabola.png"), currentPoint.getxCoord(), currentPoint.getyCoord());
//
//            }
//            Rocket.isFLying = false;
//        }



    }

    private void drawBackground() {
        if (model.getWorld() != null) {
            GraphicsContext gcgf = canvas_gamefield.getGraphicsContext2D();
            if (ClientModel.getInstance().getWorld().isWorldChanged()) {
                ClientModel.getInstance().getWorld().setWorldChanged();
                gcgf.clearRect(0, 0, canvas_gamefield.getWidth(), canvas_gamefield.getHeight());

                // 土地表层颜色
                for (Surface surface : ClientModel.getInstance().getWorld().getGameWorld()) {
                    gcgf.setStroke(Color.LAWNGREEN);
                    gcgf.setLineWidth(4);
                    gcgf.strokePolygon(surface.getxCoords(), surface.getyCoords(), surface.getxCoords().length);
                }

                // 土地的颜色
                for (int i = 0; i < ClientModel.getInstance().getWorld().getGameWorld().size(); i++) {
//                    gcgf.setFill(Color.SANDYBROWN);

                    gcgf.setFill(new Color(0.451,0.290,0.0785,1));
                    gcgf.setFill(new ImagePattern(new Image("/images/EarthPattern.png")));
//                    gcgf.
                    gcgf.fillPolygon(ClientModel.getInstance().getWorld().getGameWorld().get(i).getxCoords(),
                            ClientModel.getInstance().getWorld().getGameWorld().get(i).getyCoords(),
                            ClientModel.getInstance().getWorld().getGameWorld().get(i).getxCoords().length);
                }
            }
        }
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    private void drawForeground() {
        if (model.getOtherPlayers().size() <= 0 || model.getOtherPlayers().size() % 2 == 0) {
            gc.drawImage(new Image("/images/wait.png"), 384, 160, 256, 256);
            gc.setFont(new Font("System", 18));
            gc.fillText("等待玩家中......", 515 - getStringWidth("等待玩家中......", new Font("System", 18)) / 2, 420);
        } else {
            for (Player p : model.getOtherPlayers()) {
                if (p != null && p.getPosition() != null) {
                    if (!p.isDead()) {
                        if (p.getTeam() != null && model.getLocalPlayer().getTeam() != null && !p.getTeam().equals(model.getLocalPlayer().getTeam())) {
                            gc.drawImage(new Image("/images/enemy_arrow.png"), p.getPosition().getxCoord() - 6,
                                    p.getPosition().getyCoord() - 70, 11, 10);
                        }

                        // 名字
                        gc.setFill(Color.BLACK);
                        gc.setFont(new Font("System", 12));
                        gc.fillText(p.getName(), p.getPosition().getxCoord() - (getStringWidth(p.getName(), new Font("System", 12)) / 2), p.getPosition().getyCoord() - 45);

                        // 生命值
                        gc.setFill(Color.RED);
                        gc.setFont(new Font("System", 10));
                        gc.fillText(String.format("%d%%", p.getHealth()), p.getPosition().getxCoord() -
                                (getStringWidth(String.format("%d%%", p.getHealth()), new Font("System", 10)) / 2), p.getPosition().getyCoord() - 30);
                    } else {
                        gc.setFill(Color.RED);
                        gc.setFont(new Font("System", 10));
                        gc.fillText(p.getName(), p.getPosition().getxCoord() - (getStringWidth(p.getName(), new Font("System", 10)) / 2), p.getPosition().getyCoord() - 25);
                    }
                }
            }
            //Localplayersign
            if (model.getCurrentPlayer() != null && model.getCurrentPlayer().getPosition() != null)
                if (!model.getCurrentPlayer().equals(model.getLocalPlayer()))
                    // 当前行动的玩家头上的箭头
                    gc.drawImage(new Image("/images/current_arrow.png"), model.getCurrentPlayer().getPosition().getxCoord() - 6,
                            model.getCurrentPlayer().getPosition().getyCoord() - 70, 11, 10);
                // 本地玩家头上的箭头
            if (model.getLocalPlayer() != null && model.getLocalPlayer().getPosition() != null)
                gc.drawImage(new Image("/images/local_arrow.png"), model.getLocalPlayer().getPosition().getxCoord() - 6,
                        model.getLocalPlayer().getPosition().getyCoord() - 70, 11, 10);

            // 死亡玩家
            if (model.getLocalPlayer() != null && model.getLocalPlayer().isDead()) {
                gc.drawImage(new Image("/images/dead.png"), 384, 160, 256, 256);
            }

            if (model.getCurrentPlayer() != null && model.getCurrentPlayer().getPosition() != null) {
                // 准星
                double x = model.getCurrentPlayer().getPosition().getxCoord();
                double y = model.getCurrentPlayer().getPosition().getyCoord();

                double angle360 = 0;
                double a = 0;
                double b = 0;
                double curAngle = model.getCurrentPlayer().getShoot().getAngle();

                if (curAngle > 0) {
                    a = Math.sin(Math.toRadians(curAngle)) * 70;
                    b = Math.cos(Math.toRadians(curAngle)) * 70;
                } else if (curAngle < 0) {
                    angle360 = curAngle + 360;
                    a = Math.sin(Math.toRadians(angle360)) * 70;
                    b = Math.cos(Math.toRadians(angle360)) * 70;
                }
                else{
                    b = 70;
                }
//                gc.setFill(Color.RED);
//                gc.fillOval(x + b - 4, y - a - 4, 8, 8);
//                gc.setFill(Color.BROWN);
//                gc.fillOval(x + b - 3, y - a - 3, 6, 6);
//                if ( model.getCurrentPlayer().dir == Player.direction.left )
                    double w  = 15;
                    gc.drawImage(new Image("/images/sight_RD.png"), x + b, y - a + w - 35 ,w,w);
                    gc.drawImage(new Image("/images/sight_RU.png"), x + b, y - a - 35 ,w,w);
                    gc.drawImage(new Image("/images/sight_LU.png"), x + b - w, y - a - 35,w,w);
                    gc.drawImage(new Image("/images/sight_LD.png"), x + b - w, y - a + w - 35,w,w);


//                else
//                    gc.drawImage(new Image("/images/sight.png"), x + b - 8, y - a );


            }
        }
    }

    public double getStringWidth(String text, Font font) {
        Text l = new Text(text);
        l.setFont(font);
        return l.getLayoutBounds().getWidth();
    }
}
