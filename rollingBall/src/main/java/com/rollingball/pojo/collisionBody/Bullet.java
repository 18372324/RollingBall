package com.rollingball.pojo.collisionBody;

public class Bullet extends BaseCollision {
    public static final int TYPE_DAMAGE = 1;//Damage bullet
    public static final int TYPE_SLOW = 2;  //Deceleration bullet

    public float[] dir = {0f, 0f};//Set the default direction of the bullet
    public int type = TYPE_DAMAGE;//The default is a damage bullet

    public Bullet() {
    }

    public Bullet(float curX, float curY, float baseSpeed) {
        super(curX, curY, baseSpeed);
    }
}
