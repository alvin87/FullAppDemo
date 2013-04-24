package com.app43.appclient.module.abstracts.activity;
/**
 * 
 */
import com.app43.appclient.AppClient;

public class BaseListViewWithExitActivity extends BaseListViewActivity {
    @Override
    public void onBackPressed() {
        ((AppClient) this.getApplication()).exit(this);
    }
}
