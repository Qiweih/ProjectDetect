package com.beacon.projectdetect.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.beacon.projectdetect.R;
import com.google.firebase.FirebaseApp;

/**
 * Created by qiwhuang on 4/4/2017.
 */

public class SplashActivity extends Activity{
    public static String BeaconId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        //initialize  firebase
        FirebaseApp.initializeApp(this);

        // Wait for 3 sec before start
        Thread timerThread = new Thread(){
            public void run(){
                try{
                    sleep(3000);
                }catch(InterruptedException e){
                    e.printStackTrace();
                }finally{
                    Intent intent = new Intent(SplashActivity.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        };
        timerThread.start();

    }
    @Override
    public void onStart(){
        super.onStart();
    }

    @Override
    public void onRestart(){
        super.onRestart();
    }

    @Override
    public void onResume(){
        super.onResume();
    }

    @Override
    public void onStop(){
        super.onStop();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

}
