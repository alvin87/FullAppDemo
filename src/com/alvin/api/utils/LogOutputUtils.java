package com.alvin.api.utils;

import android.util.Log;

/**
 * 主要控制logcat 输出 项目名称：app43 类名称：CommonContrl 类描述： 创建人：APP43 创建时间：2012-2-20
 * 上午9:18:12 修改人：APP43 修改时间：2012-2-20 上午9:18:12 修改备注：
 * 
 * @version
 * 
 */
public class LogOutputUtils {

    public static boolean isDebug = true;

    public static void e(String tag, String msg) {
        if (isDebug) {
            Log.e(tag, msg);
        }
    }

    public static void i(String tag, String msg) {
        if (isDebug) {
            Log.i(tag, msg);
        }
    }

    public static void v(String tag, String msg) {
        if (isDebug) {
            Log.v(tag, msg);
        }
    }

    public static void d(String tag, String msg) {
        if (isDebug) {
            Log.d(tag, msg);
        }
    }

}
