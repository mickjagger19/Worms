package gameobjects;

import java.io.Serializable;

/**
 * 被发往 server 的类， 包含发射的角度
 *
 */
public class Shoot implements Serializable{

    //角度
    private double angle = 10;

    // 速度
    private double currentSpeed = 0.5;

    private boolean fired = false;

    private boolean changed = false;

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
        changed = true;
    }

    public void setCurrentSpeed(double currentSpeed) {
        this.currentSpeed = currentSpeed;
        changed = true;
    }

    public void setFired(boolean fired) {
        this.fired = fired;
        changed = true;
    }

    @Override
    public String toString() {
        return "Shoot{" +
                "angle=" + angle +
                ", currentSpeed=" + currentSpeed +
                ", fired=" + fired +
                '}';
    }

    public boolean hasChanged() {
        return changed;
    }

    public void setChanged(boolean changed) {
        this.changed = changed;
    }
}
