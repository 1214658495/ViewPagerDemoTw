package com.bydauto.myviewpager;

//import android.app.FragmentLoading;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Process;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.byd.lighttextview.LightButton;
import com.bydauto.myviewpager.connectivity.IChannelListener;
import com.bydauto.myviewpager.connectivity.IFragmentListener;
import com.bydauto.myviewpager.fragment.FragmentPlaybackList;
import com.bydauto.myviewpager.fragment.FragmentRTVideo;
import com.bydauto.myviewpager.fragment.FragmentSetting;
import com.bydauto.myviewpager.view.MyDialog;

import org.json.JSONObject;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * @author byd_tw
 */
public class MainActivity extends AppCompatActivity implements IChannelListener, IFragmentListener {
    private static final String TAG = "MainActivity";

    private final static String KEY_CONNECTIVITY_TYPE = "connectivity_type";
    @BindView(R.id.fl_main)
    FrameLayout flMain;
    //    private final static String KEY_NEVER_SHOW = "key_never_show";
    private int mConnectivityType;
    public SharedPreferences mPref;

    public RemoteCam mRemoteCam;

    @BindView(R.id.rb_realTimeVideo)
    RadioButton rbRealTimeVideo;
    @BindView(R.id.rb_playbackList)
    RadioButton rbPlaybackList;
    @BindView(R.id.rb_setting)
    RadioButton rbSetting;
    @BindView(R.id.rg_group)
    RadioGroup rgGroup;
    //    @BindView(R.id.vp_main)
//    NoScrollViewPager vpMain;
    @BindView(R.id.btn_back)
    LightButton btnBack;

    //    @BindView(R.id.btn_test)
//    LightButton btnTest;
    //    @BindView(R.id.vp)
//    ViewPager vp;
//    private ArrayList<ImageView> imageLists;
//    private MyFragmentPagerAdapter myFragmentPagerAdapter;
    private List<Fragment> fragments;
    private static FragmentRTVideo fragmentRTVideo = FragmentRTVideo.newInstance();
    private static FragmentPlaybackList fragmentPlaybackList = FragmentPlaybackList.newInstance();
    private static FragmentSetting fragmentSetting = FragmentSetting.newInstance();
    private int seconds;
    //    private FragmentRTVideo fragmentRTVideo;
    private Handler mHandler = new Handler();
    private Runnable runnable;
    private String recordTime;
    private Fragment fragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mPref = getPreferences(MODE_PRIVATE);
        getPrefs(mPref);
//        initView();
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        mRemoteCam = new RemoteCam(this);
        mRemoteCam.setChannelListener(this).setConnectivity(mConnectivityType)
                .setWifiInfo(wifiManager.getConnectionInfo().getSSID().replace("\"", ""), getWifiIpAddr());
        mRemoteCam.startSession();

//        fragments = new ArrayList<>();
//        fragments.add(new FragmentRTVideo());
            fragmentPlaybackList.setRemoteCam(mRemoteCam);
//        fragmentRTVideo.setRemoteCam(mRemoteCam);
//        fragments.add(fragmentRTVideo);
//        fragments.add(fragmentPlaybackList);
//        fragments.add(new FragmentSetting());
//        myFragmentPagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), fragments);
//
//        vpMain.setAdapter(myFragmentPagerAdapter);
        rgGroup.check(R.id.rb_realTimeVideo);
        if (fragment == null) {
            fragment = fragmentRTVideo;
            getSupportFragmentManager().beginTransaction().replace(flMain.getId(), fragment).commitAllowingStateLoss();
        }
//        switchFragment(0);
        rgGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.rb_realTimeVideo:
//                        switchFragment(0);

                        fragment = fragmentRTVideo;
                        break;
                    case R.id.rb_playbackList:
//                        vpMain.setCurrentItem(1, false);
//                        switchFragment(1);
                        fragment = fragmentPlaybackList;
                        break;
                    case R.id.rb_setting:
//                        vpMain.setCurrentItem(2, false);
//                        switchFragment(2);
                        fragment = fragmentSetting;
                        break;
                    default:
                        break;
                }
                if (fragment != null) {
                    getSupportFragmentManager().beginTransaction().replace(flMain.getId(), fragment).commitAllowingStateLoss();
                }
            }
        });

//        vpMain.setOffscreenPageLimit(2);
//        initData();
//        vp.setAdapter(new MyPagerAdapter());

//        runnable = new Runnable( ) {
//            @Override
//            public void run ( ) {
//                fragmentRTVideo.getTime(++seconds);
//                mHandler.postDelayed(this,1000);
//                //postDelayed(this,2000)方法安排一个Runnable对象到主线程队列中
//            }
//        };
    }


    private void getPrefs(SharedPreferences preferences) {
        mConnectivityType = preferences.getInt(KEY_CONNECTIVITY_TYPE, RemoteCam
                .CAM_CONNECTIVITY_WIFI_WIFI);
//        neverShow = mPref.getBoolean(KEY_NEVER_SHOW, false);
    }

    public void putPrefs(SharedPreferences preferences) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(KEY_CONNECTIVITY_TYPE, mConnectivityType);
//        editor.putBoolean(KEY_NEVER_SHOW, neverShow);
        editor.commit();
    }

    private String getWifiIpAddr() {
        WifiManager mgr = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        int ip = mgr.getConnectionInfo().getIpAddress();
        return String.format("%d.%d.%d.%d", (ip & 0xFF), (ip >> 8 & 0xFF), (ip >> 16 & 0xFF), ip
                >> 24);
    }

    @Override
    protected void onPause() {
        super.onPause();
        putPrefs(mPref);
    }

    @OnClick(R.id.btn_back)
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                MyDialog myDialog = MyDialog.newInstance(0, "退出程序？");
                myDialog.show(getFragmentManager(), "back");
                myDialog.setOnDialogButtonClickListener(new MyDialog.OnDialogButtonClickListener() {
                    @Override
                    public void okButtonClick() {

                    }

                    @Override
                    public void cancelButtonClick() {

                    }
                });
                break;
//            case R.id.btn_test:
//                MyDialog myDialogTest = MyDialog.newInstance(1);
//                myDialogTest.show(getFragmentManager(), "test");
//                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mRemoteCam.stopSession();
        finish();
        Log.e(TAG, "kill the process to force fresh launch next time");
        Process.killProcess(Process.myPid());
    }

    @Override
    public void onChannelEvent(final int type, final Object param, final String... array) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch (type & IChannelListener.MSG_MASK) {
                    case IChannelListener.CMD_CHANNEL_MSG:
                        handleCmdChannelEvent(type, param, array);
                        return;
//                    case IChannelListener.DATA_CHANNEL_MSG:
//                        handleDataChannelEvent(type, param);
//                        return;
//                    case IChannelListener.STREAM_CHANNEL_MSG:
//                        handleStreamChannelEvent(type, param);
//                        return;
                    default:
                        break;
                }
            }
        });
    }

    private void handleCmdChannelEvent(int type, Object param, String... array) {
//        if (type >= 80) {
//            handleCmdChannelError(type, param);
//            return;
//        }

        switch (type) {
            case IChannelListener.CMD_CHANNEL_EVENT_SHOW_ALERT:
                String str = (String) param;
                Toast.makeText(getApplicationContext(), str, Toast.LENGTH_SHORT).show();
                break;

            case IChannelListener.CMD_CHANNEL_EVENT_START_SESSION:
                mRemoteCam.getAllSettings();
                mRemoteCam.appStatus();
                mRemoteCam.micStatus();
                mRemoteCam.getTotalFreeSpace();
                mRemoteCam.getTotalFileCount();
                break;
            case IChannelListener.CMD_CHANNEL_EVENT_TAKE_PHOTO:
                Toast.makeText(getApplicationContext(), "拍照成功！", Toast.LENGTH_SHORT).show();
                break;
            case IChannelListener.CMD_CHANNEL_EVENT_APP_STATE:
                boolean isRecord = (boolean) param;
                Log.e(TAG, "handleCmdChannelEvent: isRecord = " + isRecord);
                // TODO: 2017/12/20
                fragmentRTVideo.setRecordState(isRecord);
//                fragmentRTVideo.setRecordState();
                break;
            case IChannelListener.CMD_CHANNEL_EVENT_MIC_STATE:
                boolean isMicOn = (boolean) param;
                Log.e(TAG, "handleCmdChannelEvent: isMicOn = " + isMicOn);
                // TODO: 2017/12/20
                fragmentRTVideo.setMicState(isMicOn);
                break;
            case IChannelListener.CMD_CHANNEL_EVENT_RECORD_TIME:
                // TODO: 2017/12/25
                fragmentRTVideo.updateRecordTime((String) param);
//                seconds = Integer.parseInt((String) param);
//                mHandler.postDelayed(runnable,1000);
//                Timer timer = new Timer();
//                timer.schedule(new RecordTimeTask(), 1000);
            case IChannelListener.CMD_CHANNEL_EVENT_START_LS:
                // TODO: 2017/12/27 开始发送获取视频的列表，需做刷新或提示
                break;
            case IChannelListener.CMD_CHANNEL_EVENT_LS:
                fragmentPlaybackList.updateDirContents((JSONObject) param);
                break;

            case IChannelListener.CMD_CHANNEL_EVENT_GET_THUMB_TEST:
                if ((boolean) param) {
                    fragmentPlaybackList.isYuvDownload = true;
                } else {
                    fragmentPlaybackList.isYuvDownload = false;
                }
                Log.e(TAG, "handleCmdChannelEvent: main EVENT_GET_THUMB");

                break;

            case IChannelListener.CMD_CHANNEL_EVENT_GET_THUMB_FAIL:
                if ((boolean) param) {
                    fragmentPlaybackList.isThumbGetFail = true;
                } else {
                    fragmentPlaybackList.isThumbGetFail = false;
                }
                Log.e(TAG, "handleCmdChannelEvent: main EVENT_THUMB_CHECK");
                break;
            default:
                break;

        }
    }

    public String getRecordTime() {
        return recordTime;
    }


    @Override
    public void onFragmentAction(int type, Object param, Integer... array) {
        switch (type) {
            case IFragmentListener.ACTION_PHOTO_START:
                mRemoteCam.takePhoto();
                break;
            case IFragmentListener.ACTION_RECORD_START:
                boolean isRecord = (boolean) param;
                if (isRecord) {
                    mRemoteCam.startRecord();
                } else {
                    mRemoteCam.stopRecord();
                }
                break;
            case IFragmentListener.ACTION_MIC_ON:
                boolean isMicOn = (boolean) param;
                if (isMicOn) {
                    mRemoteCam.startMic();
                } else {
                    mRemoteCam.stopMic();
                }
                break;
            case IFragmentListener.ACTION_RECORD_TIME:
                mRemoteCam.getRecordTime();
                break;
            case IFragmentListener.ACTION_FS_LS:
                mRemoteCam.listDir((String) param);
                break;

            default:
                break;
        }
    }

}
