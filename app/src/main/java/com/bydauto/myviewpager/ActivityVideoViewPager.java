package com.bydauto.myviewpager;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bydauto.myviewpager.fragment.FragmentVideoDetail;
import com.bydauto.myviewpager.view.MyViewPager;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by byd_tw on 2017/11/30.
 */

public class ActivityVideoViewPager extends AppCompatActivity {
    @BindView(R.id.vp_videoViewPager)
    MyViewPager vpVideoViewPager;
    @BindView(R.id.btn_back_to_videoGridview)
    ImageButton btnBackToVideoGridview;
    @BindView(R.id.tv_title_video)
    TextView tvTitleVideo;
    @BindView(R.id.tv_vpVideoIndex)
    TextView tvVpVideoIndex;
    @BindView(R.id.rl_bar_showVideoTitle)
    RelativeLayout rlBarShowVideoTitle;
    @BindView(R.id.btn_share_VideoPreview)
    ImageButton btnShareVideoPreview;
    @BindView(R.id.btn_delete_VideoPreview)
    ImageButton btnDeleteVideoPreview;
    @BindView(R.id.btn_VideoZoom)
    ImageButton btnVideoZoom;
    @BindView(R.id.ll_bar_editVideo)
    LinearLayout llBarEditVideo;

    private ArrayList<String> urlsList;

    private int currentItem;
    private static final int FADE_OUT = 1;

    public class MyHandler extends Handler {
        private WeakReference<ActivityVideoViewPager> mActivityViewPager;

        MyHandler(ActivityVideoViewPager activityVideoViewPager) {
            mActivityViewPager = new WeakReference<>(activityVideoViewPager);

        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case FADE_OUT:
                    if (rlBarShowVideoTitle.getVisibility() == View.VISIBLE) {
                        rlBarShowVideoTitle.setVisibility(View.INVISIBLE);
                    }

                    if (llBarEditVideo.getVisibility() == View.VISIBLE) {
                        llBarEditVideo.setVisibility(View.INVISIBLE);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private final MyHandler mHandler = new MyHandler(this);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_video_viewpager);
        ButterKnife.bind(this);
        initData();
    }

    private void initData() {
        urlsList = new ArrayList<>();

        Intent intent = getIntent();
        urlsList = intent.getStringArrayListExtra("mUrlsList");
        currentItem = intent.getIntExtra("position", 0);

        vpVideoViewPager.setAdapter(new VideoDetailFragmentPagerAdapter(getSupportFragmentManager()));
        vpVideoViewPager.setCurrentItem(currentItem);
        vpVideoViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        Message msg = mHandler.obtainMessage(FADE_OUT);
        mHandler.removeMessages(FADE_OUT);
        mHandler.sendMessageDelayed(msg, 3000);
    }

    @OnClick({R.id.btn_back_to_videoGridview, R.id.btn_share_VideoPreview, R.id.btn_delete_VideoPreview, R.id.btn_VideoZoom})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_back_to_videoGridview:
                break;
            case R.id.btn_share_VideoPreview:
                break;
            case R.id.btn_delete_VideoPreview:
                break;
            case R.id.btn_VideoZoom:
                break;
            default:
                break;
        }
    }

    private class VideoDetailFragmentPagerAdapter extends FragmentStatePagerAdapter {
        public VideoDetailFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return FragmentVideoDetail.newInstance(urlsList, urlsList.get(position));
        }

        @Override
        public int getCount() {
            return urlsList.size();
        }
    }

}
