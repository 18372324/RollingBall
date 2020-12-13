package com.rollingball.thread;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

import com.rollingball.view.MainView;

public class MainThread extends Thread {
    public boolean flag = true;
    public boolean isPause = false;
    MainView mainView;
    SurfaceHolder surfaceHolder;

    public MainThread(MainView mainView){
        this.mainView = mainView;
        this.surfaceHolder = mainView.getHolder();
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
                        mainView.onMyDraw(c);//绘制
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
