package com.rollingball.thread;

import android.widget.Toast;

import com.rollingball.cons.GameData;
import com.rollingball.pojo.collisionBody.Ball;
import com.rollingball.pojo.collisionBody.Bullet;
import com.rollingball.pojo.collisionBody.Turret;
import com.rollingball.view.GameView;

/**
 * calculates the variations in positions of bullets and turrets
 */
public class BulletThread extends Thread {
    GameView gameView;
    boolean flag = true;
    boolean isPause = false;

    public BulletThread(GameView gameView){
        this.gameView = gameView;
    }

    /**
     * pause the thread
     */
    public synchronized void onThreadPause() {
        isPause = true;
    }

    /**
     * thread waits, Not available for external calls
     */
    private void onThreadWait() {
        try {
            synchronized (this) {
                this.wait();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * resume the thread
     */
    public synchronized void onThreadResume() {
        isPause = false;
        this.notify();
    }

    public void run()
    {
        while(flag)
        {
            while(isPause)onThreadWait();
            //If there is no turret, add a new turret
            if(gameView.turrets.isEmpty()){
                addNormalTurret();
            }
            //Update the position of the bullet
            for (int i = 0; i < gameView.bullets.size(); i++){
                Bullet b = gameView.bullets.get(i);
                i = updatePosition(b, i);
            }

            try
            {
                //sleep for about 30 fps
                Thread.sleep(30);
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }

            for (int i = 0; i < gameView.turrets.size(); i++){
                Turret t = gameView.turrets.get(i);
                if(t.getTimer() > 0){
                    t.setTimer(t.getTimer() - 50);
                }else{
                    if(!t.isShot){
                        //Shoot a bullet at random
                        t.isShot = true;
                        t.setTimer(((int)Math.random() * 2000) / 500 * 500 + 1000);
                        shot((int)(Math.random() * 110 + 1),t);
                    }else{
                        //Destroy this turret
                        gameView.turrets.remove(t);
                        if(i != 0)i--;
                    }
                }
            }
        }
    }

    /**
     * Add a new turret and shoot a bullet
     */
    public void addNormalTurret(){
        int level = gameView.level > 4 ? 4 : gameView.level;
        for(int i = 0; i < level; i++){
            for(int j = 0; j <= level / 4; j++){
                Turret t = GameData.getTurret(i, gameView.bm_turrets[i].getWidth(), gameView.bm_turrets[i].getHeight(),
                        gameView.top, gameView.h - gameView.bm_turrets[i].getHeight() * 2,
                        0, gameView.w - gameView.bm_turrets[i].getWidth());
                t.type = i;
                t.pic = gameView.bm_turrets[t.type];
                gameView.turrets.add(t);
            }
        }

    }

    /**
     * Shoot a bullet
     * @param type
     */
    public void shot(int type, Turret t){
        Bullet b = new Bullet(t.curr_x + (t.width - gameView.bm_bullet_damage.getWidth()) / 2 , t.curr_y + (t.height - gameView.bm_bullet_damage.getHeight()) / 2, gameView.level / 10 * GameData.LEVEL_THRESHOLD);
        b.width = gameView.bm_bullet_damage.getWidth();
        b.height = gameView.bm_bullet_damage.getHeight();
        float dis = (float)Math.sqrt(b.getDistance(gameView.ball));
        b.dir[0] = (- b.curr_x + gameView.ball.curr_x) / dis;
        b.dir[1] = (- b.curr_y + gameView.ball.curr_y) / dis;
        if(type > 10){
            b.baseSpeed += GameData.BULLET_DAMAGE_SPEED;
            b.type = Bullet.TYPE_DAMAGE;
        }else{
            b.baseSpeed += GameData.BULLET_SLOW_SPEED;
            b.type = Bullet.TYPE_SLOW;
        }
        b.dx = b.baseSpeed * b.dir[0];
        b.dy = b.baseSpeed * b.dir[1];
        gameView.bullets.add(b);
    }

    /**
     * update the position of all bullets
     * @param b
     * @param index
     * @return
     */
    private int updatePosition(Bullet b,int index){
        //Determine whether to collide with the player ball
        if(b.isCollision(gameView.ball)){
            switch (b.type){
                case Bullet.TYPE_DAMAGE:
                    //Dead, game over
                    //stop all
                    gameView.gameOver();
                    break;
                case Bullet.TYPE_SLOW:
                    //Remove this slow bullet
                    gameView.bullets.remove(b);
                    //player ball switch to deceleration state
                    gameView.ball.baseSpeed = GameData.BALL_SLOW_SPEED;
                    gameView.ball.setTimer(5000);//Set 5s deceleration time
                    gameView.ball.type = Ball.BALL_SLOW;
                    if(index != 0)return index - 1;
                    break;
                default:
                    break;
            }
        }
        float dx = b.dx;
        float dy = b.dy;
        //Determine whether the X direction hits the wall, if it hits the wall, it will disappear
        if(b.curr_x + dx >= 0 && b.curr_x + dx <= gameView.getWidth() - b.width){
            b.curr_x = b.curr_x + dx;
        }else{
            gameView.bullets.remove(b);
            //One point is added if the bullet disappears
            gameView.score++;

            if(gameView.score % GameData.LEVEL_THRESHOLD == 0){
                //level up
                gameView.level++;
                gameView.promptTime = 1200;
            }
            if(index != 0)return index - 1;
        }
        if(b.curr_y + dy >= 0 && b.curr_y + dy <= gameView.getHeight() - b.width){
            b.curr_y = b.curr_y + dy;
        }else{
            gameView.bullets.remove(b);
            //One point is added if the bullet disappears
            gameView.score++;
            if(gameView.score % GameData.LEVEL_THRESHOLD == 0){
                //level up
                gameView.level++;
                gameView.promptTime = 1200;
            }
            if(index != 0)return index - 1;
        }
        return index;
    }

}
