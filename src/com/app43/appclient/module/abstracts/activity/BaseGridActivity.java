package com.app43.appclient.module.abstracts.activity;

import com.app43.appclient.R;
import com.app43.appclient.module.adapter.BaseGridViewAdapter;

import android.app.Activity;
import android.os.Handler;
import android.widget.GridView;
import android.widget.TextView;

public class BaseGridActivity extends SendDataWithExitBaseActivity {

    protected int imagesId[];// 分类图片资源Id
    protected int textId[];// 分类文本Id
    protected String urls[];// 分类url
    protected int titleId;
    protected Activity tarActivity;
    boolean ishot;

    public BaseGridActivity(boolean ishot) {
        this.ishot = ishot;
    }

    @Override
    protected void handleViews(String jsonString) {

    }

    @Override
    protected Handler initHandle() {
        return null;
    }

    /*
     * 配置数据imagesId,textId,urls
     */
    protected void setupData() {
    }

    @Override
    public void setupViews() {
        setContentView(R.layout.hot_grid_activity);
        setupData();
        TextView textView = (TextView) findViewById(R.id.grid_view_top_title);
        textView.setText(this.getResources().getText(titleId));
        GridView gridView = (GridView) findViewById(R.id.hot_gridview);
        BaseGridViewAdapter gridViewAdapter = new BaseGridViewAdapter(this,
                imagesId, textId, urls, tarActivity, ishot);
        gridViewAdapter.setAppCount(imagesId.length);
        gridView.setAdapter(gridViewAdapter);
    }

}
