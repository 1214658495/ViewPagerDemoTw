package com.byd.vtdr2;

//import android.app.FragmentLoading;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.byd.lighttextview.LightButton;
import com.byd.vtdr2.connectivity.IChannelListener;
import com.byd.vtdr2.connectivity.IFragmentListener;
import com.byd.vtdr2.fragment.FragmentPlaybackList;
import com.byd.vtdr2.fragment.FragmentRTVideo;
import com.byd.vtdr2.fragment.FragmentSetting;
import com.byd.vtdr2.utils.Config;
import com.byd.vtdr2.utils.DownloadUtil;
import com.byd.vtdr2.utils.Utility;
import com.byd.vtdr2.view.AddSingleButtonDialog;
import com.byd.vtdr2.view.MyDialog;
import com.byd.vtdr2.view.ProgressDialogFragment;
import com.byd.vtdr2.view.SingleButtonShowDialog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

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

    private static FragmentRTVideo fragmentRTVideo = FragmentRTVideo.newInstance();
    private static FragmentPlaybackList fragmentPlaybackList = FragmentPlaybackList.newInstance();
    private static FragmentSetting fragmentSetting = FragmentSetting.newInstance();
    private Fragment fragment;
    private String appStateStr;
    private MyDialog myDialog;
    private ArrayList<Model> selectedLists;
    private int selectedCounts;
    private int hadDelete;
    private int doingDownFileCounts = 0;
    private String mGetFileName;
    String dirName = Environment.getExternalStorageDirectory() + "/" + "行车记录仪/";
    private ProgressDialogFragment progressDialogFragment;
    private SingleButtonShowDialog singleButtonShowDialog;
    public static final int EXTERNAL_STORAGE_REQ_CODE = 10;
    public static final int VERSION_IS_NEWEST = 11;
    private volatile boolean isVersionNewest;

    private int newVerCode;
    private String newVerName = "", downloadURL = "", newVerDetail = "";
    private String newSaveApkName = "";
    private ProgressDialog prg_dialog;
    private String connect_err = " ", lastCar = "";
    private Handler handlerUpdate = new Handler();
    private BroadcastReceiver receiver;
    private boolean isNetworkConnected;
    private AddSingleButtonDialog addSingleButtonDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature();
        setContentView(R.layout.activity_main);
        requestPermission();
        checkUpdateThread();
        ButterKnife.bind(this);
        initConnect();
    }

    @Override
    protected void onResume() {
        super.onResume();
        /*receiverNetworkBroadcast();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION); //网络连接消息
//        filter.addAction(EthernetManager.ETHERNET_STATE_CHANGED_ACTION); //以太网消息
        this.registerReceiver(receiver, filter);*/
    }

    private void receiverNetworkBroadcast() {
        if (receiver == null) {
            receiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    //得到广播意图
                    final String action = intent.getAction();
                    //检查网络状态
                    if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {
                        Log.e(TAG, "ConnectivityManager.CONNECTIVITY_ACTION ");
                        //NetworkInfo info = intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
                        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//                        NetworkInfo networkInfo_4G = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
//                        NetworkInfo networkInfo_Wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
//                        NetworkInfo networkInfo_Eth = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET);
//                        if (networkInfo_4G != null || networkInfo_Wifi != null) {
//                            checkUpdateThread();
//                        }
//
//                        if (networkInfo_Eth != null && networkInfo_Eth.isConnected()) {
//                            dismissDialog();
//                            initConnect();
//                        }

//                        以太网是内网，优先级会降低。使用getActiveNetworkInfo得不到它。
//                        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
//                        Network  network = connectivityManager.getActiveNetwork();
//                        Network[] networks = connectivityManager.getAllNetworks();
                        //用于存放网络连接信息
//                        StringBuilder sb = new StringBuilder();
//                        //通过循环将网络信息逐个取出来
//                        for (int i=0; i < networks.length; i++){
//                            //获取ConnectivityManager对象对应的NetworkInfo对象
//                            NetworkInfo networkInfo = connectivityManager.getNetworkInfo(networks[i]);
//                            sb.append(networkInfo.getTypeName() + " connect is " + networkInfo.isConnected());
//                        }
//                        Log.e(TAG, "onReceive: networkInfo.getTypeName()" + sb);
//                        Toast.makeText(context, sb.toString(),Toast.LENGTH_LONG).show();
//                        NetworkInfo info = connectivityManager.getNetworkInfo(networks[1]);
//                        NetworkInfo info = connectivityManager.getNetworkInfo(network);
                        NetworkInfo info = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET);
                        if (info == null) {
                            isNetworkConnected = false;
//                            rbRealTimeVideo.setClickable(false);
                            showSingleButtonTipDialog("请打开网络连接...");

                            Toast.makeText(context, " no Network connection !", Toast.LENGTH_LONG).show();
                        } else {
                            int type = info.getType();
                            Log.e(TAG, "onReceive: type" + type);
                            NetworkInfo.State st;
                            android.net.NetworkInfo.State state = info.getState(); //得到此时的连接状态
                            if (type == ConnectivityManager.TYPE_MOBILE) {    //判断网络类型
                                Log.e(TAG, "TYPE_MOBILE ");
                                if (state == android.net.NetworkInfo.State.CONNECTED) {   //判断网络状态
                                    checkUpdateThread();

                                    Log.e(TAG, "MOBILE！CONNECTED");
//                                    Toast.makeText(context, "MOBILE！ connection successfully!", Toast.LENGTH_SHORT).show();
                                } else if (state == android.net.NetworkInfo.State.DISCONNECTED) {
                                    Log.e(TAG, "MOBILE！DISCONNECTED");
//                                    Toast.makeText(context, "MOBILE！DISCONNECTED", Toast.LENGTH_SHORT).show();
                                }
                            } else if (type == ConnectivityManager.TYPE_WIFI) { //WiFi
                                Log.e(TAG, "TYPE_WIFI ");
//                                Toast.makeText(context, "TYPE_WIFI ", Toast.LENGTH_SHORT).show();
                                if (state == android.net.NetworkInfo.State.CONNECTED) {   //判断网络状态
                                    Log.e(TAG, "WIFI！CONNECTED");
                                    WifiManager mgr = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                                    int ip = mgr.getConnectionInfo().getIpAddress();
                                    if ((ip & 0xFF) == 192 && (ip >> 8 & 0xFF) == 168 && (ip >> 16 & 0xFF) == 42) {
                                        dismissDialog();
                                        initConnect();
                                    } else {
                                        checkUpdateThread();
                                    }
//                                    Toast.makeText(context, "WIFI！ connection successfully!", Toast.LENGTH_SHORT).show();
                                } else if (state == android.net.NetworkInfo.State.DISCONNECTED) {
                                    Log.e(TAG, "WIFI！DISCONNECTED");
//                                    Toast.makeText(context, "WIFI！ DISCONNECTED!", Toast.LENGTH_SHORT).show();
                                }
                            } else if (type == ConnectivityManager.TYPE_ETHERNET) {
                                Log.e(TAG, "TYPE_ETHERNET ");
//                                Toast.makeText(context, "TYPE_ETHERNET", Toast.LENGTH_SHORT).show();
                                if (state == android.net.NetworkInfo.State.CONNECTED) {   //判断网络状态
                                    dismissDialog();
                                    initConnect();
                                    Log.e(TAG, "以太网！CONNECTED");
//                                    Toast.makeText(context, "以太网！ connection successfully!", Toast.LENGTH_SHORT).show();
                                } else if (state == android.net.NetworkInfo.State.DISCONNECTED) {
                                    Log.e(TAG, "以太网！DISCONNECTED");
//                                    Toast.makeText(context, "以太网！DISCONNECTED", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    } else {
                        Log.e(TAG, "other android.net.NetworkInfo.State");
                    }
                }
            };
        }
    }

    private void initConnect() {
        isNetworkConnected = true;
//        rbRealTimeVideo.setOnClickListener(null);
//        rbPlaybackList.setOnClickListener(null);
//        rbSetting.setOnClickListener(null);
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
                        fragment = fragmentRTVideo;
                        break;
                    case R.id.rb_playbackList:
                        fragment = fragmentPlaybackList;
                        break;
                    case R.id.rb_setting:
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
      /*  int type = NetworkUtils.getAPNType(getApplicationContext());
        if (type == ConnectivityManager.TYPE_WIFI) {
            WifiManager mgr = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            int ip = mgr.getConnectionInfo().getIpAddress();
            return String.format("%d.%d.%d.%d", (ip & 0xFF), (ip >> 8 & 0xFF), (ip >> 16 & 0xFF), ip
                    >> 24);
        } else if (type == ConnectivityManager.TYPE_ETHERNET) {
//            得到自己的ip
            return ServerConfig.PADIP;
//            return Settings.System.getString(getContentResolver(),Settings.System.);
        }
        return null;*/

        return ServerConfig.PADIP;
    }

    @Override
    protected void onPause() {
        super.onPause();
        // TODO: 2018/2/3 如下的作用不知
//        putPrefs(mPref);
//        此处解注册因为app从后台快速切换回来
//        unregisterReceiver(receiver);
//        Toast.makeText(this, "onPause", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    private void showSingleButtonTipDialog(String msg) {
        if (singleButtonShowDialog == null) {
            singleButtonShowDialog = SingleButtonShowDialog.newInstance(msg);
            singleButtonShowDialog.show(getFragmentManager(), "text");
            singleButtonShowDialog.setOnDialogButtonClickListener(new SingleButtonShowDialog.OnDialogButtonClickListener() {
                @Override
                public void okButtonClick() {
//                    showClickNetworkTipDialog();
                    if (singleButtonShowDialog != null) {
                        singleButtonShowDialog = null;
                    }
                    if (!isNetworkConnected) {
                        rbRealTimeVideo.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                showSingleButtonTipDialog("请连接到行车记录仪的网络");
                            }
                        });
                        rbPlaybackList.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                showSingleButtonTipDialog("请连接到行车记录仪的网络");
                            }
                        });
                        rbSetting.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                showSingleButtonTipDialog("请连接到行车记录仪的网络");
                            }
                        });
                    }
                }

                @Override
                public void cancelButtonClick() {

                }
            });
        }

       /* if (addSingleButtonDialog == null) {
            addSingleButtonDialog = AddSingleButtonDialog.newInstance(msg);
            addSingleButtonDialog.show(getFragmentManager(), "text");
            addSingleButtonDialog.setOnDialogButtonClickListener(new AddSingleButtonDialog.OnDialogButtonClickListener() {
                @Override
                public void okButtonClick() {
                    if (addSingleButtonDialog != null) {
                        addSingleButtonDialog = null;
                    }
                    if (!isNetworkConnected) {
                        rbRealTimeVideo.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                showSingleButtonTipDialog("请连接到行车记录仪的网络");
                            }
                        });
                        rbPlaybackList.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                showSingleButtonTipDialog("请连接到行车记录仪的网络");
                            }
                        });
                        rbSetting.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                showSingleButtonTipDialog("请连接到行车记录仪的网络");
                            }
                        });
                    }
                }

                @Override
                public void cancelButtonClick() {

                }
            });
        }*/
    }

    private void dismissDialog() {
        if (singleButtonShowDialog != null) {
            singleButtonShowDialog.dismiss();
        }
    }

    private void addSingleButtonDialog(String msg) {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        addSingleButtonDialog = AddSingleButtonDialog.newInstance(msg);
        if (addSingleButtonDialog != null) {
//                        DialogFragment dialogFragment = addSingleButtonDialog;
            if (!addSingleButtonDialog.isAdded()) {
                fragmentTransaction.add(addSingleButtonDialog, AddSingleButtonDialog.class.getName());
                if (!isFinishing() && !isDestroyed()) {
                    fragmentTransaction.commitAllowingStateLoss();
                }
            }
        }
        addSingleButtonDialog.setOnDialogButtonClickListener(new AddSingleButtonDialog.OnDialogButtonClickListener() {
            @Override
            public void okButtonClick() {

            }

            @Override
            public void cancelButtonClick() {

            }
        });
    }

    private void showMydialog(int style, String msg) {
        myDialog = MyDialog.newInstance(style, msg);
        // TODO: 2018/1/5 失败如何处理
        myDialog.show(getFragmentManager(), MyDialog.class.getName());
        myDialog.setOnDialogButtonClickListener(new MyDialog.OnDialogButtonClickListener() {
            @Override
            public void okButtonClick() {

            }

            @Override
            public void cancelButtonClick() {

            }
        });
    }

//    @Override
//    public void onConfigurationChanged(Configuration config) {
//        super.onConfigurationChanged(config);
//        setContentView(R.layout.activity_main);
//    }

    @OnClick(R.id.btn_back)
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                onBackPressed();
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
//        如下只有为getSupportFragmentManager时才能弹出
        int count = getSupportFragmentManager().getBackStackEntryCount();
        if (count == 0) {
            if (!isNetworkConnected) {
                finish();
                return;
            }
            if (fragment == fragmentPlaybackList && fragmentPlaybackList.isMultiChoose) {
                fragmentPlaybackList.cancelMultiChoose();
            } else {
                super.onBackPressed();
                mRemoteCam.stopSession();
                finish();
                Log.e(TAG, "kill the process to force fresh launch next time");
                Process.killProcess(Process.myPid());
            }
        } else {
            getSupportFragmentManager().popBackStack();
        }


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
                    case IChannelListener.DATA_CHANNEL_MSG:
                        handleDataChannelEvent(type, param);
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
                // TODO: 2018/1/8 旋转屏后dialog触发就闪退
                // TODO: 2018/3/9 当是其他提醒时没有提示了
                if ("CARD_REMOVED".equals(str)) {
                    str = "请插入存储卡！";
                    fragmentRTVideo.showCheckSdCordTag(false);
                    addSingleButtonDialog(str);
                } else if ("CARD_INSERTED".equals(str)) {
                    str = "存储卡已插入！";
//                    if (singleButtonShowDialog != null) {
//                        singleButtonShowDialog.dismiss();
//                    }
//                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
//                    Fragment fragment = getSupportFragmentManager().findFragmentByTag(AddSingleButtonDialog.class.getName());
//                    if (fragment != null) {
//                        addSingleButtonDialog.dismiss();
//                        if (!isFinishing() && !isDestroyed()) {
//                            fragmentTransaction.commitAllowingStateLoss();
//                        }
//                    }
                    if (addSingleButtonDialog != null) {
                        addSingleButtonDialog.dismiss();
                    }

                    fragmentRTVideo.showCheckSdCordTag(true);
                    // TODO: 2018/1/8 卡插入后，如何更新文件列表？
                } else {
                    addSingleButtonDialog(str);
                }
               /* if (myDialog != null) {
                    myDialog.dismiss();
                }
                myDialog = MyDialog.newInstance(1, str);
                myDialog.show(getFragmentManager(), "SHOW_ALERT");*/
                break;

            case IChannelListener.CMD_CHANNEL_EVENT_GET_SPACE:
                fragmentRTVideo.showCheckSdCordTag(false);
                // 如下操作不会闪退
                addSingleButtonDialog("请插入存储卡！");
                /*if (myDialog != null) {
                    myDialog.dismiss();
                }
                myDialog = MyDialog.newInstance(1, "请插入存储卡！");
                myDialog.show(getFragmentManager(), "SHOW_ALERT1");*/
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
            case IChannelListener.CMD_CHANNEL_EVENT_LOCK_VIDEO:
                Toast.makeText(getApplicationContext(), "锁定视频成功！", Toast.LENGTH_SHORT).show();
                break;
            case IChannelListener.CMD_CHANNEL_EVENT_APP_STATE:
//                boolean isRecord = (boolean) param;
//                Log.e(TAG, "handleCmdChannelEvent: isRecord = " + isRecord);
//                // TODO: 2017/12/20
                Log.e(TAG, "handleCmdChannelEvent: /////isGetFormatedAppState");
//                if (isFormat) {
//                    isGetFormatedAppState = true;
//                }
                appStateStr = (String) param;
                if (Objects.equals(appStateStr, "record")) {
                    fragmentRTVideo.setRecordState(true);
                } else if (Objects.equals(appStateStr, "vf")) {
                    fragmentRTVideo.setRecordState(false);
                } else if (Objects.equals(appStateStr, "idle")) {
//                    Toast.makeText(getApplicationContext(), "请重启记录仪！", Toast.LENGTH_LONG).show();
                    addSingleButtonDialog("请重启记录仪！");
                    // TODO: 2018/1/4 如下显示弹窗，旋转就闪退。
                }
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

            case IChannelListener.CMD_CHANNEL_EVENT_FORMAT_SD:
                boolean isFormatSD = (boolean) param;
                if (isFormatSD) {
                    myDialog = MyDialog.newInstance(1, "存储卡格式化完成！");
                    myDialog.show(getFragmentManager(), "FormatDone");
                    // TODO: 2018/1/5 如下发送后记录仪来不及反应，答复
//                    mRemoteCam.appStatus();
//                    mRemoteCam.startRecord();
//                                    fragmentPlaybackList.showSD();
                } else {
                    myDialog = MyDialog.newInstance(0, "存储卡格式化失败！");
                    // TODO: 2018/1/5 失败如何处理
                    myDialog.show(getFragmentManager(), "back");
                    myDialog.setOnDialogButtonClickListener(new MyDialog.OnDialogButtonClickListener() {
                        @Override
                        public void okButtonClick() {

                        }

                        @Override
                        public void cancelButtonClick() {

                        }
                    });

                }
                break;
            case IChannelListener.CMD_CHANNEL_EVENT_DEL:
                hadDelete++;
                if (hadDelete == selectedCounts) {
                    if (fragmentPlaybackList.currentRadioButton == ServerConfig.RB_RECORD_VIDEO) {
                        fragmentPlaybackList.showRecordList();
                    } else if (fragmentPlaybackList.currentRadioButton == ServerConfig.RB_LOCK_VIDEO) {
                        fragmentPlaybackList.showLockVideoList();
                    } else {
                        fragmentPlaybackList.showCapturePhotoList();
                    }
                    hadDelete = 0;
                }
                break;
            case IChannelListener.CMD_CHANNEL_EVENT_RECORD_START_FAIL:
                showMydialog(1, "开启录像失败！");
                fragmentRTVideo.setRecordState(false);
                break;
            case IChannelListener.CMD_CHANNEL_EVENT_RECORD_STOP_FAIL:
                showMydialog(1, "关闭录像失败！");
                fragmentRTVideo.setRecordState(true);
                break;
            default:
                break;

        }
    }

    private void handleDataChannelEvent(int type, Object param) {
        switch (type) {
            case IChannelListener.DATA_CHANNEL_EVENT_GET_START:
                myDialog = MyDialog.newInstance(2, "正在下载...");
                myDialog.show(getFragmentManager(), "doingDownload");
                myDialog.setOnDialogButtonClickListener(new MyDialog.OnDialogButtonClickListener() {
                    @Override
                    public void okButtonClick() {

                    }

                    @Override
                    public void cancelButtonClick() {

                    }
                });
                break;
            default:
                break;
        }
    }


    @Override
    public void onFragmentAction(int type, Object param, Integer... array) {
        switch (type) {
            case IFragmentListener.ACTION_PHOTO_START:
                mRemoteCam.takePhoto();
                break;
            case IFragmentListener.ACTION_LOCK_VIDEO_START:
                mRemoteCam.lockPhoto();
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
            case IFragmentListener.ACTION_DEFAULT_SETTING:
                mRemoteCam.defaultSetting();
                break;

            case IFragmentListener.ACTION_FS_FORMAT_SD:
//                isFormat = true;
//                mRemoteCam.stopRecord();
//                若关闭录像后马上发指令检测app的状态，则为idle；
//                mRemoteCam.appStatus();
//                while (!isGetFormatedAppState) {
//
//                }
//                isGetFormatedAppState = false;
//                isFormat = false;
//                if (Objects.equals(appStateStr, "vf")) {
                mRemoteCam.formatSD((String) param);
//                }
                break;
            case IFragmentListener.ACTION_FS_DELETE_MULTI:
                selectedLists = (ArrayList<Model>) param;
                selectedCounts = selectedLists.size();
                break;
            case IFragmentListener.ACTION_FS_DELETE:
                mRemoteCam.deleteFile((String) param);
                break;
            case IFragmentListener.ACTION_FS_DOWNLOAD:
                if (param != null) {

                } else {
                    downloadFiles();
                }
                break;
            default:
                break;
        }
    }

    private void downloadFiles() {
        if (progressDialogFragment != null) {
            progressDialogFragment = null;
        }
        for (int i = 0; i < selectedCounts; i++) {
            if (fragmentPlaybackList.currentRadioButton == ServerConfig.RB_RECORD_VIDEO) {
//                mGetFileName = "/tmp/SD0/NORMAL/" + selectedLists.get(doingDownFileCounts).getName();
                mGetFileName = "http://" + ServerConfig.VTDRIP + "/SD0/NORMAL/" + selectedLists.get(i).getName();
            } else if (fragmentPlaybackList.currentRadioButton == ServerConfig.RB_LOCK_VIDEO) {
//                mGetFileName = "/tmp/SD0/EVENT/" + selectedLists.get(doingDownFileCounts).getName();
                mGetFileName = "http://" + ServerConfig.VTDRIP + "/SD0/EVENT/" + selectedLists.get(i).getName();
            } else {
//                mGetFileName = "/tmp/SD0/PHOTO/" + selectedLists.get(doingDownFileCounts).getName();
                mGetFileName = "http://" + ServerConfig.VTDRIP + "/SD0/PHOTO/" + selectedLists.get(i).getName();
            }
//            doingDownFileCounts++;
            doingDownFileCounts = i;
            String fileName = Environment.getExternalStorageDirectory() + "/行车记录仪"
                    + mGetFileName.substring(mGetFileName.lastIndexOf('/'));
            File file = new File(fileName);
            if (!file.exists()) {
                final DownloadUtil downloadUtil = DownloadUtil.get();
                downloadUtil.download(mGetFileName, "行车记录仪", new DownloadUtil.OnDownloadListener() {
                    @Override
                    public void onDownloadStart() {
                        Log.e(TAG, "onDownloadStart: " + mGetFileName);
                        if (fragmentPlaybackList.currentRadioButton == ServerConfig.RB_RECORD_VIDEO
                                || fragmentPlaybackList.currentRadioButton == ServerConfig.RB_LOCK_VIDEO) {
                            synchronized (this) {
                                if (progressDialogFragment == null) {
                                    progressDialogFragment = ProgressDialogFragment.newInstance("正在下载...");
                                    progressDialogFragment.show(getFragmentManager(), "text");
                                    progressDialogFragment.setOnDialogButtonClickListener(new ProgressDialogFragment.OnDialogButtonClickListener() {
                                        @Override
                                        public void okButtonClick() {

                                        }

                                        @Override
                                        public void cancelButtonClick() {
                                            downloadUtil.cancelDownload();
                                        }
                                    });
                                }
                            }
                        }
                    }

                    @Override
                    public void onDownloadSuccess() {
                        Log.e(TAG, "onDownloadSuccess: 下载完成" + mGetFileName);
                        if (doingDownFileCounts == (selectedCounts - 1)) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    showTipDialog("下载完成！");
                                    doingDownFileCounts = 0;
                                }
                            });

                        }
                    }

                    @Override
                    public void onDownloading(final int progress) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (progressDialogFragment != null) {
                                    progressDialogFragment.setProgressText(progress);
                                }
                            }
                        });
                        Log.e(TAG, "onDownloading: 下载中" + progress);
                    }

                    @Override
                    public void onDownloadFailed() {
//                        Utils.showToast(MainActivity.this, "下载失败");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showTipDialog("下载失败");
                            }
                        });
                        Log.e(TAG, "onDownloadFailed: 下载失败");
                    }
                });
            } else {
                if (doingDownFileCounts == (selectedCounts - 1)) {
                    Toast.makeText(MainActivity.this, "文件已下载", Toast.LENGTH_SHORT).show();
                }
            }
        }

       /* if (doingDownFileCounts < selectedCounts) {
            if (fragmentPlaybackList.currentRadioButton == ServerConfig.RB_RECORD_VIDEO) {
//                mGetFileName = "/tmp/SD0/NORMAL/" + selectedLists.get(doingDownFileCounts).getName();
                mGetFileName = "http://192.168.42.1/SD0/NORMAL/" + selectedLists.get(doingDownFileCounts).getName();
            } else if (fragmentPlaybackList.currentRadioButton == ServerConfig.RB_LOCK_VIDEO) {
//                mGetFileName = "/tmp/SD0/EVENT/" + selectedLists.get(doingDownFileCounts).getName();
                mGetFileName = "http://192.168.42.1/SD0/EVENT/" + selectedLists.get(doingDownFileCounts).getName();
            } else {
//                mGetFileName = "/tmp/SD0/PHOTO/" + selectedLists.get(doingDownFileCounts).getName();
                mGetFileName = "http://192.168.42.1/SD0/PHOTO/" + selectedLists.get(doingDownFileCounts).getName();
            }
            // TODO: 2018/1/17 做下载
            DownloadUtil.get().download(mGetFileName, "行车记录仪", new DownloadUtil.OnDownloadListener() {
                @Override
                public void onDownloadSuccess() {
//                        Utils.showToast(MainActivity.this, "下载完成");
//                    showTipDialog("下载完成");

                    Log.e(TAG, "onDownloadSuccess: 下载完成");
                    downloadFiles();

                }
                @Override
                public void onDownloading(int progress) {
//                        progressBar.setProgress(progress);
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                showTip("下载中");
//                            }
//                        });
                    Log.e(TAG, "onDownloadSuccess: 下载中");
                }
                @Override
                public void onDownloadFailed() {
//                        Utils.showToast(MainActivity.this, "下载失败");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showTipDialog("下载失败");
                        }
                    });
                    Log.e(TAG, "onDownloadSuccess: 下载失败");
                }
            });*/

//            String fileName = Environment.getExternalStorageDirectory() + "/行车记录仪"
//                    + mGetFileName.substring(mGetFileName.lastIndexOf('/'));
//            File file = new File(fileName);
//            if (!file.exists()) {
//                mRemoteCam.getFile(mGetFileName);
//            } else {
//                doingDownFileCounts++;
//                showTipDialog("此文件已下载！");
//            }

        /*} else {
            doingDownFileCounts = 0;
            showTipDialog("下载完成！");
        }*/
    }

    public void showTipDialog(String msg) {
        if (myDialog != null) {
            myDialog.dismiss();
        }
        if (progressDialogFragment != null) {
            progressDialogFragment.dismiss();
        }
        myDialog = MyDialog.newInstance(1, msg);
        myDialog.show(getFragmentManager(), "showTipDialog");
    }

    public void requestPermission() {
        //判断当前Activity是否已经获得了该权限
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            //如果App的权限申请曾经被用户拒绝过，就需要在这里跟用户做出解释
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
//                Toast.makeText(this,"please give me the permission",Toast.LENGTH_SHORT).show();
                showTipDialog("请打开app的存储权限");
            } else {
                //进行权限请求
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        EXTERNAL_STORAGE_REQ_CODE);
            }
        }
    }

    private void checkUpdateThread() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                if (getUpdateServer()) {
                    int verCode = Utility
                            .getVerCode(MainActivity.this);
                    if (newVerCode > verCode) {
                        updateHandler.sendEmptyMessage(99);
                    } else {
                        updateHandler.sendEmptyMessage(VERSION_IS_NEWEST);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showSingleButtonTipDialog("请连接到行车记录仪的网络！");
                                // TODO: 2018/2/2 不再主线程可能显示不了！！！！！！
                            }
                        });
                    }
                }
            }
        }).start();
    }

    private boolean getUpdateServer() {
        try {
//            得到json地址的json文件，里面包含了下载地址
            String url = Utility.UPDATE_DIR + Utility.UPDATE_JSONVER;
//            String verjson = Utility.getContent(url);
            String verjson = DownloadUtil.get().getStringContent(url);
            JSONArray array = new JSONArray(verjson);
            if (array.length() > 0) {
                JSONObject obj = array.getJSONObject(0);
                try {
                    newVerCode = Integer.parseInt(obj.getString("verCode"));
                    newVerName = obj.getString("verName");
                    downloadURL = obj.getString("downloadURL");
                    newSaveApkName = obj.getString("apkname") + ".apk";
                    newVerDetail = obj.getString("detail");
                } catch (Exception e) {
                    // TODO: handle exception
                    newVerCode = -1;
                    newVerName = "";
                    return false;
                }
            }
        } catch (Exception e) {
            // TODO: handle exception
            //Log.e("connect to update error", e.toString());
            return false;
        }
        return true;
    }

    /**
     * 更新当前app
     */
    private void updateAppVer() {
        try {
            String detail = getVerDetail(newVerDetail);
            StringBuffer sb = new StringBuffer();
            sb.append(getString(R.string.appname_tag)).append("\n\n")
                    .append("V").append(newVerName)
                    .append(getString(R.string.features)).append("\n")
                    .append(detail);
            Dialog dialog = new AlertDialog.Builder(
                    MainActivity.this)
                    .setTitle(getString(R.string.strNewerVer))
                    .setMessage(sb.toString())
                    // 设置内容
                    .setPositiveButton(getString(R.string.updateNow),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                  /*  prg_dialog = new ProgressDialog(
                                            MainActivity.this);
                                    prg_dialog
                                            .setTitle(getString(R.string.isDownLoading));
                                    prg_dialog.setIndeterminate(false);
                                    prg_dialog.setCancelable(false);
                                    prg_dialog
                                            .setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                                    prg_dialog.setMax(100);
                                    // 获取系统设置
//                                    downLoadApk(downloadURL);*/
                                    downloadApkByOkhttp(downloadURL);
                                }
                            })
                    .setNegativeButton(getString(R.string.downloadlater),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int whichButton) {
                                    // 点击取消后
                                    dialog.dismiss();
                                }
                            }).create();
            // 显示对话框
            dialog.show();
        } catch (Exception e) {
            // TODO: handle exception
            connect_err = e.toString();
            updateHandler.sendEmptyMessage(Config.ERROR);
        }
    }

    /**
     * 根据中英文解析 版本详细信息
     *
     * @param strDetail
     * @return
     */
    private String getVerDetail(String strDetail) {

        int startIndx = 0, endIndx = 0;
        String strTemp = "";
        if (strDetail.length() > 10) {
            Locale local = Locale.getDefault();
            if (local.getLanguage().equalsIgnoreCase("zh")) {
                startIndx = strDetail.indexOf("##zh#");
            }
            if (local.getLanguage().equalsIgnoreCase("en")) {
                startIndx = strDetail.indexOf("##en#");
            }
            if (startIndx != -1) {
                endIndx = strDetail.indexOf("##", startIndx + 5);
                if (endIndx == -1) {
                    // 英文
                    strTemp = strDetail.substring(startIndx + 5);
                } else {// 表示中文
                    strTemp = strDetail.substring(startIndx + 5, endIndx - 1);
                }
            } else {
                strTemp = "";
            }
        }

        return strTemp;
    }

    private void downloadApkByOkhttp(String downloadURL) {
//        prg_dialog.show();
        final DownloadUtil downloadUtil = DownloadUtil.get();
        downloadUtil.get().download(downloadURL, "行车记录仪", new DownloadUtil.OnDownloadListener() {
            @Override
            public void onDownloadSuccess() {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        showTipDialog("下载完成！");
//                    }
//                });
                if (myDialog != null) {
                    myDialog.dismiss();
                }
                if (progressDialogFragment != null) {
                    progressDialogFragment.dismiss();
                }
                down();
            }

            @Override
            public void onDownloading(final int progress) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (progressDialogFragment != null) {
                            progressDialogFragment.setProgressText(progress);
                        }
                    }
                });
            }

            @Override
            public void onDownloadFailed() {

            }

            @Override
            public void onDownloadStart() {
                if (myDialog != null) {
                    myDialog.dismiss();
                }
                if (progressDialogFragment == null) {
                    progressDialogFragment = ProgressDialogFragment.newInstance("正在下载...");
                    progressDialogFragment.show(getFragmentManager(), "text");
                    progressDialogFragment.setOnDialogButtonClickListener(new ProgressDialogFragment.OnDialogButtonClickListener() {
                        @Override
                        public void okButtonClick() {

                        }

                        @Override
                        public void cancelButtonClick() {
                            downloadUtil.cancelDownload();
                        }
                    });
                }

            }
        });
    }

    void down() {
        handlerUpdate.post(new Runnable() {
            @Override
            public void run() {
//                prg_dialog.cancel();
                update();
            }
        });
    }

    private void update() {
        String fileName = Environment.getExternalStorageDirectory() + "/行车记录仪"
                + "/vtdr.apk";
        File file = new File(fileName);
        if (!file.exists()) {
            return;
        }

        Intent intent = new Intent(Intent.ACTION_VIEW);
        //判断是否是AndroidN以及更高的版本
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(MainActivity.this, BuildConfig
                    .APPLICATION_ID + "" +
                    ".fileProvider", file);
//                    ".fileProvider", updateFile);
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        startActivity(intent);
    }

    private Handler updateHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Intent intent = null;
            switch (msg.what) {
//                case Config.DOWNLOAD_VALUE:// 更新下载进度
//                    prg_dialog.setProgress((int) (nDownloaded * 100 / fileSize));
//                    break;
//                case Config.DOWNLOAD_SUCCESS:// 下载完成
//                    prg_dialog.dismiss();
//                    break;
//                case Config.DOWNLOAD_FAIL:
//                    Toast.makeText(MainActivity.this,
//                            getString(R.string.strFectch_NewerVer_error),
//                            Toast.LENGTH_LONG).show();
//                    prg_dialog.dismiss();
//                    break;
                case Config.ERROR:
                    Toast.makeText(MainActivity.this, connect_err,
                            Toast.LENGTH_LONG).show();
                    break;
                case 99:
                    updateAppVer();
                    break;
                case VERSION_IS_NEWEST:
                    isVersionNewest = true;
                    break;
                default:
                    break;
            }
        }
    };

}
