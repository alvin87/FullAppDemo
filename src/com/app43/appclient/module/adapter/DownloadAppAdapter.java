package com.app43.appclient.module.adapter;

/**
 * 通用程序列表适配器
 */
import com.alvin.api.utils.LogOutputUtils;
import com.alvin.api.utils.SettingsUtils;
import com.app43.appclient.R;
import com.app43.appclient.module.abstracts.activity.BaseActivity;
import com.app43.appclient.module.tabframe.AppManagerActivity.SetupBackgroung;

import android.app.Activity;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import java.util.HashMap;
import java.util.Map;

public class DownloadAppAdapter extends ProgressListViewAdapter {

    private Integer count = 0, lastCount = 0;// 因为要开线程去读取数据库,所以要设置一个最新点击的标记,避免多次更新
    SetupBackgroung setupBackgroung;

    public DownloadAppAdapter(Activity activity) {
        super(activity, DownloadAppAdapter.class.getSimpleName());
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final int localId = position;
        // LogOutput.e("count", "id:" + localId);
        ViewHolder holder = null; // 使用viewholder加载图片速度提高50%
        Map<String, Object> map = new HashMap<String, Object>();
        map = setupViews(convertView, R.layout.recommend_app_list_item, holder);
        convertView = (View) map.get(SettingsUtils.VIEW);// 获取converView
        holder = (ViewHolder) map.get(SettingsUtils.VIEWHOLDE);// 获取vviewHold
        holder.summary.setVisibility(View.GONE);
        if (progressBar != null) {
            progressBar.setVisibility(View.INVISIBLE);
        }
        if (position <= serverAppList.size() - 1) {
            // 设置图片
            setImage(serverAppList, position, holder, firstPosition,
                    lastPosition, scrollStop, defaultDrawable);

            // ----------------------------------
            setSafe(holder.safeImageView, R.drawable.list_safe, serverAppList
                    .get(localId).getSafe());
            // 名字

            setName(holder.name, serverAppList.get(position).getTitle());
            // 大小
            setSize(holder.size, serverAppList.get(localId).getSize());
            // ------------------------------------------------------------设置按钮
            setAppManagerButtonText(localId, holder, convertView,
                    setupBackgroung);
        }

        return convertView;
    }

    public void updateList(ProgressBar pb, SetupBackgroung setupBackgroung) {
        isAppManaActivity = true;
        this.setupBackgroung = setupBackgroung;
        progressBar = pb;
        setAppcount(0);
        readDB(lastHandler);
        // for (int i = 0; i < serverAppList.size(); i++) {
        // serverAppList.get(i).setDownloadUrl(apkUrl + i + ".apk");
        // }

    }

    private void readDB(final Handler handler) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                serverAppList.clear();
                lastCount++;
                count = lastCount;
                synchronized (count) {
                    serverAppList = BaseActivity.dbApp_download
                            .getDownloadApp();
                    setAppcount(serverAppList.size());
                    handler.sendEmptyMessage(count);
                }
                LogOutputUtils
                        .e(TAG, "释放资源" + "isAppactivity: " + isAppManaActivity);
            }
        }).start();
    }

    Handler lastHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg.what == lastCount) {
                setupBackgroung.setbg(serverAppList.size());
                notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }

        };
    };
}
