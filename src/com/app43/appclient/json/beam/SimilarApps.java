package com.app43.appclient.json.beam;

import com.app43.appclient.bean.App;

import java.util.List;

public class SimilarApps {

    String packageName;
    List<App> apps;
    public String getPackageName() {
        return packageName;
    }
    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }
    public List<App> getApps() {
        return apps;
    }
    public void setApps(List<App> apps) {
        this.apps = apps;
    }
    
    
}
