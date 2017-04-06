package com.instavenues.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.instavenues.R;
import com.instavenues.helper.AnimationHandler;
import com.instavenues.helper.StatusBarHandler;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by MelDiSooQi on 3/24/2017.
 */

public class SplashActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarHandler.ChangeStatusBarColor(this,R.color.colorPrimaryDark);
        setContentView(R.layout.activity_splash);

        ImageView logo = (ImageView) findViewById(R.id.logo);

        AnimationHandler animationHandler = new AnimationHandler();
        animationHandler.layerAnimationWithListener(this, logo, R.anim.fade_in);
        animationHandler.addObserver(new Observer() {
            @Override
            public void update(Observable observable, Object data) {
                openNewPage();
            }
        });

    }

    private void openNewPage()
    {
            Intent i = new Intent(this, GetStartedActivity.class);
            startActivity(i);
            AnimationHandler.pageAnimation(this, R.anim.fade_in, R.anim.fade_out);

            finish();
    }
}
