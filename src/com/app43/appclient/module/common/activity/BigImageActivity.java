package com.app43.appclient.module.common.activity;

import cn.com.pcgroup.common.android.utils.DisplayUtils;

import com.alvin.api.utils.SettingsUtils;
import com.alvin.api.utils.UMengAnalyseUtils;
import com.app43.appclient.R;
import com.app43.appclient.module.abstracts.activity.SendDataNoMenuActivity;
import com.app43.appclient.module.adapter.GalleryListAdapter;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.FrameLayout;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

public class BigImageActivity extends SendDataNoMenuActivity {

    FrameLayout bigImageLayout;
    private List<ImageView> pointImageList = new ArrayList<ImageView>();
    Gallery gallery;
    List<String> bigImageList = new ArrayList<String>();
    ProgressBar progressBar;
    int position;

    @Override
    protected void handleViews(String jsonString) {

    }

    @Override
    protected Handler initHandle() {
        return null;
    }

    @Override
    public void setupViews() {
        bigImageList = getAppList();
        setContentView(R.layout.big_image_activity);
        bigImageLayout = (FrameLayout) findViewById(R.id.bigImage);
        gallery = (Gallery) findViewById(R.id.gallery);
        progressBar = (ProgressBar) findViewById(R.id.big_image_loadprogress);
        addHeaderPointImage(bigImageList.size(), R.id.pointImage, this,
                pointImageList);
        int width = this.getResources().getDisplayMetrics().widthPixels;
        int heigh = this.getResources().getDisplayMetrics().heightPixels;
        GalleryListAdapter imageListAdapter = new GalleryListAdapter(this,
                width, heigh, 0, R.drawable.gallery_default,
                SettingsUtils.CONTENT_BIGIMG);
        gallery.setOnItemSelectedListener(new OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> paramAdapterView,
                    View paramView, int paramInt, long paramLong) {
                for (int i = 0; i < pointImageList.size(); i++) {
                    if ((paramInt % pointImageList.size()) == i) {
                        pointImageList.get(i).setImageResource(
                                R.drawable.information_focus_cirlce_current);
                    } else {
                        pointImageList.get(i).setImageResource(
                                R.drawable.information_focus_cirlce);
                    }
                }
            }

            public void onNothingSelected(AdapterView<?> paramAdapterView) {
            }
        });
        gallery.setAdapter(imageListAdapter);
        gallery.setSelection(300 + position);
        gallery.setHorizontalFadingEdgeEnabled(false);
        imageListAdapter.updateImage(bigImageList, progressBar);
    }

    private List<String> getAppList() {
        List<String> list = new ArrayList<String>();
        Bundle bundle = getIntent().getExtras();
        String url = bundle.getString(SettingsUtils.BIG_IMAGE_URL);
        position = bundle.getInt(SettingsUtils.BIG_IMAGE_POSITION);
        String[] urlStrings = new String[] {};
        if (url.contains(",")) {
            urlStrings = url.split(",");
            for (int i = 0; i < urlStrings.length; i++) {
                list.add(urlStrings[i]);
            }
        } else {
            list.add(url);
        }

        return list;
    }

    /**
     * 动态添加圆点
     */
    public static void addHeaderPointImage(int size, int lineLayoutId,
            Activity activity, List<ImageView> list) {
        LinearLayout linearLayout = (LinearLayout) activity
                .findViewById(lineLayoutId);
        ImageView focusView;
        ViewGroup.LayoutParams p = new ViewGroup.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        for (int i = 0; i < size; i++) {
            focusView = new ImageView(activity);
            if (i == (size - 1)) {
                focusView.setPadding(DisplayUtils.convertDIP2PX(activity, 10),
                        DisplayUtils.convertDIP2PX(activity, 5),
                        DisplayUtils.convertDIP2PX(activity, 10),
                        DisplayUtils.convertDIP2PX(activity, 5));
            } else {
                focusView.setPadding(DisplayUtils.convertDIP2PX(activity, 10),
                        DisplayUtils.convertDIP2PX(activity, 5),
                        DisplayUtils.convertDIP2PX(activity, 0),
                        DisplayUtils.convertDIP2PX(activity, 5));
            }
            linearLayout.addView(focusView, p);
            list.add(focusView);
        }
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
    /**
     * 去除圆点
     */
    // public void removeHeaderPointImage() {
    // for (ImageView iamgeView : pointImageList) {
    // bigImageLayout.removeView(iamgeView);
    // }
    // pointImageList.clear();
    // }
}
