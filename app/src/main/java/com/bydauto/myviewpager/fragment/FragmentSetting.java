package com.bydauto.myviewpager.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.byd.lighttextview.LightButton;
import com.bydauto.myviewpager.R;

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

    public static FragmentSetting newInstance() {
        FragmentSetting fragmentSetting = new FragmentSetting();

        return fragmentSetting;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
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
                break;
            case R.id.btn_memoryCard_format:
                break;
            case R.id.btn_firmwareVersion:
                break;
            case R.id.btn_appVersion:
                break;
            default:
                break;
        }
    }

}
