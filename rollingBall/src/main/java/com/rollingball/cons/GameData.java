package com.rollingball.cons;

import com.rollingball.pojo.collisionBody.Turret;

/**
 * Load some constants corresponding to game data
 */
public class GameData {
    public static final float BALL_NORMAL_SPEED = 5f;
    public static final float BALL_SLOW_SPEED = 1f;
    public static final float BULLET_DAMAGE_SPEED = 12f;
    public static final float BULLET_SLOW_SPEED = 24f;
    public static final int LEVEL_THRESHOLD = 10;


    /**
     * get a turret in a random position
     * @param place
     * @param w
     * @param h
     * @param top
     * @param bottom
     * @param left
     * @param right
     * @return
     */
    public static Turret getTurret(int place, int w, int h, int top, int bottom, int left, int right){
        Turret t = new Turret();
        t.width = w;
        t.height = h;
        switch (place){
            case 0:
                //at the top
                t.curr_x = ((float)Math.random() * ((right - left) / t.width)) * t.width;
                t.curr_y = top;
                t.type = 0;
                break;
            case 1:
                //at the bottom
                t.curr_x = ((float)Math.random() * ((right - left) / t.width)) * t.width;
                t.curr_y = bottom;
                t.type = 1;
                break;
            case 2:
                //at the left
                t.curr_x = left;
                t.curr_y = ((float)Math.random() * ((bottom - top) / t.height)) * t.height + top;
                t.type = 2;
                break;
            case 3:
                //at the right
                t.curr_x = right;
                t.curr_y = ((float)Math.random() * ((bottom - top) / t.height)) * t.height + top;
                t.type = 3;
                break;
            default:
                break;
        }
        return t;
    }

}
