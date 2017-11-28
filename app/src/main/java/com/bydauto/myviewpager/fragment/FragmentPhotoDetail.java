//package com.bydauto.myviewpager.fragment;
//
//import android.os.Bundle;
//import android.support.annotation.Nullable;
//import android.support.v4.app.Fragment;
//import android.support.v4.view.PagerAdapter;
//import android.support.v4.view.ViewPager;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageButton;
//import android.widget.ImageView;
//
//import com.bydauto.myviewpager.R;
//
//import java.util.ArrayList;
//
//import butterknife.BindView;
//import butterknife.ButterKnife;
//import butterknife.OnClick;
//import butterknife.Unbinder;
//
///**
// * Created by byd_tw on 2017/11/1.
// */
//
//public class FragmentPhotoDetail extends Fragment {
//
//    Unbinder unbinder;
//    @BindView(R.id.vp_photoDetail)
//    ViewPager vpPhotoDetail;
//    @BindView(R.id.btn_zoom)
//    ImageButton btnZoom;
//    @BindView(R.id.btn_delete_preview)
//    ImageButton btnDeletePreview;
//    @BindView(R.id.btn_share_preview)
//    ImageButton btnSharePreview;
//    private ArrayList<ImageView> imageLists;
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.frament_photo_detail, container, false);
//        unbinder = ButterKnife.bind(this, view);
//        initData();
//        vpPhotoDetail.setAdapter(new MyPagerAdapter());
//        return view;
//    }
//
//    private void initData() {
//        int[] imageResIDs = {R.mipmap.ic_launcher,
//                R.mipmap.ic_launcher_round, R.mipmap.ic_launcher};
//        imageLists = new ArrayList<>();
//        for (int imageResID : imageResIDs) {
//            ImageView imageView = new ImageView(getContext());
//            imageView.setBackgroundResource(imageResID);
//            imageLists.add(imageView);
//        }
//    }
//
//    @Override
//    public void onPause() {
//        super.onPause();
//    }
//
//    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
//        unbinder.unbind();
//
//    }
//
//    @OnClick({R.id.btn_zoom, R.id.btn_delete_preview, R.id.btn_share_preview})
//    public void onViewClicked(View view) {
//        switch (view.getId()) {
//            case R.id.btn_zoom:
//                break;
//            case R.id.btn_delete_preview:
//                break;
//            case R.id.btn_share_preview:
//                break;
//                default:
//        }
//    }
//
//    public class MyPagerAdapter extends PagerAdapter {
//
//
//        @Override
//        public int getCount() {
//            return imageLists.size();
//        }
//
//        @Override
//        public boolean isViewFromObject(View view, Object object) {
//            return view == object;
//        }
//
//        @Override
//        public Object instantiateItem(ViewGroup container, int position) {
//            container.addView(imageLists.get(position));
//            return imageLists.get(position);
//        }
//
//        @Override
//        public void destroyItem(ViewGroup container, int position, Object object) {
//            container.removeView((View) object);
//        }
//
//    }
//}
