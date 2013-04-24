package com.app43.appclient.module.db;

/**
 * 用户收藏列表数据表
 */
import com.alvin.api.utils.SettingsUtils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public class DBFavourite extends DbHandle {
    public final static String sql = "CREATE TABLE IF NOT EXISTS "
            + SettingsUtils.FAVOURITE_APP + " ("
            + " id INTEGER primary key autoincrement, "
            + " app_id INTEGER not null , " // appId
            + " date NUMERIC " // 时间
            + ");";

    public DBFavourite(Context context, int version) {
        super(context, version);
    }

    /**
     * 插入数据
     * 
     * @param tableName
     * @param appid
     * @return true 则插入成功
     */
    public boolean insert(int appid) {
        boolean flag = true;

        ContentValues initialValues = new ContentValues();
        initialValues.put("app_id", appid);
        initialValues.put("date", System.currentTimeMillis());
        db.beginTransaction();
        flag = db.insert(SettingsUtils.FAVOURITE_APP, null, initialValues) > 0;// 若initialvalues为空时则插入null

        db.setTransactionSuccessful();
        db.endTransaction();
        return flag;
    }

    /**
     * 删除数据
     * 
     * @param appid
     * @return true 则删除成功
     */
    public boolean delete(int appid) {
        boolean flag = true;
        db.beginTransaction();
        flag = db.delete(SettingsUtils.FAVOURITE_APP, "app_id =" + appid, null) > 0;
        db.setTransactionSuccessful();
        db.endTransaction();
        return flag;
    }

    /**
     * 查询所有收藏夹
     * 
     * @return
     */
    public Cursor query() {
        boolean flag = true;
        db.beginTransaction();
        Cursor cursor = db.rawQuery("select distinct  *  from "
                + SettingsUtils.FAVOURITE_APP + " order by date desc", null);

        db.setTransactionSuccessful();
        db.endTransaction();
        return cursor;

    }

    /**
     * 获取收藏夹appsID
     * 
     * @param userString
     * @return
     */
    public int[] getAppID() {
        Cursor mcursor = query();
        int[] Apps = new int[mcursor.getCount()];
        mcursor.moveToFirst();
        for (int i = 0; i < Apps.length; i++) {
            Apps[i] = mcursor.getInt((mcursor.getColumnIndex("app_id")));
            mcursor.moveToNext();
            // System.out.println(i+"app_id"+Apps[i]);
        }
        if (mcursor != null) {
            mcursor.close();
        }
        return Apps;
    }

}
