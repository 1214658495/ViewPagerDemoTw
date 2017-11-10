package fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.byd.lighttextview.LightCheckBox;
import com.byd.lighttextview.LightRadioButton;
import com.byd.lighttextview.LightTextView;
import com.bydauto.myviewpager.Images;
import com.bydauto.myviewpager.R;
import com.bydauto.myviewpager.ServerConfig;

import java.util.List;

import adapter.MyFragmentPagerAdapter;
import adapter.PhotoWallAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by byd_tw on 2017/11/1.
 */

public class FragmentPlaybackList extends Fragment implements AdapterView.OnItemLongClickListener, AdapterView.OnItemClickListener {
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

    @BindView(R.id.btn_cancel)
    LightCheckBox btnCancel;
    @BindView(R.id.btn_share)
    LightCheckBox btnShare;
    @BindView(R.id.btn_export)
    LightCheckBox btnExport;
    @BindView(R.id.btn_delete)
    LightCheckBox btnDelete;
    @BindView(R.id.btn_selectall)
    LightCheckBox btnSelectall;
    @BindView(R.id.ll_editItemBar)
    LinearLayout llEditItemBar;
    @BindView(R.id.tv_editNav)
    LightTextView tvEditNav;
    @BindView(R.id.rl_menu_edit)
    RelativeLayout rlMenuEdit;
    @BindView(R.id.iv_line_blowMenuEdit)
    ImageView ivLineBlowMenuEdit;
    private List<Fragment> fragments;
    private MyFragmentPagerAdapter myFragmentPagerAdapter;
    private PhotoWallAdapter mAdapter;
    public boolean isMultiChoose = false;


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
                        Toast.makeText(getContext(), "recordvideo", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(getContext(), "lockvideo", Toast.LENGTH_SHORT).show();
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
        gvDataList.setOnItemClickListener(this);
        gvDataList.setOnItemLongClickListener(this);
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

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
        if (isMultiChoose) {

        } else {
            isMultiChoose = true;
            llEditItemBar.setVisibility(View.VISIBLE);
            rgGroupDetail.setVisibility(View.INVISIBLE);
            switch (mAdapter.currentRadioButton) {
                case ServerConfig.RB_RECORD_VIDEO:
                    tvEditNav.setText("记录视频");
                    break;
                case ServerConfig.RB_LOCK_VIDEO:
                    tvEditNav.setText("锁定视频");
                    break;
                case ServerConfig.RB_CAPTURE_PHOTO:
                    tvEditNav.setText("抓拍照片");
                    break;
                default:
                    break;
            }

        }
        return false;
    }

    @OnClick({R.id.btn_cancel, R.id.btn_share, R.id.btn_export, R.id.btn_delete, R.id.btn_selectall})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_cancel:
                llEditItemBar.setVisibility(View.INVISIBLE);
                rgGroupDetail.setVisibility(View.VISIBLE);
                isMultiChoose = false;
                break;
            case R.id.btn_share:
                break;
            case R.id.btn_export:
                break;
            case R.id.btn_delete:
                break;
            case R.id.btn_selectall:
                break;
            default:
                break;
        }
    }
}

