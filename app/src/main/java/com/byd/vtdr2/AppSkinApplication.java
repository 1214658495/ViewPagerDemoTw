package com.byd.vtdr2;

import android.app.Application;
import android.hardware.bydauto.energy.AbsBYDAutoEnergyListener;
import android.hardware.bydauto.energy.BYDAutoEnergyDevice;
import android.os.Handler;
import android.os.Message;

import com.byd.vtdr2.widget.Theme;
import com.byd.vtdr2.widget.ThemeManager;

import org.greenrobot.eventbus.EventBus;

import skin.support.SkinCompatManager;
import skin.support.constraint.app.SkinConstraintViewInflater;

import static android.hardware.bydauto.energy.BYDAutoEnergyDevice.ENERGY_OPERATION_ECONOMY;
import static android.hardware.bydauto.energy.BYDAutoEnergyDevice.ENERGY_OPERATION_SPORT;

/**
 * Created by ximsfei on 2017/1/10.
 */

public class AppSkinApplication extends Application {
    private BYDAutoEnergyDevice mBYDAutoEnergyDevice;
    private ThemeManager themeManager;

    private Handler modelChange = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            if (msg.what == ENERGY_OPERATION_ECONOMY) {
                themeManager.updateTheme(Theme.NORMAL);
                SkinCompatManager.getInstance().restoreDefaultTheme();
                EventBus.getDefault().post(new MessageEvent());
            } else if (msg.what == ENERGY_OPERATION_SPORT) {
                themeManager.updateTheme(Theme.SPORT);
                SkinCompatManager.getInstance().loadSkin("sport", null, SkinCompatManager.SKIN_LOADER_STRATEGY_BUILD_IN);
                EventBus.getDefault().post(new MessageEvent());
            }
        }

    };

    AbsBYDAutoEnergyListener absBYDAutoEnergyListener = new AbsBYDAutoEnergyListener() {
        @Override
        public void onOperationModeChanged(int type) {
            // TODO Auto-generated method stub
            super.onOperationModeChanged(type);
            modelChange.sendEmptyMessage(type);
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
//        SkinCircleImageViewManager.init(this);
//        SkinMaterialManager.init(this);
//        SkinConstraintManager.init(this);
//        SkinCardViewManager.init(this);
//        SkinFlycoTabLayoutManager.init(this);
//        SkinCompatManager.init(this).loadSkin();
//        SkinCompatManager.init(this)
        SkinCompatManager.withoutActivity(this)
//                .addStrategy(new CustomSDCardLoader())          // 自定义加载策略，指定SDCard路径
//                .addInflater(new SkinMaterialViewInflater())    // material design
                .addInflater(new SkinConstraintViewInflater())  // ConstraintLayout
//                .addInflater(new SkinCardViewInflater())        // CardView v7
//                .addInflater(new SkinCircleImageViewInflater()) // hdodenhof/CircleImageView
//                .addInflater(new SkinFlycoTabLayoutInflater())  // H07000223/FlycoTabLayout
//                .setSkinStatusBarColorEnable(false)             // 关闭状态栏换肤
//                .setSkinWindowBackgroundEnable(false)           // 关闭windowBackground换肤
//                .setSkinAllActivityEnable(false)                // true: 默认所有的Activity都换肤; false: 只有实现SkinCompatSupportable接口的Activity换肤
                .loadSkin();
//        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);


        themeManager = ThemeManager.getInstance();
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
                mBYDAutoEnergyDevice = BYDAutoEnergyDevice.getInstance(getApplicationContext());
                mBYDAutoEnergyDevice.registerListener(absBYDAutoEnergyListener);
//            }
//        }).start();

        int mode =  mBYDAutoEnergyDevice .getOperationMode();
        if(mode == ENERGY_OPERATION_ECONOMY ){
            //经济模式
//            showMainSkinTheme(Theme.NORMAL);
            themeManager.updateTheme(Theme.NORMAL);
            SkinCompatManager.getInstance().restoreDefaultTheme();
        }else if(mode == ENERGY_OPERATION_SPORT){
            //运动模式
//            showMainSkinTheme(Theme.SPORT);
            themeManager.updateTheme(Theme.SPORT);
            SkinCompatManager.getInstance().loadSkin("sport", null, SkinCompatManager.SKIN_LOADER_STRATEGY_BUILD_IN);
        }
    }


}
