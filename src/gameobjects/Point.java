package gameobjects;

import java.io.Serializable;

/**
 * 地图上的一个点
 */
public class Point implements Serializable, Comparable {

    private double xCoord;
    private double yCoord;


    @Override
    public int compareTo(Object o) {
        Point object = (Point)o;
        if ( this.getxCoord() > object.getxCoord() ){
            return 1;
        }
        else return -1;
    }

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

//        System.out.println("我在" + this.xCoord);
    }

    public void setyCoord(double yCoord) {
        if ( yCoord > 576 ) {
            this.yCoord = 600;
        }
        else {
            this.yCoord = yCoord;
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
