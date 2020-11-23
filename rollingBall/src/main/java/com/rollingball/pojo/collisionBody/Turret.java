package com.rollingball.pojo.collisionBody;

import android.graphics.Bitmap;

/**
 * Turret Class
 * The same turret only has one bullet at a time
 */
public class Turret extends BaseCollision{
    private int timer = 3000; // 3000ms
    public int type;          // to distinguish the position of the turret
    public boolean isShot = false;
    public Bitmap pic;
    public Turret() {
        timer = ((int)Math.random() * 2000) / 500 * 500 + 1000;
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
