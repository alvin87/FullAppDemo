package com.app43.appclient.module.appmanager;

import com.alvin.api.utils.LogOutputUtils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;

import java.util.ArrayList;
import java.util.List;

public class ReadInstallAppThread extends Thread implements Runnable {
    private static ReadInstallAppThread instance;
    private static OnClickInstallListener listener;
    private static Context context;
    private static int count = 0;
    static Handler handler;
    private static List<Integer> queue = new ArrayList<Integer>();

    private ReadInstallAppThread() {
        start();
    }

    public static ReadInstallAppThread getInstance(
            OnClickInstallListener iListener, Context iContext, Handler iHandler) {
        listener = iListener;
        context = iContext;
        handler = iHandler;
        if (instance == null) {
            instance = new ReadInstallAppThread();
        }
        addTask();
        return instance;
    }

    @Override
    public void run() {
        super.run();
        // LogOutput.e("readThread", "run()");

        while (true) {
            List<PackageInfo> installedList = new ArrayList<PackageInfo>();
            while (queue.size() > 0) {
                PackageManager pm = context.getPackageManager();
                String appName = context.getPackageName();// 程序包名
                List<PackageInfo> sysInstalledList = pm.getInstalledPackages(0);
                LogOutputUtils.i("readThread", "sysInstalledList.size()"
                        + sysInstalledList.size());
                for (PackageInfo info : sysInstalledList) {
                    LogOutputUtils.e("readThread", "packageName:"+info.packageName
                            +"appName:"+info.applicationInfo.loadLabel(pm));
                    if ((info.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                        // 非系统进程
                        if (!info.packageName.equals(appName)) {
                            installedList.add(info);
                        }
                    }
                }
                LogOutputUtils.e("readThread", "installedList.size()"
                        + installedList.size());

                queue.remove(0);
            }
            listener.onClickInstall(installedList, handler);
            try {
                synchronized (instance) {
                    LogOutputUtils.i("readThread", "wait()");
                    instance.wait();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void addTask() {
        count++;
        queue.add(count);
        synchronized (instance) {
            LogOutputUtils.i("readThread", "notify()");
            instance.notify();
        }
    }

    public interface OnClickInstallListener {
        public void onClickInstall(List<PackageInfo> list, Handler handler);
    }
}
