package com.app43.appclient.module.install_introduce;

import com.app43.appclient.bean.App;

import java.util.List;

/**
 * 兴趣推荐的数据结构类
 * 
 * 项目名称：app43 类名称：InfoGroup 类描述： 创建人：APP43 创建时间：2012-2-9 上午11:46:42 修改人：APP43
 * 修改时间：2012-2-9 上午11:46:42 修改备注：
 * 
 * @version
 * 
 */
public class InfoGroup {
    String name;
    List<App> appList;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<App> getAppList() {
        return appList;
    }

    public void setAppList(List<App> appList) {
        this.appList = appList;
    }

}
