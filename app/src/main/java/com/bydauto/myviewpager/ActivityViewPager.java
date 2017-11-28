package com.bydauto.myviewpager;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bydauto.myviewpager.view.MyDialog;
import com.bydauto.myviewpager.view.MyImagesViewPager;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by byd_tw on 2017/11/28.
 */

public class ActivityViewPager extends AppCompatActivity {
    private static final String TAG = "ActivityViewPager";
    @BindView(R.id.vp_viewPager)
    MyImagesViewPager vpViewPager;
    @BindView(R.id.tv_vpIndex)
    TextView tvVpIndex;
    @BindView(R.id.btn_back_to_gridview)
    ImageButton btnBackToGridview;
    @BindView(R.id.tv_title_photo)
    TextView tvTitlePhoto;
    @BindView(R.id.rl_bar_showTitle)
    RelativeLayout rlBarShowTitle;
    @BindView(R.id.btn_share_preview)
    ImageButton btnSharePreview;
    @BindView(R.id.btn_delete_preview)
    ImageButton btnDeletePreview;
    @BindView(R.id.btn_zoom)
    ImageButton btnZoom;
    @BindView(R.id.ll_bar_editPhoto)
    LinearLayout llBarEditPhoto;

    private MyImagesPagerAdapter myImagesPagerAdapter;

    private ArrayList<String> urlList;

    private int currentItem;

    private static final int FADE_OUT = 1;

    public class MyHandler extends Handler {
        private WeakReference<ActivityViewPager> mActivityViewPager;

        MyHandler(ActivityViewPager activityViewPager) {
            mActivityViewPager = new WeakReference<>(activityViewPager);

        }

        @Override
        public void handleMessage(Message msg) {
//            ActivityViewPager activityViewPager = mActivityViewPager.get();
            super.handleMessage(msg);
            switch (msg.what) {
                case FADE_OUT:
                    if (rlBarShowTitle.getVisibility() == View.VISIBLE) {
                        rlBarShowTitle.setVisibility(View.INVISIBLE);
                    }

                    if (llBarEditPhoto.getVisibility() == View.VISIBLE) {
                        llBarEditPhoto.setVisibility(View.INVISIBLE);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private final MyHandler mHandler = new MyHandler(this);


//    private Handler mHandler = new Handler(){
//
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            switch (msg.what) {
//                case FADE_OUT:
//                    if (rlBarShowTitle.getVisibility() == View.VISIBLE) {
//                        rlBarShowTitle.setVisibility(View.INVISIBLE);
//                    }
//
//                    if (llBarEditPhoto.getVisibility() == View.VISIBLE) {
//                        llBarEditPhoto.setVisibility(View.INVISIBLE);
//                    }
//                    break;
//                    default:
//                        break;
//            }
//        }
//    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.layout_viewpager);
        ButterKnife.bind(this);
        initData();
    }

    private void initData() {
        urlList = new ArrayList<>();

        Intent intent = getIntent();
        urlList = intent.getStringArrayListExtra("mImgUrlsList");
        currentItem = intent.getIntExtra("position", 0);
        myImagesPagerAdapter = new MyImagesPagerAdapter(urlList, this);
        vpViewPager.setAdapter(myImagesPagerAdapter);
        vpViewPager.setCurrentItem(currentItem, false);
        tvVpIndex.setText(currentItem + 1 + "/" + urlList.size());
        vpViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                currentItem = position;
                tvVpIndex.setText(currentItem + 1 + "/" + urlList.size());
            }
        });


        Message msg = mHandler.obtainMessage(FADE_OUT);
        mHandler.removeMessages(FADE_OUT);
        mHandler.sendMessageDelayed(msg, 3000);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus && Build.VERSION.SDK_INT >= 19) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    @OnClick({R.id.btn_back_to_gridview, R.id.btn_share_preview, R.id.btn_delete_preview, R.id.btn_zoom})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_back_to_gridview:
                this.finish();
                break;
            case R.id.btn_share_preview:
                break;
            case R.id.btn_delete_preview:
                MyDialog myDialog = MyDialog.newInstance(0,"确认删除？");
                myDialog.show(getFragmentManager(), "delete");
                myDialog.setOnDialogButtonClickListener(new MyDialog.OnDialogButtonClickListener() {
                    @Override
                    public void okButtonClick() {
                        Log.e(TAG, "okButtonClick: ");
                    }

                    @Override
                    public void cancelButtonClick() {
                        Log.e(TAG, "cancelButtonClick: ");
                    }
                });
                break;
            case R.id.btn_zoom:
                break;
            default:
                break;
        }
    }

    public class MyImagesPagerAdapter extends PagerAdapter {

        private ArrayList<String> imageUrls;
        private AppCompatActivity activity;

        public MyImagesPagerAdapter(ArrayList<String> imageUrls, AppCompatActivity activity) {
            this.imageUrls = imageUrls;
            this.activity = activity;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            String url = imageUrls.get(position);
            PhotoView photoView = new PhotoView(activity);
            Glide.with(activity)
                    .load(url)
                    .placeholder(R.mipmap.ic_launcher)
                    .into(photoView);
            container.addView(photoView);
//        photoView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
////                activity.finish();
////                Log.e(TAG, "onClick: photoView.setOnClickListener");
//            }
//        });
            photoView.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
                @Override
                public void onPhotoTap(View view, float v, float v1) {
                    showTitleBar();
                }

                @Override
                public void onOutsidePhotoTap() {
                    showTitleBar();
                }
            });
            return photoView;
        }

        void showTitleBar() {
            //                    如下考虑算法优化
            if (rlBarShowTitle.getVisibility() == View.VISIBLE) {
                rlBarShowTitle.setVisibility(View.INVISIBLE);
            } else {
                rlBarShowTitle.setVisibility(View.VISIBLE);
            }

            if (llBarEditPhoto.getVisibility() == View.VISIBLE) {
                llBarEditPhoto.setVisibility(View.INVISIBLE);
            } else {
                llBarEditPhoto.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public int getCount() {
            return imageUrls != null ? imageUrls.size() : 0;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }
    }
}
