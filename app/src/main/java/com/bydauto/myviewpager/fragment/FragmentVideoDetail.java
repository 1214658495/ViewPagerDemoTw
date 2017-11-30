package com.bydauto.myviewpager.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bydauto.myviewpager.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by byd_tw on 2017/11/1.
 */

public class FragmentVideoDetail extends Fragment {


    Unbinder unbinder;
    @BindView(R.id.sv_videoPlayView)
    SurfaceView svVideoPlayView;
    @BindView(R.id.ib_playVideo)
    ImageButton ibPlayVideo;
    @BindView(R.id.tv_test)
    TextView tvTest;

    private ArrayList<ImageView> imageLists;
    private ArrayList<String> urlsList;
    private String url;

    public static FragmentVideoDetail newInstance(ArrayList<String> urlsList, String url) {
        FragmentVideoDetail fragmentVideoDetail = new FragmentVideoDetail();
        Bundle bundle = new Bundle();
        bundle.putStringArrayList("urlsList", urlsList);
        bundle.putString("url", url);
        fragmentVideoDetail.setArguments(bundle);
        return fragmentVideoDetail;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        urlsList = getArguments().getStringArrayList("urlsList");
        url = getArguments().getString("url");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frament_video_detail, container, false);
        unbinder = ButterKnife.bind(this, view);
        initData();
        return view;
    }

    private void initData() {
        tvTest.setText(url);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();

    }


    @OnClick(R.id.ib_playVideo)
    public void onViewClicked() {
    }
}
