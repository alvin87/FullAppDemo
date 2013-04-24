package com.app43.download.test;

import com.alvin.api.utils.LogOutputUtils;
import com.alvin.api.utils.SettingsUtils;
import com.alvin.api.utils.UMengAnalyseUtils;
import com.app43.appclient.bean.App;
import com.app43.appclient.module.abstracts.activity.BaseActivity;
import com.app43.appclient.module.common.activity.FocusContentViewActivity;
import com.app43.appclient.module.hot.GameListActivity;
import com.app43.appclient.module.hot.HotListActivity;
import com.app43.appclient.module.menu.FavouriteActivity;
import com.app43.appclient.module.receive.ClickCancleReceive;
import com.app43.appclient.module.receive.ClickCancleReceive.OnClickCancleListener;
import com.app43.appclient.module.tabframe.GuessInterestActivity;
import com.app43.appclient.module.tabframe.HotGridActivity;
import com.app43.appclient.module.tabframe.RecommendAppActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import java.util.HashMap;
import java.util.Map;

public class ClickAdapter {
    private static final String TAG = ClickAdapter.class.getSimpleName();
    private static ClickAdapter instance;
    private static Map<String, App> nameMap = new HashMap<String, App>();// <appName,app>
    // 用来存储下载过的应用
    public static Map<String, Long> timeMap = new HashMap<String, Long>();// 加个时间戳,为了区分是否为取消之后的
    private static ClickCancleReceive clickCancleReceive;

    private ClickAdapter() {

    }

    public static ClickAdapter getInstances(Context context) {
        if (instance == null) {
            instance = new ClickAdapter();
        }
        if (clickCancleReceive == null) {
            // 如果点击取消按钮则触发下载队列删除此appName
            OnClickCancleListener listener = new OnClickCancleListener() {

                @Override
                public void OnClickCancle(String appName, Context context) {
                    LogOutputUtils.e(TAG, "点击了取消的");
                    if (appName != null) {
                        if (nameMap.containsKey(appName)) {
                            removeMap(appName);
                            // ProgressListViewAdapter.dbDelete(appName,
                            // "接收到点击取消的动作");
                            Intent intent = new Intent();
                            intent.setAction(SettingsUtils.PROGRESS_BROADCAST_RECEIVE);
                            intent.putExtra(SettingsUtils.APK_NAME, appName);
                            intent.putExtra(SettingsUtils.CANCLE, true);
                            context.sendBroadcast(intent);
                            removeDownloadService(context, appName);
                        }
                    }
                }
            };
            clickCancleReceive = new ClickCancleReceive(listener);
            IntentFilter filter = new IntentFilter();
            filter.addAction(SettingsUtils.CANCLE_INTENT);
            context.registerReceiver(clickCancleReceive, filter);
            // 注册广播接收器

        }
        return instance;
    }

    // 重新下载程序
    public void download(App app, Activity activity) {

        if (!nameMap.containsKey(app.getTitle())) {
            LogOutputUtils.i(TAG, "downloads" + app.getDownloadUrl());
            LogOutputUtils.e(TAG, "guess:"
                    + (activity instanceof GuessInterestActivity));

            if (activity instanceof FocusContentViewActivity) {
                if (UMengAnalyseUtils.activity2Content == 3) {
                    UMengAnalyseUtils.onEvents(activity,
                            UMengAnalyseUtils.DOWNLOAD_GUESSS, app.getTitle());
                } else if (UMengAnalyseUtils.activity2Content == 1) {
                    UMengAnalyseUtils.onEvents(activity,
                            UMengAnalyseUtils.DOWNLOAD_RECOMMENDS,
                            app.getTitle());
                } else if (UMengAnalyseUtils.activity2Content == 2) {
                    UMengAnalyseUtils.onEvents(activity,
                            UMengAnalyseUtils.DOWNLOAD_HOTS,
                            app.getCategory_title());
                } else if (UMengAnalyseUtils.activity2Content == 5) {
                    UMengAnalyseUtils.onEvents(activity,
                            UMengAnalyseUtils.DOWNLOAD_FAVs, app.getTitle());
                } else if (UMengAnalyseUtils.activity2Content == 6) {
                    UMengAnalyseUtils.onEvents(activity,
                            UMengAnalyseUtils.DOWNLOAD_FOCUSS, app.getTitle());
                } else if (UMengAnalyseUtils.activity2Content == 4) {
                    UMengAnalyseUtils.onEvents(activity,
                            UMengAnalyseUtils.DOWNLOAD_MUST, app.getTitle());
                }
            } else if (activity instanceof GuessInterestActivity) {
                UMengAnalyseUtils.onEvents(activity,
                        UMengAnalyseUtils.DOWNLOAD_GUESSS, app.getTitle());
            } else if (activity instanceof RecommendAppActivity) {
                UMengAnalyseUtils.onEvents(activity,
                        UMengAnalyseUtils.DOWNLOAD_RECOMMENDS, app.getTitle());
                if (UMengAnalyseUtils.lableClick == 3) {
                    UMengAnalyseUtils.onEvents(activity,
                            UMengAnalyseUtils.DOWNLOAD_MUST, app.getTitle());
                }
            } else if (activity instanceof HotListActivity
                    || activity instanceof HotGridActivity
                    || activity instanceof GameListActivity) {
                UMengAnalyseUtils.onEvents(activity,
                        UMengAnalyseUtils.DOWNLOAD_HOTS,
                        app.getCategory_title());
            } else if (activity instanceof FavouriteActivity) {
                UMengAnalyseUtils.onEvents(activity,
                        UMengAnalyseUtils.DOWNLOAD_FAVs, app.getTitle());
            }
            UMengAnalyseUtils.onEvents(activity,
                    UMengAnalyseUtils.DOWNLOAD_TOTALs, app.getTitle());
            // nameMap.put(app.getTitle(), app);
            // timeMap.put(app.getTitle(), System.currentTimeMillis());
            Long currentTime = System.currentTimeMillis();
            addMap(app, currentTime);
            addDownLoadService(app, activity, currentTime);
        }
    }

    // 开启下载服务
    private void addDownLoadService(App app, Context context, Long lCurrentTime) {
        // 每开启一次下载都要写到数据库,待重新进入程序的时候可以续传下载
        BaseActivity.dbApp_download.insertProgress(app, 0);
        // LogOutput.e(TAG, "开启下载服务" + app.getTitle());
        Intent intent = new Intent(SettingsUtils.SERVICEDOWNLOAD);
        intent.putExtra(SettingsUtils.TASK_FLAG, SettingsUtils.ADD_TASK);
        intent.putExtra(SettingsUtils.APP, app);
        intent.putExtra(SettingsUtils.CURRENT_TIME, lCurrentTime);
        // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startService(intent);
    }

    // 断点续传的下载改为重新下载 第一版不做,后期再做.
    // public void loading(App app, int progress, Context context) {
    //
    // if (!nameMap.containsKey(app.getTitle())) {
    // LogOutput.i(TAG, "loading" + app.getDownloadUrl());
    // // nameMap.put(app.getTitle(), app);
    // // timeMap.put(app.getTitle(), System.currentTimeMillis());
    // Long currentTime = System.currentTimeMillis();
    // addMap(app, currentTime);
    // addDownLoadService(app, context, currentTime);
    // // 断点续传使用
    // // ApkDownloadThread.progressMap.put(app.getName(), progress);
    // }
    // }

    /**
     * 通知服务删除下载线程
     */
    private static void removeDownloadService(Context context, String appName
    // App iApp, int iPosition
    ) {
        // LogOutput.e(TAG, "开启多少次服务" + count++);
        LogOutputUtils.e(TAG, "通知服务取消任务");
        Intent intent = new Intent(SettingsUtils.SERVICEDOWNLOAD);
        intent.putExtra(SettingsUtils.TASK_FLAG, SettingsUtils.REMOVE_TASK);
        intent.putExtra(SettingsUtils.APK_NAME, appName);
        context.startService(intent);
    }

    public static void addMap(App app, Long lCurrentTime) {
        app.setDownDate(lCurrentTime);
        nameMap.put(app.getTitle(), app);
        timeMap.put(app.getTitle(), lCurrentTime);
    }

    public static void removeMap(String appName) {
        if (nameMap.containsKey(appName)) {
            nameMap.remove(appName);
        }
        if (timeMap.containsKey(appName)) {
            timeMap.remove(appName);
        }
    }

}
