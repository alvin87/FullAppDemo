package com.app43.appclient.module.tabframe;

import com.alvin.api.utils.LogOutputUtils;
import com.alvin.api.utils.SettingsUtils;
import com.alvin.api.utils.UMengAnalyseUtils;
import com.app43.appclient.R;
import com.app43.appclient.module.abstracts.activity.BaseListViewWithExitActivity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import java.util.Iterator;
import java.util.Map;

public class GuessInterestActivity extends BaseListViewWithExitActivity {

    // static boolean firstIn = true;
    static Bundle insatance;
    String url ;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        insatance = savedInstanceState;
        isRefresh = true;
        isGuess = true;
        url= SettingsUtils.URL_GUESS;
        Map<Long, Long> behavior = dbUser_behavior.getThreeBehavior();
        if (behavior != null && behavior.size() > 0) {
            Iterator iterator = behavior.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry entry = (Map.Entry) iterator.next();
                url += "," + entry.getKey();
            }

            // TODO 获取用户行为修改发送接口url
        } else {
            url += "0";
        }
        setInitData("GuessInterestActivity",
                R.layout.guess_interest_app_activity, R.string.tab_guess_name,
                url);
        super.onCreate(savedInstanceState);
        TextView topTitle = (TextView) findViewById(R.id.information_top_title);
        topTitle.setText("换一批");
        topTitle.setBackgroundResource(R.drawable.change_list_selector);
        topTitle.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                listProgressBar.setVisibility(View.VISIBLE);
                serverAppList.clear();
                sendJsonData(url, pageNo, getJsonHandler, GuessInterestActivity.this,
                        true);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        UMengAnalyseUtils.onResume(this);
        // if (!firstIn) {
        if (!insideActivity) {
            LogOutputUtils.e(TAG, "每次都进来");
           
            // isInstallAppFinish=false;
            // readInstallApp(); //暂时无测出有内存泄漏,但是重载不发送数据

            // onCreate(insatance);//内存泄漏严重,但是可以随时刷新

            // 内存泄漏较少,而且可以重载就刷新,但是不知道有无bug
            aListAdapter.setAppcount(0);
            isGuess = true;
            if (!isGuess) {
                footLayout.setVisibility(View.GONE);
            }
            footerViewIsVisible = false;
            listProgressBar.setVisibility(View.VISIBLE);
            listView.setAdapter(aListAdapter);
            serverAppList.clear();
            isServerAppFinish = false;
            readInstallApp();
            readLocalApp();
            pageNo = -1;
            sendJsonData(downloadUrl, pageNo, getJsonHandler, this, true);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // firstIn = false;
        insideActivity = false;
        UMengAnalyseUtils.onPause(this);
    }

}
