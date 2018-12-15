package gameobjects;

import java.io.Serializable;

/**
 *
 * 地图上的一个点
 */
public class Point implements Serializable {

    private int xCoord;
    private int yCoord;

    // 是否被更新过
    private boolean changed = false;

    //指定坐标的构造器
    public Point(int xCoord, int yCoord) {
        this.xCoord = xCoord;
        this.yCoord = yCoord;
    }

    public int getxCoord() {
        return xCoord;
    }

    public int getyCoord() {
        return yCoord;
    }

    public void setxCoord(int xCoord) {
        this.xCoord = xCoord;
        changed = true;
    }

    public void setyCoord(int yCoord) {
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
