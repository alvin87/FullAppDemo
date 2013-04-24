package com.alvin.api.utils;

import com.app43.appclient.R;
import com.app43.appclient.module.launch.LauncherActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Parcelable;

/**
 * 
 * 
 * 项目名称：app43 类名称：LuancherUtils 类描述： 主要用于启动程序时所要做的相关动作类 创建人：APP43 创建时间：2012-3-12
 * 下午5:40:15 修改人：APP43 修改时间：2012-3-12 下午5:40:15 修改备注：
 * 
 * @version
 * 
 */
public class LuancherUtils {

    /**
     * 作用:创建快捷方式
     */
    public static void createShortcut(final Context context, final int appIcon) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                SettingsUtils.USERINFO_COLLECTION, Activity.MODE_PRIVATE);

        boolean isCreated = sharedPreferences.getBoolean(
                SettingsUtils.SHORTUCT, false);

        if (!isCreated) {
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
            alertBuilder
                    .setTitle("快捷创建提示")
                    .setMessage("是否创建快捷方式?")
                    .setPositiveButton("确定",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                        int id) {

                                    UMengAnalyseUtils.onEvents(context,
                                            UMengAnalyseUtils.VIEW_SHORTUCT);
                                    Intent myIntent = new Intent();
                                    myIntent.setClass(context,
                                            LauncherActivity.class);
                                    myIntent.setAction("android.intent.action.MAIN");
                                    myIntent.addCategory("android.intent.category.LAUNCHER");

                                    Intent addIntent = new Intent(
                                            "com.android.launcher.action.INSTALL_SHORTCUT");

                                    Parcelable icon = Intent.ShortcutIconResource
                                            .fromContext(context, appIcon); // 获取快捷键的图标

                                    addIntent.putExtra(
                                            Intent.EXTRA_SHORTCUT_NAME,
                                            context.getResources().getString(
                                                    R.string.app_name));// 快捷方式的标题
                                    addIntent
                                            .putExtra(
                                                    Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
                                                    icon);// 快捷方式的图标
                                    addIntent.putExtra(
                                            Intent.EXTRA_SHORTCUT_INTENT,
                                            myIntent);// 快捷方式的动作
                                    addIntent.putExtra("duplicate", false);
                                    context.sendBroadcast(addIntent);// 发送广播
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
            Editor editor = sharedPreferences.edit();
            editor.putBoolean(SettingsUtils.SHORTUCT, true);
            editor.commit();
        }

    }
}
