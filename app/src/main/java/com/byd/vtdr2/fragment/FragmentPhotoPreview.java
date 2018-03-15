package com.byd.vtdr2.fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.byd.vtdr2.ActivityImagesViewPager;
import com.byd.vtdr2.MainActivity;
import com.byd.vtdr2.Model;
import com.byd.vtdr2.R;
import com.byd.vtdr2.ServerConfig;
import com.byd.vtdr2.connectivity.IFragmentListener;
import com.byd.vtdr2.view.MyDialog;
import com.byd.vtdr2.view.MyViewPager;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by byd_tw on 2018/3/15.
 */

public class FragmentPhotoPreview extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private ArrayList<Model> mParam1;
    private int mParam2;
    Unbinder unbinder;
    @BindView(R.id.vp_viewPager)
    MyViewPager vpViewPager;
    @BindView(R.id.tv_vpIndex)
    TextView tvVpIndex;
    @BindView(R.id.btn_back_to_gridview)
    ImageButton btnBackToGridview;
    @BindView(R.id.tv_title_photo)
    TextView tvTitlePhoto;
    @BindView(R.id.rl_bar_showTitle)
    RelativeLayout rlBarShowTitle;
    @BindView(R.id.btn_share_preview)
    ImageButton btnSharePreview;
    @BindView(R.id.btn_delete_preview)
    ImageButton btnDeletePreview;
    @BindView(R.id.btn_zoom)
    ImageButton btnZoom;
    @BindView(R.id.ll_bar_editPhoto)
    LinearLayout llBarEditPhoto;

    private MyImagesPagerAdapter myImagesPagerAdapter;

    private ArrayList<Model> photoLists;

    private int currentItem;

    private static final int FADE_OUT = 1;
    private IFragmentListener mListener;

    public FragmentPhotoPreview() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setRetainInstance(true);
        Bundle bundle = getArguments();//从activity传过来的Bundle
        if (bundle != null) {
            photoLists = (ArrayList<Model>) bundle.getSerializable("mPhotoList");
            currentItem = (bundle.getInt("position"));
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.frament_photo_preview, container, false);
        unbinder = ButterKnife.bind(this, view);
        if (savedInstanceState != null) {
            ArrayList<String> name = savedInstanceState.getStringArrayList("name");
            ArrayList<String> time = savedInstanceState.getStringArrayList("time");
            ArrayList<Integer> size = savedInstanceState.getIntegerArrayList("size");
            photoLists = new ArrayList<>();
            int length = name.size();
            for (int i = 0; i < length; i++) {
                Model temp = new Model(name.get(i), time.get(i), size.get(i));
                photoLists.add(temp);
            }
            currentItem = (savedInstanceState.getInt("position"));
        }
        if (photoLists.size() != 0) {
            initData();
        }
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mListener = (IFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement IFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private void initData() {
        myImagesPagerAdapter = new MyImagesPagerAdapter(photoLists, this);
        vpViewPager.setAdapter(myImagesPagerAdapter);
        vpViewPager.setCurrentItem(currentItem, false);
        vpViewPager.setOffscreenPageLimit(0);
//        tvVpIndex.setText(currentItem + 1 + "/" + urlList.size());
        tvTitlePhoto.setText(photoLists.get(currentItem).getName());
        tvVpIndex.setText(currentItem + 1 + "/" + photoLists.size());
        vpViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                currentItem = position;
                tvVpIndex.setText(currentItem + 1 + "/" + photoLists.size());
                tvTitlePhoto.setText(photoLists.get(currentItem).getName());
            }
        });


        Message msg = mHandler.obtainMessage(FADE_OUT);
        mHandler.removeMessages(FADE_OUT);
        mHandler.sendMessageDelayed(msg, 3000);
    }


    @OnClick({R.id.btn_back_to_gridview, R.id.btn_share_preview, R.id.btn_delete_preview, R.id
            .btn_zoom})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_back_to_gridview:
                // TODO: 2017/11/29 删除完成了，需要去更新gridview
                getActivity().getSupportFragmentManager().popBackStack();
                break;
            case R.id.btn_share_preview:
                break;
            case R.id.btn_delete_preview:
                MyDialog myDialog = MyDialog.newInstance(0, "确认删除？");
                myDialog.show(getFragmentManager(), "delete");
                myDialog.setOnDialogButtonClickListener(new MyDialog.OnDialogButtonClickListener() {
                    @Override
                    public void okButtonClick() {
                        // TODO: 2017/11/29  删除照片
//                        myImagesPagerAdapter.destroyItem(vpViewPager,currentItem,vpViewPager.);
//                        vpViewPager.removeViewAt(vpViewPager.getCurrentItem());
                        photoLists.remove(currentItem);
                        myImagesPagerAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void cancelButtonClick() {
                    }
                });
                break;
            case R.id.btn_zoom:
                Intent intent = new Intent(view.getContext(), ActivityImagesViewPager.class);
                intent.putExtra("mPhotoList", photoLists);
                intent.putExtra("position", currentItem);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    private final MyHandler mHandler = new MyHandler(this);

    public class MyHandler extends Handler {
        private WeakReference<FragmentPhotoPreview> mActivityViewPager;

        MyHandler(FragmentPhotoPreview activityImagesViewPager) {
            mActivityViewPager = new WeakReference<>(activityImagesViewPager);

        }

        @Override
        public void handleMessage(Message msg) {
//            ActivityImagesViewPager activityViewPager = mActivityViewPager.get();
            super.handleMessage(msg);
            switch (msg.what) {
                case FADE_OUT:
                    if (rlBarShowTitle.getVisibility() == View.VISIBLE) {
                        rlBarShowTitle.setVisibility(View.INVISIBLE);
                    }

                    if (llBarEditPhoto.getVisibility() == View.VISIBLE) {
                        llBarEditPhoto.setVisibility(View.INVISIBLE);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    public class MyImagesPagerAdapter extends PagerAdapter {

        private ArrayList<Model> mPhotoLists;
        private MainActivity activity;

        public MyImagesPagerAdapter(ArrayList<Model> mPhotoLists, FragmentPhotoPreview activity) {
//            this.imageUrls = imageUrls;
            this.mPhotoLists = mPhotoLists;
            this.activity = (MainActivity) getActivity();
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
//            String url = imageUrls.get(position);
//            String url = "http://" + ServerConfig.VTDRIP + "/SD0/NORMAL/" +
//            model.getName();
            String url = "http://" + ServerConfig.VTDRIP + "/SD0/PHOTO/" + mPhotoLists.get
                    (position).getName();
            PhotoView photoView = new PhotoView(activity);
            Glide.with(activity).load(url).into(photoView);
            container.addView(photoView);

            photoView.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
                @Override
                public void onPhotoTap(View view, float v, float v1) {
                    showTitleBar();
                }

                @Override
                public void onOutsidePhotoTap() {
                    showTitleBar();
                }
            });
            return photoView;
        }

        void showTitleBar() {
            //                    如下考虑算法优化
            if (rlBarShowTitle.getVisibility() == View.VISIBLE) {
                rlBarShowTitle.setVisibility(View.INVISIBLE);
            } else {
                rlBarShowTitle.setVisibility(View.VISIBLE);
            }

            if (llBarEditPhoto.getVisibility() == View.VISIBLE) {
                llBarEditPhoto.setVisibility(View.INVISIBLE);
            } else {
                llBarEditPhoto.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public int getCount() {
//            return imageUrls != null ? imageUrls.size() : 0;
            return mPhotoLists != null ? mPhotoLists.size() : 0;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        ArrayList<String> name = new ArrayList<>();
        ArrayList<String> time = new ArrayList<>();
        ArrayList<Integer> size = new ArrayList<>();

        int flag = photoLists.size();
        for (int i = 0; i < flag; i++) {
            name.add(photoLists.get(i).getName());
            time.add(photoLists.get(i).getTime());
            size.add(photoLists.get(i).getSize());
        }
        outState.putStringArrayList("name", name);
        outState.putStringArrayList("time", time);
        outState.putIntegerArrayList("size", size);
        outState.putInt("position", currentItem);
        super.onSaveInstanceState(outState);
    }
}
