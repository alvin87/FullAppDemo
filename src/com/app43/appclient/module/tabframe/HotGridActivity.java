package com.app43.appclient.module.tabframe;

import com.alvin.api.utils.SettingsUtils;
import com.alvin.api.utils.UMengAnalyseUtils;
import com.app43.appclient.R;
import com.app43.appclient.module.abstracts.activity.BaseGridActivity;
import com.app43.appclient.module.hot.HotListActivity;

public class HotGridActivity extends BaseGridActivity {

    public HotGridActivity() {
        super(true);
    }

    protected void setupData() {
        imagesId = new int[] { R.drawable.hot1, R.drawable.hot2,
                R.drawable.hot3, R.drawable.hot4, R.drawable.hot5,
                R.drawable.hot6, R.drawable.hot7, R.drawable.hot8,
                R.drawable.hot9, R.drawable.hot10, R.drawable.hot11,
                R.drawable.hot12, R.drawable.hot13, R.drawable.hot14,
                R.drawable.hot15, R.drawable.hot16, R.drawable.hot17,
                R.drawable.hot18 };
        textId = new int[] { R.string.hot1, R.string.hot2, R.string.hot3,
                R.string.hot4, R.string.hot5, R.string.hot6, R.string.hot7,
                R.string.hot8, R.string.hot9, R.string.hot10, R.string.hot11,
                R.string.hot12, R.string.hot13, R.string.hot14, R.string.hot15,
                R.string.hot16, R.string.hot17, R.string.hot18 };
        urls = new String[] { SettingsUtils.URL_HOT_1, SettingsUtils.URL_HOT_2,
                SettingsUtils.URL_HOT_3, SettingsUtils.URL_HOT_4,
                SettingsUtils.URL_HOT_5, SettingsUtils.URL_HOT_6,
                SettingsUtils.URL_HOT_7, SettingsUtils.URL_HOT_8,
                SettingsUtils.URL_HOT_9, SettingsUtils.URL_HOT_10,
                SettingsUtils.URL_HOT_11, SettingsUtils.URL_HOT_12,
                SettingsUtils.URL_HOT_13, SettingsUtils.URL_HOT_14,
                SettingsUtils.URL_HOT_15, SettingsUtils.URL_HOT_16,
                SettingsUtils.URL_HOT_17, SettingsUtils.URL_HOT_18 };
        titleId = R.string.tab_hot_name;
        tarActivity = new HotListActivity();
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
