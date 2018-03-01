//package com.bydauto.myviewpager.adapter;
//
//import android.support.v4.view.PagerAdapter;
//import android.support.v7.app.AppCompatActivity;
//import android.util.Log;
//import android.view.View;
//import android.view.ViewGroup;
//
//import com.bumptech.glide.Glide;
//import com.bydauto.myviewpager.R;
//
//import java.util.ArrayList;
//
//import uk.co.senab.photoview.PhotoView;
//import uk.co.senab.photoview.PhotoViewAttacher;
//
//import static android.content.ContentValues.TAG;
//
///**
// * Created by byd_tw on 2017/11/28.
// */
//
//public class MyImagesPagerAdapter extends PagerAdapter {
//
//    private ArrayList<String> imageUrls;
//    private AppCompatActivity activity;
//
//    public MyImagesPagerAdapter(ArrayList<String> imageUrls, AppCompatActivity activity) {
//        this.imageUrls = imageUrls;
//        this.activity = activity;
//    }
//
//    @Override
//    public Object instantiateItem(ViewGroup container, int position) {
//        String url = imageUrls.get(position);
//        PhotoView photoView = new PhotoView(activity);
//        Glide.with(activity)
//                .load(url)
//                .placeholder(R.mipmap.ic_launcher)
//                .into(photoView);
//        container.addView(photoView);
////        photoView.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View view) {
//////                activity.finish();
//////                Log.e(TAG, "onClick: photoView.setOnClickListener");
////            }
////        });
//        photoView.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
//            @Override
//            public void onPhotoTap(View view, float v, float v1) {
//                Log.e(TAG, "onClick: photoView.onPhotoTap//////");
//            }
//
//            @Override
//            public void onOutsidePhotoTap() {
//                Log.e(TAG, "onClick: photoView.onOutsidePhotoTap//////");
//            }
//        });
//        return photoView;
//    }
//
//    @Override
//    public int getCount() {
//        return imageUrls != null ? imageUrls.size() : 0;
//    }
//
//    @Override
//    public boolean isViewFromObject(View view, Object object) {
//        return view == object;
//    }
//
//    @Override
//    public void destroyItem(ViewGroup container, int position, Object object) {
//        container.removeView((View) object);
//    }
//
//    @Override
//    public int getItemPosition(Object object) {
//        return POSITION_NONE;
//    }
//}
