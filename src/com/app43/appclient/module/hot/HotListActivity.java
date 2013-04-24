package com.app43.appclient.module.hot;

import com.alvin.api.utils.LogOutputUtils;
import com.alvin.api.utils.SettingsUtils;
import com.app43.appclient.R;
import com.app43.appclient.module.abstracts.activity.BaseListViewActivity;

import android.os.Bundle;
import android.view.KeyEvent;

public class HotListActivity extends BaseListViewActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Bundle bundle = getIntent().getExtras();
        String top_kind_name = this.getResources().getString(
                bundle.getInt(SettingsUtils.CATEGORY_NAME));
         LogOutputUtils.e(HotListActivity.class.getSimpleName(), top_kind_name);
        setInitData("HotListActivity", R.layout.hot_app_activity,
                bundle.getInt(SettingsUtils.CATEGORY_NAME),
                bundle.getString(SettingsUtils.PAGEURL));
        super.onCreate(savedInstanceState);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            onBackPressed();
            return true;
        }
        return false;
    }
}
