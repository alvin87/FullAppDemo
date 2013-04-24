package com.alvin.api.utils;

import cn.com.pcgroup.common.android.utils.NetworkUtils;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 主要用于Moss统计 项目名称：app43 类名称：UMengAnalyse 类描述： 创建人：APP43 创建时间：2012-2-20
 * 上午10:23:25 修改人：APP43 修改时间：2012-2-20 上午10:23:25 修改备注：
 * 
 * @version
 * 
 */
public class MossAnalyseUtils {

    private static String TAG = MossAnalyseUtils.class.getSimpleName();
    private static String DEV_ID = "DEV-ID";
    private static String CLIENT_TIME = "clienttime";
    private static String APP = "app";
    private static String JSON = "json";
    private static String EVENT = "event";
    private static String VERSION = "version";
    private static String OSVERSION = "osversion";
    private static String CREATETIME = "createtime";
    private static String DURATION = "duration";
    private static String DEV_NAME = "DEV-NAME";
    private static String DEV_TYPE = "DEV-Type";
    private static String DEV_MODEL = "DEV-Model";
    private static String OS_VERSION = "OS-version";
    private static String RESOLUTION = "resolution";
    private static String ANDROID_PHONE = "ANDROID PHONE";
    private static String NET = "net";
    private static String LAB = "lab";
    private static String UE = "ue";
    private static String IP = "http://moss.pconline.cn/receiver?";
    // private static String IP = "http://192.168.74.33:8008/receiver?";
    private static String APP_ID = "122";// moss后台中锋锋网Android版的ID
    public static int EVENT_USER = 13;// 事件ID,用户自定义事件
    public static int EVENT_OPEN = 5;// 事件ID,打开应用事件
    public static int EVENT_CLOSE = 12;// 事件ID,结束应用事件
    public static int EVENT_DEVINFO = 8;// 事件ID,第一次打开app发送
    private static String DURATION_TIME = "0";

    public static void onEvents(final int eventid, final String ueString,
            final String labString) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String jsonString = "";
                long time = System.currentTimeMillis();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
                        "yyyy-MM-dd HH:mm:ss");
                String currentTime = simpleDateFormat.format(time);
                LogOutputUtils.e(TAG, "event:" + eventid);
                switch (eventid) {
                case 13:// 用户自定义标签
                    jsonString = "[{\"" + EVENT + "\":" + eventid + ",\""
                            + VERSION + "\":\""
                            + PhoneInfoUtils.getAppVersion() + "\",\""
                            + CREATETIME + "\":\"" + currentTime + "\",\""
                            + DURATION + "\":" + DURATION_TIME + ",\"" + LAB
                            + "\":\"" + labString + "\",\"" + UE + "\":\""
                            + ueString + "\"}]";
                    break;

                case 5:
                    String netState = "";
                    switch (NetworkUtils.getNetworkState(SettingsUtils.context)) {
                    case 0:
                        netState = "unknown";
                        break;
                    case 1:
                        netState = "Wi-Fi";
                        break;
                    case 2:
                        netState = "2G/3G";
                        break;

                    default:
                        break;
                    }
                    jsonString = "[{\"" + EVENT + "\":" + eventid + ",\""
                            + VERSION + "\":\""
                            + PhoneInfoUtils.getAppVersion() + "\",\""
                            + CREATETIME + "\":\"" + currentTime + "\",\""
                            + NET + "\":\"" + netState + "\",\"" + OSVERSION
                            + "\":\"" + PhoneInfoUtils.getOs() + "\"}]";
                    break;
                case 12:
                    jsonString = "[{\"" + EVENT + "\":" + eventid + ",\""
                            + VERSION + "\":\""
                            + PhoneInfoUtils.getAppVersion() + "\",\""
                            + CREATETIME + "\":\"" + currentTime + "\",\""
                            + DURATION + "\":" + DURATION_TIME + "}]";
                    break;

                case 8:
                    jsonString = "[{\"" + EVENT + "\":" + eventid + ",\""
                            + CREATETIME + "\":\"" + currentTime + "\",\""
                            + DEV_NAME + "\":\"" + PhoneInfoUtils.getEmail()
                            + "\",\"" + DEV_TYPE + "\":\"" + ANDROID_PHONE
                            + "\",\"" + DEV_MODEL + "\":\""
                            + PhoneInfoUtils.getPhoneVersion() + "\",\""
                            + OS_VERSION + "\":\"" + PhoneInfoUtils.getOs()
                            + "\",\"" + RESOLUTION + "\":\""
                            + PhoneInfoUtils.getResolution() + "\"" + "}]";
                    break;
                default:
                    break;
                }
                LogOutputUtils.e(TAG, "jsonString:" + jsonString);
                sendData(eventid, jsonString);
            }
        }).start();
    }

    private static void sendData(int event, String jsonString) {
        String url = IP;
        long time = System.currentTimeMillis();
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair(DEV_ID, PhoneInfoUtils.getImei()));
        params.add(new BasicNameValuePair(CLIENT_TIME, String.valueOf(time)));
        if (event != EVENT_DEVINFO) {
            params.add(new BasicNameValuePair(APP, APP_ID));
        }
        params.add(new BasicNameValuePair(JSON, jsonString));
        boolean success = false;
        success = AHttpUtils.postUrl(url, params);
        LogOutputUtils.e(TAG, "evnet:" + event + " ,success:" + success);
        if (!success) {
            LogOutputUtils.e(TAG, "evnet:" + event + " send wrong!");
        }
    }

}
