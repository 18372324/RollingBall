package com.rollingball.thread;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

import com.rollingball.view.GameView;

/**
 * this class just do one thing ---- draw the images one frame by one frame
 */
public class DrawThread extends Thread {
    public boolean flag = true;
    boolean isPause = false;
    GameView gameView;
    SurfaceHolder surfaceHolder;

    public DrawThread(GameView gameView){
        this.gameView = gameView;
        this.surfaceHolder = gameView.getHolder();
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

    public void run(){
        Canvas c;
        while (this.flag) {
            c = null;
            while(isPause)onThreadWait();
            if(!isPause)
            {
                try {
                    // lock the whole canvas to draw
                    c = this.surfaceHolder.lockCanvas(null);
                    synchronized (this.surfaceHolder) {
                        gameView.onMyDraw(c);//绘制
                    }
                } finally {
                    if (c != null) {
                        //unlock canvas after drawing
                        this.surfaceHolder.unlockCanvasAndPost(c);
                    }
                }
            }

            try{
                //the higher the frame rate, the smoother the animation
                Thread.sleep(12);
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
    }

}
