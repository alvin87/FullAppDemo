package com.app43.appclient.module.adapter;

import com.alvin.api.utils.SettingsUtils;
import com.app43.appclient.R;
import com.app43.appclient.module.hot.GameListActivity;
import com.app43.appclient.module.hot.HotListActivity;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class BaseGridViewAdapter extends BaseAdapter {

    protected int[] imageId, textId;
    protected String[] urls;
    Activity targetActivity,oldTarActivity;
    Activity activity;
    int count;
     BaseGridViewHolder baseGridViewhold;
     boolean ishot;//是否热门排行

    public BaseGridViewAdapter(Activity activity, int[] images, int[] texts,
            String url[], Activity tarActivity,boolean ishot) {
        this.activity = activity;
        imageId = images;
        textId = texts;
        urls = url;
        targetActivity = tarActivity;
        oldTarActivity=targetActivity;
        this.ishot=ishot;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        
        if (convertView == null) {
            baseGridViewhold = new BaseGridViewHolder();
            convertView = LayoutInflater.from(activity).inflate(
                    R.layout.hot_grid_item, null);
            baseGridViewhold.icon = (ImageView) convertView
                    .findViewById(R.id.hot_grid_item_image);
            baseGridViewhold.name = (TextView) convertView
                    .findViewById(R.id.hot_grid_item_text);
            convertView.setTag(baseGridViewhold);
        } else {
            baseGridViewhold = (BaseGridViewHolder) convertView.getTag();
        }
        baseGridViewhold.icon.setImageDrawable(activity.getResources().getDrawable(
                imageId[position]));
        baseGridViewhold.name.setText(activity.getResources().getText(textId[position]));
        viewClick(convertView, position);
        return convertView;
    }

    protected void viewClick(View view, final int position) {
        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ishot){
                    Intent intent = new Intent();
                    if(position==0){
                        targetActivity=new GameListActivity();
                    }else {
                        targetActivity=new HotListActivity();
                    }
                    intent.setClass(activity, targetActivity.getClass());
                    intent.putExtra(SettingsUtils.PAGEURL, urls[position]);
                    intent.putExtra(SettingsUtils.CATEGORY_NAME, textId[position]);
                    activity.startActivity(intent);
                }else {
                    Intent intent = new Intent();
                    intent.setClass(activity, targetActivity.getClass());
                    intent.putExtra(SettingsUtils.PAGEURL, urls[position]);
                    intent.putExtra(SettingsUtils.CATEGORY_NAME, textId[position]);
                    activity.startActivity(intent);
                }
               
            }
        });
    }


    public void setAppCount(int count) {
        this.count = count;
    }

    @Override
    public int getCount() {
        return count;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private static class BaseGridViewHolder {
        public ImageView icon = null;
        public TextView name = null, size = null, summary = null;
        public String iconurl, downName;// 回调映射时对应的item
        public Button imageButton
//        , delButton
        ;
        public CheckBox checkBox;
        public ProgressBar progressBar;// 进度条
        public TextView version;
    }
}
