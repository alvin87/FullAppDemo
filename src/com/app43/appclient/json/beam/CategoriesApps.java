package com.app43.appclient.json.beam;

import com.app43.appclient.bean.App;

import java.util.List;

/**
 * 分类列表类
 * @author pc
 *
 */
public class CategoriesApps {

    private int id;
    private String name;
    private List<App> apps;
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public List<App> getApps() {
        return apps;
    }
    public void setApps(List<App> apps) {
        this.apps = apps;
    }
    
    
}
