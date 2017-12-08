package com.bydauto.myviewpager.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;

import com.bydauto.myviewpager.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by byd_tw on 2017/11/1.
 */

public class FragmentRTVideo extends Fragment {
    @BindView(R.id.iv_rt_record_video)
    CheckBox ivRtRecordVideo;
    @BindView(R.id.btn_rt_capture_photo)
    ImageButton btnRtCapturePhoto;
    @BindView(R.id.iv_rt_lock_video)
    ImageButton ivRtLockVideo;
    @BindView(R.id.iv_rt_record_voice)
    CheckBox ivRtRecordVoice;
    @BindView(R.id.sv_recordVideo)
    SurfaceView svRecordVideo;
    Unbinder unbinder;

    private String url = "rtsp://192.168.42.1/live" ;
//    private String url = "rtsp://192.168.42.1/tmp/SD0/EVENT/2017-11-28-19-09-56.MP4" ;
//    private SurfaceHolder surfaceHolder;
//    private IjkMediaPlayer player;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rtvideo, container, false);
        unbinder = ButterKnife.bind(this, view);
//        initData();
        return view;
    }

//    private void initData() {
//
//        // 初始化播放器
//        IjkMediaPlayer.loadLibrariesOnce(null);
//        IjkMediaPlayer.native_profileBegin("libijkplayer.so");
//
//        surfaceHolder = svRecordVideo.getHolder();
////        surfaceHolder.setFixedSize(getActivity().getWindowManager().getDefaultDisplay()
////                .getWidth(), getActivity().getWindowManager().getDefaultDisplay().getWidth()
////                / 16 * 9);
//        surfaceHolder.addCallback(new SurfaceHolder.Callback() {
//            @Override
//            public void surfaceCreated(SurfaceHolder holder) {
//                openVideo();
//                player.start();
//            }
//
//            @Override
//            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
//
//            }
//
//            @Override
//            public void surfaceDestroyed(SurfaceHolder holder) {
//
//            }
//        });
//    }
//
//    public void openVideo(){
//        release();
//
//        try {
//            player = new IjkMediaPlayer();
//
//            player.setDataSource(url);
//            player.setDisplay(surfaceHolder);
//
//            player.setAudioStreamType(AudioManager.STREAM_MUSIC);
//            player.setScreenOnWhilePlaying(true);
//            player.prepareAsync();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    public void release() {
//        if (player != null) {
//            player.reset();
//            player.release();
//            player = null;
////            AudioManager am = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
////            am.abandonAudioFocus(null);
//        }
//    }
//
//    @Override
//    public void onResume(){
//        super.onResume();
//        // activity 可见时尝试继续播放
//        if (player != null){
//            player.start();
//        }
//    }
//
//    @Override
//    public void onStop() {
//        super.onStop();
//        player.pause();
//    }
//
//    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
//        unbinder.unbind();
//    }
//
//    @OnClick({R.id.iv_rt_record_video, R.id.btn_rt_capture_photo, R.id.iv_rt_lock_video, R.id.iv_rt_record_voice})
//    public void onViewClicked(View view) {
//        switch (view.getId()) {
//            case R.id.iv_rt_record_video:
//                break;
//            case R.id.btn_rt_capture_photo:
//                break;
//            case R.id.iv_rt_lock_video:
//                break;
//            case R.id.iv_rt_record_voice:
//                break;
//                default:
//                    break;
//        }
//    }
}
