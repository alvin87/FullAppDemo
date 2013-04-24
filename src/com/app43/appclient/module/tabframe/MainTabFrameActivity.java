package com.app43.appclient.module.tabframe;

/*
 *重绘activity会变小
 *
 */
import com.alvin.api.components.TabHostActivityGroup;
import com.alvin.api.utils.LuancherUtils;
import com.alvin.api.utils.SettingsUtils;
import com.alvin.api.utils.UMengAnalyseUtils;
import com.app43.appclient.R;
import com.app43.appclient.bean.App;
import com.app43.appclient.module.abstracts.activity.BaseActivity;
import com.app43.appclient.module.adapter.ProgressListViewAdapter;
import com.app43.appclient.module.db.DBApp_download;
import com.feedback.NotificationType;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TabHost.TabSpec;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class MainTabFrameActivity extends TabHostActivityGroup {
    protected final static String TAG = MainTabFrameActivity.class
            .getSimpleName();
    protected ArrayList<App> arrayList = new ArrayList<App>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
        setItemActivityHeight();
        updateMainTabView();
    }

    /**
     * 作用:初始化数据,添加统计,获取数据库对象等
     */
    private void initData() {

        // 添加友盟更新以及在线配置事件
//        UMengAnalyseUtils.update(this);
//        UMengAnalyseUtils.enableNewReplyNotification(this,
//                NotificationType.AlertDialog);

        // 创建快捷方式
        LuancherUtils.createShortcut(this, R.drawable.app43_icon);

        // 获取数据库对象
        BaseActivity.dbApp_download = new DBApp_download(this,
                SettingsUtils.DATABASEVERSION);

        // 获取数据库下载的app
        Map<String, App> map = BaseActivity.dbApp_download.getAllDownloadApk();

        // 如果有没下载完的继续下载
        Iterator<Entry<String, App>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Entry<String, App> entry = iterator.next();
            App app = ((App) entry.getValue());
            if (app.getProgress() < 100) {
                ProgressListViewAdapter.downloadApk(app, this);
            }
        }

        // 如果是从从通知栏点击进来bundle不为null
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            flag = bundle.getInt(SettingsUtils.NOTIFICATION_ONCLIAK, 0);
            arrayList = bundle
                    .getParcelableArrayList(SettingsUtils.INSTALL_RECOMMEND);
        }
    }

    protected void tabhostAddTab(android.widget.TabHost tabHost,
            int itemLayoutId, int textId, int iconID, int itemId,
            String itemIdString, Class activityClass) {
        RelativeLayout tabLayout = (RelativeLayout) LayoutInflater.from(this)
                .inflate(itemLayoutId, null);
        TextView item0TabText = (TextView) tabLayout
                .findViewById(R.id.tab_text);
        ImageView item0TabIcon = (ImageView) tabLayout
                .findViewById(R.id.tab_icon);
        item0TabText.setText(this.getString(textId));
        item0TabIcon.setBackgroundResource(iconID);

        // 初始化精品intent 和tabspec并添加到tabhost
        Intent tabIntent = new Intent();
        tabIntent.setClass(this, activityClass);
        tabIntent.putExtra("item", itemId);
        tabIntent.putParcelableArrayListExtra(SettingsUtils.INSTALL_RECOMMEND,
                arrayList);
        TabSpec tabSpec = tabHost.newTabSpec(itemIdString);
        tabSpec.setIndicator(tabLayout);// 加载view
        tabSpec.setContent(tabIntent);
        tabHost.addTab(tabSpec);

    };
}
