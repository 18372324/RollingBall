package com.rollingball.thread;

import com.rollingball.view.MazeView;

/**
 * control the movement of ball in a maze model
 */
public class MazeMoveThread extends Thread{
    MazeView mazeView;
    boolean flag = true;
    boolean isPause = false;

    private int[][] map = {//1 represents wall, 2 represents destination
            {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
            {1,0,0,0,0,0,1,0,0,1,0,0,0,0,1},
            {1,0,0,0,0,0,1,0,0,1,0,0,0,0,1},
            {1,1,1,0,0,1,1,0,0,1,1,1,0,0,1},
            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,0,0,1,1,1,1,0,0,1,1,1,0,0,1},
            {1,0,0,1,0,0,1,0,0,0,1,0,0,0,1},
            {1,0,0,1,0,0,1,0,0,0,1,0,0,0,1},
            {1,0,0,1,0,0,0,0,0,0,1,0,0,0,1},
            {1,0,0,1,0,0,0,0,0,0,1,0,0,1,1},
            {1,1,1,1,0,0,1,0,0,1,1,0,0,0,1},
            {1,0,0,1,0,0,1,0,0,1,0,0,0,0,1},
            {1,0,0,1,0,0,1,0,0,1,0,0,0,0,1},
            {1,0,0,1,0,0,1,0,0,1,1,1,0,0,1},
            {1,0,0,0,0,0,1,0,0,1,0,0,0,0,1},
            {1,0,0,0,0,0,1,0,0,1,0,0,0,0,1},
            {1,1,1,1,1,1,1,1,1,1,0,0,1,1,1},
            {1,0,0,0,0,0,1,0,0,0,0,0,1,0,1},
            {1,0,0,0,0,0,1,0,0,0,0,0,1,0,1},
            {1,0,0,1,0,0,0,0,0,1,1,1,1,0,1},
            {1,0,0,1,0,0,0,0,0,0,0,0,0,0,1},
            {1,0,0,1,1,1,1,0,0,0,0,0,0,0,1},
            {1,0,0,0,0,0,0,1,1,1,1,1,1,1,1},
            {1,0,0,0,0,0,0,0,0,0,1,0,0,0,1},
            {1,0,0,1,1,0,0,0,0,0,1,0,2,0,1},
            {1,0,0,0,1,0,0,1,0,0,0,0,0,0,1},
            {1,0,0,0,1,0,0,1,0,0,0,0,0,0,1},
            {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1}
    };

    private int row = 29;// the row number
    private int col = 15;// the column number


    public MazeMoveThread(MazeView mazeView){
        this.mazeView = mazeView;
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
            float dx = mazeView.ball.dx;
            float dy = mazeView.ball.dy;
            float x = mazeView.ball.curr_x;
            float y = mazeView.ball.curr_y;

//            //Determine whether the X direction hits the wall, if it hits the wall, restore
//            if(mazeView.ball.curr_x + dx >= 0 && mazeView.ball.curr_x + dx <= mazeView.getWidth() - mazeView.ball.width){
//                mazeView.ball.curr_x = mazeView.ball.curr_x + dx;
//            }
//            //Determine whether the Y direction hits the wall, if it hits the wall, restore
//            if(mazeView.ball.curr_y + dy >= 0 &&
//                    mazeView.ball.curr_y + dy <= mazeView.getHeight() - mazeView.ball.height){
//                mazeView.ball.curr_y = mazeView.ball.curr_y + dy;
//            }
            if(!isBarrier((int)x, (int)y, (int)dx, (int)dy)){
                mazeView.ball.curr_x = x + dx;
                mazeView.ball.curr_y = y + dy;
            }

//            System.out.println("x: " + mazeView.ball.curr_x);
//            System.out.println("y: " + mazeView.ball.curr_y);


            try
            {
                //the game fps is about to 30
                Thread.sleep(30);
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }

        }
    }

    /**
     * Determine whether the ball hits an obstacle
     *
     * @param x
     * @param y
     * @return
     */
    private Boolean isBarrier(int x, int y, int dx, int dy){
        float dCol = (float)mazeView.getWidth() / (float)col;
        float dRow = (float)mazeView.getHeight() / (float)row;

        //Upper left corner
        int m1 = (int)((x + dx) / dCol);
        int n1 = (int)((y + dy) / dRow);

        //Bottom left
        int m2 = (int)((x + dx) / dCol);
        int n2 = (int)((y + dy + mazeView.ball.height) / dRow);

        //Upper right corner
        int m3 = (int)((x + dx + mazeView.ball.width) / dCol);
        int n3 = (int)((y + dy) / dRow);

        //Bottom right corner
        int m4 = (int)((x + dx + mazeView.ball.width) / dCol);
        int n4 = (int)((y + dy + mazeView.ball.height) / dRow);;
        //Determine whether to arrive at the destination
        if(map[n1][m1] == 2 || map[n2][m2] == 2 || map[n3][m3] == 2 || map[n4][m4] == 2){
            mazeView.gameOver();
        }
        return map[n1][m1] == 1 || map[n2][m2] == 1 || map[n3][m3] == 1 || map[n4][m4] == 1;
    }

}
