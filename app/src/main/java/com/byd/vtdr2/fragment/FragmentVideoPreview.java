package com.byd.vtdr2.fragment;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.byd.vtdr2.ActivityRTVideo;
import com.byd.vtdr2.R;
import com.pili.pldroid.player.AVOptions;
import com.pili.pldroid.player.PLMediaPlayer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static android.content.ContentValues.TAG;


/**
 * Created by byd_tw on 2017/11/1.
 */

public class FragmentVideoPreview extends Fragment {


    Unbinder unbinder;
    @BindView(R.id.sv_videoPlayView)
    SurfaceView svVideoPlayView;
    @BindView(R.id.ib_playVideo)
    ImageButton ibPlayVideo;

    //    @BindView(R.id.loadingView)
    private LinearLayout LoadingView;
    @BindView(R.id.btn_back_to_videoGridview)
    ImageButton btnBackToVideoGridview;
    @BindView(R.id.tv_title_video)
    TextView tvTitleVideo;

    //    @BindView(R.id.rl_bar_showVideoTitle)
//    RelativeLayout rlBarShowVideoTitle;
    private RelativeLayout rlBarShowVideoTitle;
    //    @BindView(R.id.btn_stop)
    private ImageButton btnStop;
    //    @BindView(R.id.tv_currentTime)
//    TextView tvCurrentTime;
    private TextView tvCurrentTime;
    @BindView(R.id.sb_mediaCtrlBar)
    SeekBar sbMediaCtrlBar;
    //    @BindView(R.id.tv_endTime)
//    TextView tvEndTime;
    private TextView tvEndTime;
    @BindView(R.id.btn_VideoZoom)
    ImageButton btnVideoZoom;
    //    @BindView(R.id.ll_bar_editVideo)
//    LinearLayout llBarEditVideo;
    private LinearLayout llBarEditVideo;
    //    @BindView(R.id.btn_start)
    private ImageButton btnStart;

    private ArrayList<ImageView> imageLists;
    private ArrayList<String> urlsList;
    private String url;
    private String fileName;
    //    private SurfaceHolder surfaceHolder;
//    private IjkMediaPlayer player;
    private AVOptions mAVOptions;
    private PLMediaPlayer mMediaPlayer;

    private static final int SHOW_PROGRESS = 0;
    private static final int SHOW_CONTROLLER = 1;
    private static final int SHOW_END = 3;

    private boolean isShowControl;
    private boolean isVideoStop;

    private int durationtime = 0 ;
    private int CurrentTime = 0;
    private MyThreadTimecount myThreadTimecount;
    private  boolean ouTthread = false ;
    private AudioManager audioManager;
    public boolean reload = false;

    protected Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SHOW_PROGRESS:
                    long pos = setProgress();
                    break;
                case SHOW_CONTROLLER:
                    showControlBar();
                    break;
                case SHOW_END:
                    CurrentTime =0;
                    long pos1 = setProgress();
                    if (mMediaPlayer != null) {
                        mMediaPlayer.seekTo(CurrentTime * 1000);
                        mMediaPlayer.pause();
                    }
                    if (myThreadTimecount !=null) {
                        myThreadTimecount.pauseThread();//暂停线程运行
                    }

                    btnStop.setVisibility(View.INVISIBLE);
                    btnStart.setVisibility(View.VISIBLE);
                    isVideoStop = true;
                    mHandler.removeMessages(SHOW_CONTROLLER);
                    mHandler.sendEmptyMessageDelayed(SHOW_CONTROLLER, 3000);
                    break;
                default:
                    break;
            }
        }
    };

    public static FragmentVideoPreview newInstance(String url) {
        FragmentVideoPreview fragmentVideoPreview = new FragmentVideoPreview();
        Bundle bundle = new Bundle();
//        bundle.putStringArrayList("urlsList", urlsList);
        bundle.putString("url", url);
        fragmentVideoPreview.setArguments(bundle);
        return fragmentVideoPreview;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        urlsList = getArguments().getStringArrayList("urlsList");
        url = getArguments().getString("url");
        if (url.contains("LOCK")) {
            fileName = url.substring(31);
        } else {
            fileName = url.substring(32);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frament_video_preview, container, false);
        unbinder = ButterKnife.bind(this, view);
        tvCurrentTime = view.findViewById(R.id.tv_currentTime);
        tvEndTime = view.findViewById(R.id.tv_endTime);
        rlBarShowVideoTitle = view.findViewById(R.id.rl_bar_showVideoTitle);
        llBarEditVideo = view.findViewById(R.id.ll_bar_editVideo);
        LoadingView = view.findViewById(R.id.loadingView);
        btnStart = view.findViewById(R.id.btn_start);
        btnStop = view.findViewById(R.id.btn_stop);
        initData();
        reload = true;
        return view;
    }

    private void initData() {
        tvTitleVideo.setText(fileName);

        svVideoPlayView.getHolder().addCallback(new SurfaceHolder.Callback() {
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
//                使用如下多次点击视频会有闪退
//                new MyTheard().start();
            }
        });
        svVideoPlayView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showControlBar();
                mHandler.removeMessages(SHOW_CONTROLLER);
                mHandler.sendEmptyMessageDelayed(SHOW_CONTROLLER, 3000);
            }
        });

        mHandler.sendEmptyMessage(SHOW_PROGRESS);
        mHandler.sendEmptyMessageDelayed(SHOW_CONTROLLER, 3000);

        mAVOptions = new AVOptions();

        // the unit of timeout is ms
        mAVOptions.setInteger(AVOptions.KEY_PREPARE_TIMEOUT, 10 * 1000);
        mAVOptions.setInteger(AVOptions.KEY_GET_AV_FRAME_TIMEOUT, 10 * 1000);
        mAVOptions.setInteger(AVOptions.KEY_PROBESIZE, 128 * 1024);
        // Some optimization with buffering mechanism when be set to 1
        mAVOptions.setInteger(AVOptions.KEY_LIVE_STREAMING, 0);

        // 1 -> hw codec enable, 0 -> disable [recommended]
        mAVOptions.setInteger(AVOptions.KEY_MEDIACODEC, 0);

        // whether start play automatically after prepared, default value is 1
        mAVOptions.setInteger(AVOptions.KEY_START_ON_PREPARED, 0);

        audioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
        audioManager.requestAudioFocus(null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
    }

    private void showControlBar() {
        if (mMediaPlayer == null) {
            return;
        }
        if (isShowControl) {
            rlBarShowVideoTitle.setVisibility(View.VISIBLE);
            llBarEditVideo.setVisibility(View.VISIBLE);
            if (btnStart.getVisibility() == View.INVISIBLE && isVideoStop) {
                btnStart.setVisibility(View.VISIBLE);
            }
            if (btnStop.getVisibility() == View.INVISIBLE && !isVideoStop) {
                btnStop.setVisibility(View.VISIBLE);
            }
        } else {
            rlBarShowVideoTitle.setVisibility(View.INVISIBLE);
            llBarEditVideo.setVisibility(View.INVISIBLE);
            if (btnStart.getVisibility() == View.VISIBLE) {
                btnStart.setVisibility(View.INVISIBLE);
            }
            if (btnStop.getVisibility() == View.VISIBLE) {
                btnStop.setVisibility(View.INVISIBLE);
            }
        }
        isShowControl = !isShowControl;
//
    }

    private void prepare() {

        if (mMediaPlayer != null) {
            mMediaPlayer.setDisplay(svVideoPlayView.getHolder());
//            if (!mIsLiveStreaming) {
            mMediaPlayer.seekTo(mMediaPlayer.getCurrentPosition());
//            }
            return;
        }

        try {
            mMediaPlayer = new PLMediaPlayer(getActivity(), mAVOptions);

            mMediaPlayer.setOnPreparedListener(mOnPreparedListener);
            mMediaPlayer.setOnCompletionListener(mOnCompletionListener);
            mMediaPlayer.setOnInfoListener(mOnInfoListener);
            mMediaPlayer.setOnBufferingUpdateListener(mOnBufferingUpdateListener);

            mMediaPlayer.setWakeMode(getActivity(), PowerManager.PARTIAL_WAKE_LOCK);
            mMediaPlayer.setDataSource(url);
            mMediaPlayer.setDisplay(svVideoPlayView.getHolder());
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
        if (mMediaPlayer!=null && isVideoStop) {
            mMediaPlayer.start();
            audioManager.requestAudioFocus(null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
            if (myThreadTimecount != null) {
                myThreadTimecount.resumeThread();//恢复线程运行
            }
            btnStop.setVisibility(View.VISIBLE);
            btnStart.setVisibility(View.INVISIBLE);
            isVideoStop = false;
            mHandler.removeMessages(SHOW_CONTROLLER);
            mHandler.sendEmptyMessageDelayed(SHOW_CONTROLLER, 3000);
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        if (mMediaPlayer != null) {
            mMediaPlayer.pause();
        }
        isVideoStop = true;
        //getFragmentManager().beginTransaction().addToBackStack(null);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private PLMediaPlayer.OnPreparedListener mOnPreparedListener = new PLMediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(PLMediaPlayer mp) {
            Log.i(TAG, "On Prepared !");
            mMediaPlayer.start();
            long duration = mMediaPlayer.getDuration();
            durationtime = (int)(duration/1000) ;
            /*
            * 视屏播放后开始进度条初始化
            * */
            sbMediaCtrlBar.setMax(durationtime);
            sbMediaCtrlBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (!fromUser) {
                        return;
                    }
                    CurrentTime = progress;
                    long newposition = ( progress) * 1000;
                    String time = generateTime(newposition);
                    tvCurrentTime.setText(time);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    long duration = mMediaPlayer.getDuration();
                    mMediaPlayer.seekTo(seekBar.getProgress() * 1000);
                    CurrentTime = seekBar.getProgress();
                }
            });
            myThreadTimecount = new MyThreadTimecount();
            myThreadTimecount.start();


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
                    // TODO: 2018/3/12   java.lang.NullPointerException: Attempt to invoke virtual method 'void android.widget.LinearLayout.setVisibility(int)' on a null object reference
                    LoadingView.setVisibility(View.GONE);
                    HashMap<String, String> meta = mMediaPlayer.getMetadata();
                    Log.i(TAG, "meta: " + meta.toString());
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

    /**
     * Listen the event of playing complete
     * For playing local file, it's called when reading the file EOF
     * For playing network stream, it's called when the buffered bytes played over
     * <p>
     * If setLooping(true) is called, the player will restart automatically
     * And ｀onCompletion｀ will not be called
     */
    private PLMediaPlayer.OnCompletionListener mOnCompletionListener = new PLMediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(PLMediaPlayer mp) {
            Log.e(TAG, "Play Completed !");
//            showToastTips("Play Completed !");
//            finish();
            // TODO: 2017/12/18 播放结束的逻辑交互处理
            isVideoStop = true;
            showControlBar();
//            CurrentTime =0;
//            durationtime =0;
//            long pos = setProgress();
//            mMediaPlayer.seekTo(CurrentTime * 1000);
//            mMediaPlayer.pause();
//            if (myThreadTimecount !=null) {
//                myThreadTimecount.pauseThread();//暂停线程运行
//            }
//
//            btnStop.setVisibility(View.INVISIBLE);
//            btnStart.setVisibility(View.VISIBLE);
//            isVideoStop = true;
//            mHandler.removeMessages(SHOW_CONTROLLER);
//            mHandler.sendEmptyMessageDelayed(SHOW_CONTROLLER, 3000);

        }
    };

    @OnClick({R.id.btn_back_to_videoGridview, R.id.btn_stop, R.id.btn_VideoZoom, R.id.btn_start})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_back_to_videoGridview:
//                当未添加到返回栈时使用如下
//                getFragmentManager().beginTransaction().remove(this).commit();
                if (mMediaPlayer != null) {
                    if (mMediaPlayer.isPlaying()) {
                        mMediaPlayer.stop();
//                    release();
                    }
                }
                reload = false;
                getActivity().getSupportFragmentManager().popBackStack();
                break;
            case R.id.btn_stop:
                mMediaPlayer.pause();
                if (myThreadTimecount !=null) {
                    myThreadTimecount.pauseThread();//暂停线程运行
                }

                btnStop.setVisibility(View.INVISIBLE);
                btnStart.setVisibility(View.VISIBLE);
                isVideoStop = true;
                mHandler.removeMessages(SHOW_CONTROLLER);
                mHandler.sendEmptyMessageDelayed(SHOW_CONTROLLER, 3000);
                break;
            case R.id.btn_VideoZoom:
                mMediaPlayer.pause();
                if (myThreadTimecount !=null) {
                    myThreadTimecount.pauseThread();//暂停线程运行
                }

                btnStop.setVisibility(View.INVISIBLE);
                btnStart.setVisibility(View.VISIBLE);
                isVideoStop = true;
                Intent intent = new Intent(view.getContext(), ActivityRTVideo.class);
                intent.putExtra("fileName", fileName);
                intent.putExtra("CurrentTime", CurrentTime);
                intent.putExtra("url", url);
                startActivity(intent);
                break;
            case R.id.btn_start:
                mMediaPlayer.start();
                if (myThreadTimecount !=null) {
                    myThreadTimecount.resumeThread();//恢复线程运行
                }

                btnStop.setVisibility(View.VISIBLE);
                btnStart.setVisibility(View.INVISIBLE);
                isVideoStop = false;
                mHandler.removeMessages(SHOW_CONTROLLER);
                mHandler.sendEmptyMessageDelayed(SHOW_CONTROLLER, 3000);
            default:
                break;
        }
    }

    private long setProgress() {
        // TODO: 2017/12/15 setprogress 的逻辑、handler的运行原理。
        if (mMediaPlayer == null) {
            return 0;
        }
        long currentPosition = mMediaPlayer.getCurrentPosition();
        long duration = mMediaPlayer.getDuration();
        if (tvCurrentTime != null && tvEndTime != null && sbMediaCtrlBar != null) {
            tvCurrentTime.setText(generateTime(CurrentTime*1000));
            tvEndTime.setText(generateTime(duration));
            sbMediaCtrlBar.setProgress((int) CurrentTime);
        }

        return currentPosition;
    }

    private static String generateTime(long position) {
        int totalSeconds = (int) (position / 1000);

        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;

        if (hours > 0) {
            return String.format(Locale.US, "%02d:%02d:%02d", hours, minutes,
                    seconds);
        } else {
            return String.format(Locale.US, "%02d:%02d", minutes, seconds);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    public void releaseWithoutStop() {
        if (mMediaPlayer != null) {
            mMediaPlayer.setDisplay(null);
            System.gc();
            Runtime.getRuntime().runFinalization();
            System.gc();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ouTthread =true;
//        替换为如下一行
//        release();
        new MyTheard().start();
        AudioManager audioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
        audioManager.abandonAudioFocus(null);
    }

    public void release() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
//            如下被屏蔽，改为了在onDestroy中单线程执行
// TODO: 2018/3/12 导致内存越来越大
            mMediaPlayer.release();
            mMediaPlayer = null;
            System.gc();
        }
    }

    private class MyTheard extends Thread {
        @Override
        public void run() {
            release();
        }
    }
    private class MyThreadTimecount extends Thread {
        private final Object lock = new Object();
        private boolean pause = false;

        /**
         * 调用这个方法实现暂停线程
         */
        void pauseThread() {
            pause = true;
        }

        /**
         * 调用这个方法实现恢复线程的运行
         */
        void resumeThread() {
            pause = false;
            synchronized (lock) {
                lock.notifyAll();
            }
        }

        /**
         * 注意：这个方法只能在run方法里调用，不然会阻塞主线程，导致页面无响应
         */
        void onPause() {
            synchronized (lock) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void run() {
            super.run();
            try {
                int index = 0;
                while (!ouTthread) {
                    // 让线程处于暂停等待状态
                    while (pause) {
                        onPause();
                    }
                    try {
                        System.out.println(index);
                        Thread.sleep(1000);
                        ++index;
                        ++CurrentTime;
                        Log.i(TAG, "time count = " + CurrentTime  );
                        if (CurrentTime>durationtime)
                        {
                            CurrentTime =0;
                            mHandler.sendEmptyMessage(SHOW_PROGRESS);
                            mHandler.sendEmptyMessage(SHOW_END);

                        }else {
                            mHandler.sendEmptyMessage(SHOW_PROGRESS);

                        }

                    } catch (InterruptedException e) {
                        //捕获到异常之后，执行break跳出循环
                        break;
                    }
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    }
}
