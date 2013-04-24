package com.app43.appclient.module.adapter;

/**
 * 通用程序列表适配器
 */
import com.alvin.api.utils.LogOutputUtils;
import com.alvin.api.utils.SettingsUtils;
import com.alvin.api.utils.UMengAnalyseUtils;
import com.app43.appclient.R;
import com.app43.appclient.bean.App;
import com.app43.appclient.module.abstracts.activity.BaseActivity;
import com.app43.appclient.module.hot.GameListActivity;
import com.app43.appclient.module.hot.HotListActivity;
import com.app43.appclient.module.tabframe.GuessInterestActivity;
import com.app43.appclient.module.tabframe.HotGridActivity;
import com.app43.appclient.module.tabframe.RecommendAppActivity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AppListAdapter extends ProgressListViewAdapter {

    public AppListAdapter(Activity activity, ArrayList<App> installRecommendApps) {
        super(activity, AppListAdapter.class.getSimpleName());
        SharedPreferences sharedPreferences = activity.getSharedPreferences(
                SettingsUtils.USERINFO_COLLECTION, activity.MODE_PRIVATE);
        if (sharedPreferences.getBoolean(SettingsUtils.ISFIRSTIN, true)// 是否第一次进来
        ) {
            if (installRecommendApps != null && !installRecommendApps.isEmpty()) {
                for (int i = 0; i < installRecommendApps.size(); i++) {
                    LogOutputUtils.e(TAG, "装机下载 i: " + i
                            + installRecommendApps.get(i).getTitle());
                    downloadApk(installRecommendApps.get(i), activity);
                }
            }
            sharedPreferences.edit().putBoolean(SettingsUtils.ISFIRSTIN, false)
                    .commit();
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null; // 使用viewholder加载图片速度提高50%
        final int localId = position;
        LogOutputUtils.i("count", "id:" + localId);

        Map<String, Object> map = new HashMap<String, Object>();
        map = setupViews(convertView, R.layout.recommend_app_list_item, holder);
        convertView = (View) map.get(SettingsUtils.VIEW);// 获取converView
        holder = (ViewHolder) map.get(SettingsUtils.VIEWHOLDE);// 获取vviewHold

        if (progressBar != null) {
            progressBar.setVisibility(View.INVISIBLE);
        }
        if (position <= serverAppList.size() - 1) {
            // 设置图片
            setImage(serverAppList, position, holder, firstPosition,
                    lastPosition, scrollStop, defaultDrawable);

            // ----------------------------------
            setSafe(holder.safeImageView, R.drawable.list_safe, serverAppList
                    .get(localId).getSafe());
            // 名字

            setName(holder.name, serverAppList.get(position).getTitle());
            // 简介
            setDetail(holder.summary, serverAppList.get(position).getSummary());
            // 大小
            setSize(holder.size, serverAppList.get(localId).getSize());
            // ------------------------------------------------------------设置按钮
            setButtonText(localId, holder);
            // item点击监听
            convertView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO WebView跳转
                    // 点击跳转
                    App app = serverAppList.get(localId);
                    if (activity instanceof RecommendAppActivity) {
                        UMengAnalyseUtils.onEvents(activity,
                                UMengAnalyseUtils.VIEW_RECOMMENDs,
                                app.getTitle());
                        if (UMengAnalyseUtils.lableClick == 3) {
                            UMengAnalyseUtils.onEvents(activity,
                                    UMengAnalyseUtils.VIEW_RECOMMEND_MUST,
                                    app.getTitle());
                        }

                    } else if (activity instanceof GuessInterestActivity) {
                        UMengAnalyseUtils.onEvents(activity,
                                UMengAnalyseUtils.VIEW_GUESSs, app.getTitle());
                    } else if (activity instanceof HotGridActivity
                            || activity instanceof HotListActivity
                            || activity instanceof GameListActivity) {
                        UMengAnalyseUtils.onEvents(activity,
                                UMengAnalyseUtils.VIEW_HOTs,
                                app.getCategory_title());
                    }

                    onClickItemToContent(contentNameMap.get(serverAppList.get(
                            localId).getTitle()), pageUrl, pageNo, localId,
                            serverAppList, activity);
                }
            });
        }

        return convertView;
    }

    @Override
    public void updateList(ArrayList<App> sApps, boolean serverFinish,
            List<App> lApps, boolean localFinish, List<App> iApps,
            boolean installFinish, ProgressBar pb, String pageUrl, int pageNo,
            String who) {
        LogOutputUtils.i("appListAdapter", "谁进来: " + who);
        dbMap = BaseActivity.dbApp_download.getAllDownloadApk();
        progressBar = pb;
        serverAppList = sApps;
        localAppList = lApps;
        installAppList = iApps;
        isServerAppFinish = serverFinish;
        isLocalAppFinish = localFinish;
        isInstallAppFinish = installFinish;
        this.pageNo = pageNo;
        this.pageUrl = pageUrl;
        // for (int i = 0; i < serverAppList.size(); i++) {
        // serverAppList.get(i).setDownloadUrl(apkUrl + i + ".apk");
        // }
        if (isServerAppFinish && isLocalAppFinish && isInstallAppFinish) {
            notifyDataSetChanged();
        }
    }

}
