package com.bydauto.myviewpager.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by byd_tw on 2017/11/22.
 */

public class MyImagesViewPager extends ViewPager {
    public MyImagesViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyImagesViewPager(Context context) {
        super(context);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        try {
            return super.onInterceptTouchEvent(ev);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
