package com.example.textrecognizer;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;

import com.mikhaellopez.circularimageview.CircularImageView;

public class SplashScreen extends AppCompatActivity {

    //For duration setting of progressbar
    private ProgressBar progressBar;
    private int progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //To convert into fullscreen activity and to remove title bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash_screen);

        progressBar = (ProgressBar) findViewById(R.id.progressBarId);

        //Handling progressbar duration
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                doWork();
                startmain();
            }
        });
        thread.start();

        //Creating circular imageview
        CircularImageView circularImageView = (CircularImageView)findViewById(R.id.imageIv);
        // Set Border
        circularImageView.setBorderColor(getResources().getColor(R.color.border));
        circularImageView.setBorderWidth(10);
    }
    //For changing the value of progressbar in milli seconds
    public void doWork(){
        for(progress =   0; progress <= 100 ; progress+=1){
            try {
                Thread.sleep(50);
                progressBar.setProgress(progress);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    public void startmain(){
        Intent intent = new Intent(SplashScreen.this,MainActivity.class);
        startActivity(intent);
        finish();
    }
    //For exiting from the app
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }
}
