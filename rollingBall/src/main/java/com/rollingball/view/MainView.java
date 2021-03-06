package com.rollingball.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.rollingball.Activity.GameActivity;
import com.rollingball.Activity.MainActivity;
import com.rollingball.Activity.MazeActivity;
import com.rollingball.R;
import com.rollingball.pojo.bitmap.MyBitMap;
import com.rollingball.thread.MainThread;

import java.util.ArrayList;


/***************************************************************************************************
 * Reference: Some the images are available at http://box.ptpress.com.cn/y/978-7-115-47555-8
 * Others are drawn by myself
 ***************************************************************************************************
 */
public class MainView extends SurfaceView implements SurfaceHolder.Callback {
    private Bitmap bm_background;
    private Bitmap bm_play1;
    private Bitmap bm_play2;
    private Bitmap bm_exit1;
    private Bitmap bm_exit2;
    private Bitmap bm_dodge1;
    private Bitmap bm_dodge2;
    private Bitmap bm_maze1;
    private Bitmap bm_maze2;

    public MainActivity activity;

    public MainThread mainThread;

    Paint bitmapPaint;//bitMap paint

    ArrayList<MyBitMap> menu = new ArrayList<>();
    ArrayList<Bitmap> up = new ArrayList<>();
    ArrayList<Bitmap> down = new ArrayList<>();

    private int w; //map width
    private int h; //map height
    private boolean isPlay = false;// Determine whether it is choosing a gameplay

    public MainView(Context context, AttributeSet attrs){
        super(context, attrs);
        isPlay = false;
        this.activity = (MainActivity)context;
        //Set the implementer of the lifecycle callback interface
        this.getHolder().addCallback(this);
        //Get the width and height of the screen
        Point point = new Point();
        activity.getWindowManager().getDefaultDisplay().getRealSize(point);
        w = point.x;
        h = point.y;

        //load static images
        bm_background = BitmapFactory.decodeResource(activity.getResources(), R.drawable.home);
        bm_background = Bitmap.createScaledBitmap(bm_background, w, h, true);

        bm_play1 = BitmapFactory.decodeResource(activity.getResources(), R.drawable.play1);
        up.add(bm_play1);
        bm_play2 = BitmapFactory.decodeResource(activity.getResources(), R.drawable.play2);
        down.add(bm_play2);
        MyBitMap play = new MyBitMap(bm_play1, (w - bm_play1.getWidth()) /2, 3 * bm_play1.getHeight() / 2);
        menu.add(play);

        bm_exit1 = BitmapFactory.decodeResource(activity.getResources(), R.drawable.exit1);
        up.add(bm_exit1);
        bm_exit2 = BitmapFactory.decodeResource(activity.getResources(), R.drawable.exit2);
        down.add(bm_exit2);
        MyBitMap exit = new MyBitMap(bm_exit1, (w - bm_exit1.getWidth()) /2, 6 * bm_play1.getHeight() / 2);
        menu.add(exit);

        bm_dodge1 = BitmapFactory.decodeResource(activity.getResources(), R.drawable.dodge1);
        up.add(bm_dodge1);
        bm_dodge2 = BitmapFactory.decodeResource(activity.getResources(), R.drawable.dodge2);
        down.add(bm_dodge2);
        MyBitMap dodge = new MyBitMap(bm_dodge1, (w - bm_dodge1.getWidth()) /2, 3 * bm_dodge1.getHeight() / 2);
        menu.add(dodge);

        bm_maze1 = BitmapFactory.decodeResource(activity.getResources(), R.drawable.maze1);
        up.add(bm_maze1);
        bm_maze2 = BitmapFactory.decodeResource(activity.getResources(), R.drawable.maze2);
        down.add(bm_maze2);
        MyBitMap maze = new MyBitMap(bm_maze1, (w - bm_maze1.getWidth()) /2, 6 * bm_maze1.getHeight() / 2);
        menu.add(maze);

        //get the instance of paint
        bitmapPaint = new Paint();
        bitmapPaint.setAntiAlias(true); // antiAlias

        //get the instance of thread
        mainThread = new MainThread(this);

    }


    /**
     * detects which button is touched when menu is shown
     * @param event
     * @return
     */
    public boolean onTouchEvent(MotionEvent event){
        float x = event.getX();
        float y = event.getY();

        //The menu is touched
        if(!isPlay){
            for (int i = 0; i < 2; i++){
                MyBitMap mbp = menu.get(i);
                if(x >= mbp.x && x <= (mbp.pic.getWidth() + mbp.x)
                        && y >= mbp.y && y <= (mbp.y + mbp.pic.getHeight())){
                    switch (event.getAction()){
                        case MotionEvent.ACTION_DOWN:
                            mbp.pic = down.get(i);
                            break;
                        case MotionEvent.ACTION_UP:
                            mbp.pic = up.get(i);
                            action(i);
                            break;
                        default:
                            break;
                    }
                }
                else if(MotionEvent.ACTION_UP == event.getAction()){
                    for(int j = 0; j < 2; j++){
                        mbp = menu.get(j);
                        mbp.pic = up.get(j);
                    }
                }
            }
        }else{
            for (int i = 2; i < menu.size(); i++){
                MyBitMap mbp = menu.get(i);
                if(x >= mbp.x && x <= (mbp.pic.getWidth() + mbp.x)
                        && y >= mbp.y && y <= (mbp.y + mbp.pic.getHeight())){
                    switch (event.getAction()){
                        case MotionEvent.ACTION_DOWN:
                            mbp.pic = down.get(i);
                            break;
                        case MotionEvent.ACTION_UP:
                            mbp.pic = up.get(i);
                            action(i);
                            break;
                        default:
                            break;
                    }
                }
                else if(MotionEvent.ACTION_UP == event.getAction()){
                    for(int j = 2; j < menu.size(); j++){
                        mbp = menu.get(j);
                        mbp.pic = up.get(j);
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
                //play button
                isPlay = true;
                break;
            case 1:
                //exit button
                //pause the thread
                mainThread.onThreadPause();
                intent = new Intent(activity,MainActivity.class);
                intent.putExtra("exit", true);
                activity.startActivity(intent);
                break;
            case 2:
                //dodge button
                mainThread.onThreadPause();
                intent = new Intent(activity, GameActivity.class);
                activity.startActivity(intent);
                activity.finish();
                break;
            case 3:
                //maze button
                mainThread.onThreadPause();
                intent = new Intent(activity, MazeActivity.class);
                activity.startActivity(intent);
                activity.finish();
                break;

            default:
                break;
        }
    }

    public void onMyDraw(Canvas canvas)
    {
        //draw background map
        canvas.drawBitmap(bm_background, 0, 0, bitmapPaint);
        //draw the menu elements
        if(!isPlay){
            for(int i = 0; i < 2; i++){
                MyBitMap mbp = menu.get(i);
                canvas.drawBitmap(mbp.pic, mbp.x, mbp.y, bitmapPaint);
            }
        }else{
            for(int i = 2; i < menu.size(); i++){
                MyBitMap mbp = menu.get(i);
                canvas.drawBitmap(mbp.pic, mbp.x, mbp.y, bitmapPaint);
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
        mainThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

    }
}
