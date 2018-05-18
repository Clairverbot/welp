package com.example.clair.welp;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableResource;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SplashScreen extends FragmentActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        ImageView imageView= findViewById(R.id.ivSplash);

        Glide.with(this)
                .load(R.drawable.welp)
                .into(imageView);

        Thread splashThread= new Thread(){
            public void run(){
                try {
                    sleep(6000);
                }  catch(InterruptedException e) {
                    e.printStackTrace();
                } finally
                {
                    // Launch the MainActivity class
                    Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                    startActivity(intent);
                }

            }
        };

        splashThread.start();
    }
}
