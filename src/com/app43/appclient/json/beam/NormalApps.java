package com.app43.appclient.json.beam;
/*
 * 通用app列表类
 */
import com.app43.appclient.bean.App;

import java.util.List;

public class NormalApps {
    
    private List<App> focusApps;        // 焦点图app列表
    private List<App> apps;             // 普通app列表
    private int total;                  // app总数
    public List<App> getFocusApps() {
        return focusApps;
    }
    public void setFocusApps(List<App> focusApps) {
        this.focusApps = focusApps;
    }
    public List<App> getApps() {
        return apps;
    }
    public void setApps(List<App> apps) {
        this.apps = apps;
    }
    public int getTotal() {
        return total;
    }
    public void setTotal(int total) {
        this.total = total;
    }
    
    
}
