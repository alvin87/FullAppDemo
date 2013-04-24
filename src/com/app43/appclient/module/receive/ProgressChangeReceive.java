package com.app43.appclient.module.receive;

import com.alvin.api.components.DialogAndToast;
import com.alvin.api.utils.LogOutputUtils;
import com.alvin.api.utils.SettingsUtils;
import com.app43.appclient.module.abstracts.activity.BaseActivity;
import com.app43.appclient.module.adapter.ProgressListViewAdapter;
import com.app43.download.test.ClickAdapter;
import com.app43.download.test.ShowNotifycation;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;

import java.io.File;

public class ProgressChangeReceive extends BroadcastReceiver {
    public static final String TAG = ProgressChangeReceive.class
            .getSimpleName();
    DataChange dataChange;
    private static Integer RegisterCount = 0;// 总共注册了多少个广播,
    // 确保每个接收器都接收到广播才可以关闭避免在ProgressListAdapter中的ClickAdapter.timeMap没有此apk时return;

    private static Integer UnRegisterCount = 0;// 已经广播的个数

    public ProgressChangeReceive(DataChange iDataChange) {
        addCount();
        if (dataChange == null) {
            dataChange = iDataChange;
        }
    }

    private static void addCount() {
        synchronized (RegisterCount) {
            RegisterCount++;
            LogOutputUtils.e(TAG, "RegisterCount:" + RegisterCount);
        }
    }

    /**
     * 作用:当启用progress通知的activity退出时需要删除刚刚注册的监听
     */
    public static void reuseRegisCount() {
        synchronized (RegisterCount) {
            RegisterCount--;
            LogOutputUtils.e(TAG, "RegisterCount:" + RegisterCount);
        }
    }

    /**
     * 作用:当下载完成时,必须等全部注册的监听接收到才可以删除对应的apkName
     */
    public static void removeCount(String apkName, Context context) {
        synchronized (UnRegisterCount) {
            UnRegisterCount++;
            LogOutputUtils.e(TAG, "UnRegisterCount:" + UnRegisterCount);
        }
        if (ProgressChangeReceive.equlsCount()) {
            // autoInstall(context, apkName);//自动安装 暂时不用,不完整,在终端页或者热门分类不会自动更新状态
            ProgressChangeReceive.initCount();
            ClickAdapter.removeMap(apkName);
        }
    }

    public static void initCount() {
        synchronized (UnRegisterCount) {
            UnRegisterCount = 0;
            LogOutputUtils.e(TAG, "UnRegisterCount:" + UnRegisterCount);
        }
    }

    /**
     * 作用:如果已经广播的个数和注册的个数相等则可以删除此apkName
     */
    public static boolean equlsCount() {
        return UnRegisterCount == RegisterCount;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String apkName = null;
        apkName = intent.getExtras().getString(SettingsUtils.APK_NAME);
        if (apkName != null && !apkName.equals("") && apkName != "") {
            if (intent.getBooleanExtra(SettingsUtils.CANCLE, false)) {
                dataChange.cancle(apkName);
            } else {
                int progress = intent.getExtras()
                        .getInt(SettingsUtils.PROGRESS);
                Long currentTiem = intent.getLongExtra(
                        SettingsUtils.CURRENT_TIME, -1);
                dataChange.OnDataChange(apkName, progress, currentTiem);
            }
        }
    }

    // 当监听的数据变化时触发
    public interface DataChange {
        public void OnDataChange(String apkName, int progress, Long currenTime);

        public void cancle(String apkName);
    }

    private static void autoInstall(final Context context, final String apkName) {
        LogOutputUtils.e(TAG, "自动打开啊");
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
        alertBuilder
                .setTitle("安装提示")
                .setMessage(apkName + " 已下载完成,是否安装?")
                .setPositiveButton("安装", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ShowNotifycation.installCancle(apkName);
                        if (!ProgressListViewAdapter.isSdEnable(context)) {
                            return;
                        }
                        String filePath = SettingsUtils.getRootPath() + apkName
                                + ".apk";
                        File file = new File(filePath);
                        if (file.exists() && file.isFile()) {// 如果apk文件存在

                            PackageManager pm = context.getPackageManager();
                            PackageInfo info = pm.getPackageArchiveInfo(
                                    filePath, PackageManager.GET_ACTIVITIES);
                            if (info != null) {
                                ApplicationInfo appInfo = info.applicationInfo;

                                // 安装apk方法
                                Intent i = new Intent(Intent.ACTION_VIEW);
                                i.setDataAndType(Uri.fromFile(file),
                                        "application/vnd.android.package-archive");
                                context.startActivity(i);
                                // setNullOnclick(imageButton);
                            }
                        } else {// 如果apk文件不存在
                            DialogAndToast.showToast(context, "文件不存在,重新下载");
                        }
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        alertBuilder.create().show();

    }
}
