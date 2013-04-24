package com.app43.appclient.module.adapter;

/**
 * 精品推荐焦点图适配器
 */
import com.alvin.api.utils.AsyncImageLoader;
import com.alvin.api.utils.AsyncImageLoader.ImageCallback;
import com.alvin.api.utils.LogOutputUtils;
import com.alvin.api.utils.SettingsUtils;
import com.app43.appclient.R;
import com.app43.appclient.bean.App;
import com.app43.appclient.module.abstracts.activity.BaseActivity;
import com.app43.appclient.module.receive.ProgressChangeReceive;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FocusAppListAdapter extends ProgressListViewAdapter {
    final String TAG = FocusAppListAdapter.class.getSimpleName();
    int mGalleryItemBackground;
    private Activity activity;
    public static int MAX = Integer.MAX_VALUE; // 取最大值 循环用
    private List<App> focusList = new ArrayList<App>();
    DisplayMetrics dm;
    ProgressBar progressBar;

    int width, heigh, padding, defaultID;
    ScaleType scaleType;

    ProgressChangeReceive progressChangeReceive;

    public FocusAppListAdapter(Activity activity, int width, int heigh,
            int padding, int defalutID, ScaleType scaleType) {
        super(activity, FocusAppListAdapter.class.getSimpleName());
        this.activity = activity;
        appcount = MAX;
        dm = new DisplayMetrics();
        dm = activity.getResources().getDisplayMetrics();
        this.width = width;
        this.heigh = heigh;
        this.padding = padding;
        this.defaultID = defalutID;
        this.scaleType = scaleType;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final ImageView imageView = new ImageView(activity);
        imageView.setImageDrawable(activity.getResources().getDrawable(
                defaultID));
        if (focusList.size() != 0) {

            final int index = position % focusList.size();// 设置图片滑动循环

            ViewHolder holder = null; // 使用viewholder加载图片速度提高50%
            final int localId = index;
            LogOutputUtils.i("count", "id:" + localId);

            Map<String, Object> map = new HashMap<String, Object>();
            map = setupViews(convertView, R.layout.recommend_app_list_item,
                    holder);
            convertView = (View) map.get(SettingsUtils.VIEW);// 获取converView
            holder = (ViewHolder) map.get(SettingsUtils.VIEWHOLDE);// 获取vviewHold

            // ------------------------------------------------------------设置按钮
            setButtonText(localId, holder);

            // -------------------------------------------真正焦点图的vview,上面是判断进度

            if (null != focusList && index <= focusList.size() - 1) {
                String imgUrl = focusList.get(index).getImage();
                if (!(imgUrl == null && imgUrl.equals(""))) {
                    Drawable drawable = AsyncImageLoader.loadBitmap(imgUrl);
                    if (drawable != null) {
                        imageView.setImageDrawable(drawable);
                    } else {
                        progressBar.setVisibility(View.VISIBLE);
                        AsyncImageLoader.loadDrawable(focusList.get(index)
                                .getImage(), new ImageCallback() {
                            public void imageLoaded(Drawable imageDrawable,
                                    String imageUrl) {
                                progressBar.setVisibility(View.INVISIBLE);
                                if (null != imageDrawable) {
                                    imageView.setImageDrawable(imageDrawable);
                                }
                            }
                        });
                    }
                }
            }
        }

        // 设置显示比例类型
        imageView.setScaleType(scaleType);
        // 设置布局图片100×200显示
        // Log.e(TAG, "width: " + width + "heigh: " + heigh + "padding: "
        // + padding+"den"+activity.getResources().getDisplayMetrics().density+
        // "phone width:"+activity.getWindowManager().getDefaultDisplay().getWidth()
        // +" phone height:"+activity.getWindowManager().getDefaultDisplay().getHeight());
        imageView.setLayoutParams(new Gallery.LayoutParams(width + 2 * padding,
                heigh + padding * 4));

        imageView.setPadding(padding, padding * 2, padding, padding * 2);
        return imageView;
    }

    @Override
    public void updateList(ArrayList<App> sApps, boolean serverFinish,
            List<App> lApps, boolean localFinish, List<App> iApps,
            boolean installFinish, ProgressBar pb, String pageUrl, int pageNo,
            String who) {
        LogOutputUtils.e(TAG, "" + who);
        dbMap = BaseActivity.dbApp_download.getAllDownloadApk();
        progressBar = pb;
        localAppList = lApps;
        installAppList = iApps;
        isServerAppFinish = serverFinish;
        isLocalAppFinish = localFinish;
        isInstallAppFinish = installFinish;
        focusList = sApps;
        serverAppList = sApps;
        this.pageNo = pageNo;
        this.pageUrl = pageUrl;

        if (isServerAppFinish && isLocalAppFinish && isInstallAppFinish) {
            notifyDataSetChanged();
        }
    }
}
