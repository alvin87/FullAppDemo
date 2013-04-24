package com.app43.download.test;

import com.alvin.api.utils.LogOutputUtils;
import com.alvin.api.utils.SettingsUtils;
import com.app43.appclient.bean.App;
import com.app43.appclient.module.db.DBApp_download;
import com.app43.download.test.DownloadApkThread.ProgressListener;
import com.app43.download.test.DownloadApkThread.TaskListener;

import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BroadProgressThread extends Thread {
    private static String TAG = BroadProgressThread.class.getSimpleName();
    private static BroadProgressThread instance;
    // private static Map<String, ProgressData> taskMap = new HashMap<String,
    // ProgressData>();
    // private static List<String> appList = new ArrayList<String>();
    private static Map<String, Integer> nameProgress = new HashMap<String, Integer>();// 下载队列的映射用来控制取消appList<appName,在下载队列的位置>
    private static DBApp_download dbApp_download = null;
    private static ProgressListener progressListener;// 进度监听器
    private static TaskListener taskListener;// 任务监听器
    private static List<String> nameList = new ArrayList<String>();// 根据此队列来按序广播买个下载任务
    private static Context context;

    // private static int currentProgress;
    private BroadProgressThread() {
        start();
    }

    public static BroadProgressThread getBroadProgressThread(Context mContext,
            DownloadApkThread downloadApkThread) {
        if (context == null) {
            context = mContext;
        }
        if (dbApp_download == null) {
            dbApp_download = new DBApp_download(context,
                    SettingsUtils.DATABASEVERSION);
        }
        if (instance == null) {
            instance = new BroadProgressThread();
        }

        if (progressListener == null) {
            progressListener = new ProgressListener() {

                @Override
                public void progressChange(String appName, int progress) {
                    nameProgress.put(appName, progress);
                }
            };
            downloadApkThread.addProgressListener(progressListener);
        }

        if (taskListener == null) {
            taskListener = new TaskListener() {

                @Override
                public void addTask(App app) {
                    // addMapList(app, index);
                    // queueMap.put(app.getName(), 0);
                    nameList.add(app.getTitle());
                    LogOutputUtils.i("broadThreadRun", "notify");
                    synchronized (instance) {
                        instance.notify();
                    }
                }

                @Override
                public void removeTask(String appName) {
                    // if (nameList.contains(appName)) {
                    // nameList.remove(appName);
                    // }
                }
            };
            // downloadApkThread.addTaskListener(taskListener);
        }
        return instance;
    }

    @Override
    public void run() {
        super.run();
        while (true) {
            LogOutputUtils.i("broadThreadRun", "run()");
            while (nameList.size() > 0) {

                LogOutputUtils.i("nameList", "size" + nameList.size());
                String currentName = nameList.get(0);
                // 已经添加到下载线程了,但是由于网络或设计问题,nameProgress暂时好唉没有此name
                while (nameProgress.get(currentName) == null) {
                    try {
                        sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                while (currentName.equals(nameList.get(0))
                        && nameProgress.get(currentName) < 100) {
                    LogOutputUtils.e("progress",
                            currentName + nameProgress.get(currentName));
                    Intent intent = new Intent();
                    intent.setAction(SettingsUtils.PROGRESS_BROADCAST_RECEIVE);
                    intent.putExtra(SettingsUtils.APK_NAME, currentName);
                    intent.putExtra(SettingsUtils.PROGRESS,
                            nameProgress.get(currentName));
                    context.sendBroadcast(intent);
                    try {
                        sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                Intent intent = new Intent();
                intent.setAction(SettingsUtils.PROGRESS_BROADCAST_RECEIVE);
                intent.putExtra(SettingsUtils.APK_NAME, currentName);
                intent.putExtra(SettingsUtils.PROGRESS, 100);
                context.sendBroadcast(intent);
                if (currentName.equals(nameList.get(0))) {
                    nameProgress.remove(nameList.get(0));
                    nameList.remove(0);
                }
            }

            try {
                synchronized (instance) {
                    LogOutputUtils.i("instance", "wait()");
                    instance.wait();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    // private static void addMapList(App app, int index) {
    // appList.add(app.getName());
    // ProgressData data = new ProgressData();
    // data.setApp(app);
    // data.setPosition(index);
    // taskMap.put(app.getName(), data);
    // queueMap.put(app.getName(), appList.size() - 1);
    // }
    //
    // private void removeMapList(String appName) {
    // if (queueMap.containsKey(appName)
    // && appList.contains(queueMap.get(appName))
    // && taskMap.containsKey(appName)) {
    //
    // appList.remove(queueMap.get(appName));
    // taskMap.remove(appName);
    // queueMap.remove(appName);
    // }
    // }

    public static class ProgressData {
        App app;
        int position;

        public App getApp() {
            return app;
        }

        public void setApp(App app) {
            this.app = app;
        }

        public int getPosition() {
            return position;
        }

        public void setPosition(int position) {
            this.position = position;
        }

    }

    // public static class NameProgress implements Parcelable {
    // String appName;
    // int progress;
    //
    // public NameProgress() {
    //
    // }
    //
    // public NameProgress(Parcel in) {
    //
    // }
    //
    // public String getAppName() {
    // return appName;
    // }
    //
    // public void setAppName(String appName) {
    // this.appName = appName;
    // }
    //
    // public int getProgress() {
    // return progress;
    // }
    //
    // public void setProgress(int progress) {
    // this.progress = progress;
    // }
    //
    // @Override
    // public int describeContents() {
    // return 0;
    // }
    //
    // @Override
    // public void writeToParcel(Parcel dest, int flags) {
    // dest.writeString(appName);
    // dest.writeInt(progress);
    // }
    //
    // public static final Parcelable.Creator<NameProgress> CREATOR = new
    // Parcelable.Creator<BroadProgressThread.NameProgress>() {
    //
    // @Override
    // public NameProgress createFromParcel(Parcel source) {
    // NameProgress nameProgress = new NameProgress();
    // nameProgress.appName = source.readString();
    // nameProgress.progress = source.readInt();
    // return nameProgress;
    // }
    //
    // @Override
    // public NameProgress[] newArray(int size) {
    // return new NameProgress[size];
    // }
    //
    // };
    //
    // }
    //
    // // 写进度到数据库
    // private static void writeToDb(App app, int progress) {
    // dbApp_download.insertProgress(app, progress);
    // }
}
