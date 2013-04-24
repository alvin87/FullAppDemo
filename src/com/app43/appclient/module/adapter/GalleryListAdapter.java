package com.app43.appclient.module.adapter;

/**
 * 通用焦点图适配器
 */
import com.alvin.api.utils.AsyncImageLoader;
import com.alvin.api.utils.AsyncImageLoader.ImageCallback;
import com.alvin.api.utils.LogOutputUtils;
import com.alvin.api.utils.SettingsUtils;
import com.app43.appclient.bean.App;
import com.app43.appclient.module.abstracts.activity.BaseActivity;
import com.app43.appclient.module.adapter.BaseOnClickListItemAdapter.ProgressData;
import com.app43.appclient.module.receive.ProgressChangeReceive;
import com.app43.appclient.module.receive.ProgressChangeReceive.DataChange;
import com.app43.download.test.ClickAdapter;

import android.app.Activity;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

public class GalleryListAdapter extends BaseAdapter {
    static final String TAG = GalleryListAdapter.class.getSimpleName();
    int mGalleryItemBackground;
    private Activity activity;
    private int count = Integer.MAX_VALUE; // 取最大值 循环用
    // static final public int MAXIMAGE = 5; // 图片数
    private List<String> focusList = new ArrayList<String>();
    DisplayMetrics dm;
    ProgressBar progressBar;

    int width, heigh, padding, defaultID;
    ScaleType scaleType;

    List<App> localList = new ArrayList<App>();
    ProgressChangeReceive progressChangeReceive;

    // static int max;//判断有多少张图片然后循环

    public GalleryListAdapter(Activity activity, int width, int heigh,
            int padding, int defalutID, ScaleType scaleType) {
        this.activity = activity;
        dm = new DisplayMetrics();
        dm = activity.getResources().getDisplayMetrics();
        this.width = width;
        this.heigh = heigh;
        this.padding = padding;
        this.defaultID = defalutID;
        this.scaleType = scaleType;
    }

    public int getCount() {
        return count;
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        final ImageView imageView = new ImageView(activity);
        imageView.setImageDrawable(activity.getResources().getDrawable(
                defaultID));
        if (focusList.size() != 0) {
            final int index = position % focusList.size();// 设置图片滑动循环

            if (null != focusList && index <= focusList.size() - 1) {
                String imgUrl = focusList.get(index);
                if (!(imgUrl == null && imgUrl.equals(""))) {
                    Drawable drawable = AsyncImageLoader.loadBitmap(imgUrl);
                    if (drawable != null) {
                        imageView.setImageDrawable(drawable);
                    } else {
                        progressBar.setVisibility(View.VISIBLE);
                        AsyncImageLoader.loadDrawable(focusList.get(index),
                                new ImageCallback() {
                                    public void imageLoaded(
                                            Drawable imageDrawable,
                                            String imageUrl) {
                                        progressBar
                                                .setVisibility(View.INVISIBLE);
                                        if (null != imageDrawable) {
                                            imageView
                                                    .setImageDrawable(imageDrawable);
                                        }
                                    }
                                });
                    }
                }
            }
        }

        // 设置显示比例类型
        imageView.setScaleType(scaleType);
        // 设置布局图片110×200显示
        imageView.setLayoutParams(new Gallery.LayoutParams(width, heigh));
        imageView.setPadding(padding, 0, padding, 0);
        return imageView;
    }

    private void setUpDataChange() {
        DataChange dataChange = new DataChange() {
            @Override
            public void cancle(String apkName) {
                // 如果是取消按钮的广播就直接更新

                ProgressListViewAdapter.dbDelete(apkName,
                        "终端页接收到ClickAdapter的广播点击取消下载线程");
                // setAppManaActivity(apkName);
                ProgressListViewAdapter.removeProgreMap(apkName);
            }

            @Override
            public void OnDataChange(String sApkName, int iProgress,
                    Long lCurrentTime) {
                LogOutputUtils.i(TAG + ":onreceive", sApkName + "size:"
                        + focusList.size());

                // LogOutput.e(TAG,
                // "name"+sApkName+"|| 是否包含名字 "+(!ClickAdapter.timeMap.containsKey(sApkName))+"|| 时间等不等"+(ClickAdapter.timeMap.get(sApkName)-lCurrentTime!=0));

                if ((!ClickAdapter.timeMap.containsKey(sApkName))
                        || (ClickAdapter.timeMap.get(sApkName) - lCurrentTime != 0)) {
                    // 如果下载adapter没有此apk名字或者时间戳不匹配则只负责显示

                    return;
                }
                //
                // LogOutput.e(TAG,
                // iProgress
                // + "progress"
                // + "更新"
                // + BaseActivity.dbApp_download
                // .updateAppProgress(sApkName,
                // lCurrentTime, iProgress));

                if (iProgress >= 100) {
                    String tempName = sApkName;
                    ProgressListViewAdapter.removeProgreMap(tempName);
                    ProgressChangeReceive.removeCount(tempName, activity);

                    LogOutputUtils.e(TAG, "下载完成" + tempName);
                    ProgressListViewAdapter.dbMap = BaseActivity.dbApp_download
                            .getAllDownloadApk();
                } else {
                    ProgressData progressData = new ProgressData();
                    progressData.setProgress(iProgress);
                    progressData.setApkName(sApkName);
                    ProgressListViewAdapter.progressMap.put(
                            progressData.getApkName(), progressData);
                }

            }
        };

        progressChangeReceive = new ProgressChangeReceive(dataChange);
        IntentFilter filter = new IntentFilter();
        filter.addAction(SettingsUtils.PROGRESS_BROADCAST_RECEIVE);
        activity.registerReceiver(progressChangeReceive, filter);
    }

    public void updateImage(List<String> foApps, ProgressBar pb) {
        progressBar = pb;
        focusList = foApps;
        notifyDataSetChanged();
    }

}
