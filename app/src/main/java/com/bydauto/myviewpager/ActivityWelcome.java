package com.bydauto.myviewpager;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 *
 * @author byd_tw
 * @date 2017/12/21
 */

public class ActivityWelcome extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: 2017/12/21 firstLanuch
        setContentView(R.layout.layout_welcome);
        startMainActivity();
    }

    private void startMainActivity() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(ActivityWelcome.this,MainActivity.class));
                ActivityWelcome.this.finish();
            }
        },600);
    }
}
