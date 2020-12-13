package com.rollingball.view;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import com.rollingball.Activity.MainActivity;
import com.rollingball.Activity.MazeActivity;
import com.rollingball.R;
import com.rollingball.cons.GameData;
import com.rollingball.pojo.bitmap.MyBitMap;
import com.rollingball.pojo.collisionBody.Ball;
import com.rollingball.thread.MazeDrawThread;
import com.rollingball.thread.MazeMoveThread;


/***************************************************************************************************
 * Reference: Some the images are available at http://box.ptpress.com.cn/y/978-7-115-47555-8
 * Others are drawn by myself
 ***************************************************************************************************
 */
public class MazeView extends SurfaceView implements SurfaceHolder.Callback {

    public Bitmap bm_background;
    public Bitmap bm_barrier;
    public Bitmap bm_ball;
    private Bitmap bm_des;
    private Bitmap bm_next1;
    private Bitmap bm_next2;
    private Bitmap bm_restart1;
    private Bitmap bm_restart2;
    private Bitmap bm_main_menu1;
    private Bitmap bm_main_menu2;
    private Bitmap bm_continue1;
    private Bitmap bm_continue2;

    private Bitmap bm_congratulation;


    private MazeActivity mazeActivity;

    public MazeDrawThread mazeDrawThread;
    public MazeMoveThread mazeMoveThread;

    private boolean isPause = false;
    private boolean isOver = false;

    public Ball ball = new Ball(0, 0, GameData.BALL_NORMAL_SPEED);

    public int w; //map width
    public int h; //map height

    Paint bitmapPaint;//bitMap paint

    private MyBitMap[] pauseMenu = new MyBitMap[2];//Menu items that need to be drawn when game is paused
    private MyBitMap[] overMenu = new MyBitMap[3];//Menu items that need to be drawn at the end of the game
    private Bitmap[] up = new Bitmap[5];
    private Bitmap[] down = new Bitmap[5];

    public MazeView(MazeActivity activity){
        super(activity);
        this.mazeActivity = activity;
        //Set the implementer of the lifecycle callback interface
        this.getHolder().addCallback(this);

        //Get the width and height of the screen
        Point point = new Point();
        activity.getWindowManager().getDefaultDisplay().getRealSize(point);
        w = point.x;
        h = point.y;

        //load static images
        //Draw a background image to cover the screen
        bm_background = BitmapFactory.decodeResource(activity.getResources(), R.drawable.back);
        bm_background = Bitmap.createScaledBitmap(bm_background, w, h, true);

        bm_barrier = BitmapFactory.decodeResource(activity.getResources(), R.drawable.maze);
        bm_barrier = Bitmap.createScaledBitmap(bm_barrier, w, h, true);

        bm_des = BitmapFactory.decodeResource(activity.getResources(), R.drawable.destination);

        bm_main_menu1 = BitmapFactory.decodeResource(activity.getResources(), R.drawable.main_menu1);
        up[0] = bm_main_menu1;
        up[2] = bm_main_menu1;
        bm_main_menu2 = BitmapFactory.decodeResource(activity.getResources(), R.drawable.main_menu2);
        down[0] = bm_main_menu2;
        down[2] = bm_main_menu2;
        MyBitMap tmp = new MyBitMap(bm_main_menu1, (w - bm_main_menu1.getWidth()) /2, (h + 2 * bm_main_menu1.getHeight()) / 2);
        pauseMenu[0] = tmp;
        tmp = new MyBitMap(bm_main_menu1, (w - bm_main_menu1.getWidth()) /2, (h + 3 * bm_main_menu1.getHeight()) / 2);
        overMenu[0] = tmp;

        bm_continue1 = BitmapFactory.decodeResource(activity.getResources(), R.drawable.continue1);
        up[1] = bm_continue1;
        bm_continue2 = BitmapFactory.decodeResource(activity.getResources(), R.drawable.continue2);
        down[1] = bm_continue2;
        tmp = new MyBitMap(bm_continue1, (w - bm_continue1.getWidth()) /2, (h - 2 * bm_continue1.getHeight()) / 2);
        pauseMenu[1] = tmp;

        bm_restart1 = BitmapFactory.decodeResource(activity.getResources(), R.drawable.restart1);
        up[3] = bm_restart1;
        bm_restart2 = BitmapFactory.decodeResource(activity.getResources(), R.drawable.restart2);
        down[3] = bm_restart2;
        tmp = new MyBitMap(bm_restart1, (w - bm_restart1.getWidth()) /2, h / 2);
        overMenu[1] = tmp;

        bm_next1 = BitmapFactory.decodeResource(activity.getResources(), R.drawable.next1);
        up[4] = bm_next1;
        bm_next2 = BitmapFactory.decodeResource(activity.getResources(), R.drawable.next2);
        down[4] = bm_next2;
        tmp = new MyBitMap(bm_next1, (w - bm_next1.getWidth()) /2, (h - 3 * bm_next1.getHeight()) / 2);
        overMenu[2] = tmp;

        //Congratulations to players when they arrive at the destination
        bm_congratulation = BitmapFactory.decodeResource(activity.getResources(), R.drawable.congratulation);

        bm_ball = BitmapFactory.decodeResource(activity.getResources(), R.drawable.ball_normal);

        //get the instance of paint
        bitmapPaint = new Paint();
        bitmapPaint.setAntiAlias(true); // antiAlias

        init();

        //get the instance of thread
        mazeDrawThread = new MazeDrawThread(this);
        mazeMoveThread = new MazeMoveThread(this);

    }

    /**
     * init the game
     */
    public void init(){

        //set the initial position of ball
        ball = new Ball(0, 0, GameData.BALL_NORMAL_SPEED);
        ball.width = bm_ball.getWidth();
        ball.height = bm_ball.getHeight();
        ball.curr_x = w * 2 / 15f;
        ball.curr_y = h / 29f;

        //clear the flag
        isOver = false;
        isPause = false;

    }

    /**
     * switch game status
     */
    public void switchStatus(){
        if (!isOver){
            if(isPause){
                continueGame();
            }else{
                pauseGame();
            }
        }
    }

    private void continueGame(){
        mazeMoveThread.onThreadResume();
        isPause = false;
    }

    private void pauseGame(){
        mazeMoveThread.onThreadPause();
        isPause = true;
    }

    /**
     * game is over
     */
    public void gameOver(){
        mazeMoveThread.onThreadPause();
        isPause = true;
        isOver = true;
    }

    public void onMyDraw(Canvas canvas)
    {
        //draw background map
        canvas.drawBitmap(bm_background, 0, 0, bitmapPaint);

        //draw the barriers
        canvas.drawBitmap(bm_barrier, 0, 0, bitmapPaint);

        //draw destination
        canvas.drawBitmap(bm_des, getWidth() * 12 / 15, getHeight() * 25 / 29, bitmapPaint);

        //draw ball
        canvas.drawBitmap(bm_ball, ball.curr_x, ball.curr_y, bitmapPaint);

        //if game stops
        if(isPause){
            //if game is over
            if(isOver){
                canvas.drawBitmap(bm_congratulation, (w - bm_congratulation.getWidth() + 32) / 2, (h - 3 * bm_restart1.getHeight() - 2 * bm_congratulation.getHeight()) / 2, bitmapPaint);
                for(int i = 0; i < overMenu.length; i++){
                    canvas.drawBitmap(overMenu[i].pic, overMenu[i].x, overMenu[i].y, bitmapPaint);
                }
            }else{
                for(int i = 0; i < pauseMenu.length; i++){
                    canvas.drawBitmap(pauseMenu[i].pic, pauseMenu[i].x, pauseMenu[i].y, bitmapPaint);
                }
            }
        }

    }

    /**
     * detects which button is touched when menu is shown
     * @param event
     * @return
     */
    public boolean onTouchEvent(MotionEvent event){
        float x = event.getX();
        float y = event.getY();
        //overMenu[i]
        if(isPause){
            //the game over menu is touched
            if(isOver){
                for (int i = 0; i < overMenu.length; i++){
                    if(x >= overMenu[i].x && x <= (overMenu[i].pic.getWidth() + overMenu[i].x)
                            && y >= overMenu[i].y && y <= (overMenu[i].y + overMenu[i].pic.getHeight())){
                        switch (event.getAction()){
                            case MotionEvent.ACTION_DOWN:
                                overMenu[i].pic = down[i + 2];
                                break;
                            case MotionEvent.ACTION_UP:
                                overMenu[i].pic = up[i + 2];
                                action(i + 2);
                                break;
                            default:
                                break;
                        }
                    }else if(MotionEvent.ACTION_UP == event.getAction()){
                        for(int j = 0; j < overMenu.length; j++){
                            overMenu[j].pic = up[j + 2];
                        }
                    }
                }
            }else{
                for (int i = 0; i < pauseMenu.length; i++){
                    if(x >= pauseMenu[i].x && x <= (pauseMenu[i].pic.getWidth() + pauseMenu[i].x)
                            && y >= pauseMenu[i].y && y <= (pauseMenu[i].y + pauseMenu[i].pic.getHeight())){
                        switch (event.getAction()){
                            case MotionEvent.ACTION_DOWN:
                                pauseMenu[i].pic = down[i];
                                break;
                            case MotionEvent.ACTION_UP:
                                pauseMenu[i].pic = up[i];
                                action(i);
                                break;
                            default:
                                break;
                        }
                    }else if(MotionEvent.ACTION_UP == event.getAction()){
                        for(int j = 0; j < pauseMenu.length; j++){
                            pauseMenu[j].pic = up[j];
                        }
                    }
                }
            }

        }

        return true;
    }

    /**
     * take action when finger is up after touching a button
     * @param id
     */
    private void action(int id){
        Intent intent;
        switch (id){
            case 0:
            case 2:
                //main menu button
                intent = new Intent(mazeActivity, MainActivity.class);
                mazeActivity.startActivity(intent);
                mazeActivity.finish();
                break;
            case 1:
                //continue button
                continueGame();
                break;
            case 3:
                //restart button
                init();
                mazeMoveThread.interrupt();
                mazeMoveThread = new MazeMoveThread(this);
                mazeMoveThread.start();
                break;
            case 4:
                //next stage button
                Toast.makeText(mazeActivity, "Developing!!!", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {//Called when created
        Canvas canvas = surfaceHolder.lockCanvas();//get the instance of canvas
        try{
            synchronized(surfaceHolder){
                onMyDraw(canvas);//begin to draw
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        finally{
            if(canvas != null){
                surfaceHolder.unlockCanvasAndPost(canvas);
            }
        }
        //start the thread
        mazeMoveThread.start();
        mazeDrawThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

    }
}
