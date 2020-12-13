package com.rollingball.pojo.collisionBody;

public class Ball extends BaseCollision {
    public static final int BALL_NORMAL = 1;//Indicates that the ball is currently in a normal state
    public static final int BALL_SLOW = 2;//Indicates that the ball is currently decelerating


    private int timer = 0;
    public int type = BALL_NORMAL;


    public Ball() {
    }


    public Ball(float curX, float curY, float baseSpeed) {
        super(curX, curY, baseSpeed);
    }

    public int getTimer() {
        return timer;
    }

    public void setTimer(int timer) {
        this.timer = timer;
    }
}
