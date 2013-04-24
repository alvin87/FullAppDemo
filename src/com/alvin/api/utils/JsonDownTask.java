package com.alvin.api.utils;

import android.os.Handler;

/**
 * 接口Json下载
 *     
 * 项目名称：app43    
 * 类名称：AppDownTask    
 * 类描述：    
 * 创建人：pc    
 * 创建时间：2011-11-11 上午9:53:24    
 * 修改人：pc    
 * 修改时间：2011-11-11 上午9:53:24    
 * 修改备注：    
 * @version     
 *
 */
public class JsonDownTask {
    public Class activityClass;
    public String url;
    public boolean refresh = false;
    public Handler handler;
    public boolean clear = false;// 只针对精品推荐切换title时使用

    public boolean isClear() {
        return clear;
    }

    public void setClear(boolean clear) {
        this.clear = clear;
    }

    public Class getActivityClass() {
        return activityClass;
    }

    public void setActivityClass(Class activityClass) {
        this.activityClass = activityClass;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isRefresh() {
        return refresh;
    }

    public void setRefresh(boolean refresh) {
        this.refresh = refresh;
    }

    public Handler getHandler() {
        return handler;
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

}
