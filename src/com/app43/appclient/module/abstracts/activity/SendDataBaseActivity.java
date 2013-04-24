package com.app43.appclient.module.abstracts.activity;

import com.alvin.api.components.DialogAndToast;
import com.alvin.api.utils.JsonDownTask;
import com.alvin.api.utils.LogOutputUtils;
import com.alvin.api.utils.SettingsUtils;
import com.alvin.api.utils.SingleThreadDownloadUtils;
import com.app43.appclient.module.tabframe.RecommendAppActivity;
import com.app43.appclient.module.utils.ParseJsonUtils;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;

import java.io.FileInputStream;

/*
 * 带有收发数据和菜单功能的Activity
 */
public abstract class SendDataBaseActivity extends MenuActivity {

    private String TAG = SendDataBaseActivity.class.getSimpleName();

    /**
     * 精品推荐发送数据
     * 
     * @param url
     * @param noPage
     * @param mHandler
     * @param activity
     * @param refresh
     * @param isClear
     *            是否清空队列,精品推荐Activity切换title时为true
     */
    protected void sendJsonData(String url, int noPage, Handler mHandler,
            Activity activity, boolean refresh) {
        if (url != null && !url.equals("")) {
            String pageUrl = "";
            if (noPage >= 0) {
                pageUrl = url + noPage;
            } else {
                pageUrl = url;
            }
            JsonDownTask task = new JsonDownTask();
            task.setActivityClass(activity.getClass());
            task.setUrl(pageUrl);
            task.setRefresh(refresh);
            task.setHandler(mHandler);
            if (activity.getClass().equals(RecommendAppActivity.class)) {
                task.setClear(true);
            }
            LogOutputUtils.i(TAG, "pageUrl" + pageUrl + "// refresh" + refresh);
            SingleThreadDownloadUtils sender = SingleThreadDownloadUtils
                    .getInstance();
            try {
                sender.addDownloadTask(task);
            } catch (Exception e) {
                e.printStackTrace();
                LogOutputUtils.i(TAG, "addDownloadTask error" + task.getUrl());
            }
        }
    }

    /**
     * 作用:每个继承的子activity都要实现一个发送数据之后响应的handle.
     * 可以调用通用handle方法normallHandle(),也可以自己实现
     */
    protected abstract Handler initHandle();

    protected Handler normallHandle(final String tagString) {
        Handler handler = new Handler() {
            public void handleMessage(Message msg) {
                // LogOutput.e(TAG, ""+msg.what);
                String jsonString = ParseJsonUtils
                        .getJsonStrByFile((FileInputStream) msg.obj);
                if (jsonString == null) {
                    LogOutputUtils.i(tagString, "parse Json null");
                    DialogAndToast.showError(SendDataBaseActivity.this);
                    return;
                }
                switch (msg.what) {
                case SettingsUtils.JSON_SUCCESS:
                    handleViews(jsonString);
                    break;
                case SettingsUtils.JSON_NET_ERROR:
                    DialogAndToast.showError(SendDataBaseActivity.this);
                    break;
                default:
                    break;
                }
            };
        };
        return handler;
    }

    /**
     * 每个继承的非抽象Activity都要写一个handle来调用handleView重绘
     * 
     * @param jsonString
     */
    protected abstract void handleViews(String jsonString);

}
