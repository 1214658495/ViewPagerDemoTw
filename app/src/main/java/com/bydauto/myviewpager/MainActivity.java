package com.bydauto.myviewpager;

//import android.app.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.byd.lighttextview.LightButton;

import java.util.ArrayList;
import java.util.List;

import com.bydauto.myviewpager.adapter.MyFragmentPagerAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.bydauto.myviewpager.fragment.FragmentPlaybackList;
import com.bydauto.myviewpager.fragment.FragmentRTVideo;
import com.bydauto.myviewpager.fragment.FragmentSetting;
import com.bydauto.myviewpager.view.MyDialog;
import com.bydauto.myviewpager.view.NoScrollViewPager;

/**
 * @author byd_tw
 */
public class MainActivity extends AppCompatActivity {

    @BindView(R.id.rb_realTimeVideo)
    RadioButton rbRealTimeVideo;
    @BindView(R.id.rb_playbackList)
    RadioButton rbPlaybackList;
    @BindView(R.id.rb_setting)
    RadioButton rbSetting;
    @BindView(R.id.rg_group)
    RadioGroup rgGroup;
    @BindView(R.id.vp_main)
    NoScrollViewPager vpMain;
    @BindView(R.id.btn_back)
    LightButton btnBack;
//    @BindView(R.id.btn_test)
//    LightButton btnTest;
    //    @BindView(R.id.vp)
//    ViewPager vp;
//    private ArrayList<ImageView> imageLists;
    private MyFragmentPagerAdapter myFragmentPagerAdapter;
    private List<Fragment> fragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        fragments = new ArrayList<>();
        fragments.add(new FragmentRTVideo());
        fragments.add(new FragmentPlaybackList());
        fragments.add(new FragmentSetting());
        myFragmentPagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), fragments);

        vpMain.setAdapter(myFragmentPagerAdapter);
        rgGroup.check(R.id.rb_realTimeVideo);

        rgGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.rb_realTimeVideo:
                        vpMain.setCurrentItem(0, false);
                        break;
                    case R.id.rb_playbackList:
                        vpMain.setCurrentItem(1, false);
                        break;
                    case R.id.rb_setting:
                        vpMain.setCurrentItem(2, false);
                        break;
                    default:
                        break;
                }
            }
        });

        vpMain.setOffscreenPageLimit(2);
//        initData();
//        vp.setAdapter(new MyPagerAdapter());
    }

    @OnClick(R.id.btn_back)
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                MyDialog myDialog = MyDialog.newInstance(0);
                myDialog.show(getFragmentManager(), "back");
                break;
//            case R.id.btn_test:
//                MyDialog myDialogTest = MyDialog.newInstance(1);
//                myDialogTest.show(getFragmentManager(), "test");
//                break;
            default:
                break;
        }
    }




    /*private void initData() {
        int[] imageResIDs = {R.mipmap.ic_launcher,
                R.mipmap.ic_launcher_round, R.mipmap.ic_launcher};
        imageLists = new ArrayList<>();
        for (int imageResID : imageResIDs) {
            ImageView imageView = new ImageView(this);
            imageView.setBackgroundResource(imageResID);
            imageLists.add(imageView);
        }
    }*/


   /* public class MyPagerAdapter extends PagerAdapter {


        @Override
        public int getCount() {
            return imageLists.size();
        }

        @Override
        public boolean isViewFromObject(View com.bydauto.myviewpager.view, Object object) {
            return com.bydauto.myviewpager.view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(imageLists.get(position));
            return imageLists.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

    }*/
}
