package com.bydauto.myviewpager.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.LruCache;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.byd.lighttextview.LightButton;
import com.byd.lighttextview.LightCheckBox;
import com.byd.lighttextview.LightRadioButton;
import com.byd.lighttextview.LightTextView;
import com.bydauto.myviewpager.ActivityImagesViewPager;
import com.bydauto.myviewpager.Images;
import com.bydauto.myviewpager.R;
import com.bydauto.myviewpager.RemoteCam;
import com.bydauto.myviewpager.ServerConfig;
import com.bydauto.myviewpager.Videos;
import com.bydauto.myviewpager.adapter.MyFragmentPagerAdapter;
import com.bydauto.myviewpager.connectivity.IFragmentListener;
import com.bydauto.myviewpager.view.MyDialog;
import com.jakewharton.disklrucache.DiskLruCache;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


/**
 * Created by byd_tw on 2017/11/1.
 */

public class FragmentPlaybackList extends Fragment implements AdapterView.OnItemLongClickListener, AdapterView.OnItemClickListener {
    private static final String TAG = "FragmentPlaybackList";
    @BindView(R.id.rg_groupDetail)
    RadioGroup rgGroupDetail;
    //    @BindView(R.id.vp_itemPreview)
//    NoScrollViewPager vpItemPreview;

    Unbinder unbinder;
    @BindView(R.id.rb_recordvideo)
    LightRadioButton rbRecordvideo;
    @BindView(R.id.rb_lockvideo)
    LightRadioButton rbLockvideo;
    @BindView(R.id.rb_capturephoto)
    LightRadioButton rbCapturephoto;
    @BindView(R.id.gv_dataList)
    GridView mGridViewList;

    @BindView(R.id.btn_cancel)
    LightButton btnCancel;
    @BindView(R.id.btn_share)
    LightButton btnShare;
    @BindView(R.id.btn_export)
    LightButton btnExport;
    @BindView(R.id.btn_delete)
    LightButton btnDelete;
    @BindView(R.id.btn_selectall)
    LightCheckBox btnSelectall;
    @BindView(R.id.ll_editItemBar)
    LinearLayout llEditItemBar;
    @BindView(R.id.tv_editNav)
    LightTextView tvEditNav;
    //    @BindView(R.id.rl_menu_edit)
//    RelativeLayout rlMenuEdit;
    @BindView(R.id.iv_line_blowMenuEdit)
    ImageView ivLineBlowMenuEdit;
    @BindView(R.id.ib_search)
    ImageButton ibSearch;
    @BindView(R.id.fl_videoPlayPreview)
    FrameLayout flVideoPlayPreview;

//    FragmentVideoPlay fragmentVideoDetail;
    FragmentVideoDetail fragmentVideoDetail;

    private List<Fragment> fragments;
    private MyFragmentPagerAdapter myFragmentPagerAdapter;
    private PhotoWallAdapter mAdapter;
    public boolean isMultiChoose = false;

    private int screenHeight;
    private int screenWidth;

    private ArrayList<String> urlsList;
    private ArrayList<String> urlVideosList;
    private ArrayList<String> selectedUrlsList;
    private ArrayList<Integer> selectedIntsList;

    private RemoteCam mRemoteCam;
    private String mPWD;
    private IFragmentListener mListener;

    public static FragmentPlaybackList newInstance() {
        FragmentPlaybackList fragmentPlaybackList = new FragmentPlaybackList();

        return fragmentPlaybackList;
    }

    public void setRemoteCam(RemoteCam mRemoteCam) {
        Log.e(TAG, "setRemoteCam: "+ this.mRemoteCam);
        this.mRemoteCam = mRemoteCam;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_playback, container, false);
        unbinder = ButterKnife.bind(this, view);
        initData();
        return view;

    }

    private void initData() {
        if (mAdapter == null) {
//            mPWD = mRemoteCam.videoFolder();
            mPWD = "/tmp/SD0/NORMAL";
            listDirContents(mPWD);
        }
        urlsList = new ArrayList<>();
        urlVideosList = new ArrayList<>();
        selectedUrlsList = new ArrayList<>();
        Collections.addAll(urlsList, Images.imageThumbUrls);
        Collections.addAll(urlVideosList, Videos.videosThumbUrls);

        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        if (getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {

            //屏幕高度
//         screenHeight = display.getHeight();
            screenHeight = dm.heightPixels;
            //屏幕宽度
//         screenWidth = display.getWidth();
            screenWidth = dm.widthPixels;
        } else {
            screenHeight = dm.heightPixels;
            screenWidth = (int) (dm.widthPixels * 0.52);
        }


//        ColumnInfo colInfo = calculateColumnWidthAndCountInRow(screenWidth, 90,8);

        rgGroupDetail.check(R.id.rb_recordvideo);

        rgGroupDetail.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.rb_recordvideo:
                        mAdapter.currentRadioButton = ServerConfig.RB_RECORD_VIDEO;
                        mGridViewList.setNumColumns(1);
//                        mAdapter.notifyDataSetChanged();
//                        if (mAdapter != null) {
//                            mAdapter.clear();
//                            mAdapter.cancelAllTasks();
//                        }
                        Toast.makeText(getContext(), "recordvideo", Toast.LENGTH_SHORT).show();
//                        vpItemPreview.setCurrentItem(0, false);
                        break;
                    case R.id.rb_lockvideo:
                        mAdapter.currentRadioButton = ServerConfig.RB_LOCK_VIDEO;
                        mGridViewList.setNumColumns(1);
//                        mAdapter.notifyDataSetChanged();
//                        if (mAdapter != null) {
//                            mAdapter.clear();
//                            mAdapter.cancelAllTasks();
//                        }
                        Toast.makeText(getContext(), "lockvideo", Toast.LENGTH_SHORT).show();
//                        vpItemPreview.setCurrentItem(1, false);
                        break;
                    case R.id.rb_capturephoto:
                        mAdapter.currentRadioButton = ServerConfig.RB_CAPTURE_PHOTO;
//                        mGridViewList.setNumColumns(3);

                        ColumnInfo colInfo = calculateColumnWidthAndCountInRow(screenWidth, 300, 12);
                        int rowNum = mGridViewList.getCount() % colInfo.countInRow == 0 ? mGridViewList.getCount() / colInfo.countInRow : mGridViewList.getCount() / colInfo.countInRow + 1;
//                        mGridViewList.setLayoutParams(new ConstraintLayout.LayoutParams(screenWidth,rowNum*colInfo.width+(rowNum-1)*2));
                        mGridViewList.setNumColumns(colInfo.countInRow);
//                        mGridViewList.setHorizontalSpacing();
//                        if (mAdapter != null) {
//                            mAdapter.clear();
//                            mAdapter.cancelAllTasks();
//                        }
//                        mAdapter.notifyDataSetChanged();
//                        vpItemPreview.setCurrentItem(2, false);
                        break;
                    default:
                        break;
                }
                mAdapter.notifyDataSetChanged();
//                mGridViewList.invalidateViews();
                mGridViewList.setAdapter(mAdapter);

            }
        });

        mAdapter = new PhotoWallAdapter(getContext(), 0, Images.imageThumbUrls, mGridViewList);
        mGridViewList.setAdapter(mAdapter);
        mGridViewList.setNumColumns(1);
        mGridViewList.setOnItemClickListener(this);
        mGridViewList.setOnItemLongClickListener(this);
    }

    private void listDirContents(String psd) {
        if (psd != null) {
            mListener.onFragmentAction(IFragmentListener.ACTION_FS_LS, psd);
        }
    }

    //存放计算后的单元格相关信息
    class ColumnInfo {
        //单元格宽度。
        public int width = 0;
        //每行所能容纳的单元格数量。
        public int countInRow = 0;
    }

    /**
     * 根据手机屏幕宽度，计算gridview每个单元格的宽度
     *
     * @param screenWidth 屏幕宽度
     * @param width       单元格预设宽度
     * @param padding     单元格间距
     * @return
     */
    private ColumnInfo calculateColumnWidthAndCountInRow(int screenWidth, int width, int padding) {
        ColumnInfo colInfo = new ColumnInfo();
        int colCount = 0;
        //判断屏幕是否刚好能容纳下整数个单元格，若不能，则将多出的宽度保存到space中
        int space = screenWidth % width;

        if (space == 0) { //正好容纳下
            colCount = screenWidth / width;
        } else if (space >= (width / 2)) { //多出的宽度大于单元格宽度的一半时，则去除最后一个单元格，将其所占的宽度平分并增加到其他每个单元格中
            colCount = screenWidth / width;
            space = width - space;
            width = width + space / colCount;
        } else {  //多出的宽度小于单元格宽度的一半时，则将多出的宽度平分，并让每个单元格减去平分后的宽度
            colCount = screenWidth / width + 1;
            width = width - space / colCount;
        }

        colInfo.countInRow = colCount;
        //计算出每行的间距总宽度，并根据单元格的数量重新调整单元格的宽度
        colInfo.width = width - ((colCount + 1) * padding) / colCount;

        return colInfo;
    }

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

    @Override
    public void onPause() {
        super.onPause();
        mAdapter.fluchCache();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        // 退出程序时结束所有的下载任务
        mAdapter.cancelAllTasks();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if (!isMultiChoose) {
            Intent intent;
            if (mAdapter.currentRadioButton == ServerConfig.RB_RECORD_VIDEO
                    || mAdapter.currentRadioButton == ServerConfig.RB_LOCK_VIDEO) {
//                fragmentVideoDetail = FragmentVideoPlay.newInstance(urlVideosList,urlVideosList.get(i));
                fragmentVideoDetail = FragmentVideoDetail.newInstance(urlVideosList,urlVideosList.get(i));
//                fragmentVideoDetail.show(getFragmentManager(),"videoPlay");
                getFragmentManager().beginTransaction().replace(flVideoPlayPreview.getId(), fragmentVideoDetail).commitAllowingStateLoss();
//                flVideoPlayPreview.setClickable(true);
//                intent = new Intent(view.getContext(), ActivityVideoViewPager.class);
//                intent.putStringArrayListExtra("mUrlsList", urlVideosList);

            } else {
                intent = new Intent(view.getContext(), ActivityImagesViewPager.class);
                intent.putStringArrayListExtra("mUrlsList", urlsList);
                intent.putExtra("position", i);
                startActivity(intent);
            }
//            intent.putExtra("position", i);
//            startActivity(intent);
        } else {
//            checkbox初始状态默认为false。
            boolean isSelected = mAdapter.getIsSelectedAt(i);
//            如下判断后续使用
            if (!isSelected) {
//                selectedUrlsList.add(urlsList.get(i));
//                selectedIntsList.add(i);

            } else {
//                selectedUrlsList.remove(urlsList.get(i));
//                selectedIntsList.remove(i);
            }
            Log.e(TAG, "onItemClick: isSelected = " + isSelected + ";i = " + i);
//            此处把指定位置变为true，并通知item更新。
            mAdapter.setItemIsSelectedMap(i, !isSelected);

        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
        if (isMultiChoose) {

        } else {
            isMultiChoose = true;
            llEditItemBar.setVisibility(View.VISIBLE);
            rgGroupDetail.setVisibility(View.INVISIBLE);
            ibSearch.setVisibility(View.INVISIBLE);
//            view.setBackgroundColor(Color.parseColor("#1CC9FE"));
            switch (mAdapter.currentRadioButton) {
                case ServerConfig.RB_RECORD_VIDEO:
                    tvEditNav.setText("记录视频");
                    break;
                case ServerConfig.RB_LOCK_VIDEO:
                    tvEditNav.setText("锁定视频");
                    break;
                case ServerConfig.RB_CAPTURE_PHOTO:
                    tvEditNav.setText("抓拍照片");
                    break;
                default:
                    break;
            }

        }
        return false;
    }

    @OnClick({R.id.btn_cancel, R.id.btn_share, R.id.btn_export, R.id.btn_delete, R.id.btn_selectall})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_cancel:
                llEditItemBar.setVisibility(View.INVISIBLE);
                rgGroupDetail.setVisibility(View.VISIBLE);
                ibSearch.setVisibility(View.VISIBLE);
                isMultiChoose = false;
                mAdapter.isSelectedMap.clear();
                btnSelectall.setChecked(false);
                break;
            case R.id.btn_share:
                break;
            case R.id.btn_export:
                break;
            case R.id.btn_delete:
//                for (String url: selectedUrlsList) {
//
//                }
//                for (int i : selectedIntsList) {
//                    urlsList.
//                }

//                urlsList.remove()
                // TODO: 2017/11/29 删除gridview item
                break;
            case R.id.btn_selectall:
                if (btnSelectall.isChecked()) {
                    mAdapter.isSelectedMap.clear();
                    for (int i = 0; i < mGridViewList.getAdapter().getCount(); i++) {
                        mAdapter.setItemIsSelectedMap(i, true);
                    }
                } else {
                    mAdapter.isSelectedMap.clear();
                    for (int i = 0; i < mGridViewList.getAdapter().getCount(); i++) {
                        mAdapter.setItemIsSelectedMap(i, false);
                    }
                }
                break;
            default:
                break;
        }
//        更新checkbox，隐藏
        mAdapter.notifyDataSetChanged();
    }

    @OnClick(R.id.ib_search)
    public void onViewClicked() {
        MyDialog myDialogTest = MyDialog.newInstance(1, "正在搜索");
        myDialogTest.show(getActivity().getFragmentManager(), "test");
//        Toast.makeText(getActivity(), "search", Toast.LENGTH_SHORT).show();
    }


    /**
     * GridView的适配器，负责异步从网络上下载图片展示在照片墙上。
     *
     * @author guolin
     */
    public class PhotoWallAdapter extends ArrayAdapter<String> {
        private static final String TAG = "PhotoWallAdapter";

        /**
         * 记录所有正在下载或等待下载的任务。
         */
        private Set<BitmapWorkerTask> taskCollection;

        /**
         * 图片缓存技术的核心类，用于缓存所有下载好的图片，在程序内存达到设定值时会将最少最近使用的图片移除掉。
         */
        private LruCache<String, Bitmap> mMemoryCache;

        /**
         * 图片硬盘缓存核心类。
         */
        private DiskLruCache mDiskLruCache;

        /**
         * GridView的实例
         */
        private GridView mPhotoWall;

        /**
         * 记录每个子项的高度。
         */
        private int mItemHeight = 0;

        public int currentRadioButton = ServerConfig.RB_RECORD_VIDEO;

        private SparseBooleanArray isSelectedMap;

        public PhotoWallAdapter(Context context, int textViewResourceId, String[] objects,
                                GridView photoWall) {
            super(context, textViewResourceId, objects);
            mPhotoWall = photoWall;
            isSelectedMap = new SparseBooleanArray();
            taskCollection = new HashSet<BitmapWorkerTask>();
            // 获取应用程序最大可用内存
            int maxMemory = (int) Runtime.getRuntime().maxMemory();
            int cacheSize = maxMemory / 8;
            // 设置图片缓存大小为程序最大可用内存的1/8
            mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
                @Override
                protected int sizeOf(String key, Bitmap bitmap) {
                    return bitmap.getByteCount();
                }
            };
            try {
                // 获取图片缓存路径
                File cacheDir = getDiskCacheDir(context, "thumb");
                if (!cacheDir.exists()) {
                    cacheDir.mkdirs();
                }
                // 创建DiskLruCache实例，初始化缓存数据
                mDiskLruCache = DiskLruCache
                        .open(cacheDir, getAppVersion(context), 1, 10 * 1024 * 1024);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            String url = getItem(position);
//            Log.e(TAG, "getView: url" + url);
//            Log.e(TAG, "getView: currentRadioButton" + currentRadioButton);
            View view;
//        ImageView imageView = null;
            if (convertView == null) {
                if (currentRadioButton == ServerConfig.RB_RECORD_VIDEO) {
                    view = LayoutInflater.from(getContext()).inflate(R.layout.layout_record_video_item, null);
                } else if (currentRadioButton == ServerConfig.RB_LOCK_VIDEO) {
                    view = LayoutInflater.from(getContext()).inflate(R.layout.layout_lock_video_item, null);
                } else {
                    view = LayoutInflater.from(getContext()).inflate(R.layout.layout_capture_photo_item, null);
                }
            } else {
                view = convertView;
            }

            LightCheckBox cbMuliChoose = view.findViewById(R.id.cb_cbx);
            if (isMultiChoose) {
                cbMuliChoose.setVisibility(View.VISIBLE);
                cbMuliChoose.setChecked(getIsSelectedAt(position));
//                如下怎么实现还是没理解
                // TODO: 2017/11/29  如下怎么实现还是没理解
                if (mAdapter.currentRadioButton == ServerConfig.RB_CAPTURE_PHOTO) {
                    if (getIsSelectedAt(position)) {
                        view.setBackgroundColor(Color.parseColor("#1CC9FE"));
                    } else {
                        view.setBackgroundColor(Color.TRANSPARENT);
                    }
                }
            } else {
                cbMuliChoose.setVisibility(View.INVISIBLE);
//                当按下取消后，isMultiChoose为falae了，所以执行下面。
                if (mAdapter.currentRadioButton == ServerConfig.RB_CAPTURE_PHOTO) {
                    view.setBackgroundColor(Color.TRANSPARENT);
                }
            }
//		final ImageView imageView ;= com.bydauto.myviewpager.view.findViewById(R.id.photo);
            ImageView imageView;

//  if (currentRadioButton == ServerConfig.RB_RECORD_VIDEO ||
//                currentRadioButton == ServerConfig.RB_LOCK_VIDEO) {
            imageView = view.findViewById(R.id.iv_videoPhoto);
//        }
//        else if (currentRadioButton == ServerConfig.RB_CAPTURE_PHOTO) {
//            imageView = com.bydauto.myviewpager.view.findViewById(R.id.photo);
//        }
//		if (imageView.getLayoutParams().height != mItemHeight) {
//			imageView.getLayoutParams().height = mItemHeight;
//		}
            // 给ImageView设置一个Tag，保证异步加载图片时不会乱序
            imageView.setTag(url);
            imageView.setImageResource(R.drawable.empty_photo);
            loadBitmaps(imageView, url);
            return view;
        }

        /**
         * 将一张图片存储到LruCache中。
         *
         * @param key    LruCache的键，这里传入图片的URL地址。
         * @param bitmap LruCache的键，这里传入从网络上下载的Bitmap对象。
         */
        public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
            if (getBitmapFromMemoryCache(key) == null) {
                mMemoryCache.put(key, bitmap);
            }
        }

        /**
         * 从LruCache中获取一张图片，如果不存在就返回null。
         *
         * @param key LruCache的键，这里传入图片的URL地址。
         * @return 对应传入键的Bitmap对象，或者null。
         */
        public Bitmap getBitmapFromMemoryCache(String key) {
            return mMemoryCache.get(key);
        }

        /**
         * 加载Bitmap对象。此方法会在LruCache中检查所有屏幕中可见的ImageView的Bitmap对象，
         * 如果发现任何一个ImageView的Bitmap对象不在缓存中，就会开启异步线程去下载图片。
         */
        public void loadBitmaps(ImageView imageView, String imageUrl) {
            try {
                Bitmap bitmap = getBitmapFromMemoryCache(imageUrl);
                if (bitmap == null) {
                    BitmapWorkerTask task = new BitmapWorkerTask();
                    taskCollection.add(task);
                    task.execute(imageUrl);
                } else {
                    if (imageView != null && bitmap != null) {
                        imageView.setImageBitmap(bitmap);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /**
         * 取消所有正在下载或等待下载的任务。
         */
        public void cancelAllTasks() {
            if (taskCollection != null) {
                for (BitmapWorkerTask task : taskCollection) {
                    task.cancel(false);
                }
            }
        }

        /**
         * 根据传入的uniqueName获取硬盘缓存的路径地址。
         */
        public File getDiskCacheDir(Context context, String uniqueName) {
            String cachePath;
            if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                    || !Environment.isExternalStorageRemovable()) {
                cachePath = context.getExternalCacheDir().getPath();
            } else {
                cachePath = context.getCacheDir().getPath();
            }
            return new File(cachePath + File.separator + uniqueName);
        }

        /**
         * 获取当前应用程序的版本号。
         */
        public int getAppVersion(Context context) {
            try {
                PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(),
                        0);
                return info.versionCode;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            return 1;
        }

        /**
         * 设置item子项的高度。
         */
        public void setItemHeight(int height) {
            if (height == mItemHeight) {
                return;
            }
            mItemHeight = height;
            notifyDataSetChanged();
        }

        /**
         * 使用MD5算法对传入的key进行加密并返回。
         */
        public String hashKeyForDisk(String key) {
            String cacheKey;
            try {
                final MessageDigest mDigest = MessageDigest.getInstance("MD5");
                mDigest.update(key.getBytes());
                cacheKey = bytesToHexString(mDigest.digest());
            } catch (NoSuchAlgorithmException e) {
                cacheKey = String.valueOf(key.hashCode());
            }
            return cacheKey;
        }

        /**
         * 将缓存记录同步到journal文件中。
         */
        public void fluchCache() {
            if (mDiskLruCache != null) {
                try {
                    mDiskLruCache.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private String bytesToHexString(byte[] bytes) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < bytes.length; i++) {
                String hex = Integer.toHexString(0xFF & bytes[i]);
                if (hex.length() == 1) {
                    sb.append('0');
                }
                sb.append(hex);
            }
            return sb.toString();
        }

        public boolean getIsSelectedAt(int i) {
            if (isSelectedMap.get(i)) {
                return isSelectedMap.get(i);
            }
            return false;
        }

        public void setItemIsSelectedMap(int position, boolean isSelected) {
            this.isSelectedMap.put(position, isSelected);
            notifyDataSetChanged();
        }

        /**
         * 异步下载图片的任务。
         *
         * @author guolin
         */
        class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap> {

            /**
             * 图片的URL地址
             */
            private String imageUrl;

            @Override
            protected Bitmap doInBackground(String... params) {
                imageUrl = params[0];
                FileDescriptor fileDescriptor = null;
                FileInputStream fileInputStream = null;
                DiskLruCache.Snapshot snapShot = null;
                try {
                    // 生成图片URL对应的key
                    final String key = hashKeyForDisk(imageUrl);
                    // 查找key对应的缓存
                    snapShot = mDiskLruCache.get(key);
                    if (snapShot == null) {
                        // 如果没有找到对应的缓存，则准备从网络上请求数据，并写入缓存
                        DiskLruCache.Editor editor = mDiskLruCache.edit(key);
                        if (editor != null) {
                            OutputStream outputStream = editor.newOutputStream(0);
                            if (downloadUrlToStream(imageUrl, outputStream)) {
                                editor.commit();
                            } else {
                                editor.abort();
                            }
                        }
                        // 缓存被写入后，再次查找key对应的缓存
                        snapShot = mDiskLruCache.get(key);
                    }
                    if (snapShot != null) {
                        fileInputStream = (FileInputStream) snapShot.getInputStream(0);
                        fileDescriptor = fileInputStream.getFD();
                    }
                    // 将缓存数据解析成Bitmap对象
                    Bitmap bitmap = null;
                    if (fileDescriptor != null) {
                        bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor);
                    }
                    if (bitmap != null) {
                        // 将Bitmap对象添加到内存缓存当中
                        addBitmapToMemoryCache(params[0], bitmap);
                    }
                    return bitmap;
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (fileDescriptor == null && fileInputStream != null) {
                        try {
                            fileInputStream.close();
                        } catch (IOException e) {
                        }
                    }
                }
                return null;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                super.onPostExecute(bitmap);
                // 根据Tag找到相应的ImageView控件，将下载好的图片显示出来。
                ImageView imageView = (ImageView) mPhotoWall.findViewWithTag(imageUrl);
                if (imageView != null && bitmap != null) {
                    imageView.setImageBitmap(bitmap);
                }
                taskCollection.remove(this);
            }

            /**
             * 建立HTTP请求，并获取Bitmap对象。
             *
             * @param urlString 图片的URL地址
             * @return 解析后的Bitmap对象
             */
            private boolean downloadUrlToStream(String urlString, OutputStream outputStream) {
                HttpURLConnection urlConnection = null;
                BufferedOutputStream out = null;
                BufferedInputStream in = null;
                try {
                    final URL url = new URL(urlString);
                    urlConnection = (HttpURLConnection) url.openConnection();
                    in = new BufferedInputStream(urlConnection.getInputStream(), 8 * 1024);
                    out = new BufferedOutputStream(outputStream, 8 * 1024);
                    int b;
                    while ((b = in.read()) != -1) {
                        out.write(b);
                    }
                    return true;
                } catch (final IOException e) {
                    e.printStackTrace();
                } finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                    try {
                        if (out != null) {
                            out.close();
                        }
                        if (in != null) {
                            in.close();
                        }
                    } catch (final IOException e) {
                        e.printStackTrace();
                    }
                }
                return false;
            }

        }

    }


}

