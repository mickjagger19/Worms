package debug;

import client.Rocket;
import gameobjects.Explosion;
import gameobjects.Point;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import gameobjects.GameWorld;

import java.net.URL;
import java.util.*;

public class TestController implements Initializable {


    public ImageView backgroundImage;
    @FXML
    private Canvas canvas;

    private GraphicsContext gc;
    private GameWorld gameWorld;
    private Rocket rocket1;
    private Rocket rocket2;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        gameWorld = new GameWorld((int) canvas.getWidth(), (int) canvas.getHeight());
        gc = canvas.getGraphicsContext2D();

        Timer timer = new Timer(true);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (rocket1 != null) {
                    Explosion explosion;
                    explosion = rocket1.fly(gameWorld);
                    if (explosion != null) {
                        rocket1 = null;
                        gameWorld.destroySurface(explosion);
                    }
                }
                if (rocket2 != null) {
                    Explosion explosion;

                    explosion = rocket2.fly(gameWorld);

                    if (explosion != null) {
                        rocket2 = null;
                        gameWorld.destroySurface(explosion);
                    }
                }
                drawSurface();
            }
        }, new Date(), 20);

        canvas.setOnMousePressed(event -> {
            if (event.getButton().equals(MouseButton.PRIMARY)) {
                Explosion explosion = new Explosion(new Point((int) event.getX(), (int) event.getY()));
                gameWorld.destroySurface(explosion);

                double[] xCoord = new double[explosion.getBorder().length];
                double[] grasYCoord = new double[explosion.getBorder().length];

                for (int i = 0; i < explosion.getBorder().length; i++) {
                    xCoord[i] = explosion.getBorder()[i].getxCoord();
                    grasYCoord[i] = explosion.getBorder()[i].getyCoord();
                }

                gc.setFill(Color.RED);
                gc.fillPolygon(xCoord, grasYCoord, explosion.getBorder().length);
            } else if (event.getButton().equals(MouseButton.MIDDLE)) {
                rocket1 = new Rocket(new Point((int) event.getX(), (int) event.getY()), 45, 45);
                rocket2 = new Rocket(new Point((int) event.getX(), (int) event.getY()), 45, -225);
            } else {
                System.out.println(gameWorld.containsPoint(new Point((int) event.getX(), (int) event.getY())));
                gameWorld.getNearestPoint(new Point((int) event.getX(), (int) event.getY()));
            }

        });
    }

    private void drawSurface() {

        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);

//        for (int i = 0; i < gameWorld.getSurface().size(); i++) {
            gc.strokePolyline(gameWorld.getSurface().getxCoords(), gameWorld.getSurface().getyCoords(), gameWorld.getSurface().getxCoords().length);
//        }


        if (rocket1 != null) {
            gc.setFill(Color.RED);
            gc.fillOval(rocket1.getPosition().getxCoord() - 3, rocket1.getPosition().getyCoord() - 3, 6, 6);
        }
        if (rocket2 != null) {
            gc.setFill(Color.RED);
            gc.fillOval(rocket2.getPosition().getxCoord() - 3, rocket2.getPosition().getyCoord() - 3, 6, 6);
        }
    }
}