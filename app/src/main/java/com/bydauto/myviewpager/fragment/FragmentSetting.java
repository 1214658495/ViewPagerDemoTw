package com.bydauto.myviewpager.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.byd.lighttextview.LightButton;
import com.bydauto.myviewpager.R;
import com.bydauto.myviewpager.connectivity.IFragmentListener;
import com.bydauto.myviewpager.view.MyDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by byd_tw on 2017/11/1.
 */

public class FragmentSetting extends Fragment {
    @BindView(R.id.iv_line_settingtext)
    ImageView ivLineSettingtext;
    @BindView(R.id.iv_bg_settingtext)
    ImageView ivBgSettingtext;

    @BindView(R.id.btn_memoryCard_format)
    LightButton btnMemoryCardFormat;

    @BindView(R.id.btn_firmwareVersion)
    LightButton btnFirmwareVersion;
    @BindView(R.id.btn_appVersion)
    LightButton btnAppVersion;
    @BindView(R.id.btn_default_setting)
    LightButton btnDefaultSetting;
    Unbinder unbinder;
    @BindView(R.id.tv_test)
    TextView tvTest;

    private IFragmentListener mListener;
    private MyDialog myDialog;

    public static FragmentSetting newInstance() {
        FragmentSetting fragmentSetting = new FragmentSetting();

        return fragmentSetting;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // retain this fragment
//        setRetainInstance(true);
//        if (savedInstanceState != null) {
//            myDialog = (MyDialog) getActivity().getFragmentManager().findFragmentByTag("default_setting");
//            if (myDialog != null) {
//                myDialog.setOnDialogButtonClickListener((MyDialog.OnDialogButtonClickListener) this);
//            }
//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        unbinder = ButterKnife.bind(this, view);
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

    @OnClick({R.id.btn_default_setting, R.id.btn_memoryCard_format, R.id.btn_firmwareVersion, R.id.btn_appVersion})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_default_setting:
                myDialog = MyDialog.newInstance(0, "是否恢复默认设置？");
                myDialog.show(getActivity().getFragmentManager(), "default_setting");
                myDialog.setOnDialogButtonClickListener(new MyDialog.OnDialogButtonClickListener() {
                    @Override
                    public void okButtonClick() {
                        mListener.onFragmentAction(IFragmentListener.ACTION_DEFAULT_SETTING, null);
                    }

                    @Override
                    public void cancelButtonClick() {
                        // TODO: 2018/1/4 点击取消有空指针异常
                    }
                });
                break;
            case R.id.btn_memoryCard_format:
                myDialog = MyDialog.newInstance(0, "是否格式化存储卡？");
                myDialog.show(getActivity().getFragmentManager(), "memoryCard");
                myDialog.setOnDialogButtonClickListener(new MyDialog.OnDialogButtonClickListener() {
                    @Override
                    public void okButtonClick() {
                        // TODO: 2017/11/29  删除照片
                        mListener.onFragmentAction(IFragmentListener.ACTION_FS_FORMAT_SD, "C:");
                    }

                    @Override
                    public void cancelButtonClick() {
                    }
                });
                break;
            case R.id.btn_firmwareVersion:
                break;
            case R.id.btn_appVersion:
                String ver = getAppVersion(getContext());
                myDialog = MyDialog.newInstance(1, "App版本" + ver);
                myDialog.show(getActivity().getFragmentManager(), "memoryCard");
                break;
            default:
                break;
        }
    }

    /**
     * 获取当前应用程序的版本号。
     */
    public String getAppVersion(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(),
                    0);
//            return info.versionCode;
            return info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "1.0.0";
    }

}
