package fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.RadioGroup;

import com.bydauto.myviewpager.Images;
import com.bydauto.myviewpager.R;

import java.util.ArrayList;
import java.util.List;

import adapter.MyFragmentPagerAdapter;
import adapter.PhotoWallAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import view.NoScrollViewPager;

/**
 * Created by byd_tw on 2017/11/1.
 */

public class FragmentPlayback extends Fragment {
    @BindView(R.id.rg_groupDetail)
    RadioGroup rgGroupDetail;
    @BindView(R.id.vp_itemPreview)
    NoScrollViewPager vpItemPreview;
    @BindView(R.id.gv_dataList)
    GridView gvDataList;
    Unbinder unbinder;
    private List<Fragment> fragments;
    private MyFragmentPagerAdapter myFragmentPagerAdapter;
    private PhotoWallAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_playback, container, false);
        unbinder = ButterKnife.bind(this, view);
        initdata();
        return view;

    }

    private void initdata() {
        //新建fragment集合对象，传递给FragmentPagerAdapter
        fragments = new ArrayList<>();
        fragments.add(new FragmentVideoPreview());
        fragments.add(new FragmentVideoPreview());
        fragments.add(new FragmentPhotoPreview());
        myFragmentPagerAdapter = new MyFragmentPagerAdapter(getActivity().getSupportFragmentManager(), fragments);

        vpItemPreview.setOffscreenPageLimit(2);
        vpItemPreview.setAdapter(myFragmentPagerAdapter);

        rgGroupDetail.check(R.id.rb_recordvideo);

        rgGroupDetail.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.rb_recordvideo:
                        vpItemPreview.setCurrentItem(0, false);
                        break;
                    case R.id.rb_lockvideo:
                        vpItemPreview.setCurrentItem(1, false);
                        break;
                    case R.id.rb_capturephoto:
                        vpItemPreview.setCurrentItem(2, false);
                        break;
                    default:
                        break;
                }
            }
        });

//        datalists = new ArrayList<>();
//        for (int i = 0; i < 45; i++) {
//            datalists.add("item" + i);
//        }
        mAdapter = new PhotoWallAdapter(getContext(), 0, Images.imageThumbUrls, gvDataList);
        gvDataList.setAdapter(mAdapter);
    }

    @Override
    public void onPause() {
        super.onPause();
        mAdapter.fluchCache();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        // 退出程序时结束所有的下载任务
        mAdapter.cancelAllTasks();
    }

}

