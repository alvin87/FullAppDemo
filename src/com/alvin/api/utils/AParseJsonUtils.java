package com.alvin.api.utils;

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

/**
 * Json解析器公有类
 * 
 * 项目名称：app43 类名称：AParseJsonUtils 类描述： 创建人：pc 创建时间：2012-3-19 下午5:04:32 修改人：pc
 * 修改时间：2012-3-19 下午5:04:32 修改备注：
 * 
 * @version
 * 
 */
public class AParseJsonUtils {
    private final static String TAG = AParseJsonUtils.class.getSimpleName();
    private static URL url;

    /**
     * 通过url获得输入流
     * 
     * @param strUrl
     * @return
     */
    public static InputStream getInputStreamByUrl(String strUrl) {
        try {
            url = new URL(strUrl);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                return connection.getInputStream();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 通过url获得字符串
     */
    // public static String getStrByUrls(String urlStr, int expire, boolean
    // refresh) {
    // try {
    // // bufferReader=new BufferedReader(new
    // // InputStreamReader(getInputStreamByUrl(urlStr),"UTF-8"));
    // InputStream inputStream = HttpUtils.invokeWithCache(urlStr,
    // CacheUtils.CACHE_INTERNAL, expire, refresh)
    // .getInputStream();
    // if (inputStream != null) {
    // BufferedReader bufferReader = new BufferedReader(
    // new InputStreamReader(inputStream));
    // StringBuffer sb = new StringBuffer();
    // String lineStr;
    // while ((lineStr = bufferReader.readLine()) != null) {
    // sb.append(lineStr + "\r\n");
    // }
    // return sb.toString();
    // }
    // } catch (Exception e) {
    // e.printStackTrace();
    // }
    // return null;
    // }

    /**
     * 通过json文件获得字符串
     * 
     * @return
     */
    // public static String getJsonStrByFile(File jsonFile) {
    // try {
    // if (null != jsonFile && jsonFile.exists()) {
    // BufferedReader bufferReader = new BufferedReader(
    // new InputStreamReader(new FileInputStream(jsonFile)));
    // StringBuffer sb = new StringBuffer();
    // String lineStr;
    // while ((lineStr = bufferReader.readLine()) != null) {
    // sb.append(lineStr + "\r\n");
    // }
    // return sb.toString();
    // }
    // } catch (Exception e) {
    // e.printStackTrace();
    // }
    // return null;
    // }

    /**
     * 通过json文件获得字符串
     * 
     */
    public static String getJsonStrByFile(FileInputStream inputStream) {
        try {
            if (null != inputStream) {
                BufferedReader bufferReader = new BufferedReader(
                        new InputStreamReader(inputStream));
                StringBuffer sb = new StringBuffer();
                String lineStr;
                while ((lineStr = bufferReader.readLine()) != null) {
                    sb.append(lineStr + "\r\n");
                }
                return sb.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据json字符串获取文章列表 针对纯数组的json
     * 
     * @param jsonString
     * @return
     * @throws JSONException
     */
    // public static Map<String, List<App>> getItemInforMap(String jsonString)
    // throws JSONException {
    // Map<String, List<App>> inforMap = new HashMap<String, List<App>>();
    // JSONObject jsonObj = new JSONObject(jsonString);
    // Iterator<String> keyIter = jsonObj.keys();
    // while (keyIter.hasNext()) {
    // String key = keyIter.next();
    // JSONArray focusArray = jsonObj.getJSONArray(key);
    // inforMap.put(key, getAppList(focusArray, key));
    // }
    // return inforMap;
    // }
    //

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
     * 根据字符串获取jsonObject
     * 
     * @param jsonObject
     * @return
     * @throws JSONException
     */
    public static Map<String, JSONObject> getJsonObject(String jsonString)
            throws JSONException {
        Map<String, JSONObject> object = new HashMap<String, JSONObject>();
        JSONObject jsonObj = new JSONObject(jsonString);
        Iterator<String> keyIter = jsonObj.keys();
        while (keyIter.hasNext()) {
            String key = keyIter.next();
            JSONObject jsonObject = jsonObj.getJSONObject(key);
            object.put(key, jsonObject);
        }
        return object;
    }

}
