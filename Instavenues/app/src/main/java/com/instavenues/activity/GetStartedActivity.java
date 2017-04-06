package com.instavenues.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.instavenues.R;
import com.instavenues.helper.AnimationHandler;
import com.instavenues.helper.StatusBarHandler;

/**
 * Created by MelDiSooQi on 3/24/2017.
 */

public class GetStartedActivity extends AppCompatActivity
{
    private Button letsGo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarHandler.ChangeStatusBarColor(this,R.color.colorPrimaryDark);
        setContentView(R.layout.activity_getstarted);

        letsGo = (Button) findViewById(R.id.btn_letsGo);
        letsGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openNewPage();
            }
        });
    }

    private void openNewPage()
    {
            Intent i = new Intent(this, HomeActivity.class);
            startActivity(i);
            AnimationHandler.pageAnimation(this, R.anim.fade_in, R.anim.fade_out);

            finish();
    }
}
