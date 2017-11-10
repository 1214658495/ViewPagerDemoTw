package fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.byd.lighttextview.LightRadioButton;
import com.bydauto.myviewpager.Images;
import com.bydauto.myviewpager.R;
import com.bydauto.myviewpager.ServerConfig;

import java.util.List;

import adapter.MyFragmentPagerAdapter;
import adapter.PhotoWallAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by byd_tw on 2017/11/1.
 */

public class FragmentPlaybackList extends Fragment {
    @BindView(R.id.rg_groupDetail)
    RadioGroup rgGroupDetail;
    //    @BindView(R.id.vp_itemPreview)
//    NoScrollViewPager vpItemPreview;

    Unbinder unbinder;
    @BindView(R.id.rb_recordvideo)
    LightRadioButton rbRecordvideo;
    @BindView(R.id.rb_lockvideo)
    LightRadioButton rbLockvideo;
    @BindView(R.id.rb_capturephoto)
    LightRadioButton rbCapturephoto;
    @BindView(R.id.gv_dataList)
    GridView gvDataList;
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
//        fragments = new ArrayList<>();
//        fragments.add(new FragmentVideoDetail());
//        fragments.add(new FragmentVideoDetail());
//        fragments.add(new FragmentPhotoDetail());
//        myFragmentPagerAdapter = new MyFragmentPagerAdapter(getActivity().getSupportFragmentManager(), fragments);
//
//        vpItemPreview.setOffscreenPageLimit(2);
//        vpItemPreview.setAdapter(myFragmentPagerAdapter);

        rgGroupDetail.check(R.id.rb_recordvideo);

        rgGroupDetail.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.rb_recordvideo:
                        mAdapter.currentRadioButton = ServerConfig.RB_RECORD_VIDEO;
                        gvDataList.setNumColumns(1);
//                        mAdapter.notifyDataSetChanged();
//                        if (mAdapter != null) {
//                            mAdapter.clear();
//                            mAdapter.cancelAllTasks();
//                        }
                        Toast.makeText(getContext(),"recordvideo",Toast.LENGTH_SHORT).show();
//                        vpItemPreview.setCurrentItem(0, false);
                        break;
                    case R.id.rb_lockvideo:
                        mAdapter.currentRadioButton = ServerConfig.RB_LOCK_VIDEO;
                        gvDataList.setNumColumns(1);
//                        mAdapter.notifyDataSetChanged();
//                        if (mAdapter != null) {
//                            mAdapter.clear();
//                            mAdapter.cancelAllTasks();
//                        }
                        Toast.makeText(getContext(),"lockvideo",Toast.LENGTH_SHORT).show();
//                        vpItemPreview.setCurrentItem(1, false);
                        break;
                    case R.id.rb_capturephoto:
                        mAdapter.currentRadioButton = ServerConfig.RB_CAPTURE_PHOTO;
                        gvDataList.setNumColumns(3);
//                        if (mAdapter != null) {
//                            mAdapter.clear();
//                            mAdapter.cancelAllTasks();
//                        }
//                        mAdapter.notifyDataSetChanged();
//                        vpItemPreview.setCurrentItem(2, false);
                        break;
                    default:
                        break;
                }
                mAdapter.notifyDataSetChanged();
//                gvDataList.invalidateViews();
                gvDataList.setAdapter(mAdapter);

            }
        });

        mAdapter = new PhotoWallAdapter(getContext(), 0, Images.imageThumbUrls, gvDataList);
        gvDataList.setAdapter(mAdapter);
        gvDataList.setNumColumns(1);
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

