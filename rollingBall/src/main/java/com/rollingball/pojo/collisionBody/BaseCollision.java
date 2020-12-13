package com.rollingball.pojo.collisionBody;

/**
 * Parent Class for detecting collision
 */
public class BaseCollision {
    public int width = 0;
    public int height = 0;
    public float baseSpeed = 0f;//velocity coefficient
    public float curr_x = 0;
    public float curr_y = 0;
    public float dx = 0;//Motion speed X component
    public float dy = 0;//Motion speed Y component

    public BaseCollision() {

    }
    public BaseCollision(float curX, float curY, float baseSpeed) {
        this.curr_x = curX;
        this.curr_y = curY;
        this.baseSpeed = baseSpeed;
    }

    /**
     * Get the square of the distance between the centers of two collision bodies
     * @param other
     * @return
     */
    public float getDistance(BaseCollision other){
        return (float) (Math.pow(this.curr_x + this.width / 2 - other.curr_x - other.width / 2, 2) + Math.pow(this.curr_y + this.height / 2 - other.curr_y - other.height / 2, 2));
    }

    /**
     * Determine whether the current collision body collides with the target collision body
     * @param other
     * @return
     */
    public boolean isCollision(BaseCollision other){
        //Calculate the square of the actual center distance
        float dis = getDistance(other);
        //Compare whether the center distance is too close
        return dis <= Math.pow((this.width + other.width) / 2.0f, 2);
    }


}
