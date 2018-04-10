package com.byd.vtdr2;

//import android.app.FragmentLoading;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.ContentObserver;
import android.database.Cursor;
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
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.byd.lighttextview.LightButton;
import com.byd.lighttextview.LightRadioButton;
import com.byd.vtdr2.connectivity.IChannelListener;
import com.byd.vtdr2.connectivity.IFragmentListener;
import com.byd.vtdr2.fragment.FragmentPlaybackList;
import com.byd.vtdr2.fragment.FragmentRTVideo;
import com.byd.vtdr2.fragment.FragmentSetting;
import com.byd.vtdr2.utils.Config;
import com.byd.vtdr2.utils.DownloadUtil;
import com.byd.vtdr2.utils.Utility;
import com.byd.vtdr2.view.AddSingleButtonDialog;
import com.byd.vtdr2.view.CustomDialog;
import com.byd.vtdr2.view.MyDialog;
import com.byd.vtdr2.view.ProgressDialogFragment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

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
    private static Fragment fragment;
    private String appStateStr;
    private MyDialog myDialog;
    private ArrayList<Model> selectedLists;
    private static int selectedCounts;
    private int hadDelete;
    private int doingDownFileCounts = 0;
    private String mGetFileName;
    String dirName = Environment.getExternalStorageDirectory() + "/" + "行车记录仪/";
    private ProgressDialogFragment progressDialogFragment;
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
    private static CustomDialog customDialog = null;

    //控制弹出框的显示，页面切换网络错误时，弹出一次控制
    public static boolean isDialogShow = false;
    private static Toast toast;
    //    private Toast mToast;
    private final ScheduledExecutorService worker =
            Executors.newSingleThreadScheduledExecutor();
    private static ScheduledFuture<?> mScheduledTask;
    private boolean isReconnecting;

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
      /*  receiverNetworkBroadcast();
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
                        } else {
                          /*  if (!info.isConnected()) {
//                                showToastTips("以太网断开");
                                showConfirmDialog("以太网断开");
                                isDialogShow = true;
//                                Toast.makeText(this,Toast.LENGTH_SHORT,"以太网断开").show();
                            } else {
//                                showToastTips("以太网已连接");
                                showConfirmDialog("以太网已连接");

                            }*/
                        }
                       /* if (info == null) {
                            isNetworkConnected = false;
//                            rbRealTimeVideo.setClickable(false);
                            showSingleButtonTipDialog(getString(R.string.connect_fail));

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
                        }*/
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
        isDialogShow = false;
        mScheduledTask = worker.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                mRemoteCam.socketTest();
            }
        }, 0, 3, TimeUnit.SECONDS);
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
        if (fragment == null) {
            fragment = fragmentRTVideo;
            getSupportFragmentManager().beginTransaction().replace(flMain.getId(), fragment)
                    .commitAllowingStateLoss();
            rgGroup.check(R.id.rb_realTimeVideo);
        }


        rbRealTimeVideo.setOnCheckedChangeListener(new LightRadioButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (fragment != fragmentRTVideo) {
                        if (fragmentPlaybackList.fragmentVideoPreview != null) {
                            if (fragmentPlaybackList.fragmentVideoPreview.isVisible() ||
                                    fragmentPlaybackList.fragmentVideoPreview.reload) {
                                //getSupportFragmentManager().popBackStack();
                                fragmentPlaybackList.fragmentVideoPreview.reload = false;
                                fragmentPlaybackList.getFragmentManager().popBackStack();
                            }
                        } else if (fragmentPlaybackList.fragmentPhotoPreview != null) {
                            if (fragmentPlaybackList.fragmentPhotoPreview.isVisible()
                                    || fragmentPlaybackList.fragmentPhotoPreview.reload) {
                                fragmentPlaybackList.fragmentPhotoPreview.reload = false;
                                fragmentPlaybackList.getFragmentManager().popBackStack();
                            }
                        }
                        fragment = fragmentRTVideo;
                        getSupportFragmentManager().beginTransaction().replace(flMain.getId(), fragment).commitAllowingStateLoss();
                    }
                }
            }
        });
        rbPlaybackList.setOnCheckedChangeListener(new LightRadioButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (fragment != fragmentPlaybackList) {
                        fragment = fragmentPlaybackList;
                        getSupportFragmentManager().beginTransaction().replace(flMain.getId(), fragment).commitAllowingStateLoss();
                    }

                }
            }
        });
        rbSetting.setOnCheckedChangeListener(new LightRadioButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (fragment != fragmentSetting) {
                        if (fragmentPlaybackList.fragmentVideoPreview != null) {
                            if (fragmentPlaybackList.fragmentVideoPreview.isVisible() ||
                                    fragmentPlaybackList.fragmentVideoPreview.reload) {
                                // getSupportFragmentManager().popBackStack();
                                fragmentPlaybackList.fragmentVideoPreview.reload = false;
                                fragmentPlaybackList.getFragmentManager().popBackStack();
                            }

                        } else if (fragmentPlaybackList.fragmentPhotoPreview != null) {
                            if (fragmentPlaybackList.fragmentPhotoPreview.isVisible()
                                    || fragmentPlaybackList.fragmentPhotoPreview.reload) {
                                fragmentPlaybackList.fragmentPhotoPreview.reload = false;
                                fragmentPlaybackList.getFragmentManager().popBackStack();
                            }
                        }
                        fragment = fragmentSetting;
                        getSupportFragmentManager().beginTransaction().replace(flMain.getId(), fragment).commitAllowingStateLoss();
                    }
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
//        int type = NetworkUtils.getAPNType(getApplicationContext());
//        if (type == ConnectivityManager.TYPE_WIFI) {
//            WifiManager mgr = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
//            int ip = mgr.getConnectionInfo().getIpAddress();
//            return String.format("%d.%d.%d.%d", (ip & 0xFF), (ip >> 8 & 0xFF), (ip >> 16 & 0xFF), ip
//                    >> 24);
//        } else if (type == ConnectivityManager.TYPE_ETHERNET) {
////            得到自己的ip
//            return ServerConfig.PADIP;
////            return Settings.System.getString(getContentResolver(),Settings.System.);
//        }
//        return null;
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
        if (customDialog != null) {
            customDialog = null;
        }
        if (mScheduledTask != null) {
            mScheduledTask.cancel(false);
        }

    }


/*    private void showAddSingleButtonDialog(String msg) {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        addSingleButtonDialog = AddSingleButtonDialog.newInstance(msg);
        if (addSingleButtonDialog != null) {
//                        DialogFragment dialogFragment = showAddSingleButtonDialog;
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
        // TODO: 2018/3/13 旋转后，再弹会闪退
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
    }*/

    private void showToastTips(String tips) {
        if (toast == null) {
            toast = Toast.makeText(this, tips, Toast.LENGTH_SHORT);
        } else {
            toast.setText(tips);
        }
        toast.show();

       /* if (mToast != null) {
            mToast.cancel();
        }
        mToast = Toast.makeText(this, tips, Toast.LENGTH_SHORT);
        mToast.show();*/
    }

    private void showConfirmDialog(String tips) {
        if (customDialog != null && !isFinishing()) {
            customDialog.dismiss();
        }
        CustomDialog.Builder builder = new CustomDialog.Builder(this);
        customDialog = builder.cancelTouchOut(false)
                .view(R.layout.fragment_custom_dialog)
                .style(R.style.CustomDialog)
                .setTitle(tips)
                .addViewOnclick(R.id.btn_dialogSure, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        customDialog.dismiss();
                        isDialogShow = false;
                    }
                })
                .build();
        customDialog.show();
    }

    private void showCrossDialog(String tips) {
        if (customDialog != null && !isFinishing()) {
            customDialog.dismiss();
        }
        CustomDialog.Builder builder = new CustomDialog.Builder(this);
        customDialog = builder.cancelTouchOut(true)
                .view(R.layout.fragment_cross_dialog)
                .style(R.style.CustomDialog)
                .setTitle(tips)
                .addViewOnclick(R.id.btn_closeDialog, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        customDialog.dismiss();
                    }
                })
                .build();
        customDialog.show();
    }

    private void showDoubleButtonDialog(String tips) {
        if (customDialog != null && !isFinishing()) {
            customDialog.dismiss();
        }
        CustomDialog.Builder builder = new CustomDialog.Builder(this);
        customDialog = builder.cancelTouchOut(false)
                .view(R.layout.fragment_doublebutton_dialog)
                .style(R.style.CustomDialog)
                .setTitle(tips)
                .addViewOnclick(R.id.btn_dialogSure, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mRemoteCam.formatSD("C:");
                        customDialog.dismiss();
                    }
                })
                .addViewOnclick(R.id.btn_dialogCancel, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        customDialog.dismiss();
                    }
                })
                .build();
        customDialog.show();
    }

    private void showWaitingDialog(String tips) {
        if (customDialog != null && !isFinishing()) {
            customDialog.dismiss();
        }
        CustomDialog.Builder builder = new CustomDialog.Builder(this);
        customDialog = builder.cancelTouchOut(false)
                .view(R.layout.fragment_waiting_dialog)
                .style(R.style.CustomDialog)
                .setTitle(tips)
                .build();
        customDialog.show();
    }

    @Override
    public void onConfigurationChanged(Configuration config) {
        super.onConfigurationChanged(config);
//        setContentView(R.layout.activity_main);
    }

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
                        return;
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
        if (type >= 80) {
            handleCmdChannelError(type, param);
            return;
        }

        switch (type) {
            case IChannelListener.CMD_CHANNEL_EVENT_SHOW_ALERT:
                String str = (String) param;
                // TODO: 2018/1/8 旋转屏后dialog触发就闪退
                // TODO: 2018/3/9 当是其他提醒时没有提示了
                if ("CARD_REMOVED".equals(str)) {
                    str = getString(R.string.card_removed);
                    fragmentRTVideo.showCheckSdCordTag(false);
//                    showAddSingleButtonDialog(str);
                    showConfirmDialog(str);
                } else if ("CARD_INSERTED".equals(str)) {
                    str = getString(R.string.card_inserted);
//                    if (singleButtonShowDialog != null) {
//                        singleButtonShowDialog.dismiss();
//                    }
//                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
//                    Fragment fragment = getSupportFragmentManager().findFragmentByTag(AddSingleButtonDialog.class.getName());
//                    if (fragment != null) {
//                        showAddSingleButtonDialog.dismiss();
//                        if (!isFinishing() && !isDestroyed()) {
//                            fragmentTransaction.commitAllowingStateLoss();
//                        }
//                    }
//                    if (addSingleButtonDialog != null) {
//                        addSingleButtonDialog.dismiss();
//                    }
                    if (customDialog != null && !isFinishing()) {
                        customDialog.dismiss();
                    }
                    showWaitingDialog(getString(R.string.card_readying));
//                    showToastTips(str);
                    new java.util.Timer().schedule(
                            new java.util.TimerTask() {
                                @Override
                                public void run() {
                                    initConnect();
                                    if (customDialog != null && !isFinishing()) {
                                        customDialog.dismiss();
                                    }
                                    if (fragment == fragmentRTVideo) {
                                        rgGroup.check(R.id.rb_realTimeVideo);
                                        fragmentRTVideo = FragmentRTVideo.newInstance();
                                        fragment = fragmentRTVideo;
                                        getSupportFragmentManager().beginTransaction().replace(flMain.getId(), fragment).commitAllowingStateLoss();
                                    }
//                                    mRemoteCam.appStatus();
                                }
                            }, 4000);

                    fragmentRTVideo.showCheckSdCordTag(true);
                    // TODO: 2018/1/8 卡插入后，如何更新文件列表？
                } else if ("CONNECT_FAIL".equals(str)) {
                    str = getString(R.string.connect_fail);
//                    showAddSingleButtonDialog(getString(R.string.connect_fail));

                   /* if (fragment == fragmentRTVideo) {
                        fragmentRTVideo.showAddSingleButtonDialogFrgRT(getString(R.string.connect_fail));
                    } else if (fragment == fragmentPlaybackList) {
                        fragmentPlaybackList.showAddSingleButtonDialogFrgPL(getString(R.string.connect_fail));
                    } else if (fragment == fragmentSetting) {
                        fragmentSetting.showAddSingleButtonDialogFrgSET(getString(R.string.connect_fail));
                    }*/

                    /*customDialog = new TestDialog(this,R.style.TestDialog);
                    customDialog.show();
                    customDialog.setTitle(getString(R.string.connect_fail));*/
                    if (!isDialogShow) {
                        showConfirmDialog(str);
                        isDialogShow = true;
                    }
                } else {
//                    showAddSingleButtonDialog(str);
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
//                showAddSingleButtonDialog(getString(R.string.card_removed));
                showConfirmDialog(getString(R.string.card_removed));
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
                boolean isCapturePhoto = (boolean) param;
                if (isCapturePhoto) {
                    showToastTips(getString(R.string.Pictures_success));
                } else {
                    showToastTips(getString(R.string.Pictures_fail));
                }
                break;
            case IChannelListener.CMD_CHANNEL_EVENT_LOCK_VIDEO:
                boolean isLockPhoto = (boolean) param;
                if (isLockPhoto) {
                    showToastTips(getString(R.string.LockVideo_success));
                } else {
                    showToastTips(getString(R.string.LockVideo_fail));
                }
                break;
            case IChannelListener.CMD_CHANNEL_EVENT_FRIMWORK_VERSION:
                String str1 = (String) param;
                fragmentSetting.getfirmwareVersion(str1);
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
//                    showAddSingleButtonDialog(getString(R.string.reboot_drivingReorder));
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
                    showCrossDialog(getString(R.string.format_ok));
                    /*myDialog = MyDialog.newInstance(1, getString(R.string.format_ok));
                    myDialog.show(getFragmentManager(), "FormatDone");*/
                    // TODO: 2018/1/5 如下发送后记录仪来不及反应，即无应答
//                    mRemoteCam.appStatus();
//                    mRemoteCam.startRecord();
//                                    fragmentPlaybackList.showSD();
                } else {
                    showDoubleButtonDialog(getString(R.string.format_fail));
                   /* myDialog = MyDialog.newInstance(0, getString(R.string.format_fail));
                    // TODO: 2018/1/5 失败如何处理
                    myDialog.show(getFragmentManager(), "back");
                    myDialog.setOnDialogButtonClickListener(new MyDialog.OnDialogButtonClickListener() {
                        @Override
                        public void okButtonClick() {

                        }

                        @Override
                        public void cancelButtonClick() {

                        }
                    });*/

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
//                showMydialog(1, "开启录像失败！");
                showToastTips(getString(R.string.OpenVideo_success));
                fragmentRTVideo.setRecordState(false);
                break;
            case IChannelListener.CMD_CHANNEL_EVENT_RECORD_STOP_FAIL:
//                showMydialog(1, "关闭录像失败！");
                showToastTips(getString(R.string.OpenVideo_fail));
                fragmentRTVideo.setRecordState(true);
                break;

            case IChannelListener.CMD_CHANNEL_EVENT_WAKEUP_START:
//                showToastTips("Waking up the Remote Camera START");
                Log.e(TAG, "handleCmdChannelEvent: Waking up the Remote Camera START");
                break;
            case IChannelListener.CMD_CHANNEL_EVENT_CONNECTED:
            case IChannelListener.CMD_CHANNEL_EVENT_WAKEUP_OK:
                if (isReconnecting) {
                    if (customDialog != null && !isFinishing()) {
                        customDialog.dismiss();
                    }
                    initConnect();
//                    if (fragment == fragmentRTVideo) {
                    rgGroup.check(R.id.rb_realTimeVideo);
                    fragmentRTVideo = FragmentRTVideo.newInstance();
                    fragment = fragmentRTVideo;
                    getSupportFragmentManager().beginTransaction().replace(flMain.getId(), fragment).commitAllowingStateLoss();
//                    }
//                    else if (fragment == fragmentPlaybackList) {
//                        fragmentPlaybackList = FragmentPlaybackList.newInstance();
//                        fragment = fragmentPlaybackList;
//                        getSupportFragmentManager().beginTransaction().replace(flMain.getId(), fragment).commitAllowingStateLoss();
//                    }

                }
                isReconnecting = false;
//                dismissDialog();
//                showToastTips("Waking up the Remote Camera OK");
//                Log.e(TAG, "handleCmdChannelEvent: Waking up the Remote Camera OK");
                break;
            default:
                break;

        }
    }

    private void handleDataChannelEvent(int type, Object param) {
        switch (type) {
            case IChannelListener.DATA_CHANNEL_EVENT_GET_START:
                myDialog = MyDialog.newInstance(2, getString(R.string.downloading));
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

    private void handleCmdChannelError(int type, Object param) {
        switch (type) {
            case IChannelListener.CMD_CHANNEL_ERROR_INVALID_TOKEN:
//                isConnected = false;
//                if (mCurrentFrag == mRecordFrag) {
//                    mRecordFrag.hideTimer();
//                }
//                showAlertDialog("Error", "Invalid Session! Please start session first!");
                break;
            case IChannelListener.CMD_CHANNEL_ERROR_TIMEOUT:
//                isConnected = false;
//                showAlertDialog("Error", "Timeout! No response from Remote Camera!");
                if (!isDialogShow) {
                    showConfirmDialog("连接超时，记录仪无应答");
                    isDialogShow = true;
                }
//                if (mScheduledTask != null) {
//                    mScheduledTask.cancel(false);
//                }
                break;
            case IChannelListener.CMD_CHANNEL_ERROR_BLE_INVALID_ADDR:
//                showAlertDialog("Error", "Invalid bluetooth device");
                break;
            case IChannelListener.CMD_CHANNEL_ERROR_BLE_DISABLED:
                break;
            case IChannelListener.CMD_CHANNEL_ERROR_BROKEN_CHANNEL:
//                isConnected = false;
//                showAlertDialog("Error", "Lost connection with Remote Camera!");
//                resetRemoteCamera();
                break;
            case IChannelListener.CMD_CHANNEL_ERROR_CONNECT:
//                isConnected = false;
//                showAlertDialog("Error", "Cannot connect to the Camera. \n" + "Please make sure " +
//                        "the selected camera is on. \n" + "If problem persists, please reboot " +
//                        "both camera and this device.");
                break;
            case IChannelListener.CMD_CHANNEL_ERROR_WAKEUP:
//                showAlertDialog("Error", "Cannot wakeup the Remote Camera");
//                showToastTips(getString(R.string.time_out));
                if (!isDialogShow) {
                    showConfirmDialog(getString(R.string.time_out));
                    isDialogShow = true;
                }
                isReconnecting = true;

//                if (mScheduledTask != null) {
//                    mScheduledTask.cancel(true);
//                }
                Log.e(TAG, "handleCmdChannelEvent: Waking up the Remote Camera ERROR");
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
            case IFragmentListener.ACTION_FRIMWORK_VERSION:
                mRemoteCam.frimworkVersion();
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
                // TODO: 2018/4/3
//                getTimeTestNet();
//                mRemoteCam.getRecordTime();
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

    private void getTimeTestNet() {
//        mScheduledTask = worker.scheduleAtFixedRate(new Runnable() {
//            @Override
//            public void run() {
//                mRemoteCam.wakeUp();
//            }
//        }, 0, 4, TimeUnit.SECONDS);
        mScheduledTask = worker.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                mRemoteCam.socketTest();
            }
        }, 0, 3, TimeUnit.SECONDS);
    }

    /*
    *
    *
    *3.30 add
    * */
// 查询下载进度，文件总大小多少，已经下载多少？
    private long[] Id;

    public static final Uri CONTENT_URI = Uri.parse("content://downloads/my_downloads");
    private int newsize = 0, totalsize = 0;
    private int IDcount = 0;
    //获取下载管理器
    private DownloadManager downloadManager;

    class DownloadChangeObserver extends ContentObserver {

        public DownloadChangeObserver(Handler handler) {
            super(handler);
            // TODO Auto-generated constructor stub
        }

        @Override
        public void onChange(boolean selfChange) {
            queryDownloadStatus();
        }
    }

    public static double div(double v1, double v2, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException("The scale must be a positive integer or zero");
        }
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    private void queryDownloadStatus() {
        DownloadManager.Query query = new DownloadManager.Query();
        try {
            query.setFilterById(Id[IDcount-1]);
            Cursor c = downloadManager.query(query);
            if (c != null && c.moveToFirst()) {
                int status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));

                int reasonIdx = c.getColumnIndex(DownloadManager.COLUMN_REASON);
                int titleIdx = c.getColumnIndex(DownloadManager.COLUMN_TITLE);
                int fileSizeIdx = c.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES);
                int bytesDLIdx = c.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR);
                String title = c.getString(titleIdx);
                int fileSize = c.getInt(fileSizeIdx);
                int bytesDL = c.getInt(bytesDLIdx);
                newsize = bytesDL;
                totalsize = fileSize;
                // Translate the pause reason to friendly text.
                int reason = c.getInt(reasonIdx);
                StringBuilder sb = new StringBuilder();
                sb.append(title).append("\n");
                sb.append("Downloaded ").append(bytesDL).append(" / ").append(fileSize);

                // Display the status
                Log.d("tag", sb.toString());
                switch (status) {
                    case DownloadManager.STATUS_PAUSED:
                        Log.v("tag", "STATUS_PAUSED");
                        for (int j = 0; j < IDcount; j++) {
                            downloadManager.remove(Id[j]);
                        }
                        progressDialogFragment.dismissAllowingStateLoss();
                        Toast.makeText(MainActivity.this, "下载失败！", Toast
                                .LENGTH_SHORT).show();

                    case DownloadManager.STATUS_PENDING:
                        Log.v("tag", "STATUS_PENDING");
                    case DownloadManager.STATUS_RUNNING:
                        // 正在下载，不做任何事情
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //更新UI
                                double temp = div((double) newsize, (double) totalsize, 3);
                                progressDialogFragment.setProgressText((int) (temp * 100.00));

                            }
                        });
                        Log.v("tag", "STATUS_RUNNING");
                        break;
                    case DownloadManager.STATUS_SUCCESSFUL:
                        // 完成
                        Log.v("tag", "下载完成");
                        progressDialogFragment.dismissAllowingStateLoss();
                        break;
                    case DownloadManager.STATUS_FAILED:
                        // 清除已下载的内容，重新下载
                        Log.v("tag", "STATUS_FAILED");
                        downloadManager.remove(Id[IDcount-1]);
                        break;
                }
            }
        } catch (Exception e ) {
            e.printStackTrace();
        }

    }

    private void downloadFiles()
    {
        IDcount = 0;
        if (selectedCounts>5)
        {
            progressDialogFragment = ProgressDialogFragment.newInstance("请选择少于5个文件下载");
            progressDialogFragment.show(getFragmentManager(), "text");
            progressDialogFragment.setOnDialogButtonClickListener(new ProgressDialogFragment
                    .OnDialogButtonClickListener() {
                @Override
                public void okButtonClick() {

                }
                @Override
                public void cancelButtonClick() {

                }
            });
            return;
        }
        else
        if (selectedCounts == 0)
        {
            if (!isDialogShow) {
                showConfirmDialog("请选择文件！");
                isDialogShow = true;
            }
            return;
        }
        Id = new long[selectedCounts];
        boolean showdialog = false;
        downloadManager = (DownloadManager) getApplicationContext().getSystemService(Context
                .DOWNLOAD_SERVICE);
        ContentObserver mObserver;
        mObserver = new DownloadChangeObserver(null);
        getContentResolver().registerContentObserver(CONTENT_URI, true, mObserver);

        for (int i = 0; i < selectedCounts; i++) {
            if (fragmentPlaybackList.currentRadioButton == ServerConfig.RB_RECORD_VIDEO) {
                mGetFileName = "http://" + ServerConfig.VTDRIP + "/SD0/NORMAL/" + selectedLists
                        .get(i).getName();
            } else if (fragmentPlaybackList.currentRadioButton == ServerConfig.RB_LOCK_VIDEO) {
                mGetFileName = "http://" + ServerConfig.VTDRIP + "/SD0/EVENT/" + selectedLists
                        .get(i).getName();
            } else {
                mGetFileName = "http://" + ServerConfig.VTDRIP + "/SD0/PHOTO/" + selectedLists
                        .get(i).getName();
            }
            String fileName = Environment.getExternalStorageDirectory() + "/行车记录仪" + mGetFileName
                    .substring(mGetFileName.lastIndexOf('/'));
            File file = new File(fileName);
            if (!file.exists()) {
                //创建下载任务,downloadUrl就是下载链接
                DownloadManager.Request request = new DownloadManager.Request(Uri.parse
                        (mGetFileName));
                //指定下载路径和下载文件名
                request.setDestinationInExternalPublicDir("/行车记录仪/", selectedLists.get(i).getName
                        ());
                //不显示下载界面
                request.setVisibleInDownloadsUi(true);
                //将下载任务加入下载队列，否则不会进行下载
                Id[IDcount] = downloadManager.enqueue(request);
                IDcount++;
                showdialog = true;

            } else {
                Toast.makeText(MainActivity.this, R.string.File_downloaded, Toast
                        .LENGTH_SHORT).show();
                // getContentResolver().unregisterContentObserver(mObserver);

            }
        }
        if (showdialog) {
            progressDialogFragment = ProgressDialogFragment.newInstance(getString(R.string
                    .downloading));
            progressDialogFragment.show(getFragmentManager(), "text");
            progressDialogFragment.setOnDialogButtonClickListener(new ProgressDialogFragment
                    .OnDialogButtonClickListener() {
                @Override
                public void okButtonClick() {

                }

                @Override
                public void cancelButtonClick() {
                    for (int j = 0; j < Id.length; j++) {
                        downloadManager.remove(Id[j]);
                    }
                }
            });
        }

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
                showTipDialog(getString(R.string.open_permissions));
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
//                                showSingleButtonTipDialog(getString(R.string.connect_fail));
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
                    progressDialogFragment = ProgressDialogFragment.newInstance(getString(R.string.downloading));
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
