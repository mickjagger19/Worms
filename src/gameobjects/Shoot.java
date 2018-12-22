package gameobjects;

import java.io.Serializable;

/**
 *
 *  计算发射物的轨迹
 *  包含发射的角度
 *
 */
public class Shoot implements Serializable{

    //角度
    private double angle;

    // 速度
    private double currentSpeed;

    private boolean fired;

    public Shoot(double currentSpeed, double angle, boolean fired) {
        this.currentSpeed = currentSpeed;
        this.angle = angle;
        this.fired = fired;
    }

    public boolean isFired() {
        return fired;
    }

    public double getAngle() {
        return angle;
    }

    public double getCurrentSpeed() {
        return currentSpeed;
    }

    public void setAngle(double angle) {
        this.angle = angle;
    }

    public void setCurrentSpeed(double currentSpeed) {
        this.currentSpeed = currentSpeed;
    }

    public void setFired(boolean fired) {
        this.fired = fired;
    }

    @Override
    public String toString() {
        return "Shoot{" +
                "angle=" + angle +
                ", currentSpeed=" + currentSpeed +
                ", fired=" + fired +
                '}';
    }


}
