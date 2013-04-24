package com.app43.appclient.module.utils;

import com.alvin.api.utils.AParseJsonUtils;
import com.alvin.api.utils.LogOutputUtils;
import com.alvin.api.utils.SettingsUtils;
import com.app43.appclient.bean.App;
import com.app43.appclient.bean.UserInfoBean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import weibo4android.User;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ParseJsonUtils extends AParseJsonUtils {
    private final static String TAG = ParseJsonUtils.class.getSimpleName();
    private static URL url;

    /**
     * 
     * 作用: 通用AppJson解析列表
     * 
     * @return 只包含map<total,int>map<apps,list>
     *         SettingsUtils.JSON_APPS,SettingsUtils.JSON_TOTAL
     */
    public static Map<String, Object> getNormalApp(
            Map<String, Object> inforMap, String jsonString, String key_1,
            String key_2) throws JSONException {
        JSONObject jsonObj = new JSONObject(jsonString);
        String key = key_1;
        if (jsonObj.has(key)) {
            JSONArray focusArray = jsonObj.getJSONArray(key);
            inforMap.put(key, getAppList(focusArray, key));
        }

        key = key_2;
        if (jsonObj.has(key)) {
            int total = jsonObj.getInt(key);
            inforMap.put(key, total);
        }

        return inforMap;
    }

    // 焦点图 SettingsUtils.JSON_FOCUS

    public static Map<String, Object> getFocusApp(Map<String, Object> inforMap,
            String jsonString, String key1) throws JSONException {
        JSONObject jsonObj = new JSONObject(jsonString);
        String key = key1;
        if (jsonObj.has(key)) {
            LogOutputUtils.e(TAG, key);
            JSONArray focusArray = jsonObj.getJSONArray(key);
            inforMap.put(key, getAppList(focusArray, key));
        }

        return inforMap;
    }

    // /**
    // *
    // * 作用: 精品推荐里面的装机必备列表 很多个分类,每个分类有很多apps
    // * SettingsUtils.JSON_APPS,SettingsUtils.JSON_CATEGORYS_ID
    // */
    // public static List<Map<String, Object>> getCategoryApp(String jsonString,
    // String key1, String key2) throws JSONException {
    // List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
    // JSONArray cateList = new JSONArray(jsonString);
    // for (int i = 0; i < cateList.length(); i++) {
    // Map<String, Object> inforMap = new HashMap<String, Object>();
    // JSONObject jsonObj = cateList.getJSONObject(i);
    // String key = key1;
    // if (jsonObj.has(key)) {
    // JSONArray focusArray = jsonObj.getJSONArray(key);
    // inforMap.put(key, getAppList(focusArray, key));
    // }
    //
    // key = key2;
    // if (jsonObj.has(key)) {
    // int id = jsonObj.getInt(key);
    // inforMap.put(key, id);
    // }
    //
    // list.add(inforMap);
    // }
    // return list;
    // }

    /**
     * 作用: 用户信息收集页 SettingsUtils.JSON_SEX ,
     * SettingsUtils.JSON_OCCUPATION,SettingsUtils.JSON_INTEREST_LABLE
     */
    public static Map<String, List<UserInfoBean>> getUserInfoBeanMap(
            String jsonString, String key1, String key2, String key3)
            throws JSONException {
        Map<String, List<UserInfoBean>> map = new HashMap<String, List<UserInfoBean>>();
        List<UserInfoBean> list = new ArrayList<UserInfoBean>();
        JSONObject jsonObject = new JSONObject(jsonString);
        String key = key1;
        if (jsonObject.has(key)) {
            list = getUserinfoBeanList(key, jsonObject);
            map.put(key, list);
        }
        key = key2;
        if (jsonObject.has(key)) {
            list = getUserinfoBeanList(key, jsonObject);
            map.put(key, list);
        }
        key = key3;
        if (jsonObject.has(key)) {
            list = getUserinfoBeanList(key, jsonObject);
            map.put(key, list);
        }

        return map;
    }

    private static List<UserInfoBean> getUserinfoBeanList(String key,
            JSONObject jsonObject) {
        JSONArray jsonArray;
        List<UserInfoBean> list = new ArrayList<UserInfoBean>();
        try {
            jsonArray = jsonObject.getJSONArray(key);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject2 = jsonArray.getJSONObject(i);
                UserInfoBean userInfoBean = new UserInfoBean();
                userInfoBean.setId(jsonObject2
                        .getString(SettingsUtils.JSON_USERINFO_ID));
                userInfoBean.setTitle(jsonObject2
                        .getString(SettingsUtils.JSON_USERINFO_TITLE));
                list.add(userInfoBean);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            LogOutputUtils.e(TAG, "userinfo Index json error");
        }

        return list;
    }

    // 第一次进入程序装机推荐
    /**
     * 作用:map<装机推荐or兴趣推荐,应用列表>
     */
    public static Map<String, List<Map<String, Object>>> getUserInfoMap(
            String jsonString) throws JSONException {
        Map<String, List<Map<String, Object>>> map = new HashMap<String, List<Map<String, Object>>>();
        JSONObject jsonObj = new JSONObject(jsonString);

        String necceKey = SettingsUtils.JSON_NECESSARY;
        String intreKey = SettingsUtils.JSON_INTERESTED;

        if (jsonObj.has(necceKey)) {
            JSONObject necJsonObject = jsonObj.getJSONObject(necceKey);
            JSONArray jsonArray = necJsonObject
                    .getJSONArray(SettingsUtils.JSON_CATEGORIES);
            map.put(necceKey, getCategoryApp(jsonArray));
        }

        if (jsonObj.has(intreKey)) {
            JSONObject inteJsonObject = jsonObj.getJSONObject(intreKey);
            JSONArray jsonArray = inteJsonObject
                    .getJSONArray(SettingsUtils.JSON_CATEGORIES);
            map.put(intreKey, getCategoryApp(jsonArray));
        }

        return map;
    }

    // 来划分兴趣推荐和装机推荐
    /**
     * 作用:根据key来获取value,key包括:"apps" ,"分类ID"对应不同的对象列表
     */
    private static List<Map<String, Object>> getCategoryApp(JSONArray jsonArray)
            throws JSONException {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < jsonArray.length(); i++) {
            Map<String, Object> inforMap = new HashMap<String, Object>();
            JSONObject jsonObj = jsonArray.getJSONObject(i);
            String key;
            key = SettingsUtils.JSON_CATEGORYS_ID;
            Long id = jsonObj.getLong(key);
            inforMap.put(key, id);
            key = SettingsUtils.JSON_CATEGORY_TITLE;
            String title = jsonObj.getString(key);
            inforMap.put(key, title);
            key = SettingsUtils.JSON_APPS;
            JSONArray focusArray = jsonObj.getJSONArray(key);
            inforMap.put(key, getCategoryAppLists(focusArray, key, title, id));
            list.add(inforMap);
        }
        return list;
    }

    /**
     * 根据json字符串获取通用App列表map
     * 
     * @param jsonString
     * @return 通用App列表map
     */
    // public static NormalApps getNormalApps(String jsonString) throws
    // JSONException {
    // NormalApps normalApps=new NormalApps();
    // JSONObject jsonObject=new JSONObject(jsonString);
    // return normalApps;
    // }

    /**
     * 根据json数组合key获取相应的程序列表
     * 
     * @param jsonArray
     * @param key
     * @return 通用App列表
     */
    public static List<App> getAppList(JSONArray jsonArray, String key) {

        // TODO 需要修改锋锋网接口数据
        List<App> itemInforList = new ArrayList<App>();
        App app;
        JSONObject jsonObject;
        for (int i = 0; i < jsonArray.length(); i++) {
            app = new App();
            jsonObject = (JSONObject) jsonArray.opt(i);
            app = json2App(jsonObject);
            itemInforList.add(app);
        }
        return itemInforList;
    }

    private static App json2App(JSONObject jsonObject) {
        App app = new App();
        try {
            if (jsonObject.has("id")) {
                app.setId(jsonObject.getInt("id"));
                LogOutputUtils.e(TAG, "id: " + app.getId());
            }
            if (jsonObject.has("title")) {
                app.setTitle(jsonObject.getString("title"));
            }
            if (jsonObject.has("package")) {
                app.setPackageName(jsonObject.getString("package"));
            }
            if (jsonObject.has("category_id")) {
                app.setCategory_id(jsonObject.getLong("category_id"));
            }
            if (jsonObject.has("category_title")) {
                app.setCategory_title(jsonObject.getString("category_title"));
            }
            if (jsonObject.has("version_name")) {
                app.setVerName(jsonObject.getString("version_name"));
            }
            if (jsonObject.has("version_code")) {
                app.setVerCode(jsonObject.getInt("version_code"));
            }
            if (jsonObject.has("size")) {
                app.setSize(jsonObject.getString("size"));
            }
            if (jsonObject.has("summary")) {
                app.setSummary(jsonObject.getString("summary"));
            }
            if (jsonObject.has("image")) {
                app.setImage(jsonObject.getString("image"));
            }
            if (jsonObject.has("imgUrl")) {
                app.setImage(jsonObject.getString("imgUrl"));
            }
            if (jsonObject.has("url")) {
                app.setContentUrl(jsonObject.getString("url"));
            }
            if (jsonObject.has("download")) {
                app.setDownloadUrl(jsonObject.getString("download"));
            }
            if (jsonObject.has("appDetail")) {
                // LogOutput.e(TAG, "appdetail : " +
                // jsonObject.getString("appDetail"));
                app.setApp_detail(jsonObject.getString("appDetail"));
            }
            if (jsonObject.has("gallery")) {
                app.setGallery_url(jsonObject.getString("gallery"));
            }
            if (jsonObject.has("safe")) {

                app.setSafe(jsonObject.getInt("safe"));
                LogOutputUtils.e(TAG, "safe :" + app.getSafe());
            }
            if (jsonObject.has("company")) {
                app.setCompany(jsonObject.getString("company"));
                LogOutputUtils.e(TAG, "company:" + app.getCompany());
            }
            if (jsonObject.has("push_date")) {
                app.setPush_date(jsonObject.getString("push_date"));
                LogOutputUtils.e(TAG, "push_date:" + app.getPush_date());
            }
            if (jsonObject.has("d_count")) {
                app.setDownload_times(jsonObject.getLong("d_count"));
            }
        } catch (JSONException e) {
            LogOutputUtils.i(TAG, "prase  single article error ");
            e.printStackTrace();
        }

        return app;
    }

    /**
     * 作用:只针对装机推荐列表的json
     */
    public static List<App> getCategoryAppLists(JSONArray jsonArray,
            String key, String categoryTitle, long categoryId) {

        // TODO 需要修改锋锋网接口数据
        List<App> itemInforList = new ArrayList<App>();
        App app;
        JSONObject jsonObject;
        for (int i = 0; i < jsonArray.length(); i++) {
            app = new App();
            jsonObject = (JSONObject) jsonArray.opt(i);
            app = json2App(jsonObject);
            itemInforList.add(app);

        }
        return itemInforList;
    }

}
