package com.bydauto.myviewpager;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.bydauto.myviewpager.fragment.FragmentPhotoSlide;
import com.bydauto.myviewpager.view.MyImagesViewPager;

import java.util.ArrayList;

//import com.bydauto.mylistphotoview.view.MyImagesViewPager;

/**Simple TouchGallery demo based on ViewPager and Photoview.
 * Created by Trojx on 2016/1/3.
 */
public class ActivityViewPager extends AppCompatActivity {

    private MyImagesViewPager viewPager;
    private TextView tv_indicator;
    private ArrayList<String> urlList;

    private int currentItem;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().getDecorView().setSystemUiVisibility(View.INVISIBLE);
        setContentView(R.layout.layout_viewpager);

//        String[] urls={"http://7xla0x.com1.z0.glb.clouddn.com/picJarvanIV_0.jpg",
//                "http://7xla0x.com1.z0.glb.clouddn.com/picJarvanIV_1.jpg",
//                "http://7xla0x.com1.z0.glb.clouddn.com/picJarvanIV_2.jpg",
//                "http://7xla0x.com1.z0.glb.clouddn.com/picJarvanIV_3.jpg",
//                "http://7xla0x.com1.z0.glb.clouddn.com/picJarvanIV_4.jpg",
//                "http://7xla0x.com1.z0.glb.clouddn.com/picJarvanIV_5.jpg",
//                "http://7xla0x.com1.z0.glb.clouddn.com/picJarvanIV_6.jpg",
//                "http://7xla0x.com1.z0.glb.clouddn.com/picJarvanIV_0.jpg",
//                "http://7xla0x.com1.z0.glb.clouddn.com/picJarvanIV_1.jpg",
//                "http://7xla0x.com1.z0.glb.clouddn.com/picJarvanIV_2.jpg",
//                "http://7xla0x.com1.z0.glb.clouddn.com/picJarvanIV_3.jpg",
//                "http://7xla0x.com1.z0.glb.clouddn.com/picJarvanIV_4.jpg",
//                "http://7xla0x.com1.z0.glb.clouddn.com/picJarvanIV_5.jpg",
//                "http://7xla0x.com1.z0.glb.clouddn.com/picJarvanIV_6.jpg",
//                "http://7xla0x.com1.z0.glb.clouddn.com/picJarvanIV_0.jpg",
//                "http://7xla0x.com1.z0.glb.clouddn.com/picJarvanIV_1.jpg",
//                "http://7xla0x.com1.z0.glb.clouddn.com/picJarvanIV_2.jpg",
//                "http://7xla0x.com1.z0.glb.clouddn.com/picJarvanIV_3.jpg",
//                "http://7xla0x.com1.z0.glb.clouddn.com/picJarvanIV_4.jpg",
//                "http://7xla0x.com1.z0.glb.clouddn.com/picJarvanIV_5.jpg",
//                "http://7xla0x.com1.z0.glb.clouddn.com/picJarvanIV_6.jpg"};

        urlList = new ArrayList<>();
//        Collections.addAll(urlList, urls);

        Intent intent = getIntent();
        urlList = intent.getStringArrayListExtra("mImgUrlsList");
        currentItem = intent.getIntExtra("position", 0);

        viewPager =  findViewById(R.id.viewpager);
        tv_indicator = findViewById(R.id.tv_indicator);

        viewPager.setAdapter(new PictureSlidePagerAdapter(getSupportFragmentManager()));
        viewPager.setCurrentItem(currentItem);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                tv_indicator.setText(String.valueOf(position+1)+"/"+urlList.size());
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
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

    private  class PictureSlidePagerAdapter extends FragmentStatePagerAdapter {

        public PictureSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return FragmentPhotoSlide.newInstance(urlList.get(position));
        }

        @Override
        public int getCount() {
            return urlList.size();
        }
    }
}
