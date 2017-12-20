package com.bydauto.myviewpager.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bydauto.myviewpager.R;
import com.bydauto.myviewpager.RemoteCam;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by byd_tw on 2017/12/20.
 */

public class FragmentLoading extends Fragment {
    private static final String TAG = "FragmentLoading";

    @BindView(R.id.iv_logo_byd)
    ImageView ivLogoByd;
    private Unbinder unbinder;
    private RemoteCam mRemoteCam;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_loading, container, false);
        unbinder = ButterKnife.bind(this, view);
//        initData();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        connect();
    }

    private void connect() {
        mRemoteCam.startSession();
    }

    public void setRemoteCam(RemoteCam remoteCam) {
        this.mRemoteCam = remoteCam;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
