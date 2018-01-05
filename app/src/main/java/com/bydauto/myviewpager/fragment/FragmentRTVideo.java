package com.bydauto.myviewpager.fragment;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bydauto.myviewpager.R;
import com.bydauto.myviewpager.RemoteCam;
import com.bydauto.myviewpager.ServerConfig;
import com.bydauto.myviewpager.connectivity.IFragmentListener;
import com.pili.pldroid.player.AVOptions;
import com.pili.pldroid.player.PLMediaPlayer;

import java.io.IOException;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by byd_tw on 2017/11/1.
 */

public class FragmentRTVideo extends Fragment {
    private static final String TAG = "FragmentRTVideo";

    @BindView(R.id.btn_rt_capture_photo)
    ImageButton btnRtCapturePhoto;
    @BindView(R.id.iv_rt_lock_video)
    ImageButton ivRtLockVideo;
//    @BindView(R.id.sv_recordVideo)
//    SurfaceView svRecordVideo;

    Unbinder unbinder;
    //    @BindView(R.id.sv_recordVideo)
//    SurfaceView svRecordVideo;
//    @BindView(R.id.iv_icRecord)
//    ImageView ivIcRecord;

    //    @BindView(R.id.tv_timeOfSv)
//    TextView tvTimeOfSv;
    @BindView(R.id.LoadingView)
    ProgressBar LoadingView;

    private String url = "rtsp://" + ServerConfig.HOST + "/live";
    //    private String url = "rtsp://192.168.42.1/tmp/SD0/EVENT/2017-11-28-19-09-56.MP4" ;
//    private SurfaceHolder surfaceHolder;
//    private IjkMediaPlayer player;
    private AVOptions mAVOptions;
    private PLMediaPlayer mMediaPlayer;

    private IFragmentListener mListener;
    private CheckBox ivRtRecordVideo;
    private CheckBox ivRtRecordVoice;
    private SurfaceView svRecordVideo;
    private TextView tvTimeOfSv;
    private ImageView ivIcRecord;

    private RemoteCam mRemoteCam;
    private boolean isRecord;
    private boolean isMicOn;
    private Toast mToast;
    private boolean mIsStopped;
    private int seconds;

    private Runnable runnable;
    private Handler mHandler = new Handler();
// {
//        @Override
//        public void handleMessage(Message msg) {
////            super.handleMessage(msg);
//            switch (msg.what) {
//                case 1:
//
//            }
//            Bundle bundle = msg.getData();
//            String key = bundle.getString("mTime");
//            tvTimeOfSv.setText(key);
//            mHandler.sendEmptyMessage()
//        }
//    };
    private String timeStr;

    public static FragmentRTVideo newInstance() {
        FragmentRTVideo fragmentRTVideo = new FragmentRTVideo();

        return fragmentRTVideo;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rtvideo, container, false);
        unbinder = ButterKnife.bind(this, view);

        ivRtRecordVideo = view.findViewById(R.id.iv_rt_record_video);
        ivRtRecordVoice = view.findViewById(R.id.iv_rt_record_voice);
        svRecordVideo = view.findViewById(R.id.sv_recordVideo);
        tvTimeOfSv = view.findViewById(R.id.tv_timeOfSv);
//        tvTimeOfSv.setVisibility(View.VISIBLE);//test
//        tvTimeOfSv.setText("xiaobo");
        ivIcRecord = view.findViewById(R.id.iv_icRecord);
//        ivIcRecord.setVisibility(View.VISIBLE);//test

        initData();

        runnable = new Runnable( ) {
            @Override
            public void run ( ) {
                tvTimeOfSv.setText(getTime(++seconds));
                mHandler.postDelayed(this,1000);
                //postDelayed(this,2000)方法安排一个Runnable对象到主线程队列中
            }
        };

        return view;
    }

    private void initData() {
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

        AudioManager audioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
        audioManager.requestAudioFocus(null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);

    }

    private void prepare() {
        Log.e(TAG, "prepare: iiii");

        if (mMediaPlayer != null) {
            mMediaPlayer.setDisplay(svRecordVideo.getHolder());
//            if (!mIsLiveStreaming) {
//            mMediaPlayer.seekTo(mMediaPlayer.getCurrentPosition());
//            }
            return;
        }

        try {
            mMediaPlayer = new PLMediaPlayer(getActivity(), mAVOptions);

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
    }

    @Override
    public void onPause() {
        super.onPause();
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
                    LoadingView.setVisibility(View.VISIBLE);
                    break;
                case PLMediaPlayer.MEDIA_INFO_BUFFERING_END:
                case PLMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
                    LoadingView.setVisibility(View.GONE);
                    HashMap<String, String> meta = mMediaPlayer.getMetadata();
                    Log.i(TAG, "meta: " + meta.toString());
                    mListener.onFragmentAction(IFragmentListener.ACTION_RECORD_TIME, null);
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
            Log.d(TAG, "onBufferingUpdate: " + percent + "%");
        }
    };

    private PLMediaPlayer.OnErrorListener mOnErrorListener = new PLMediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(PLMediaPlayer mp, int errorCode) {
            boolean isNeedReconnect = false;
            Log.e(TAG, "Error happened, errorCode = " + errorCode);
            switch (errorCode) {
                case PLMediaPlayer.ERROR_CODE_INVALID_URI:
                    showToastTips("Invalid URL !");
                    break;
                case PLMediaPlayer.ERROR_CODE_404_NOT_FOUND:
                    showToastTips("404 resource not found !");
                    break;
                case PLMediaPlayer.ERROR_CODE_CONNECTION_REFUSED:
                    showToastTips("Connection refused !");
                    break;
                case PLMediaPlayer.ERROR_CODE_CONNECTION_TIMEOUT:
                    showToastTips("Connection timeout !");
                    isNeedReconnect = true;
                    break;
                case PLMediaPlayer.ERROR_CODE_EMPTY_PLAYLIST:
                    showToastTips("Empty playlist !");
                    break;
                case PLMediaPlayer.ERROR_CODE_STREAM_DISCONNECTED:
                    showToastTips("Stream disconnected !");
                    isNeedReconnect = true;
                    break;
                case PLMediaPlayer.ERROR_CODE_IO_ERROR:
                    showToastTips("Network IO Error !");
                    isNeedReconnect = true;
                    break;
                case PLMediaPlayer.ERROR_CODE_UNAUTHORIZED:
                    showToastTips("Unauthorized Error !");
                    break;
                case PLMediaPlayer.ERROR_CODE_PREPARE_TIMEOUT:
                    showToastTips("Prepare timeout !");
                    isNeedReconnect = true;
                    break;
                case PLMediaPlayer.ERROR_CODE_READ_FRAME_TIMEOUT:
                    showToastTips("Read frame timeout !");
                    isNeedReconnect = true;
                    break;
                case PLMediaPlayer.ERROR_CODE_HW_DECODE_FAILURE:
                    mAVOptions.setInteger(AVOptions.KEY_MEDIACODEC, AVOptions.MEDIA_CODEC_SW_DECODE);
                    isNeedReconnect = true;
                    break;
                case PLMediaPlayer.MEDIA_ERROR_UNKNOWN:
                    break;
                default:
                    showToastTips("unknown error !");
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

    private void sendReconnectMessage() {
        showToastTips("正在重连...");
        LoadingView.setVisibility(View.VISIBLE);
//        mHandler.removeCallbacksAndMessages(null);
//        mHandler.sendMessageDelayed(mHandler.obtainMessage(MESSAGE_ID_RECONNECTING), 500);
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

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
        AudioManager audioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
        audioManager.abandonAudioFocus(null);
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
                if (isRecord) {
                    showToastTips("关闭录像！");
                    if (mMediaPlayer != null) {
                        mMediaPlayer.stop();
                        mMediaPlayer.reset();
                    }
                    mMediaPlayer = null;
//                    tvTimeOfSv.setVisibility(View.INVISIBLE);
                } else {
                    showToastTips("开启录像！");
                    prepare();
//                    tvTimeOfSv.setVisibility(View.VISIBLE);
                }
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
                if (isMicOn) {
                    showToastTips("关闭录音！");
                } else {
                    showToastTips("开启录像！");
                }
                if (mListener != null) {
                    isMicOn = !isMicOn;
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

    public void updateRecordTime(String time) {

        int second = Integer.parseInt(time);
        getTime(second);
//        tvTimeOfSv.setVisibility(View.VISIBLE);
//        ivIcRecord.setVisibility(View.VISIBLE);

//        Timer timer = new Timer();
//        timer.schedule(new RecordTimeTask(), 1000);
        mHandler.postDelayed(runnable,1000);

    }

    public String getTime(int second) {
        seconds = second;
        int minute = second / 60;
        int hour = minute / 60;
        second -= minute * 60;
        minute -= hour * 60;
        timeStr = String.format("%02d:%02d:%02d", hour, minute, second);
        if (tvTimeOfSv.getVisibility() != View.VISIBLE) {
            tvTimeOfSv.setVisibility(View.VISIBLE);
            ivIcRecord.setVisibility(View.VISIBLE);
            tvTimeOfSv.setText(timeStr);
        }
        return timeStr;
//
//        getActivity().runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                tvTimeOfSv.setVisibility(View.VISIBLE);
//                ivIcRecord.setVisibility(View.VISIBLE);
//                Log.e(TAG, "getTime: tvTimeOfSv.setVisibility");
//                tvTimeOfSv.setText(timeStr);
//            }
//        });

    }


//    private class RecordTimeTask extends TimerTask {
//        @Override
//        public void run() {
//            String mTime = getTime(++seconds);
//            Message msg = new Message();
//            Bundle bundle = new Bundle();
//            bundle.putString("mTime", mTime);
//            msg.setData(bundle);
//            mHandler.sendMessage(msg);
//        }
//    }

}
