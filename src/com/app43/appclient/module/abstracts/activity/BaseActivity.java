package com.app43.appclient.module.abstracts.activity;

import cn.com.pcgroup.common.android.config.Env;
import cn.com.pcgroup.common.android.utils.DisplayUtils;

import com.alvin.api.utils.LogOutputUtils;
import com.alvin.api.utils.SettingsUtils;
import com.app43.appclient.AppClient;
import com.app43.appclient.R;
import com.app43.appclient.module.db.DBApp_download;
import com.app43.appclient.module.db.DBFavourite;
import com.app43.appclient.module.db.DBUser_behavior;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

/**
 * 峰峰客户端公用基类，用于统一处理各种数据更新或重新初始化的工作
 */
public class BaseActivity extends Activity {
    // 如果发现系统配置没有初始化则初始化配置
    public final String TAG = BaseActivity.class.getSimpleName();
    public static DBApp_download dbApp_download;
    public static DBFavourite dbFavourite;
    public static DBUser_behavior dbUser_behavior;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (AppClient.activityIsFinish) {
            AppClient.activityIsFinish = false;
        }
        if (dbApp_download == null) {
            dbApp_download = new DBApp_download(this,
                    SettingsUtils.DATABASEVERSION);
        }
        if (dbFavourite == null) {
            dbFavourite = new DBFavourite(this, SettingsUtils.DATABASEVERSION);
        }
        if (dbUser_behavior == null) {
            dbUser_behavior = new DBUser_behavior(this,
                    SettingsUtils.DATABASEVERSION);
        }
        // 获取屏幕尺寸
        Env.display = this.getWindowManager().getDefaultDisplay();
        LogOutputUtils.i(TAG, "width:" + Env.display.getWidth() + "|| height:"
                + Env.display.getHeight());
        int ScreenDipHeight = DisplayUtils.convertPX2DIP(this,
                Env.display.getHeight());
        // Config.tabContentHeight = ScreenDipHeight -
        // Config.STATUS_BAR_HEIGHT - Config.TAB_BAR_HEIGHT;
    }

}
