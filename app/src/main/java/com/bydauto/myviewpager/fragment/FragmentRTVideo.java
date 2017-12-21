package com.bydauto.myviewpager.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;

import com.bydauto.myviewpager.R;
import com.bydauto.myviewpager.RemoteCam;
import com.bydauto.myviewpager.connectivity.IFragmentListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by byd_tw on 2017/11/1.
 */

public class FragmentRTVideo extends Fragment {

    @BindView(R.id.btn_rt_capture_photo)
    ImageButton btnRtCapturePhoto;
    @BindView(R.id.iv_rt_lock_video)
    ImageButton ivRtLockVideo;
    @BindView(R.id.sv_recordVideo)

    SurfaceView svRecordVideo;
    Unbinder unbinder;

    private String url = "rtsp://192.168.42.1/live";
//    private String url = "rtsp://192.168.42.1/tmp/SD0/EVENT/2017-11-28-19-09-56.MP4" ;
//    private SurfaceHolder surfaceHolder;
//    private IjkMediaPlayer player;

    private IFragmentListener mListener;
    private CheckBox ivRtRecordVideo;
    private CheckBox ivRtRecordVoice;
    private RemoteCam mRemoteCam;
    private boolean isRecord;
    private boolean isMicOn;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rtvideo, container, false);
        ivRtRecordVideo = view.findViewById(R.id.iv_rt_record_video);
        ivRtRecordVoice = view.findViewById(R.id.iv_rt_record_voice);
        unbinder = ButterKnife.bind(this, view);
//        initData();
        return view;
    }


    @Override
    public void onAttach(Activity activity) {
        Log.e("CAM_Commands:", "onAttach");
        super.onAttach(activity);
        try {
            mListener = (IFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement IFragmentListener");
        }
    }

    public void setRemoteCam(RemoteCam mRemoteCam) {
        this.mRemoteCam = mRemoteCam;
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
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.iv_rt_record_video, R.id.btn_rt_capture_photo, R.id.iv_rt_lock_video, R.id.iv_rt_record_voice})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_rt_record_video:
                if (mListener != null) {
                    isRecord = !isRecord;
                    mListener.onFragmentAction(IFragmentListener.ACTION_RECORD_START, isRecord);
                }
                break;
            case R.id.btn_rt_capture_photo:
                if (mListener != null) {
                    mListener.onFragmentAction(IFragmentListener.ACTION_PHOTO_START, null);
                }
                break;
            case R.id.iv_rt_lock_video:
                break;
            case R.id.iv_rt_record_voice:
                if (mListener != null) {
                    isMicOn =!isMicOn;
                    mListener.onFragmentAction(IFragmentListener.ACTION_MIC_ON, isMicOn);
                }
                break;
            default:
                break;
        }
    }

    public void setRecordState(boolean isOn) {
        isRecord = isOn;
        ivRtRecordVideo.setChecked(!isOn);
    }

    public void setMicState(boolean isOn) {
        isMicOn = isOn;
        ivRtRecordVoice.setChecked(!isOn);
    }
}
