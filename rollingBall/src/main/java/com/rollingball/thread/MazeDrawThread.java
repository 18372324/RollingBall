package com.rollingball.thread;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

import com.rollingball.view.MazeView;

/**
 * draw the elements in a maze model
 */
public class MazeDrawThread extends Thread {
    public boolean flag = true;
    boolean isPause = false;
    private MazeView mazeView;
    SurfaceHolder surfaceHolder;

    public MazeDrawThread(MazeView mazeView){
        this.mazeView = mazeView;
        this.surfaceHolder = mazeView.getHolder();
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
                        mazeView.onMyDraw(c);//draw
                    }
                } finally {
                    if (c != null) {
                        //unlock canvas after drawing
                        this.surfaceHolder.unlockCanvasAndPost(c);
                    }
                }
            }

        }
    }
}
