package com.rollingball.pojo.collisionBody;

/**
 * Turret Class
 * The same turret only has one bullet at a time
 */
public class Turret extends BaseCollision{
    private int timer = 3000; // 3000ms

    public Turret() {

    }

    public Turret(float curX, float curY, float baseSpeed) {
        super(curX, curY, baseSpeed);
    }

    public int getTimer() {
        return timer;
    }

    public void setTimer(int timer) {
        this.timer = timer;
    }
}
