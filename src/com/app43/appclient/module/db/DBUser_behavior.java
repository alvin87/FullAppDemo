package com.app43.appclient.module.db;

/**
 * 猜你喜欢用户行为分析表
 */
import com.alvin.api.utils.LogOutputUtils;
import com.alvin.api.utils.SettingsUtils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.HashMap;
import java.util.Map;

public class DBUser_behavior extends DbHandle {
    String TAG = DBUser_behavior.class.getSimpleName();
    public final static String sql = "CREATE TABLE IF NOT EXISTS "
            + SettingsUtils.USER_BEHAVIOR + " ("
            + " id INTEGER primary key autoincrement, "
            + " category_id INTEGER, " // 分类ID
            + " score INTEGER" // 分类积分
            + ");";

    public DBUser_behavior(Context context, int version) {
        super(context, version);
    }

    /**
     * 插入数据
     * 
     * @param tableName
     * @param category_id
     * @return true 则插入成功
     */
    public boolean inserts(long category_id, long score) {
        boolean flag = false;
        Map<Long, Long> list = getAllBehavior();
        ContentValues initialValues = new ContentValues();
        initialValues.put("category_id", category_id);
        initialValues.put("score", score);
        beginTrans();
        LogOutputUtils.i(TAG, "更新behavior" + list.get(category_id));// 分类ID
        if (!list.containsKey(category_id)) {
            flag = db.insert(SettingsUtils.USER_BEHAVIOR, null, initialValues) > 0;
        } else {
            flag = db.update(SettingsUtils.USER_BEHAVIOR, initialValues,
                    " category_id =" + category_id, null) > 0;// 若initialvalues为空时则插入null
        }
        endTrans();
        return flag;
    }

    /**
     * 删除数据 暂时没用到
     * 
     * @param appid
     * @return true 则删除成功
     */
    public boolean delete(int appid) {
        boolean flag = true;
        beginTrans();
        flag = db.delete(SettingsUtils.USER_BEHAVIOR, "app_id =" + appid, null) > 0;
        endTrans();
        return flag;
    }

    /**
     * 查询积分高于10的appid
     * 
     * @return
     */
    public Cursor topQuery() {
        beginTrans();
        Cursor cursor = db.rawQuery("select distinct  *  from "
                + SettingsUtils.USER_BEHAVIOR
                + " where score>=10 order by score desc", null);
        endTrans();
        return cursor;
    }

    // 查询所有
    public Cursor query() {
        beginTrans();
        Cursor cursor = db.rawQuery("select distinct  *  from "
                + SettingsUtils.USER_BEHAVIOR + " order by score desc", null);
        endTrans();
        return cursor;
    }

    /**
     * 获取收藏夹appsID
     * 
     * @param userString
     * @return
     */
    public Map<Long, Long> getThreeBehavior() {
        Cursor mcursor = topQuery();

        Map<Long, Long> Apps = new HashMap<Long, Long>();
        mcursor.moveToFirst();
        for (int i = 0; i < mcursor.getCount(); i++) {
            Apps.put(mcursor.getLong(mcursor.getColumnIndex("category_id")),
                    mcursor.getLong(mcursor.getColumnIndex("category_id")));
            LogOutputUtils.e(
                    "getBehaviorAppID",
                    i + "category_id" + mcursor.getColumnIndex("category_id")
                            + " score"
                            + mcursor.getInt(mcursor.getColumnIndex("score")));
            if (i > 2) {
                break;
            }
            mcursor.moveToNext();
        }
        if (mcursor != null) {
            mcursor.close();
        }
        return Apps;
    }

    public Map<Long, Long> getAllBehavior() {
        Cursor mcursor = query();
        Map<Long, Long> Apps = new HashMap<Long, Long>();
        mcursor.moveToFirst();
        for (int i = 0; i < mcursor.getCount(); i++) {
            Apps.put(mcursor.getLong(mcursor.getColumnIndex("category_id")),
                    mcursor.getLong(mcursor.getColumnIndex("score")));
            // LogOutput.e(
            // "getBehaviorAppID",
            // i
            // + "category_id"
            // + mcursor.getLong(mcursor
            // .getColumnIndex("category_id")) + " score"
            // + mcursor.getInt(mcursor.getColumnIndex("score")));
            mcursor.moveToNext();
        }
        if (mcursor != null) {
            mcursor.close();
        }
        return Apps;
    }
}
