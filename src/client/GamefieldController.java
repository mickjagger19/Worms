package client;

import controller.Painter;
import gameobjects.*;
import gameobjects.Point;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.Paint;
import javafx.scene.shape.*;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Font;
import javafx.scene.text.Text;


import java.awt.*;
import java.awt.Rectangle;
import java.net.URL;
import java.util.*;
import java.util.List;

/**
 * 该类控制游戏中用户的动作
 * Gamefield.fxml
 */
public class GamefieldController implements Initializable {

    @FXML
    public Canvas canvas_gamefield;
    public Canvas canvas_player;
    @FXML
    private Canvas canvas;
    @FXML
    public Canvas cv_hud;

    @FXML
    public AnchorPane pane;

    private GraphicsContext gc;
    private GraphicsContext hudgc;

    private ClientModel model;

    private boolean speedUp = true;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        model = ClientModel.getInstance();

        gc = canvas.getGraphicsContext2D();

        hudgc = cv_hud.getGraphicsContext2D();


        pane.setOnKeyPressed(event -> {
//            System.out.println("current" + model.getCurrentPlayer().getName());
//            System.out.println("local" + model.getLocalPlayer();.getName());
            if (model.getCurrentPlayer() != null && model.getCurrentPlayer().equals(model.getLocalPlayer())
            ) {

                    System.out.println("检测到用户匹配");
                    if (event.getCode() == KeyCode.SPACE) {
                        System.out.println("检测到空格键");
                        double newSpeed = model.getCurrentPlayer().getShoot().getCurrentSpeed();
                        if ( speedUp ) newSpeed += 0.03;
                        else newSpeed -= 0.03;


                        if ( newSpeed > 1 ) { newSpeed  = 2 - newSpeed; speedUp = false;}
                        else if ( newSpeed < 0 ) { newSpeed  = -newSpeed; speedUp = true;}
                        model.getCurrentPlayer().getShoot().setCurrentSpeed(newSpeed);
                    }
                    if (event.getCode() == KeyCode.UP) {
                        System.out.println("检测到向上");
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

                        System.out.println("检测到向下");
                        double currentAngle = model.getLocalPlayer().getShoot().getAngle();
                        if ( model.getLocalPlayer().dir == Player.direction.left ) {
                            if ( currentAngle < 0 )
                            model.getLocalPlayer().getShoot().setAngle( currentAngle < -92 ? currentAngle + 2 : -90  );
                            else if ( currentAngle <  178 )
                                model.getLocalPlayer().getShoot().setAngle( currentAngle + 2 );
                            else model.getLocalPlayer().getShoot().setAngle( 180 );
                        }else
                            model.getLocalPlayer().getShoot().setAngle( currentAngle > -88 ? currentAngle - 2 : -90);

//                        model.getLocalPlayer().getShoot().setAngle(model.getLocalPlayer().getShoot().getAngle() <= -179 ? 180 :
//                                model.getLocalPlayer().getShoot().getAngle() == 0 ? -1 : model.getLocalPlayer().getShoot().getAngle() - 1);
                    }
                    if (event.getCode() == KeyCode.LEFT) {
                        System.out.println("检测到向左");
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
                        System.out.println("检测到向右");
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
                        model.getRockets().add(new Rocket(model.getLocalPlayer().getPosition(), speed, model.getCurrentPlayer().getShoot().getAngle()));
                        model.getLocalPlayer().getShoot().setFired(true);
                        model.sendData();
                        speedUp = true;
                    }
                }
            }
        });

        Timer timer = new Timer(true);

        // 底部状态栏
        timer.scheduleAtFixedRate(new TimerTask() {
                                      @Override
                                      public void run() {
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
                                                          hudgc.strokeText(String.format("%d%%", (int) (model.getLocalPlayer().getShoot().getCurrentSpeed() * 100)), 49, 25);

                                                          // 玩家位置信息
                                                          if (model.getLocalPlayer().getPosition() != null) {
                                                              hudgc.setStroke(Color.ORANGE);
                                                              hudgc.strokeText(String.format("%s: X: %d Y: %d",model.getLocalPlayer().getName(), model.getLocalPlayer().getPosition().getxCoord(),
                                                                      model.getLocalPlayer().getPosition().getyCoord()), 420, 33);
                                                          }

                                                          // 现在操作的玩家信息
                                                          if (model.getLocalPlayer().getPosition() != null) {
                                                              hudgc.setStroke(Color.ORANGE);
                                                              hudgc.strokeText(String.format("现在轮到：%s",model.getCurrentPlayer().getName()), 670, 33);
                                                          }


                                                          // 角度信息
                                                          hudgc.setStroke(Color.ORANGE);
                                                          hudgc.strokeText(String.format("角度: %.2f", model.getLocalPlayer().getShoot().getAngle()), 200, 33);

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
                                                  drawRockets();
                                                  drawForground();
                                              });
                                          });
                                          background.setDaemon(true);
                                          background.start();
                                      }
                                  }
                , 100, 35);
    }


    private void drawRockets() {
        for (Rocket r : model.getRockets()) {
            Explosion explosion = r.fly(model.getWorld());
            if (explosion == null) {
                gc.setFill(Color.RED);
                gc.fillOval(r.getPosition().getxCoord() - 3, r.getPosition().getyCoord() - 3, 6, 6);
            } else {
                model.getRockets().remove(r);
                explosion.calculateDamage(model.getPlayers());
                model.getWorld().destroySurface(explosion);

                double[] xCoord = new double[explosion.getBorder().length];
                double[] grasYCoord = new double[explosion.getBorder().length];

                for (int i = 0; i < explosion.getBorder().length; i++) {
                    xCoord[i] = explosion.getBorder()[i].getxCoord();
                    grasYCoord[i] = explosion.getBorder()[i].getyCoord();
                }

                gc.setFill(Color.RED);
                gc.fillPolygon(xCoord, grasYCoord, explosion.getBorder().length);
            }
            //Point destination = new Rocket(model.getLocalPlayer().getPosition(), speed, angle).calculateFlightPath(gc, model.getWorld());
        }
    }

    private void drawPlayers() {
        GraphicsContext gcPl = canvas_player.getGraphicsContext2D();
        gcPl.clearRect(0, 0, canvas.getWidth(), canvas.getWidth());
        if (model != null && model.getPlayers() != null) {
            for (Player p : model.getPlayers()) {
                if (p != null && p.getPosition() != null) {
                    int x = p.getPosition().getxCoord();
                    int y = p.getPosition().getyCoord();

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

    }

    private void drawBackground() {
        if (model.getWorld() != null) {
            GraphicsContext gcgf = canvas_gamefield.getGraphicsContext2D();
            if (ClientModel.getInstance().getWorld().isWorldChanged()) {
                ClientModel.getInstance().getWorld().setWorldChanged();
                gcgf.clearRect(0, 0, canvas_gamefield.getWidth(), canvas_gamefield.getHeight());

                // 土地表层颜色
                for (Surface surface : ClientModel.getInstance().getWorld().getGameWorld()) {
                    gcgf.setStroke(Color.GREEN);
                    gcgf.setLineWidth(5);
                    gcgf.strokePolygon(surface.getxCoords(), surface.getyCoords(), surface.getxCoords().length);
                }

                // 土地的颜色
                for (int i = 0; i < ClientModel.getInstance().getWorld().getGameWorld().size(); i++) {
                    gcgf.setFill(Color.SANDYBROWN);
                    gcgf.fillPolygon(ClientModel.getInstance().getWorld().getGameWorld().get(i).getxCoords(),
                            ClientModel.getInstance().getWorld().getGameWorld().get(i).getyCoords(),
                            ClientModel.getInstance().getWorld().getGameWorld().get(i).getxCoords().length);
                }
            }
        }
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    private void drawForground() {
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
                //Targetmarker
                int x = model.getCurrentPlayer().getPosition().getxCoord();
                int y = model.getCurrentPlayer().getPosition().getyCoord();

                double angle360 = 0;
                double a = 0;
                double b = 0;
                double curAngle = model.getCurrentPlayer().getShoot().getAngle();

                if (curAngle > 0) {
                    angle360 = curAngle;
                    a = Math.sin(Math.toRadians(curAngle)) * 50;
                    b = Math.cos(Math.toRadians(curAngle)) * 50;
                } else if (curAngle < 0) {
                    angle360 = (180 - (curAngle * -1)) + 180;
                    a = Math.sin(Math.toRadians(angle360)) * 50;
                    b = Math.cos(Math.toRadians(angle360)) * 50;
                }
                else{
                    b = 50;
                }
//                gc.setFill(Color.RED);
//                gc.fillOval(x + b - 4, y - a - 4, 8, 8);
//                gc.setFill(Color.BROWN);
//                gc.fillOval(x + b - 3, y - a - 3, 6, 6);
//                if ( model.getCurrentPlayer().dir == Player.direction.left )
                    gc.drawImage(new Image("/images/sight.png"), x + b - 10, y - a );
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
