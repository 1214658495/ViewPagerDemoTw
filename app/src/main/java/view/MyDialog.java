package view;

import android.app.DialogFragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
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

    public static MyDialog newInstance(int style) {
        MyDialog newDialog = new MyDialog();
        Bundle bundle = new Bundle();
        bundle.putInt("myStyle", style);
        newDialog.setArguments(bundle);
        return newDialog;
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
        View view;
        int myStyleNum = getArguments().getInt("myStyle", 0);
        if (myStyleNum == 0) {
            view = inflater.inflate(R.layout.fragment_dialog, container);
        } else {
            view = inflater.inflate(R.layout.fragment_simple_dialog, container);
        }
        unbinder = ButterKnife.bind(this, view);
        return view;
    }


//    @Override
//    public void onStart() {
//        super.onStart();
//        Dialog myDialog = getDialog();
//        if (myDialog != null) {
//            DisplayMetrics dm = new DisplayMetrics();
//            getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
//            myDialog.getWindow().setLayout((int) (dm.widthPixels * 0.70), (int) (dm.heightPixels * 0.25));
//        }
//    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.btn_closeDialog, R.id.btn_dialogSure, R.id.btn_dialogCancel})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_closeDialog:
                dismiss();
                break;
            case R.id.btn_dialogSure:
                break;
            case R.id.btn_dialogCancel:
                dismiss();
                break;
            default:
                break;
        }
    }
}
