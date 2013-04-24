package com.app43.appclient.module.launch;

import cn.com.pcgroup.common.android.config.Env;

import com.alvin.api.utils.MossAnalyseUtils;
import com.alvin.api.utils.PhoneInfoUtils;
import com.alvin.api.utils.SettingsUtils;
import com.alvin.api.utils.UMengAnalyseUtils;
import com.app43.appclient.R;
import com.app43.appclient.module.abstracts.activity.BaseActivity;
import com.app43.appclient.module.tabframe.MainTabFrameActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

/**
 * 启动界面
 */
public class LauncherActivity extends BaseActivity {

    private final static String TAG = LauncherActivity.class.getSimpleName();
    public final int STAY = 1; // 界面停留时间，单位：秒

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PhoneInfoUtils.setPhoneInfo(this);
        UMengAnalyseUtils.updateOnlineConfig(this);
        UMengAnalyseUtils.setDebugMode(false);
        UMengAnalyseUtils.onError(this);
        MossAnalyseUtils.onEvents(MossAnalyseUtils.EVENT_OPEN, "", "");
        // if (Settings.APK_DOWNLOAD_PATH == ""
        // || Settings.APK_DOWNLOAD_PATH.equals("")
        // ) {
        // Settings.APK_DOWNLOAD_PATH = ((CommonApplication) getApplication())
        // .getExternalFileDirectory().getAbsolutePath()+"/";
        // }
        this.setContentView(R.layout.launcher_activity);
        if (Env.versionName != null) {
            TextView textView = (TextView) this
                    .findViewById(R.id.launcher_version_text);
            // textView.setTextSize(DisplayUtils.convertDIP2PX(this, 18));
            textView.setText("V " + Env.versionName);
        }
    }

    private final Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            SharedPreferences sharedPreferences = getSharedPreferences(
                    SettingsUtils.USERINFO_COLLECTION, MODE_PRIVATE);
            Intent intent = new Intent();
            if (sharedPreferences.getBoolean(SettingsUtils.ISFIRSTIN, true)) {
                intent.setClass(LauncherActivity.this,
                        LauncherUserInfoActivity.class);
                int width = getWindowManager().getDefaultDisplay().getWidth();
                int height = getWindowManager().getDefaultDisplay().getHeight();
                PhoneInfoUtils.setResolution(width + "*" + height);
                MossAnalyseUtils.onEvents(MossAnalyseUtils.EVENT_DEVINFO, "",
                        "");
            } else {
                intent.setClass(LauncherActivity.this,
                        MainTabFrameActivity.class);
            }
            startActivity(intent);
            LauncherActivity.this.overridePendingTransition(R.anim.fade,
                    R.anim.hold);
            LauncherActivity.this.finish();
        }
    };

    private class PreloadThread extends Thread {
        private Handler handler;

        public PreloadThread(Handler handler) {
            this.handler = handler;
        }

        public void run() {
            long start = System.currentTimeMillis();
            try {
                // 初始化系统配置
                // Config.initWithoutNetwork(LauncherActivity.this);
                //
                // //初始化栏目信息
                // ChannelUtils.initClinetChannel(LauncherActivity.this);
                //
                // //初始化离线下载网络状态
                // MoreSettingActivity.defaultNetworkSetting();
                //
                // new Thread() {
                // public void run() {
                // //异步加载初始化论坛板块数据，无需访问网络
                // BbsApiService bbsApiService =
                // BbsApiService.getInstance(LauncherActivity.this);
                // bbsApiService.getRootForum();
                //
                // //异步加载初始化车型库数据
                // if(Config.WEBSITE == Config.WEBSITE_PCAUTO){
                // ProductApiService productApiService =
                // ProductApiService.getInstance(LauncherActivity.this);
                // productApiService.getBrandList();
                // }
                // }
                // }.start();
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                while ((System.currentTimeMillis() - start) <= STAY * 1000) {
                    Thread.sleep(100);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            handler.sendMessage(handler.obtainMessage());
        }
    }

    public void onResume() {
        super.onResume();
        UMengAnalyseUtils.onResume(this);
        // 预载首界面数据，然后跳转到首界面
        new PreloadThread(handler).start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        UMengAnalyseUtils.onPause(this);
    }
}
