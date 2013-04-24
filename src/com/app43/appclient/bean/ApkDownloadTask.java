package com.app43.appclient.bean;

import com.app43.download.test.DownloadApkThread.DownloadNotify;

import cn.com.pcgroup.common.android.utils.CacheUtils;
import cn.com.pcgroup.common.android.utils.HttpUtils.HttpDownloadAdapter;

/**
 * apk下载bean
 * 
 * 项目名称：app43 类名称：ApkDownloadTask 类描述： 创建人：pc 创建时间：2011-11-11 上午9:51:56 修改人：pc
 * 修改时间：2011-11-11 上午9:51:56 修改备注：
 * 
 * @version
 * 
 */
public class ApkDownloadTask {

    private String url;
    private String apkName;

    // private HttpDownloadAdapter httpDownloadAdapter;
    private int cacheType = CacheUtils.CACHE_EXTERNAL;
    private int isAppend;// 是否续传
    private DownloadNotify httpDownloadAdapter;
    private Long currentTime;

    public Long getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(Long currentTime) {
        this.currentTime = currentTime;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getApkName() {
        return apkName;
    }

    public void setApkName(String apkName) {
        this.apkName = apkName;
    }

    // public HttpDownloadAdapter getHttpDownloadAdapter() {
    // return httpDownloadAdapter;
    // }
    // public void setHttpDownloadAdapter(HttpDownloadAdapter
    // httpDownloadAdapter) {
    // this.httpDownloadAdapter = httpDownloadAdapter;
    // }

    public int getCacheType() {
        return cacheType;
    }

    public DownloadNotify getHttpDownloadAdapter() {
        return httpDownloadAdapter;
    }

    public void setHttpDownloadAdapter(DownloadNotify httpDownloadAdapter) {
        this.httpDownloadAdapter = httpDownloadAdapter;
    }

    public void setCacheType(int cacheType) {
        this.cacheType = cacheType;
    }

    public int isAppend() {
        return isAppend;
    }

    public void setAppend(int isAppend) {
        this.isAppend = isAppend;
    }

}
