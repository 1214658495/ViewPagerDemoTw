package com.byd.vtdr.view;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.TextView;

import com.byd.vtdr.R;

/**
 * Created by byd_tw on 2018/3/21.
 */

public class TestDialog extends Dialog {
    public Context context;

    public TextView tvDialogContent;
//    private String title;

    public TestDialog(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    public TestDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        this.context = context;
    }

    protected TestDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = View.inflate(context, R.layout.fragment_test_dialog, null);
        tvDialogContent = view.findViewById(R.id.tv_dialogContent);
        setContentView(view);
        setCanceledOnTouchOutside(false);
        DisplayMetrics dm = new DisplayMetrics();
        getWindow().getWindowManager().getDefaultDisplay().getMetrics(dm);
        if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            getWindow().setLayout((int) (dm.widthPixels * 0.8), (int) (dm.heightPixels * 0.25));
        } else {
            getWindow().setLayout((int) (dm.widthPixels * 0.6), (int) (dm.heightPixels * 0.5));
        }

        view.findViewById(R.id.btn_dialogSure).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });


    }

    public void setTitle(String title) {
        tvDialogContent.setText(title);
    }
}
