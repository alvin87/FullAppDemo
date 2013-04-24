package com.app43.appclient.module.utils;

import com.app43.appclient.bean.App;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;

import java.util.ArrayList;
import java.util.List;

public class InstallAppUtils {

    /**
     * 读本地安装程序
     * 
     * @author new
     */

    public static class readInstallApp extends Thread {
        Context context;
        Handler handler;
        Message message;
        List<App> localAppList = new ArrayList<App>();

        public readInstallApp(Context mContext, Handler hd) {
            context = mContext;
            handler = hd;
            message = handler.obtainMessage();
        }

        @Override
        public void run() {
            super.run();
            Intent appIntent = new Intent(Intent.ACTION_MAIN, null);
            appIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            PackageManager pm = context.getPackageManager();
            String appName = context.getPackageName();// 程序包名
            List<PackageInfo> sysInstalledList = pm.getInstalledPackages(0);
            List<PackageInfo> installedList = new ArrayList<PackageInfo>();
            for (PackageInfo info : sysInstalledList) {
                if ((info.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                    // 非系统程序
                    if (!info.packageName.equals(appName)) {
                        installedList.add(info);
                    }
                }
            }
            for (int i = 0; i < installedList.size(); i++) {
                App app = new App();
                // LogOutput.e("installThread",
                // ""+installedList.get(i).applicationInfo.loadLabel(pm));
                app.setPackageName(installedList.get(i).packageName);
                app.setTitle(String.valueOf(installedList.get(i).applicationInfo
                        .loadLabel(pm)));
                app.setVerCode(installedList.get(i).versionCode);
                localAppList.add(app);
            }
            message.obj = localAppList;
            handler.sendMessage(message);
        }
    }

}
