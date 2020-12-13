package com.rollingball.view;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Typeface;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.rollingball.Activity.GameActivity;
import com.rollingball.Activity.MainActivity;
import com.rollingball.R;
import com.rollingball.cons.GameData;
import com.rollingball.pojo.bitmap.MyBitMap;
import com.rollingball.pojo.collisionBody.Ball;
import com.rollingball.pojo.collisionBody.Bullet;
import com.rollingball.pojo.collisionBody.Turret;
import com.rollingball.thread.BallThread;
import com.rollingball.thread.BulletThread;
import com.rollingball.thread.DrawThread;

import java.util.ArrayList;

/***************************************************************************************************
 * Reference: Some the images are available at http://box.ptpress.com.cn/y/978-7-115-47555-8
 * Others are drawn by myself
 ***************************************************************************************************
 */
public class GameView extends SurfaceView implements SurfaceHolder.Callback {
    private Bitmap bm_background;
    public Bitmap bm_ball_normal;
    public Bitmap bm_ball_slow;
    public Bitmap[] bm_turrets = new Bitmap[4];
    public Bitmap bm_bullet_damage;
    public Bitmap bm_bullet_slow;
    public Bitmap bm_topBar;
    private Bitmap bm_pause1;
    private Bitmap bm_pause2;
    private MyBitMap pause;
    private Bitmap bm_restart1;
    private Bitmap bm_restart2;
    private Bitmap bm_main_menu1;
    private Bitmap bm_main_menu2;
    private Bitmap bm_continue1;
    private Bitmap bm_continue2;
    private Bitmap bm_over;
    private Bitmap bm_prompt;

    private Bitmap[] bm_counter = new Bitmap[3];

    public GameActivity activity;

    public BallThread ballThread;
    public DrawThread drawThread;
    public BulletThread bulletThread;

    private boolean isPause = false;
    private boolean isOver = false;

    Paint bitmapPaint;//bitMap paint
    Paint textPaint;  //paint for text

    public Ball ball = new Ball(0, 0, GameData.BALL_NORMAL_SPEED);

    public ArrayList<Turret> turrets = new ArrayList<>();//a collection of turrets
    public ArrayList<Bullet> bullets = new ArrayList<>();//a collection of all kinds of bullet

    private MyBitMap[] pauseMenu = new MyBitMap[3];//Menu items that need to be drawn when game is paused
    private MyBitMap[] overMenu = new MyBitMap[2];//Menu items that need to be drawn at the end of the game
    private Bitmap[] up = new Bitmap[4];
    private Bitmap[] down = new Bitmap[4];

    public int w; //map width
    public int h; //map height
    public int top;

    public int score = 0;   // current score
    public int level = 1;   // current level (the number of turret)
    int highest = 100;      // the highest score stored in local

    public int countdown = 2999; // 3s countdown before the game starts
    public int promptTime = 1200; // the time for showing prompt

    public GameView(GameActivity activity){
        super(activity);
        this.activity = activity;
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
        bm_topBar = BitmapFactory.decodeResource(activity.getResources(), R.drawable.top);
        bm_topBar = Bitmap.createScaledBitmap(bm_topBar, w, bm_topBar.getHeight(), true);

        bm_ball_normal = BitmapFactory.decodeResource(activity.getResources(), R.drawable.ball_normal);
        bm_ball_slow = BitmapFactory.decodeResource(activity.getResources(), R.drawable.ball_slow);

        bm_turrets[0] = BitmapFactory.decodeResource(activity.getResources(), R.drawable.turret_down);
        bm_turrets[1] = BitmapFactory.decodeResource(activity.getResources(), R.drawable.turret_up);
        bm_turrets[2] = BitmapFactory.decodeResource(activity.getResources(), R.drawable.turret_right);
        bm_turrets[3] = BitmapFactory.decodeResource(activity.getResources(), R.drawable.turret_left);

        bm_bullet_damage = BitmapFactory.decodeResource(activity.getResources(), R.drawable.bullet_damage);
        bm_bullet_slow = BitmapFactory.decodeResource(activity.getResources(), R.drawable.bullet_slow);

        bm_counter[0] = BitmapFactory.decodeResource(activity.getResources(), R.drawable.counter1);
        bm_counter[1] = BitmapFactory.decodeResource(activity.getResources(), R.drawable.counter2);
        bm_counter[2] = BitmapFactory.decodeResource(activity.getResources(), R.drawable.counter3);

        bm_pause1 = BitmapFactory.decodeResource(activity.getResources(), R.drawable.pause1);
        up[0] = bm_pause1;
        bm_pause2 = BitmapFactory.decodeResource(activity.getResources(), R.drawable.pause2);
        down[0] = bm_pause2;
        pause = new MyBitMap(bm_pause1, (w - bm_pause1.getWidth()) /2, bm_pause1.getHeight() / 10);

        top = pause.y * 2 + bm_pause1.getHeight() + bm_topBar.getHeight();

        bm_over = BitmapFactory.decodeResource(activity.getResources(), R.drawable.over);

        bm_restart1 = BitmapFactory.decodeResource(activity.getResources(), R.drawable.restart1);
        up[1] = bm_restart1;
        bm_restart2 = BitmapFactory.decodeResource(activity.getResources(), R.drawable.restart2);
        down[1] = bm_restart2;
        MyBitMap tmp = new MyBitMap(bm_restart1, (w - bm_restart1.getWidth()) /2, (h - 3 * bm_restart1.getHeight()) / 2);
        pauseMenu[0] = tmp;
        tmp = new MyBitMap(bm_restart1, (w - bm_restart1.getWidth()) /2, (h - bm_restart1.getHeight()) / 2);
        overMenu[0] = tmp;

        bm_main_menu1 = BitmapFactory.decodeResource(activity.getResources(), R.drawable.main_menu1);
        up[2] = bm_main_menu1;
        bm_main_menu2 = BitmapFactory.decodeResource(activity.getResources(), R.drawable.main_menu2);
        down[2] = bm_main_menu2;
        tmp = new MyBitMap(bm_main_menu1, (w - bm_main_menu1.getWidth()) /2, (h + 3 * bm_main_menu1.getHeight()) / 2);
        pauseMenu[1] = tmp;
        tmp = new MyBitMap(bm_main_menu1, (w - bm_main_menu1.getWidth()) /2, (h + 2 * bm_main_menu1.getHeight()) / 2);
        overMenu[1] = tmp;

        bm_continue1 = BitmapFactory.decodeResource(activity.getResources(), R.drawable.continue1);
        up[3] = bm_continue1;
        bm_continue2 = BitmapFactory.decodeResource(activity.getResources(), R.drawable.continue2);
        down[3] = bm_continue2;
        tmp = new MyBitMap(bm_continue1, (w - bm_continue1.getWidth()) /2, h / 2);
        pauseMenu[2] = tmp;

        bm_prompt = BitmapFactory.decodeResource(activity.getResources(), R.drawable.levelup);

        //get the instance of paint
        bitmapPaint = new Paint();
        bitmapPaint.setAntiAlias(true); // antiAlias

        //set font
        Typeface tf = Typeface.createFromAsset(activity.getAssets(), "font/baloo.ttf");
        textPaint = new Paint();
        textPaint.setTypeface(tf);
        textPaint.setTextSize(70);
        textPaint.setColor(Color.BLACK);
        textPaint.setStyle(Paint.Style.FILL);

        init();

        //get the instance of thread
        ballThread = new BallThread(this);
        drawThread = new DrawThread(this);
        bulletThread = new BulletThread(this);

    }

    /**
     * init the game
     */
    public void init(){
        countdown = 2999;
        promptTime = 1200;

        //set the initial position of ball
        ball = new Ball(0, 0, GameData.BALL_NORMAL_SPEED);
        ball.width = bm_ball_normal.getWidth();
        ball.height = bm_ball_normal.getHeight();
        ball.curr_x = (w - ball.width) / 2;
        ball.curr_y = (h - ball.width) / 2;

        score = 0;          // current score
        level = 1;          // current level
        //read from local file
        SharedPreferences pref = activity.getSharedPreferences("hscore", Context.MODE_PRIVATE);
        highest = pref.getInt("score", 0);    // the highest score stored in local

        //clear the collection of bullets and turrets
        turrets.clear();
        bullets.clear();

        //clear the flag
        isOver = false;
        isPause = false;

    }

    /**
     * game is over
     */
    public void gameOver(){
        ballThread.onThreadPause();
        bulletThread.onThreadPause();
        isPause = true;
        isOver = true;
        //if the current score is greater than highest
        if(score > highest){
            SharedPreferences.Editor editor = activity.getSharedPreferences("hscore", Context.MODE_PRIVATE).edit();
            editor.putInt("score", score);
            editor.apply();
        }
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
        ballThread.onThreadResume();
        bulletThread.onThreadResume();
        isPause = false;
    }

    private void pauseGame(){
        ballThread.onThreadPause();
        bulletThread.onThreadPause();
        isPause = true;
    }

    public void onMyDraw(Canvas canvas)
    {
        //draw background map
        canvas.drawBitmap(bm_background, 0, 0, bitmapPaint);
        //draw pause button
        canvas.drawBitmap(pause.pic, pause.x, pause.y, bitmapPaint);

        // draw top bar
        canvas.drawBitmap(bm_topBar, 0, top - bm_topBar.getHeight() / 2, bitmapPaint);

        //draw text
        String str_score = "Score: " + score;
        canvas.drawText(str_score, 10, pause.y + bm_pause1.getHeight() * 2 / 3f, textPaint);
        String str_highest = "Highest: " + highest;
        canvas.drawText(str_highest, w - (str_highest.length() + 2) * 30, pause.y + bm_pause1.getHeight() * 2 / 3f, textPaint);

        //draw ball
        canvas.drawBitmap(ball.type == Ball.BALL_NORMAL ? bm_ball_normal : bm_ball_slow, ball.curr_x, ball.curr_y, bitmapPaint);

        //draw counter
        if(countdown > 0){
            canvas.drawBitmap(bm_counter[countdown / 1000], (w - bm_counter[countdown / 1000].getWidth() + 64) / 2, (h - bm_counter[countdown / 1000].getHeight()) / 2, bitmapPaint);
        }else{
            //draw turrets and bullets
            for (int i = 0; i < turrets.size(); i++){
                Turret t = turrets.get(i);
                canvas.drawBitmap(t.pic, t.curr_x, t.curr_y, bitmapPaint);
            }
            for (int i = 0; i < bullets.size(); i++){
                Bullet b = bullets.get(i);
                canvas.drawBitmap(b.type == Bullet.TYPE_DAMAGE ? bm_bullet_damage : bm_bullet_slow, b.curr_x, b.curr_y, bitmapPaint);
            }

            //if game stops
            if(isPause){
                //if game is over
                if(isOver){
                    canvas.drawBitmap(bm_over, (w - bm_over.getWidth() + 32) / 2, (h - 2 * bm_restart1.getHeight() - 2 * bm_over.getHeight()) / 2, bitmapPaint);
                    for(int i = 0; i < overMenu.length; i++){
                        canvas.drawBitmap(overMenu[i].pic, overMenu[i].x, overMenu[i].y, bitmapPaint);
                    }
                }else{
                    for(int i = 0; i < pauseMenu.length; i++){
                        canvas.drawBitmap(pauseMenu[i].pic, pauseMenu[i].x, pauseMenu[i].y, bitmapPaint);
                    }
                }
            }
            if(promptTime > 0){
                //draw the prompt
                canvas.drawBitmap(bm_prompt, (w - bm_prompt.getWidth()) / 2, overMenu[1].y, bitmapPaint);
            }
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
        //start the drawing thread
        drawThread.start();
    }

    /**
     * detects which button is touched when menu is shown
     * @param event
     * @return
     */
    public boolean onTouchEvent(MotionEvent event){
        float x = event.getX();
        float y = event.getY();
        // if pause button is clicked
        if(x >= pause.x && x <= (pause.pic.getWidth() + pause.x)
                && y >= pause.y && y <= (pause.y + pause.pic.getHeight())){
            switch (event.getAction()){
                case MotionEvent.ACTION_DOWN:
                    pause.pic = down[0];
                    break;
                case MotionEvent.ACTION_UP:
                    pause.pic = up[0];
                    action(0);
                    break;
                default:
                    break;
            }
        }else if(MotionEvent.ACTION_UP == event.getAction()){
            pause.pic = up[0];
        }
        //overMenu[i]
        if(isPause){
            //the game over menu is touched
            if(isOver){
                for (int i = 0; i < overMenu.length; i++){
                    if(x >= overMenu[i].x && x <= (overMenu[i].pic.getWidth() + overMenu[i].x)
                            && y >= overMenu[i].y && y <= (overMenu[i].y + overMenu[i].pic.getHeight())){
                        switch (event.getAction()){
                            case MotionEvent.ACTION_DOWN:
                                overMenu[i].pic = down[i + 1];
                                break;
                            case MotionEvent.ACTION_UP:
                                overMenu[i].pic = up[i + 1];
                                action(i + 1);
                                break;
                            default:
                                break;
                        }
                    }else if(MotionEvent.ACTION_UP == event.getAction()){
                        for(int j = 0; j < overMenu.length; j++){
                            overMenu[j].pic = up[j + 1];
                        }
                        pause.pic = up[0];
                    }
                }
            }else{
                for (int i = 0; i < pauseMenu.length; i++){
                    if(x >= pauseMenu[i].x && x <= (pauseMenu[i].pic.getWidth() + pauseMenu[i].x)
                            && y >= pauseMenu[i].y && y <= (pauseMenu[i].y + pauseMenu[i].pic.getHeight())){
                        switch (event.getAction()){
                            case MotionEvent.ACTION_DOWN:
                                pauseMenu[i].pic = down[i + 1];
                                break;
                            case MotionEvent.ACTION_UP:
                                pauseMenu[i].pic = up[i + 1];
                                action(i + 1);
                                break;
                            default:
                                break;
                        }
                    }else if(MotionEvent.ACTION_UP == event.getAction()){
                        for(int j = 0; j < pauseMenu.length; j++){
                            pauseMenu[j].pic = up[j + 1];
                        }
                        pause.pic = up[0];
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
                //pause button
                switchStatus();
                break;
            case 1:
                //restart button
                init();
                ballThread.interrupt();
                ballThread = new BallThread(this);
                bulletThread.interrupt();
                bulletThread = new BulletThread(this);
//                ballThread.onThreadResume();
//                bulletThread.onThreadResume();
                break;
            case 2:
                //main menu button
                intent = new Intent(activity, MainActivity.class);
                activity.startActivity(intent);
                activity.finish();
                break;
            case 3:
                //continue button
                continueGame();
                break;
            default:
                break;
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

    }
}
