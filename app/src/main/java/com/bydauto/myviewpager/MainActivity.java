package com.bydauto.myviewpager;

//import android.app.FragmentLoading;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
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
import com.bydauto.myviewpager.utils.DownloadUtil;
import com.bydauto.myviewpager.view.MyDialog;
import com.bydauto.myviewpager.view.ProgressDialogFragment;
import com.liulishuo.filedownloader.FileDownloader;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
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
    private String appStateStr;
    private MyDialog myDialog;
    private boolean isGetFormatedAppState = false;
    private boolean isFormat = false;
    private ArrayList<Model> selectedLists;
    private int selectedCounts;
    private int hadDelete;
    private int doingDownFileCounts;
    private String mGetFileName;
    String dirName = Environment.getExternalStorageDirectory() + "/" + "行车记录仪/";
    private ProgressDialogFragment progressDialogFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
//        Aria.download(this).register();
        FileDownloader.setup(getApplicationContext());
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


//    @Override
//    public void onConfigurationChanged(Configuration config) {
//        super.onConfigurationChanged(config);
//        setContentView(R.layout.activity_main);
//    }

    @OnClick(R.id.btn_back)
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                String url = "http://192.168.42.1/SD0/PHOTO/2018-01-09-20-57-1400.JPG";
                FileDownloader.getImpl().create(url).setPath(dirName + "2018-01-09-20-57-1400.JPG").start();
            /*    //创建下载任务,downloadUrl就是下载链接
                DownloadManager.Request request = new DownloadManager.Request(Uri.parse("http://192.168.42.1/SD0/PHOTO/2018-01-12-17-59-0300.JPG"));
//指定下载路径和下载文件名
                request.setDestinationInExternalPublicDir("/行车记录仪/", "2018-01-12-17-59-0300.JPG");
//获取下载管理器
                DownloadManager downloadManager= (DownloadManager) getApplicationContext().getSystemService(Context.DOWNLOAD_SERVICE);
//将下载任务加入下载队列，否则不会进行下载
                downloadManager.enqueue(request);*/



                /*File file = new File(dirName + "2018-01-12-17-59-0300.JPG");
                if (!file.exists()) {
                    Aria.download(this).load("http://192.168.42.1/SD0/PHOTO/2018-01-12-17-59-0300.JPG")
                            .setDownloadPath(dirName + "2018-01-12-17-59-0300.JPG").start();

                } else {
                    Log.e(TAG, "onViewClicked: file.exists");
                }*/
              /*  myDialog = MyDialog.newInstance(0, "退出程序？");
                myDialog.show(getFragmentManager(), "back");
                myDialog.setOnDialogButtonClickListener(new MyDialog.OnDialogButtonClickListener() {
                    @Override
                    public void okButtonClick() {

                    }

                    @Override
                    public void cancelButtonClick() {

                    }
                });*/
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
                if ("CARD_REMOVED".equals(str)) {
                    str = "存储卡被移除！";
                } else if ("CARD_INSERTED".equals(str)) {
                    str = "存储卡已插入！";
                    // TODO: 2018/1/8 卡插入后，如何更新文件列表？
                }
                if (myDialog != null) {
                    myDialog.dismiss();
                }
                myDialog = MyDialog.newInstance(1, str);
                myDialog.show(getFragmentManager(), "SHOW_ALERT");
//                Toast.makeText(getApplicationContext(), str, Toast.LENGTH_SHORT).show();
                break;

            case IChannelListener.CMD_CHANNEL_EVENT_GET_SPACE:
                if (myDialog != null) {
                    myDialog.dismiss();
                }
                myDialog = MyDialog.newInstance(1, "请插入存储卡！");
                myDialog.show(getFragmentManager(), "SHOW_ALERT1");
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
                    Toast.makeText(getApplicationContext(), "请重启记录仪！", Toast.LENGTH_LONG).show();
                    // TODO: 2018/1/4 如下显示弹窗，旋转就闪退。

//                    MyDialog myDialogTest = MyDialog.newInstance(1, "请重启记录仪！");
//                    myDialogTest.show(this.getFragmentManager(), "recordFail");
                }
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

            default:
                break;

        }
    }

    private void handleDataChannelEvent(int type, Object param) {
        switch (type) {
            case IChannelListener.DATA_CHANNEL_EVENT_GET_START:
                myDialog = MyDialog.newInstance(2,"正在下载...");
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
        for (int i = 0; i < selectedCounts; i++) {
            if (fragmentPlaybackList.currentRadioButton == ServerConfig.RB_RECORD_VIDEO) {
//                mGetFileName = "/tmp/SD0/NORMAL/" + selectedLists.get(doingDownFileCounts).getName();
                mGetFileName = "http://192.168.42.1/SD0/NORMAL/" + selectedLists.get(i).getName();
            } else if (fragmentPlaybackList.currentRadioButton == ServerConfig.RB_LOCK_VIDEO) {
//                mGetFileName = "/tmp/SD0/EVENT/" + selectedLists.get(doingDownFileCounts).getName();
                mGetFileName = "http://192.168.42.1/SD0/EVENT/" + selectedLists.get(i).getName();
            } else {
//                mGetFileName = "/tmp/SD0/PHOTO/" + selectedLists.get(doingDownFileCounts).getName();
                mGetFileName = "http://192.168.42.1/SD0/PHOTO/" + selectedLists.get(i).getName();
            }
            doingDownFileCounts = i;
            String fileName = Environment.getExternalStorageDirectory() + "/行车记录仪"
                    + mGetFileName.substring(mGetFileName.lastIndexOf('/'));
            File file = new File(fileName);
            if (!file.exists()) {
                DownloadUtil.get().download(mGetFileName, "行车记录仪", new DownloadUtil.OnDownloadListener() {
                    @Override
                    public void onDownloadStart() {
                        Log.e(TAG, "onDownloadStart: " + mGetFileName);
//                        if (fragmentPlaybackList.currentRadioButton == ServerConfig.RB_RECORD_VIDEO
//                                || fragmentPlaybackList.currentRadioButton == ServerConfig.RB_LOCK_VIDEO) {
                            progressDialogFragment = ProgressDialogFragment.newInstance("正在下载...");
                            progressDialogFragment.show(getFragmentManager(),"text");
                            progressDialogFragment.setOnDialogButtonClickListener(new ProgressDialogFragment.OnDialogButtonClickListener() {
                                @Override
                                public void okButtonClick() {

                                }

                                @Override
                                public void cancelButtonClick() {

                                }
                            });
//                        }
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
                                progressDialogFragment.setProgressText(progress);

                            }
                        });
                        Log.e(TAG, "onDownloading: 下载中"+ progress);
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
            }
            else {
                Toast.makeText(MainActivity.this,"文件已下载",Toast.LENGTH_SHORT).show();
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

}
