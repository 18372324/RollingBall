package com.rollingball.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.rollingball.R;
import com.rollingball.view.GameView;

public class GameActivity extends AppCompatActivity {

    SensorManager sensorManager;// Manager
    Sensor sensor;              //sensor

    GameView gameView;

    //implements the SensorEvenListener interface
    private SensorEventListener sensorEventListener = new SensorEventListener(){
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {}
        @Override
        public void onSensorChanged(SensorEvent event) {
            /**
             *  X left -> positive   right -> negative
             *  Y front -> negative   back -> positive
             *  Z  up -> negative   down -> positive
             */
            //Calculate the projection direction of gravity on the screen
            float[] values = event.values;
            //Convert the value of the X-axis of the acceleration sensor to the X component of the ball motion speed
            gameView.ball.dx = -(int)(values[0] * gameView.ball.baseSpeed);
            //Convert the value of the Y-axis of the acceleration sensor to the Y component of the ball motion speed
            gameView.ball.dy = (int)(values[1] * gameView.ball.baseSpeed);
        }
    };

    /**
     * hide Navigation bar, status bar (for better game experience)
     * @param activity
     */
    public static void NavigationBarStatusBar(Activity activity){
        if (Build.VERSION.SDK_INT >= 19) {
            View decorView = activity.getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }


    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            gameView.switchStatus();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Set to portrait mode
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        NavigationBarStatusBar(this);
        //**************************sensor begin**************************
        //get the object of SensorManager
        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);//TYPE_ORIENTATION
        gameView = new GameView(this);
        gameView.requestFocus();// get the focus
        gameView.setFocusableInTouchMode(true); // can be touched
        this.setContentView(gameView);
    }

    @Override
    protected void onResume() {             //override the method onResume
        sensorManager.registerListener(     //register the Listener
            sensorEventListener,            //listener object
            sensor,                         //the sensor type
            SensorManager.SENSOR_DELAY_GAME //Frequency of sensor event delivery (SENSOR_DELAY_GAME = 52Hz for game app)
        );
        //resume the thread
        gameView.drawThread.onThreadResume();
        gameView.ballThread.onThreadResume();
        gameView.bulletThread.onThreadResume();
        super.onResume();
    }

    @Override
    protected void onPause() {
        sensorManager.unregisterListener(sensorEventListener);	//unregister the listener
        //pause the thread
        gameView.drawThread.onThreadPause();
        gameView.ballThread.onThreadPause();
        gameView.bulletThread.onThreadPause();
        super.onPause();
    }

}