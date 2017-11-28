package com.bydauto.myviewpager.view;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;

import com.byd.lighttextview.LightButton;
import com.bydauto.myviewpager.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * @author byd_tw
 * @date 2017/11/13
 */

public class MyDialog extends DialogFragment {
    @BindView(R.id.btn_closeDialog)
    ImageButton btnCloseDialog;

    @BindView(R.id.tv_dialogContent)
    TextView tvDialogContent;
    @BindView(R.id.btn_dialogSure)
    LightButton btnDialogSure;
    @BindView(R.id.btn_dialogCancel)
    LightButton btnDialogCancel;
    Unbinder unbinder;
    private int mHeight;

    private OnDialogButtonClickListener buttonClickListener;

    public static MyDialog newInstance(int style,String message) {
        MyDialog newDialog = new MyDialog();
        Bundle bundle = new Bundle();
        bundle.putInt("myStyle", style);
        bundle.putString("message",message);
        newDialog.setArguments(bundle);
        return newDialog;
    }

    public interface OnDialogButtonClickListener {
        void okButtonClick();
        void cancelButtonClick();
    }

    public void setOnDialogButtonClickListener(OnDialogButtonClickListener buttonClickListener) {
        this.buttonClickListener = buttonClickListener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        int myStyleNum = getArguments().getInt("myStyle", 0);
//        int style = 0;
//        switch (myStyleNum) {
//            case 0:
//                style = DialogFragment.STYLE_NORMAL;
//                break;
//            case 1:
//                style = DialogFragment.STYLE_NO_TITLE;
//                break;
//            case 2:
//                style = DialogFragment.STYLE_NO_FRAME;
//                break;
//            case 3:
//                style = DialogFragment.STYLE_NO_INPUT;
//                break;
//            default:
//                break;
//        }
//        setStyle(style,0);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

//        //        dialogFragment  setting size
//        getDialog().getWindow().setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
//        WindowManager.LayoutParams lp = getDialog().getWindow().getAttributes();
//        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
//        if (mHeight == 0) {
//            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
//        } else {
//            lp.height = mHeight;
//        }
//        getDialog().getWindow().setAttributes(lp);


        View view;
        int myStyleNum = getArguments().getInt("myStyle", 0);
        String message = getArguments().getString("message");
        if (myStyleNum == 0) {
            view = inflater.inflate(R.layout.fragment_dialog, container);
        } else {
            view = inflater.inflate(R.layout.fragment_simple_dialog, container);
        }
        unbinder = ButterKnife.bind(this, view);

        tvDialogContent.setText(message);

        btnDialogSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonClickListener.okButtonClick();
            }
        });

        btnDialogCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonClickListener.cancelButtonClick();
                getDialog().cancel();
            }
        });
        return view;
    }


    @Override
    public void onStart() {
        super.onStart();
        Dialog myDialog = getDialog();
        if (myDialog != null) {
            DisplayMetrics dm = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
            if (getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                myDialog.getWindow().setLayout((int) (dm.widthPixels * 0.8), (int) (dm.heightPixels * 0.25));
            } else {
                myDialog.getWindow().setLayout((int) (dm.widthPixels * 0.6), (int) (dm.heightPixels * 0.5));
            }
        }
    }

    @OnClick(R.id.btn_closeDialog)
    public void onViewClicked(View view) {
                dismiss();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
