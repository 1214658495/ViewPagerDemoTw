package com.byd.vtdr2.view;

import android.view.View;

import java.util.Calendar;

/**
 * Created by byd_tw on 2018/3/12.
 */

public abstract class NoMultiClickListener implements View.OnClickListener {
    private static final int MINI_CLICK_DELAY = 1000;
    private long lastClickTime = 0;
    @Override
    public void onClick(View view) {
        long currentTime = Calendar.getInstance().getTimeInMillis();
        if (currentTime - lastClickTime > MINI_CLICK_DELAY) {
            lastClickTime = currentTime;
            onNoMultiClick(view);
        }

    }

    private void onNoMultiClick(View view) {
    }
}
