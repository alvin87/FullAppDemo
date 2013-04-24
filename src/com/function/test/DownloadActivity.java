package com.function.test;

import cn.com.pcgroup.common.android.utils.HttpUtils;
import cn.com.pcgroup.common.android.utils.HttpUtils.HttpDownloadAdapter;

import com.alvin.api.utils.LogOutputUtils;
import com.alvin.api.utils.SettingsUtils;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

import java.io.File;

/**
 * 测试断点续传
 * 
 * 项目名称：app43 类名称：DownloadActivity 类描述： 创建人：pc 创建时间：2011-11-23 上午10:52:01 修改人：pc
 * 修改时间：2011-11-23 上午10:52:01 修改备注：
 * 
 * @version
 * 
 */
public class DownloadActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Button button = new Button(this);
        // if (Settings.APK_DOWNLOAD_PATH == ""
        // || Settings.APK_DOWNLOAD_PATH.equals("")) {
        // Settings.APK_DOWNLOAD_PATH = ((CommonApplication) getApplication())
        // .getExternalFileDirectory().getAbsolutePath() + "/";
        // }
        final File file = new File(SettingsUtils.getRootPath(), "test" + ".apk");
        final String url = "http://192.168.19.143:8087/1.apk";
        button.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                new Thread() {
                    public void run() {

                        try {
                            HttpDownloadAdapter httpDownloadAdapter1 = new HttpDownloadAdapter() {
                                @Override
                                public void notifyProgress(int progress) {
                                    LogOutputUtils.e("httpDownloadAdapter",
                                            "progress" + progress);
                                }
                            };
                            HttpUtils.download(url, file, false,
                                    httpDownloadAdapter1);
                        } catch (Exception e) {
                            e.printStackTrace();
                            // LogOutput.e(TAG, app.getName() +
                            // "DownLoad Failed");
                        }
                    };
                }.start();
            }
        });
        button.setText("不续传");
        Button button1 = new Button(this);
        button1.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                new Thread() {

                    public void run() {
                        try {
                            HttpDownloadAdapter httpDownloadAdapter = new HttpDownloadAdapter() {
                                @Override
                                public void notifyProgress(int progress) {
                                    LogOutputUtils.e("httpDownloadAdapter",
                                            "续传progress" + progress);
                                }
                            };
                            HttpUtils.download(url, file, true,
                                    httpDownloadAdapter);
                        } catch (Exception e) {
                            e.printStackTrace();
                            // LogOutput.e(TAG, app.getName() +
                            // "DownLoad Failed");
                        }
                    };
                }.start();
            }
        });
        button1.setText("续传");

        Button button2 = new Button(this);
        button2.setText("删除file");
        button2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                file.delete();
            }
        });
        Button button3 = new Button(this);
        button3.setText("关闭");
        button3.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        });
        LinearLayout layout = new LinearLayout(this);
        layout.addView(button);
        layout.addView(button1);
        layout.addView(button2);
        layout.addView(button3);
        setContentView(layout);

    }

    // try {
    // HttpDownloadItem httpDownloadItem = HttpUtils.invokeWithCache(
    // "http://wwwand.app43.com/index.php/android/api/userapps",
    // CacheUtils.CACHE_EXTERNAL, 36000, false);
    //
    // String jsonString = ParseJsonUtils
    // .getJsonStrByFile((FileInputStream) httpDownloadItem.getInputStream());
    //
    // LogOutput.e("nessary",
    // "size:"+ParseJsonUtils.getUserInfoMap(jsonString).size());
    // } catch (Exception e) {
    // // TODO Auto-generated catch block
    // e.printStackTrace();
    // }

}
