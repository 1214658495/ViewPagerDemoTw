package com.byd.vtdr.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.byd.vtdr.R;
import com.byd.vtdr.utils.Utils;
import com.byd.vtdr.widget.MyMediaController;
import com.pili.pldroid.player.AVOptions;
import com.pili.pldroid.player.PLMediaPlayer;
import com.pili.pldroid.player.widget.PLVideoTextureView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static android.content.ContentValues.TAG;

/**
 * Created by byd_tw on 2017/11/1.
 */

public class FragmentVideoPreviewTest extends Fragment {


    Unbinder unbinder;
//    @BindView(R.id.sv_videoPlayView)
//    PLVideoTextureView svVideoPlayView;
    @BindView(R.id.ib_playVideo)
    ImageButton ibPlayVideo;
    @BindView(R.id.tv_test)
    TextView tvTest;

    private ArrayList<ImageView> imageLists;
    private ArrayList<String> urlsList;
    private String url;

    private static final int MESSAGE_ID_RECONNECTING = 0x01;

    //    private MediaController mMediaController;
//    private ImediaController mMediaController;
    private MyMediaController mMediaController;
    private PLVideoTextureView mVideoView;
    private Toast mToast = null;
    private String mVideoPath = null;
    private int mRotation = 0;
    private int mDisplayAspectRatio = PLVideoTextureView.ASPECT_RATIO_FIT_PARENT; //default
    private View mLoadingView;
    private View mCoverView = null;
    private boolean mIsActivityPaused = true;
//    private int mIsLiveStreaming = 1;
    private int mIsLiveStreaming = 0;


    public static FragmentVideoPreviewTest newInstance(ArrayList<String> urlsList, String url) {
        FragmentVideoPreviewTest fragmentVideoPreviewTest = new FragmentVideoPreviewTest();
        Bundle bundle = new Bundle();
        bundle.putStringArrayList("urlsList", urlsList);
        bundle.putString("url", url);
        fragmentVideoPreviewTest.setArguments(bundle);
        return fragmentVideoPreviewTest;
    }

    private void setOptions(int codecType) {
        AVOptions options = new AVOptions();

        // the unit of timeout is ms
        options.setInteger(AVOptions.KEY_PREPARE_TIMEOUT, 10 * 1000);
        options.setInteger(AVOptions.KEY_GET_AV_FRAME_TIMEOUT, 10 * 1000);
        options.setInteger(AVOptions.KEY_PROBESIZE, 128 * 1024);
        // Some optimization with buffering mechanism when be set to 1
//        options.setInteger(AVOptions.KEY_LIVE_STREAMING, mIsLiveStreaming);
        options.setInteger(AVOptions.KEY_LIVE_STREAMING, 0);
//        if (mIsLiveStreaming == 1) {
            options.setInteger(AVOptions.KEY_DELAY_OPTIMIZATION, 0);
//        }

        // 1 -> hw codec enable, 0 -> disable [recommended]
        options.setInteger(AVOptions.KEY_MEDIACODEC, codecType);

        // whether start play automatically after prepared, default value is 1
        options.setInteger(AVOptions.KEY_START_ON_PREPARED, 0);

        mVideoView.setAVOptions(options);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        urlsList = getArguments().getStringArrayList("urlsList");
        url = getArguments().getString("url");

//
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frament_video_preview, container, false);
        unbinder = ButterKnife.bind(this, view);
        mVideoView = view.findViewById(R.id.sv_videoPlayView);
        mLoadingView = view.findViewById(R.id.loadingView);
        initData();
        return view;
    }

    private void initData() {

//        tvTest.setText(url);

        mVideoView.setBufferingIndicator(mLoadingView);
        mLoadingView.setVisibility(View.VISIBLE);
//        int codec = getIntent().getIntExtra("mediaCodec", AVOptions.MEDIA_CODEC_SW_DECODE);
        setOptions(AVOptions.MEDIA_CODEC_SW_DECODE);
//        mMediaController = new MediaController(this, false, mIsLiveStreaming==1);
        mMediaController = new MyMediaController(getActivity(), false, mIsLiveStreaming==1);
//        mMediaController = new ImediaController(this, false);
        mVideoView.setMediaController(mMediaController);

        mVideoView.setOnCompletionListener(mOnCompletionListener);
        mVideoView.setOnErrorListener(mOnErrorListener);

        mVideoView.setVideoPath(url);
        mVideoView.start();

    }

    @Override
    public void onPause() {
        super.onPause();
        mToast = null;
        mVideoView.pause();
        mIsActivityPaused = true;
    }

    @Override
    public void onResume() {
        super.onResume();
        mIsActivityPaused = false;
        mVideoView.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mVideoView.stopPlayback();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();

    }


    @OnClick(R.id.ib_playVideo)
    public void onViewClicked() {
    }

    private PLMediaPlayer.OnErrorListener mOnErrorListener = new PLMediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(PLMediaPlayer mp, int errorCode) {
            boolean isNeedReconnect = false;
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
                    setOptions(AVOptions.MEDIA_CODEC_SW_DECODE);
                    isNeedReconnect = true;
                    break;
                case PLMediaPlayer.MEDIA_ERROR_UNKNOWN:
                    break;
                default:
                    showToastTips("unknown error !");
                    break;
            }
            // Todo pls handle the error status here, reconnect or call finish()
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

    private PLMediaPlayer.OnCompletionListener mOnCompletionListener = new PLMediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(PLMediaPlayer plMediaPlayer) {
            showToastTips("Play Completed !");
//            finish();
        }
    };

    private void showToastTips(final String tips) {
        // TODO: 2017/12/8  
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                if (mToast != null) {
//                    mToast.cancel();
//                }
//                mToast = Toast.makeText(getActivity(), tips, Toast.LENGTH_SHORT);
//                mToast.show();
//            }
//        });
    }

    private void sendReconnectMessage() {
        showToastTips("正在重连...");
        mLoadingView.setVisibility(View.VISIBLE);
        mHandler.removeCallbacksAndMessages(null);
        mHandler.sendMessageDelayed(mHandler.obtainMessage(MESSAGE_ID_RECONNECTING), 500);
    }

    protected Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what != MESSAGE_ID_RECONNECTING) {
                return;
            }
            if (mIsActivityPaused || !Utils.isLiveStreamingAvailable()) {
//                如下自己添加的
                getActivity().finish();
                return;
            }
            if (!Utils.isNetworkAvailable(getActivity())) {
                sendReconnectMessage();
                return;
            }
            mVideoView.setVideoPath(mVideoPath);
            mVideoView.start();
        }
    };


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        Log.e(TAG, "setUserVisibleHint: 11111");
        if (this.isVisible())
        {
            if (!isVisibleToUser)   // If we are becoming invisible, then...
            {
                Log.e(TAG, "setUserVisibleHint: !!!isVisibleToUser");
                //pause or stop video
//                如果使用stop则视频再次可见时点击无反应
//                mVideoView.stopPlayback();
                if (mVideoView.isPlaying()){
                    mVideoView.pause();

                }
//                mVideoView.releaseSurfactexture();
            }

            if (isVisibleToUser) // If we are becoming visible, then...
            {
                Log.e(TAG, "setUserVisibleHint: isVisibleToUser");
                //play your video
                mVideoView.start();
            }
        }
    }
}
