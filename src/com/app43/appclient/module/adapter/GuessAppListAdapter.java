package com.app43.appclient.module.adapter;

/**
 * 通用程序列表适配器
 */
import com.app43.appclient.bean.App;

import android.app.Activity;

import java.util.ArrayList;

/**
 * 纯粹是为了做猜你喜欢的下载量统计而加上的
 * 
 * 项目名称：app43 类名称：GuessAppListAdapter 类描述： 创建人：APP43 创建时间：2012-2-18 下午10:40:33
 * 修改人：APP43 修改时间：2012-2-18 下午10:40:33 修改备注：
 * 
 * @version
 * 
 */
public class GuessAppListAdapter extends AppListAdapter {

    public GuessAppListAdapter(Activity activity,
            ArrayList<App> installRecommendApps) {
        super(activity, installRecommendApps);
    }

}
