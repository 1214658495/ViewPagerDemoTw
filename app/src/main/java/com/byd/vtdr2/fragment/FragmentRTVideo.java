package com.byd.vtdr2.fragment;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import com.byd.vtdr2.MainActivity;
import com.byd.vtdr2.R;
import com.byd.vtdr2.RemoteCam;
import com.byd.vtdr2.ServerConfig;
import com.byd.vtdr2.connectivity.IFragmentListener;
import com.byd.vtdr2.view.AddSingleButtonDialog;
import com.pili.pldroid.player.AVOptions;
import com.pili.pldroid.player.PLMediaPlayer;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by byd_tw on 2017/11/1.
 */

public class FragmentRTVideo extends Fragment {
    private static final String TAG = "FragmentRTVideo";
    private static final int MESSAGE_ID_RECONNECTING = 0x01;
    public static ImageButton btnRtCapturePhoto;
    public static ImageButton ivRtLockVideo;
    private static ImageView ivRtRecordVideo;
    private static ImageView ivRtRecordVoice;
    Unbinder unbinder;
    private ProgressBar loadingView;
    private static TextClock textClock;
    private static TextView tvCheckSdCard;
    private String url = "rtsp://" + ServerConfig.VTDRIP + "/live";
    private AVOptions mAVOptions;
    private PLMediaPlayer mMediaPlayer;
    private IFragmentListener mListener;
    private SurfaceView svRecordVideo;
    private static ImageView ivIcRecord;
    private FrameLayout flShotView;
    private static RemoteCam mRemoteCam;
    private static boolean isRecord;
    private static boolean isMicOn;
    private Toast mToast;
    private boolean mIsStopped;
    private int seconds;
    private static final int MINI_CLICK_DELAY = 2000;
    private static long lastClickTime = 0;
    public static long lastClickTime2 = 0;
    private static long lastClickTime3 = 0;
    private AddSingleButtonDialog addSingleButtonDialog;
    private FragmentTransaction fragmentTransaction;


    public static FragmentRTVideo newInstance() {
        FragmentRTVideo fragmentRTVideo = new FragmentRTVideo();
        return fragmentRTVideo;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.e(TAG, "onCreateView: ");
        View view = inflater.inflate(R.layout.fragment_rtvideo, container, false);
        unbinder = ButterKnife.bind(this, view);
        btnRtCapturePhoto = view.findViewById(R.id.btn_rt_capture_photo);
        ivRtLockVideo = view.findViewById(R.id.iv_rt_lock_video);
        ivRtRecordVideo = view.findViewById(R.id.iv_rt_record_video);
        ivRtRecordVoice = view.findViewById(R.id.iv_rt_record_voice);
        svRecordVideo = view.findViewById(R.id.sv_recordVideo);
        ivIcRecord = view.findViewById(R.id.iv_icRecord);
        textClock = view.findViewById(R.id.tc_count);
        tvCheckSdCard = view.findViewById(R.id.tv_checkSdCord);
        flShotView = view.findViewById(R.id.fl_shotView);
        loadingView = view.findViewById(R.id.loadingView);
        initData();
        return view;
    }

    public void setRemoteCam(RemoteCam mRemoteCam) {
        Log.e(TAG, "setRemoteCam: " + FragmentRTVideo.mRemoteCam);
        FragmentRTVideo.mRemoteCam = mRemoteCam;
    }

    private void initData() {
        mRemoteCam.appStatus();
        mRemoteCam.micStatus();
        ((MainActivity) getActivity()).isDialogShow = false;
        svRecordVideo.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                prepare();
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                releaseWithoutStop();
            }
        });

        mAVOptions = new AVOptions();

        // the unit of timeout is ms
        mAVOptions.setInteger(AVOptions.KEY_PREPARE_TIMEOUT, 10 * 1000);
        mAVOptions.setInteger(AVOptions.KEY_GET_AV_FRAME_TIMEOUT, 10 * 1000);
        mAVOptions.setInteger(AVOptions.KEY_PROBESIZE, 128 * 1024);
        // Some optimization with buffering mechanism when be set to 1
        mAVOptions.setInteger(AVOptions.KEY_LIVE_STREAMING, 1);

//        if (mIsLiveStreaming) {
        mAVOptions.setInteger(AVOptions.KEY_DELAY_OPTIMIZATION, 1);
//        }

        // 1 -> hw codec enable, 0 -> disable [recommended]
        mAVOptions.setInteger(AVOptions.KEY_MEDIACODEC, 0);

        // whether start play automatically after prepared, default value is 1
        mAVOptions.setInteger(AVOptions.KEY_START_ON_PREPARED, 0);

        // TODO: 2018/3/30 声音是否取消？
//        AudioManager audioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
//        audioManager.requestAudioFocus(null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);

    }

    public void prepare() {
        Log.e(TAG, "prepare: iiii");
        if (mMediaPlayer != null) {
            mMediaPlayer.setDisplay(svRecordVideo.getHolder());
//            mMediaPlayer.reset();
//            mMediaPlayer.stop();
//            mMediaPlayer.release();
//            mMediaPlayer = null;
//            sendReconnectMessage();
            //mMediaPlayer.prepareAsync();
//            if (!mIsLiveStreaming) {
//            mMediaPlayer.seekTo(mMediaPlayer.getCurrentPosition());
//            }
            return;
        }

        try {
            mMediaPlayer = new PLMediaPlayer(getActivity(), mAVOptions);//getActivity()
            mMediaPlayer.setDebugLoggingEnabled(false);
            mMediaPlayer.setOnPreparedListener(mOnPreparedListener);
//            mMediaPlayer.setOnVideoSizeChangedListener(mOnVideoSizeChangedListener);
//            mMediaPlayer.setOnCompletionListener(mOnCompletionListener);
            mMediaPlayer.setOnErrorListener(mOnErrorListener);
            mMediaPlayer.setOnInfoListener(mOnInfoListener);
            mMediaPlayer.setOnBufferingUpdateListener(mOnBufferingUpdateListener);

            // set replay if completed
            // mMediaPlayer.setLooping(true);

//            如下更改第一个参数
            mMediaPlayer.setWakeMode(getActivity(), PowerManager.PARTIAL_WAKE_LOCK);

            mMediaPlayer.setDataSource(url);
            mMediaPlayer.setDisplay(svRecordVideo.getHolder());
            mMediaPlayer.prepareAsync();
        } catch (UnsatisfiedLinkError e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // prepare();
        if (mMediaPlayer != null) {
            mMediaPlayer.start();
        }

    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onStop() {
        super.onStop();
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.reset();
        }
        mMediaPlayer = null;
    }

    private PLMediaPlayer.OnPreparedListener mOnPreparedListener = new PLMediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(PLMediaPlayer mp) {
            Log.i(TAG, "On Prepared !");
            mMediaPlayer.start();
//            mIsStopped = false;
        }
    };

    private PLMediaPlayer.OnInfoListener mOnInfoListener = new PLMediaPlayer.OnInfoListener() {
        @Override
        public boolean onInfo(PLMediaPlayer mp, int what, int extra) {
            Log.i(TAG, "OnInfo, what = " + what + ", extra = " + extra);
            switch (what) {
                case PLMediaPlayer.MEDIA_INFO_BUFFERING_START:
                    loadingView.setVisibility(View.VISIBLE);
                    break;
                case PLMediaPlayer.MEDIA_INFO_BUFFERING_END:
                case PLMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
                    loadingView.setVisibility(View.GONE);
                    HashMap<String, String> meta = mMediaPlayer.getMetadata();
                    Log.i(TAG, "meta: " + meta.toString());
                    if (isRecord) {
                        setRecordState(true);
//                        showRecordTag(true);
                    } else {
                        setRecordState(false);
                    }
                    setMicState(isMicOn);
                    //  mListener.onFragmentAction(IFragmentListener.ACTION_RECORD_TIME, null);
//                    showToastTips(meta.toString());
                    break;
                case PLMediaPlayer.MEDIA_INFO_SWITCHING_SW_DECODE:
                    Log.i(TAG, "Hardware decoding failure, switching software decoding!");
                    break;
                default:
                    break;
            }
            return true;
        }
    };

    private PLMediaPlayer.OnBufferingUpdateListener mOnBufferingUpdateListener = new PLMediaPlayer.OnBufferingUpdateListener() {
        @Override
        public void onBufferingUpdate(PLMediaPlayer mp, int percent) {
//            Log.d(TAG, "onBufferingUpdate: " + percent + "%");
        }
    };

    private PLMediaPlayer.OnErrorListener mOnErrorListener = new PLMediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(PLMediaPlayer mp, int errorCode) {
            boolean isNeedReconnect = false;
            Log.e(TAG, "Error happened, errorCode = " + errorCode);
            switch (errorCode) {
                case PLMediaPlayer.ERROR_CODE_INVALID_URI:
//                    showToastTips("Invalid URL !");
                    break;
                case PLMediaPlayer.ERROR_CODE_404_NOT_FOUND:
//                    showToastTips("404 resource not found !");
                    break;
                case PLMediaPlayer.ERROR_CODE_CONNECTION_REFUSED:
//                    showToastTips("Connection refused !");
                    break;
                case PLMediaPlayer.ERROR_CODE_CONNECTION_TIMEOUT:
//                    showToastTips("Connection timeout !");
                    isNeedReconnect = true;
                    break;
                case PLMediaPlayer.ERROR_CODE_EMPTY_PLAYLIST:
//                    showToastTips("Empty playlist !");
                    break;
                case PLMediaPlayer.ERROR_CODE_STREAM_DISCONNECTED:
//                    showToastTips("Stream disconnected !");
                    isNeedReconnect = true;
                    break;
                case PLMediaPlayer.ERROR_CODE_IO_ERROR:
//                    showToastTips("Network IO Error !");
                    isNeedReconnect = true;
                    break;
                case PLMediaPlayer.ERROR_CODE_UNAUTHORIZED:
//                    showToastTips("Unauthorized Error !");
                    break;
                case PLMediaPlayer.ERROR_CODE_PREPARE_TIMEOUT:
//                    showToastTips("Prepare timeout !");
                    isNeedReconnect = true;
                    break;
                case PLMediaPlayer.ERROR_CODE_READ_FRAME_TIMEOUT:
//                    showToastTips("Read frame timeout !");
                    isNeedReconnect = true;
                    break;
                case PLMediaPlayer.ERROR_CODE_HW_DECODE_FAILURE:
                    mAVOptions.setInteger(AVOptions.KEY_MEDIACODEC, AVOptions.MEDIA_CODEC_SW_DECODE);
                    isNeedReconnect = true;
                    break;
                case PLMediaPlayer.MEDIA_ERROR_UNKNOWN:
                    break;
                default:
//                    showToastTips("unknown error !");
                    break;
            }
            // Todo pls handle the error status here, reconnect or call finish()
            release();
            if (isNeedReconnect) {
                sendReconnectMessage();
            } else {
//                finish();
            }
            // Return true means the error has been handled
            // If return false, then `onCompletion` will be called
            return true;
        }
    };

    private void showToastTips(final String tips) {
//        if (mIsActivityPaused) {
//            return;
//        }
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mToast != null) {
                    mToast.cancel();
                }
                mToast = Toast.makeText(getActivity(), tips, Toast.LENGTH_SHORT);
                mToast.show();
            }
        });
    }

    public void showRecordTag(boolean isRecordOpen) {
        if (isRecordOpen) {
            textClock.setVisibility(View.VISIBLE);
            ivIcRecord.setVisibility(View.VISIBLE);
        } else {
            textClock.setVisibility(View.INVISIBLE);
            ivIcRecord.setVisibility(View.INVISIBLE);
        }
    }

    public void showCheckSdCordTag(boolean isSdInsert) {
        if (isSdInsert) {
            if (tvCheckSdCard != null) {
                tvCheckSdCard.setVisibility(View.INVISIBLE);
            }
        } else {
            if (tvCheckSdCard != null) {
                tvCheckSdCard.setVisibility(View.VISIBLE);
            }
        }
    }

    public void sendReconnectMessage() {
//        showToastTips(getString(R.string.Reconnecting));
        loadingView.setVisibility(View.VISIBLE);
        mHandler.removeCallbacksAndMessages(null);
        mHandler.sendMessageDelayed(mHandler.obtainMessage(MESSAGE_ID_RECONNECTING), 500);
    }

    protected Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what != MESSAGE_ID_RECONNECTING) {
                return;
            }
            prepare();
        }
    };


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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        release();
//        AudioManager audioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
//        audioManager.abandonAudioFocus(null);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void releaseWithoutStop() {
        if (mMediaPlayer != null) {
            mMediaPlayer.setDisplay(null);
        }
    }


    public void release() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    @OnClick({R.id.iv_rt_record_video, R.id.btn_rt_capture_photo, R.id.iv_rt_lock_video, R.id.iv_rt_record_voice})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_rt_record_video:
                /*if (isRecord) {
//                    showToastTips("关闭录像！");
                    *//*if (mMediaPlayer != null) {
                        mMediaPlayer.stop();
                        mMediaPlayer.reset();
                    }
                    mMediaPlayer = null;*//*
//                    tvTimeOfSv.setVisibility(View.INVISIBLE);
                    showRecordTag(false);
                   *//* ivRtRecordVideo.setChecked(false);*//*
                } else {
//                    showToastTips("开启录像！");
//                    prepare();
//                    mMediaPlayer.start();
//                    tvTimeOfSv.setVisibility(View.VISIBLE);
                    showRecordTag(true);
                   *//* ivRtRecordVideo.setChecked(true);*//*
                }*/
                if (mListener != null) {
                    long currentTime = Calendar.getInstance().getTimeInMillis();
                    if (currentTime - lastClickTime > MINI_CLICK_DELAY) {
//                        if (isRecord) {
//                            ivRtRecordVideo.setImageResource(R.mipmap.btn_record_video_off);
//                            showRecordTag(false);
//                        } else {
//                            ivRtRecordVideo.setImageResource(R.mipmap.btn_record_video_on);
//                            showRecordTag(true);
//                        }
                        lastClickTime = currentTime;
                        isRecord = !isRecord;
                        mListener.onFragmentAction(IFragmentListener.ACTION_RECORD_START, isRecord);
                    }
                }
                break;
            case R.id.btn_rt_capture_photo:
                flShotView.setVisibility(View.VISIBLE);
                AlphaAnimation alphaAnimation = new AlphaAnimation(1, 0);
                alphaAnimation.setDuration(100);
                alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        flShotView.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                flShotView.startAnimation(alphaAnimation);
                if (mListener != null) {
                    mListener.onFragmentAction(IFragmentListener.ACTION_PHOTO_START, null);
                }
                break;
            case R.id.iv_rt_lock_video:
                if (mListener != null) {
                    long currentTime = Calendar.getInstance().getTimeInMillis();
                    if (currentTime - lastClickTime2 > 11000) {
                        lastClickTime2 = currentTime;
                        mListener.onFragmentAction(IFragmentListener.ACTION_LOCK_VIDEO_START, null);
                    } else {
                        showToastTips(getString(R.string.video_locking));
                    }
                }
                break;
            case R.id.iv_rt_record_voice:
                if (isMicOn) {
//                    showToastTips("关闭录音！");
                } else {
//                    showToastTips("开启录音！");
                }
                if (mListener != null) {
                    long currentTime = Calendar.getInstance().getTimeInMillis();
                    if (currentTime - lastClickTime3 > MINI_CLICK_DELAY) {
                        lastClickTime3 = currentTime;
                        isMicOn = !isMicOn;
                        mListener.onFragmentAction(IFragmentListener.ACTION_MIC_ON, isMicOn);
                    }
                }
                break;
            default:
                break;
        }
    }
//
//    // TODO: 2018/4/12 状态是保存了但界面就是不会去刷新
//    @Override
//    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
//        super.onViewStateRestored(savedInstanceState);
//        if (savedInstanceState != null) {
//            boolean isSavedRecord = savedInstanceState.getBoolean("isRecord");
//            if (isSavedRecord) {
//                ivRtRecordVideo.setImageResource(R.mipmap.btn_record_video_on);
//            } else {
//                ivRtRecordVideo.setImageResource(R.mipmap.btn_record_video_off);
//            }
//        }
//    }
//
//    @Override
//    public void onSaveInstanceState(@NonNull Bundle outState) {
//        super.onSaveInstanceState(outState);
//        outState.putBoolean("isRecord",isRecord);
//    }

    public void setRecordState(boolean isOn) {
        isRecord = isOn;
        if (ivRtRecordVideo != null) {
            /*ivRtRecordVideo.setChecked(isOn);*/
            if (isOn) {
                showRecordTag(true);
                ivRtRecordVideo.setImageResource(R.mipmap.btn_record_video_off);
            } else {
                showRecordTag(false);
                ivRtRecordVideo.setImageResource(R.mipmap.btn_record_video_on);
            }
        }
    }

    public void setMicState(boolean isOn) {
        isMicOn = isOn;
        if (ivRtRecordVoice != null) {
            if (isOn) {
                ivRtRecordVoice.setImageResource(R.mipmap.btn_record_voice_off);
            } else {
                ivRtRecordVoice.setImageResource(R.mipmap.btn_record_voice_on);
            }
        }
    }

    public void updateRecordTime(String time) {
    }

    public void setImagerAple(boolean temp) {
        if (temp) {
            ivRtRecordVideo.setAlpha((float) 0.4);
            ivRtRecordVoice.setAlpha((float) 0.4);
            btnRtCapturePhoto.setAlpha((float) 0.4);
            ivRtLockVideo.setAlpha((float) 0.4);

            ivRtRecordVoice.setEnabled(false);
            btnRtCapturePhoto.setEnabled(false);
            ivRtLockVideo.setEnabled(false);
            ivRtRecordVideo.setEnabled(false);

        } else {
            ivRtRecordVideo.setAlpha((float) 1.0);
            ivRtRecordVoice.setAlpha((float) 1.0);
            btnRtCapturePhoto.setAlpha((float) 1.0);
            ivRtLockVideo.setAlpha((float) 1.0);
            ivRtRecordVoice.setEnabled(true);
            btnRtCapturePhoto.setEnabled(true);
            ivRtLockVideo.setEnabled(true);
            ivRtRecordVideo.setEnabled(true);

        }

    }

}
