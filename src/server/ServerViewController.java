package server;

import client.Rocket;
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
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Andreas on 18.06.2016.
 */
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
        model = ServerModel.getInstance();
        gc = canvas.getGraphicsContext2D();
        hudgc = cv_hud.getGraphicsContext2D();

        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    hudgc.setFont(new Font("System", 14));
                    hudgc.drawImage(new Image("/images/hud_background.png"), 0, 0, 1024, 50);

                    hudgc.setFill(Color.DARKGRAY);
                    hudgc.fillRoundRect(10, 10, 104, 24, 5, 5);
                    hudgc.setStroke(Color.BLACK);
                    hudgc.strokeRoundRect(10, 10, 104, 24, 5, 5);

                    if (model.getCurrentPlayer() != null) {
                        if (model.getCurrentPlayer().getShoot() != null) {
                            hudgc.setFill(Color.DARKRED);
                            hudgc.fillRoundRect(12, 12, model.getCurrentPlayer().getShoot().getCurrentSpeed() * 100, 20, 5, 5);
                            hudgc.setStroke(Color.WHITE);
                            hudgc.strokeText(String.format("%d%%", (int) (model.getCurrentPlayer().getShoot().getCurrentSpeed() * 100)), 49, 26);
                        }
                        if (model.getCurrentPlayer().getPosition() != null) {
                            hudgc.setFill(Color.ORANGE);
                            hudgc.fillText(String.format("%s: X:%f Y:%f   ∠ %.2f°    ♥ %d", model.getCurrentPlayer().getName(), model.getCurrentPlayer().getPosition().getxCoord(),
                                    model.getCurrentPlayer().getPosition().getyCoord(), model.getCurrentPlayer().getShoot().getAngle(), model.getCurrentPlayer().getHealth()), 250, 15);
                        }
                    }
                });
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
        }, 100, 50);
    }

    private void drawForground() {
        for (Player p : model.getOtherPlayers()) {
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
                            (getStringWidth(String.format("%d%%", p.getHealth()), new Font("System", 10)) / 2), p.getPosition().getyCoord() - 30);
                } else {
                    gc.setFill(Color.RED);
                    gc.setFont(new Font("System", 10));
                    gc.fillText(p.getName(), p.getPosition().getxCoord() - (getStringWidth(p.getName(), new Font("System", 10)) / 2), p.getPosition().getyCoord() - 25);
                }
            }
        }

        if (model.getCurrentPlayer() != null && model.getCurrentPlayer().getPosition() != null) {
            //Targetmarker
            double x = model.getCurrentPlayer().getPosition().getxCoord();
            double y = model.getCurrentPlayer().getPosition().getyCoord();

            double angle360 = 0;
            double a = 0;
            double b = 0;
            double angle = model.getCurrentShoot().getAngle();

            if (angle > 0) {
                angle360 = angle;
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


            //STATS

            //Dead Players
            Font fhd = new Font("System",14);
            Font fd = new Font("System",16);
            gc.drawImage(new Image("/images/dead.png"),2,(10+getStringHeight("A",fhd)+getStringHeight("789",fhd))/2-16,32,32);

            double wdth = getStringWidth(String.valueOf(model.getState().getInfo().getDeaths_a()),fhd);
            double wdth2 = getStringWidth(String.valueOf(model.getState().getInfo().getDeaths_b()),fhd);

            gc.setFont(fhd);
            gc.setFill(Color.RED);
            gc.fillText("A",36+wdth/2-getStringWidth("A",fhd)/2,5+getStringHeight("A",fhd));

            gc.setFill(Color.BLACK);
            gc.setFont(fd);
            gc.fillText(String.format("%d",model.getState().getInfo().getDeaths_a()),36,10+getStringHeight("A",fhd)+getStringHeight("789",fhd));

            gc.setFill(Color.RED);
            gc.setFont(fhd);
            gc.fillText("B",46+wdth+wdth2/2-getStringWidth("B",fhd)/2,5+getStringHeight("B",fhd));

            gc.setFill(Color.BLACK);
            gc.setFont(fd);
            gc.fillText(String.format("%d",model.getState().getInfo().getDeaths_b()),46+wdth,10+getStringHeight("A",fhd)+getStringHeight("789",fhd));


            //Points
            gc.drawImage(new Image("/images/badge.png"),150,(10+getStringHeight("A",fhd)+getStringHeight("789",fhd))/2-16,32,32);

            wdth = getStringWidth(String.valueOf(model.getState().getInfo().getScore_a()),fhd);
            wdth2 = getStringWidth(String.valueOf(model.getState().getInfo().getScore_b()),fhd);

            gc.setFont(fhd);
            gc.setFill(Color.RED);
            gc.fillText("A",186+wdth/2-getStringWidth("A",fhd)/2,5+getStringHeight("A",fhd));

            gc.setFill(Color.BLACK);
            gc.setFont(fd);
            gc.fillText(String.format("%d",model.getState().getInfo().getScore_a()),186,10+getStringHeight("A",fhd)+getStringHeight("789",fhd));

            gc.setFill(Color.RED);
            gc.setFont(fhd);
            gc.fillText("B",196+wdth+wdth2/2-getStringWidth("B",fhd)/2,5+getStringHeight("B",fhd));

            gc.setFill(Color.BLACK);
            gc.setFont(fd);
            gc.fillText(String.format("%d",model.getState().getInfo().getScore_b()),196+wdth,10+getStringHeight("A",fhd)+getStringHeight("789",fhd));


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

            // 土地表层
            for (Surface surface : model.getWorld().getGameWorld()) {
                gcgf.setStroke(Color.GREEN);
                gcgf.setLineWidth(5);
                gcgf.strokePolygon(surface.getxCoords(), surface.getyCoords(), surface.getxCoords().length);
            }

            // 土地颜色
            for (int i = 0; i < model.getWorld().getGameWorld().size(); i++) {
                gcgf.setFill(Color.SANDYBROWN);
                gcgf.fillPolygon(model.getWorld().getGameWorld().get(i).getxCoords(),
                        model.getWorld().getGameWorld().get(i).getyCoords(),
                        model.getWorld().getGameWorld().get(i).getxCoords().length);
            }
        }
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    public ServerModel getModel() {
        return model;
    }

    public double getStringWidth(String text, Font font) {
        Text l = new Text(text);
        l.setFont(font);
        return l.getLayoutBounds().getWidth();
    }
    public double getStringHeight(String text, Font font) {
        Text l = new Text(text);
        l.setFont(font);
        return l.getLayoutBounds().getHeight();
    }
}
