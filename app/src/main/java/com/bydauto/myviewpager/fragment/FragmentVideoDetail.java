package com.bydauto.myviewpager.fragment;

import android.media.AudioManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
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
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

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
    private SurfaceHolder surfaceHolder;
    private IjkMediaPlayer player;

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

        // 初始化播放器
        IjkMediaPlayer.loadLibrariesOnce(null);
        IjkMediaPlayer.native_profileBegin("libijkplayer.so");

        surfaceHolder = svVideoPlayView.getHolder();
//        surfaceHolder.setFixedSize(getActivity().getWindowManager().getDefaultDisplay()
//                .getWidth(), getActivity().getWindowManager().getDefaultDisplay().getWidth()
//                / 16 * 9);
        surfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                openVideo();
                player.start();
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {

            }
        });
    }

    public void openVideo(){
        release();

        try {
            player = new IjkMediaPlayer();

            player.setDataSource(url);
            player.setDisplay(surfaceHolder);

            player.setAudioStreamType(AudioManager.STREAM_MUSIC);
            player.setScreenOnWhilePlaying(true);
            player.prepareAsync();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void release() {
        if (player != null) {
            player.reset();
            player.release();
            player = null;
//            AudioManager am = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
//            am.abandonAudioFocus(null);
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        // activity 可见时尝试继续播放
        if (player != null){
            player.start();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        player.pause();
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
