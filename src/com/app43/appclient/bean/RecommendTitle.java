package com.app43.appclient.bean;

import android.content.Context;
import android.widget.TextView;

public class RecommendTitle extends TextView{

    public RecommendTitle(Context context) {
        super(context);        
    }
    String title;
    String titleUrl;
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getTitleUrl() {
        return titleUrl;
    }
    public void setTitleUrl(String titleUrl) {
        this.titleUrl = titleUrl;
    }
    
    
}
