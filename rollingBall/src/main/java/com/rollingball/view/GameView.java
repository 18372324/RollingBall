package com.rollingball.view;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.rollingball.Activity.GameActivity;
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
 * Reference: All the images are available at http://box.ptpress.com.cn/y/978-7-115-47555-8
 ***************************************************************************************************
 */
public class GameView extends SurfaceView implements SurfaceHolder.Callback {
    public Bitmap bm_background;
    public Bitmap bm_ball_normal;
    public Bitmap bm_ball_slow;
    public Bitmap bm_turret;
    public Bitmap bm_bullet_damage;
    public Bitmap bm_bullet_slow;
    public Bitmap bm_topBar;
    private Bitmap bm_restart1;
    private Bitmap bm_restart2;

    public GameActivity activity;

    BallThread ballThread;
    public DrawThread drawThread;
    BulletThread bulletThread;

    private boolean isPause = false;
    private boolean isOver = false;

    Paint bitmapPaint;//bitMap paint
    Paint textPaint;  //paint for text

    public Ball ball = new Ball(0, 0, GameData.BALL_NORMAL_SPEED);

    public ArrayList<Turret> turrets = new ArrayList<>();//a collection of turrets
    public ArrayList<Bullet> bullets = new ArrayList<>();//a collection of all kinds of bullet

    public ArrayList<MyBitMap> overMenu = new ArrayList<>();//Menu items that need to be drawn at the end of the game

    public int w; //map width
    public int h; //map height

    public int score = 0;   // current score
    int highest = 100;      // the highest score stored in local

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

        bm_turret = BitmapFactory.decodeResource(activity.getResources(), R.drawable.turret_down);

        bm_bullet_damage = BitmapFactory.decodeResource(activity.getResources(), R.drawable.bullet_damage);
        bm_bullet_slow = BitmapFactory.decodeResource(activity.getResources(), R.drawable.bullet_slow);

        bm_restart1 = BitmapFactory.decodeResource(activity.getResources(), R.drawable.restart1);
        bm_restart2 = BitmapFactory.decodeResource(activity.getResources(), R.drawable.restart2);
        MyBitMap restart = new MyBitMap(bm_restart1, (w - bm_restart1.getWidth()) /2, (h - bm_restart1.getHeight()) / 2);
        overMenu.add(restart);

        //set the initial position of ball
        ball.width = bm_ball_normal.getWidth();
        ball.height = bm_ball_normal.getHeight();
        ball.curr_x = (w - ball.width) / 2;
        ball.curr_y = (h - ball.width) / 2;

        //get the instance of paint
        bitmapPaint = new Paint();
        textPaint = new Paint();
        textPaint.setTextSize(50);
        textPaint.setColor(Color.BLACK);
        textPaint.setStyle(Paint.Style.FILL);

        bitmapPaint.setAntiAlias(true); // antiAlias

        //get the instance of thread
        ballThread = new BallThread(this);
        drawThread = new DrawThread(this);
        bulletThread = new BulletThread(this);

    }

    /**
     * game is over
     */
    public void gameOver(){
        ballThread.onThreadPause();
        bulletThread.onThreadPause();
        isPause = true;
        isOver = true;
    }

    public void onMyDraw(Canvas canvas)
    {
        //draw background map
        canvas.drawBitmap(bm_background, 0, 0, bitmapPaint);
        canvas.drawBitmap(bm_topBar, 0, bm_turret.getHeight(), bitmapPaint);
        //draw text
        String str_score = "Score: " + score;
        canvas.drawText(str_score, textPaint.getTextSize(), textPaint.getTextSize() * 1.2f, textPaint);
        String str_highest = "Highest: " + highest;
        canvas.drawText(str_highest, w - str_highest.length() * textPaint.getTextSize() / 2, textPaint.getTextSize() * 1.2f, textPaint);
        //draw turrets and bullets
        for (int i = 0; i < turrets.size(); i++){
            Turret t = turrets.get(i);
            canvas.drawBitmap(bm_turret, t.curr_x, t.curr_y, bitmapPaint);
        }
        for (int i = 0; i < bullets.size(); i++){
            Bullet b = bullets.get(i);
            canvas.drawBitmap(b.type == Bullet.TYPE_DAMAGE ? bm_bullet_damage : bm_bullet_slow, b.curr_x, b.curr_y, bitmapPaint);
        }
        //draw ball
        canvas.drawBitmap(ball.type == Ball.BALL_NORMAL ? bm_ball_normal : bm_ball_slow, ball.curr_x, ball.curr_y, bitmapPaint);
        //if game stops
        if(isPause){
            //if game is over
            if(isOver){
                for(int i = 0; i < overMenu.size(); i++){
                    MyBitMap mbp = overMenu.get(i);
                    canvas.drawBitmap(mbp.pic, mbp.x, mbp.y, bitmapPaint);
                }
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
        //start the threads
        ballThread.start();

        drawThread.start();

        bulletThread.start();
    }

    /**
     * detects which button is touched when menu is shown
     * @param event
     * @return
     */
    public boolean onTouchEvent(MotionEvent event){
        float x = event.getX();
        float y = event.getY();

        if(isOver){
            //the game over menu is touched
            for (int i = 0; i < overMenu.size(); i++){
                MyBitMap mbp = overMenu.get(i);
                if(x >= mbp.x && x <= (mbp.pic.getWidth() + mbp.x)
                        && y >= mbp.y && y <= (mbp.y + mbp.pic.getHeight())){
                    switch (event.getAction()){
                        case MotionEvent.ACTION_DOWN:
                            mbp.pic = bm_restart2;
                            break;
                        case MotionEvent.ACTION_UP:
                            mbp.pic = bm_restart1;
                            break;
                        default:
                            break;
                    }
                }
            }
        }

        return true;
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

    }
}
