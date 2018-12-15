package gameobjects;

//import javafx.scene.canvas.*;

import java.util.DoubleSummaryStatistics;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Andreas on 28.05.2016.
 *
 * Die Rocket Klasse speichert die Abschussparameter ab, und kann mithilfe der fly() Methode die Flugbahn mit der
 * übergebenen Gschwindigkeit berechnen.
 */
public class Rocket {
    private Point start;
    private double initSpeed;
    private int initialX;
    private int initialY;
    private double xSpeed;
    private double ySpeed;

    public Rocket(Point startPoint, double initialSpeed, double angle) {
        start = startPoint;
        initSpeed = initialSpeed;

        initialX = start.getxCoord();
        initialY = start.getyCoord();

        xSpeed = Math.cos(Math.toRadians(angle)) * initSpeed;
        ySpeed = Math.sin(Math.toRadians(angle)) * initSpeed;

        start.setyCoord(startPoint.getyCoord() - 20);
    }

    private Explosion explode(Point destination) {
        return new Explosion(new Point(destination.getxCoord(), destination.getyCoord()));
    }

    public Explosion fly(GameWorld world) {

        //double intitial;

        double GRAVITATIONAL_CONSTANT = 8;

        double currentX;
        double currentY;

        int finalX;
        int finalY;

        double cnt;

        if (initialY <= 576) {
            ySpeed = Math.max((ySpeed - GRAVITATIONAL_CONSTANT), -500);
            if (xSpeed > 5)
                xSpeed--;
            if (xSpeed < -5)
                xSpeed++;

            finalX = (int) (initialX + Math.round(xSpeed));
            finalY = (int) (initialY -  Math.round(ySpeed));

            currentX = initialX;
            currentY = initialY;

            //intitial = System.nanoTime();

            double moveX = (finalX - initialX) / xSpeed;
            double moveY = (finalY - initialY) / ySpeed;

            cnt = Math.abs(xSpeed);

            Point currentPoint = new Point(0, 0);

            do {

                cnt -= Math.abs(moveX);

                currentX = currentX + moveX;
                currentY = currentY + moveY;
                currentPoint.setxCoord((int) currentX);
                currentPoint.setyCoord((int) currentY);

                if (getDistance(world.getNearestPoint(currentPoint), currentPoint) < 20) {
                    if (getDistance(world.getNearestPoint(currentPoint), currentPoint) < 5 || world.containsPoint(currentPoint)) {
                        return explode(currentPoint);
                    }
                }

            } while (cnt > 0);

            initialX = finalX;
            initialY = finalY;
        } else {
            return explode(new Point(initialX, initialY));
        }
        //System.out.println(System.nanoTime() - intitial);
        return null;
    }

    public Point getPosition() {
        return new Point(initialX, initialY);
    }

    private double getDistance(Point point1, Point point2) {
        return Math.sqrt(Math.pow(Math.abs(point1.getxCoord() - point2.getxCoord()), 2) + Math.pow(Math.abs(point1.getyCoord() - point2.getyCoord()), 2));
    }
}
