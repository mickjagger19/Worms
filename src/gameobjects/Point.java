package gameobjects;

import java.io.Serializable;

/**
 * 地图上的一个点
 */
public class Point implements Serializable {

    private double xCoord;
    private double yCoord;

    // 是否被更新过
    private boolean changed = false;

    //指定坐标的构造器
    public Point(double xCoord, double yCoord) {
        this.xCoord = xCoord;
        this.yCoord = yCoord;
    }

    public double getxCoord() {
        return xCoord;
    }

    public double getyCoord() {
        return yCoord;
    }

    public void setxCoord(double xCoord) {
        this.xCoord = xCoord;
        changed = true;
    }

    public void setyCoord(double yCoord) {
        if ( yCoord > 576 ) {
            this.yCoord=600;
        }
        else {
            this.yCoord = yCoord;
            changed = true;
        }
    }

    @Override
    public String toString() {
        return "Point{" +
                "X:" + xCoord +
                ", Y:" + yCoord +
                '}';
    }

}
