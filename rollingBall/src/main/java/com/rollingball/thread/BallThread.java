package com.rollingball.thread;

import com.rollingball.cons.GameData;
import com.rollingball.pojo.collisionBody.Ball;
import com.rollingball.view.GameView;

/**
 * the ball changes the position according to gravity in this thread
 */
public class BallThread extends Thread{
    GameView gameView;
    boolean flag = true;
    boolean isPause = false;

    public BallThread(GameView gameView){
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
            //Calculate the new position of the ball
            float dx = gameView.ball.dx;
            float dy = gameView.ball.dy;
            //Determine whether the X direction hits the wall, if it hits the wall, restore
            if(gameView.ball.curr_x + dx >= 0 && gameView.ball.curr_x + dx <= gameView.getWidth() - gameView.ball.width){
                gameView.ball.curr_x = gameView.ball.curr_x + dx;
            }
            //Determine whether the Y direction hits the wall, if it hits the wall, restore
            if(gameView.ball.curr_y + dy >= gameView.top &&
                    gameView.ball.curr_y + dy <= gameView.getHeight() - gameView.ball.height){
                gameView.ball.curr_y = gameView.ball.curr_y + dy;
            }

            try
            {
                //the game fps is about to 30
                Thread.sleep(30);
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
            //the timer will not start unless the ball is in the deceleration state
            if(gameView.ball.getTimer() > 0){
                gameView.ball.setTimer(gameView.ball.getTimer() - 30);
                if(gameView.ball.getTimer() <= 0){
                    gameView.ball.baseSpeed = GameData.BALL_NORMAL_SPEED;
                    gameView.ball.setTimer(0);
                    gameView.ball.type = Ball.BALL_NORMAL;
                }
            }

        }
    }

}
