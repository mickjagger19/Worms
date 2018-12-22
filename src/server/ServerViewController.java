package server;

import client.Rocket;
import gameobjects.GameWorld;
import gameobjects.Player;
import gameobjects.Surface;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;


public class ServerViewController implements Initializable {
    @FXML
    public Canvas canvas_gamefield;
    @FXML
    private Canvas canvas;
    @FXML
    public Canvas cv_hud;
    @FXML
    public AnchorPane pane;

    private GraphicsContext gc;
    private GraphicsContext hudgc;

    private ServerModel model;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        gc = canvas.getGraphicsContext2D();
        hudgc = cv_hud.getGraphicsContext2D();
        model = ServerModel.getInstance();

        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            private void run2() {

                hudgc.setFont(new Font("Monaco", 14));

                hudgc.drawImage(new Image("/images/hud_background.png"), 0, 0, 1024, 50);

                if (model.getCurrentPlayer() != null) {

                    hudgc.setFill(Color.WHITE);
                    hudgc.fillRoundRect(10, 13, 104, 24, 5, 5);

                    hudgc.setStroke(Color.PINK);
                    hudgc.strokeRoundRect(10, 13, 104, 24, 5, 5);

                    if (model.getCurrentPlayer().getShoot() != null) {
                        //速度条填充
                        hudgc.setFill(Color.color(0.937, 0.294, 0.227, 1));
                        hudgc.fillRoundRect(12, 15, model.getCurrentPlayer().getShoot().getCurrentSpeed() * 100, 20, 5, 5);

                        //速度条数字
                        hudgc.setStroke(Color.BLACK);
                        hudgc.strokeText(String.format("%d%%", (int) (model.getCurrentPlayer().getShoot().getCurrentSpeed() * 100)), 49, 30);
                    }


                    hudgc.setStroke(Color.ORANGE);
                    hudgc.strokeText(String.format("%s: X: %f Y: %f", model.getCurrentPlayer().getName(), model.getCurrentPlayer().getPosition().getxCoord(),
                            model.getCurrentPlayer().getPosition().getyCoord()), 400, 33);


                    hudgc.setStroke(Color.ORANGE);

                    hudgc.strokeText(String.format("现在轮到%s了", model.getCurrentPlayer().getName()), 720, 33);


                    // 角度信息
                    hudgc.setStroke(Color.ORANGE);
                    hudgc.strokeText(String.format("角度: %.2f", model.getCurrentPlayer().getShoot().getAngle()), 230, 33);

                    // 生命值

                    hudgc.setFill(Color.RED);
                    hudgc.setFont(new Font("Monaco", 24));
                    hudgc.fillText(String.format("♥ %d", model.getCurrentPlayer().getHealth()), 930, 35);

                }
            }

            @Override
            public void run() {
                Platform.runLater(this::run2);
                Thread background = new Thread(() -> Platform.runLater(() -> {
                    drawBackground();
                    drawPlayers();
                    drawRockets();
                    drawForground();
                }));
                background.setDaemon(true);
                background.start();
            }
        }, 100, 50);
    }

    private void drawForground() {
        for (Player p : model.getOtherPlayers())
            if (p.getPosition() != null) {
                if (!p.isDead()) {
                    if (p.getTeam() != null && model.getCurrentPlayer() != null && model.getCurrentPlayer().getTeam() != null && !p.getTeam().equals(model.getCurrentPlayer().getTeam())) {
                        gc.drawImage(new Image("/images/enemy_arrow.png"), p.getPosition().getxCoord() - 6,
                                p.getPosition().getyCoord() - 70, 11, 10);
                    }
                    gc.setFill(Color.BLACK);
                    gc.setFont(new Font("System", 14));
                    gc.fillText(p.getName(), p.getPosition().getxCoord() - (getStringWidth(p.getName(), new Font("System", 14)) / 2), p.getPosition().getyCoord() - 45);
                    gc.setFill(Color.RED);
                    gc.setFont(new Font("System", 10));
                    gc.fillText(String.format("%d%%", p.getHealth()), p.getPosition().getxCoord() -
                            getStringWidth(String.format("%d%%", p.getHealth()), new Font("System", 10)) / 2, p.getPosition().getyCoord() - 30);
                } else {
                    gc.setFill(Color.RED);
                    gc.setFont(new Font("System", 10));
                    gc.fillText(p.getName(), p.getPosition().getxCoord() - (getStringWidth(p.getName(), new Font("System", 10)) / 2), p.getPosition().getyCoord() - 25);
                }
            }

        if (model.getCurrentPlayer() != null && model.getCurrentPlayer().getPosition() != null) {

            double x = model.getCurrentPlayer().getPosition().getxCoord();
            double y = model.getCurrentPlayer().getPosition().getyCoord();

            double angle360;
            double a = 0;
            double b = 0;
            double angle = model.getCurrentShoot().getAngle();

            if (angle > 0) {
//                angle360 = angle;
                a = Math.sin(Math.toRadians(angle)) * 50;
                b = Math.cos(Math.toRadians(angle)) * 50;
            } else if (angle < 0) {
                angle360 = (180 - (angle * -1)) + 180;
                a = Math.sin(Math.toRadians(angle360)) * 50;
                b = Math.cos(Math.toRadians(angle360)) * 50;
            }

            gc.setFill(Color.BLACK);
            gc.fillOval(x + b - 4, y - a - 4, 8, 8);
            gc.setFill(Color.GOLD);
            gc.fillOval(x + b - 3, y - a - 3, 6, 6);

            //gc.drawImage(new Image("/images/crossfade.png"), mouse.getxCoord() - 11, mouse.getyCoord() - 11, 21, 21);

            //Localplayersign
            gc.drawImage(new Image("/images/current_arrow.png"), model.getCurrentPlayer().getPosition().getxCoord() - 6,
                    model.getCurrentPlayer().getPosition().getyCoord() - 40, 11, 10);


            //Dead Players
            Font fhd = new Font("System", 14);
            Font fd = new Font("System", 16);
            gc.drawImage(new Image("/images/dead.png"), 2, (10 + getStringHeight("A", fhd) + getStringHeight("789", fhd)) / 2 - 16, 32, 32);

            double wdth = getStringWidth(String.valueOf(model.getState().getInfo().getDeaths_a()), fhd);
            double wdth2 = getStringWidth(String.valueOf(model.getState().getInfo().getDeaths_b()), fhd);

            gc.setFont(fhd);
            gc.setFill(Color.RED);
            gc.fillText("A", 36 + wdth / 2 - getStringWidth("A", fhd) / 2, 5 + getStringHeight("A", fhd));

            gc.setFill(Color.BLACK);
            gc.setFont(fd);
            gc.fillText(String.format("%d", model.getState().getInfo().getDeaths_a()), 36, 10 + getStringHeight("A", fhd) + getStringHeight("789", fhd));

            gc.setFill(Color.RED);
            gc.setFont(fhd);
            gc.fillText("B", 46 + wdth + wdth2 / 2 - getStringWidth("B", fhd) / 2, 5 + getStringHeight("B", fhd));

            gc.setFill(Color.BLACK);
            gc.setFont(fd);
            gc.fillText(String.format("%d", model.getState().getInfo().getDeaths_b()), 46 + wdth, 10 + getStringHeight("A", fhd) + getStringHeight("789", fhd));


            //Points
            gc.drawImage(new Image("/images/badge.png"), 150, (10 + getStringHeight("A", fhd) + getStringHeight("789", fhd)) / 2 - 16, 32, 32);

            wdth = getStringWidth(String.valueOf(model.getState().getInfo().getScore_a()), fhd);
            wdth2 = getStringWidth(String.valueOf(model.getState().getInfo().getScore_b()), fhd);

            gc.setFont(fhd);
            gc.setFill(Color.RED);
            gc.fillText("A", 186 + wdth / 2 - getStringWidth("A", fhd) / 2, 5 + getStringHeight("A", fhd));

            gc.setFill(Color.BLACK);
            gc.setFont(fd);
            gc.fillText(String.format("%d", model.getState().getInfo().getScore_a()), 186, 10 + getStringHeight("A", fhd) + getStringHeight("789", fhd));

            gc.setFill(Color.RED);
            gc.setFont(fhd);
            gc.fillText("B", 196 + wdth + wdth2 / 2 - getStringWidth("B", fhd) / 2, 5 + getStringHeight("B", fhd));

            gc.setFill(Color.BLACK);
            gc.setFont(fd);
            gc.fillText(String.format("%d", model.getState().getInfo().getScore_b()), 196 + wdth, 10 + getStringHeight("A", fhd) + getStringHeight("789", fhd));


            //Actual Player
        }
    }

    private void drawRockets() {
        for (Rocket r : model.getRockets()) {
            gc.setFill(Color.RED);
            gc.fillOval(r.getPosition().getxCoord() - 3, r.getPosition().getyCoord() - 3, 6, 6);
        }
    }

    private void drawPlayers() {
        if (model != null) {
            for (Player p : model.getPlayers()) {
                if (p.getPosition() != null) {
                    double x = p.getPosition().getxCoord();
                    double y = p.getPosition().getyCoord();

                    if (!p.isDead()) {
                        if (p.getShoot().getAngle() < 90 && p.getShoot().getAngle() > -90) {
                            gc.drawImage(new Image(String.format("/images/worms/Rworm%d.png", p.getWormSkin())), x - 8, y - 24, 16, 24);
                        } else {
                            gc.drawImage(new Image(String.format("/images/worms/worm%d.png", p.getWormSkin())), x - 8, y - 24, 16, 24);
                        }
                    } else {
                        gc.drawImage(new Image("/images/grave.png"), x - 8, y - 24, 16, 24);
                    }
                }
            }
        }
    }

    private void drawBackground() {
        if (model.getWorld().isWorldChanged()) {
            //model.getWorld().setWorldChanged();
            GraphicsContext gcgf = canvas_gamefield.getGraphicsContext2D();
            gcgf.clearRect(0, 0, canvas_gamefield.getWidth(), canvas_gamefield.getHeight());


            gcgf.setStroke(Color.SANDYBROWN);
            gcgf.setLineWidth(3);

            Surface surface = model.getWorld().getSurface();
            gcgf.strokePolyline(surface.getxCoords(), surface.getyCoords(), surface.getxCoords().length);


            gcgf.setFill(new ImagePattern(new Image("/images/EarthPattern.png")));

            surface = model.getWorld().getWholeSurface();
            gcgf.fillPolygon(surface.getxCoords(), surface.getyCoords(), surface.getxCoords().length);


        }
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    public ServerModel getModel() {
        return model;
    }

    private double getStringWidth(String text, Font font) {
        Text l = new Text(text);
        l.setFont(font);
        return l.getLayoutBounds().getWidth();
    }

    private double getStringHeight(String text, Font font) {
        Text l = new Text(text);
        l.setFont(font);
        return l.getLayoutBounds().getHeight();
    }
}
