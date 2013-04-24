package com.app43.appclient;

import cn.com.pcgroup.common.android.app.CommonApplication;
import cn.com.pcgroup.common.android.app.Initializer;
import cn.com.pcgroup.common.android.config.Env;
import cn.com.pcgroup.common.android.exception.CrashHandler;
import cn.com.pcgroup.common.android.utils.CacheUtils;

import com.alvin.api.utils.MossAnalyseUtils;
import com.alvin.api.utils.SettingsUtils;
import com.app43.appclient.module.db.DBApp_download;
import com.app43.appclient.module.db.DBFavourite;
import com.app43.appclient.module.db.DBUser_behavior;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;

/**
 * APP43客户端主程序
 */
public class AppClient extends CommonApplication {

    // 安装来源计数器
    public final static String DOWNLOAD_REFERENCE_APP43 = "app43";

    public static String sql = DBApp_download.sql + DBUser_behavior.sql
            + DBFavourite.sql;
    private int backKeyPressTimes = 0;

    public static boolean activityIsFinish = false, serviceIsFinish = true;// 用来判断是否关闭进程
    public static boolean isMenuExit = false;

    public AppClient() {
        super();

        if (SettingsUtils.context == null) {
            SettingsUtils.context = this;
        }
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(CrashHandler.REPORT_TYPE_FILE);
        Initializer init = new Initializer() {
            @Override
            public void init(Context context) {
            }
        };
        this.setInitializer(init);
        // 初始化全局日志TAG
        // Env.logTagPrefix = "APP43Client_";

        // 设置安装包来源
        // Env.DOWNLOAD_REFERENCE = DOWNLOAD_REFERENCE_APP43;

        this.setAppHome("app43");

        // 数据库设置
        this.setDatabaseVersion(SettingsUtils.DATABASEVERSION);
        this.setDatabaseInitSQL(sql);
        this.setDatabaseUpdateSQL(null);
    }

    public void exit(final Activity activity) {
        MossAnalyseUtils.onEvents(MossAnalyseUtils.EVENT_CLOSE, "", "");
        if (isMenuExit) {
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(activity);
            alertBuilder
                    .setTitle(this.getString(R.string.dialog_app_exit_title))
                    .setMessage(
                            this.getString(R.string.dialog_app_exit_message))
                    .setPositiveButton("退出",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                        int id) {
                                    CacheUtils.clearTempCache();
                                    Env.dbHelper.close();

                                    // 退出应用
                                    // activity.onBackPressed();
                                    // System.exit(0);
                                    // 要保持后台下载程序
                                    if (serviceIsFinish) {
                                        android.os.Process
                                                .killProcess(android.os.Process
                                                        .myPid());
                                    } else {
                                        activityIsFinish = true;
                                        activity.finish();
                                    }
                                }
                            })
                    .setNegativeButton("取消",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                        int id) {
                                    dialog.cancel();
                                }
                            });
            alertBuilder.create().show();
            isMenuExit = false;
            return;
        }
        if (serviceIsFinish) {
            if (backKeyPressTimes == 0) {
                Toast.makeText(AppClient.this, "再按一次返回键退出", Toast.LENGTH_LONG)
                        .show();
                backKeyPressTimes = 1;
                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        try {
                            Thread.sleep(2000);
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            backKeyPressTimes = 0;
                        }
                    }
                }).start();
            } else {
                android.os.Process.killProcess(android.os.Process.myPid());
            }

        } else {
            activityIsFinish = true;
            activity.finish();
        }

    }
}
