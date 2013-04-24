package com.app43.download.test;

import com.alvin.api.utils.LogOutputUtils;
import com.alvin.api.utils.SettingsUtils;
import com.app43.appclient.AppClient;
import com.app43.appclient.bean.App;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class ServiceDownload extends Service {
    private final String TAG = ServiceDownload.class.getSimpleName();
    DownloadApkThread downloadApkThread;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogOutputUtils.e(TAG, "下载服务开启,intent:" + (intent == null));
        if (intent != null) {
            if (AppClient.serviceIsFinish) {
                AppClient.serviceIsFinish = false;
            }
            if (downloadApkThread == null) {
                downloadApkThread = DownloadApkThread
                        .getDownloadApkThread(this);// 获取下载线程实例并开启线程
            }
            ShowNotifycation.getShowNotifycation();

            if (intent.getStringExtra(SettingsUtils.TASK_FLAG) != null) {
                if (intent.getStringExtra(SettingsUtils.TASK_FLAG).equals(
                        SettingsUtils.ADD_TASK)) {
                    final App app = (App) intent
                            .getParcelableExtra(SettingsUtils.APP);
                    Long lCurrentTime = (Long) intent.getLongExtra(
                            SettingsUtils.CURRENT_TIME, -1);
                    try {
                        while (!DownloadApkThread.initFinish) {

                        }
                        
                        downloadApkThread.addDownloadTask(app, lCurrentTime);// 添加下载任务
                        ShowNotifycation.addApkNotify(app.getTitle(),
                                app.getPackageName());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (intent.getStringExtra(SettingsUtils.TASK_FLAG).equals(
                        SettingsUtils.REMOVE_TASK)) {
                    LogOutputUtils.e(TAG, "服务接收到通知取消下载中的任务");
                    downloadApkThread.removeDownloadTask(intent
                            .getStringExtra(SettingsUtils.APK_NAME));// 删除任务或者取消正在下载
                    ShowNotifycation.cancleApkNotify(intent
                            .getStringExtra(SettingsUtils.APK_NAME));
                }
            }
        }

        return super.onStartCommand(intent, flags, startId);
    }

    public void killServiceProcess() {
        AppClient.serviceIsFinish = true;
        stopSelf();
        // onDestroy();
        if (AppClient.activityIsFinish) {
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    }
}
