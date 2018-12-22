package client;

//import javafx.scene.canvas.*;

import gameobjects.Explosion;
import gameobjects.GameWorld;
import gameobjects.Point;


import java.util.LinkedList;


/**
 * 发射火箭的轨迹计算
 */
public class Rocket {
    private double initialX;
    private double initialY;
    private double xSpeed;
    private double ySpeed;

//    public static Point p;

    static boolean isFLying;


    public Rocket(Point startPoint, double initialSpeed, double angle) {

        initialX = startPoint.getxCoord();
        initialY = startPoint.getyCoord() - 30;

        xSpeed = Math.cos(Math.toRadians(angle)) * initialSpeed;
        ySpeed = Math.sin(Math.toRadians(angle)) * initialSpeed;

        startPoint.setyCoord(startPoint.getyCoord() - 20);

    }

    private Explosion explode(Point destination) {
        return new Explosion(new Point(destination.getxCoord(), destination.getyCoord()));
    }

    // 计算抛物过程
    public Explosion fly(GameWorld world) {

        double GRAVITATIONAL_CONSTANT = 8;
        double currentX;
        double currentY;

            //如果初始y坐标小于576，即处于地图范围内
            // 限制向下的速度，不得超过500
        ySpeed = Math.max((ySpeed - GRAVITATIONAL_CONSTANT), -500);

        currentX = initialX;
        currentY = initialY;

        double gapTime = 0.002;

        double disX = xSpeed * gapTime;

        double disY;

        Point currentPoint = new Point(0, 0);

        isFLying = false;


        GamefieldController.tracks = new LinkedList<>();

        GamefieldController.flyAngle = new LinkedList<>();


        do {

            ySpeed = ySpeed - GRAVITATIONAL_CONSTANT*gapTime;
            currentX = currentX + disX;
            disY = ySpeed*gapTime;
            currentY = currentY - disY;
            currentPoint.setxCoord((int) currentX);
            currentPoint.setyCoord((int) currentY);

            GamefieldController.tracks.add(new Point(currentX,currentY));

            GamefieldController.flyAngle.add(Math.atan(ySpeed/xSpeed));

            if ( getDistance(world.getNearestPoint(currentPoint), currentPoint) < 2) {
                isFLying = true;
                GamefieldController.drawTimes = 0;
                return explode(currentPoint);
                }

        } while ( currentY < 576 && currentY > 0 && currentX >0 && currentX < 1100);
        isFLying = true;
        return null;
    }

    public Point getPosition() {
        return new Point(initialX, initialY);
    }

    private double getDistance(Point point1, Point point2) {
        return Math.sqrt(Math.pow(Math.abs(point1.getxCoord() - point2.getxCoord()), 2) + Math.pow(Math.abs(point1.getyCoord() - point2.getyCoord()), 2));
    }
}
