package client;

//import javafx.scene.canvas.*;

import client.GamefieldController;
import gameobjects.Explosion;
import gameobjects.GameWorld;
import gameobjects.Point;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

import java.util.LinkedList;
import java.util.List;

import static java.lang.Thread.sleep;

/**
 * 发射火箭的轨迹计算
 */
public class Rocket {
    private Point start;
    private double initSpeed;
    private double initialX;
    private double initialY;
    private double xSpeed;
    private double ySpeed;

    public static Point p;

    public static boolean isFLying;


    public Rocket(Point startPoint, double initialSpeed, double angle) {
        start = startPoint;
        initSpeed = initialSpeed;

        initialX = start.getxCoord();
        initialY = start.getyCoord() - 30;

        xSpeed = Math.cos(Math.toRadians(angle)) * initSpeed;
        ySpeed = Math.sin(Math.toRadians(angle)) * initSpeed;

        start.setyCoord(startPoint.getyCoord() - 20);

    }

    private Explosion explode(Point destination) {
        return new Explosion(new Point(destination.getxCoord(), destination.getyCoord()));
    }

    // 计算抛物过程
    public Explosion fly(GameWorld world) throws InterruptedException {

        double GRAVITATIONAL_CONSTANT = 8;
        double currentX;
        double currentY;

            //如果初始y坐标小于576，即处于地图范围内
            // 限制向下的速度，不得超过500
        ySpeed = Math.max((ySpeed - GRAVITATIONAL_CONSTANT), -500);

        currentX = initialX;
        currentY = initialY;

        double gapTime = 0.01;

        double disX = xSpeed * gapTime;

        double disY;

        Point currentPoint = new Point(0, 0);

        isFLying = false;

//        System.out.println( "我要开始循环了" );

        GamefieldController.tracks = new LinkedList<Point>();

            // 计算抛物线，用 currentPoint 存储当前的点
        do {

//            System.out.println( "我循环了一次" );
            ySpeed = ySpeed - GRAVITATIONAL_CONSTANT*gapTime;
            currentX = currentX + disX;
            disY = ySpeed*gapTime;
            currentY = currentY - disY;
            currentPoint.setxCoord((int) currentX);
            currentPoint.setyCoord((int) currentY);

            GamefieldController.tracks.add(new Point(currentX,currentY));

            if (getDistance(world.getNearestPoint(currentPoint), currentPoint) < 10) {
                isFLying = true;
//                System.out.println( "我return了，在目的地爆炸" );
//                System.out.println("总共画了" + GamefieldController.drawTimes + " 次 ");
                GamefieldController.drawTimes = 0;
                return explode(currentPoint);
                }

        } while ( currentY < 576 && currentY > 0 && currentX >0 && currentX < 1100);


//        System.out.println( "我结束循环了" );

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
