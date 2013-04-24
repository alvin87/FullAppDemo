package com.app43.appclient.module.common.activity;

/**
 * 通用列表app的终端页
 */
import com.alvin.api.utils.LogOutputUtils;
import com.alvin.api.utils.SettingsUtils;
import com.alvin.api.utils.UMengAnalyseUtils;
import com.app43.appclient.module.abstracts.activity.BaseListViewActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class ContentViewActivity extends FocusContentViewActivity {
    protected String pageUrl = "";
    protected int pageNo = -1;
    protected boolean isTurnPage = false;// 是否翻页了
    Toast toast;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        TAG = "ContentViewActivity";
        super.onCreate(savedInstanceState);
    }

    @Override
    public void finish() {
        Intent intent = new Intent();
        intent.putExtra(SettingsUtils.POSITION, position);
        intent.putParcelableArrayListExtra(SettingsUtils.SERVERAPPLIST,
                serverAppList);
        // LogOutput.e("listAdapter" ,
        // "name"+serverAppList.get(0).getName()+"isTurnPage"+isTurnPage);
        intent.putExtra(SettingsUtils.PAGEURL, pageUrl);
        intent.putExtra(SettingsUtils.PAGENO, pageNo);
        intent.putExtra(SettingsUtils.ISTURN_NEXT, isTurnPage);
        intent.putExtra(SettingsUtils.CONTENT_CLICK_ISCHANGE,
                BaseListViewActivity.isChange);
        LogOutputUtils.e(TAG, "终端页点击了: "+BaseListViewActivity.isChange);
        setResult(SettingsUtils.ACTIVITY_RESULT, intent);
        super.finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        UMengAnalyseUtils.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        UMengAnalyseUtils.onPause(this);
    }
}
