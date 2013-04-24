package com.alvin.api.utils;

import cn.com.pcgroup.common.android.config.Env;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;

import java.util.List;

public class AHttpUtils {

    private final static String USER_AGENT = Env.client + " " + Env.versionName;
    private final static String ACCEPT_ENCODING = "gzip, deflate";
    // 网络超时设置
    public static int CONNECT_TIMEOUT = 20;
    public static int DATA_TIMEOUT = 40;

    /**
     * 作用:post方法传输
     */
    public static boolean postUrl(String urlStr, List<NameValuePair> params) {
        boolean success = false;
        HttpPost request;
        HttpResponse response;
        HttpClient httpClient = null;
        try {

            HttpParams config = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(config,
                    CONNECT_TIMEOUT * 1000);
            HttpConnectionParams.setSoTimeout(config, DATA_TIMEOUT * 1000);
            httpClient = new DefaultHttpClient(config);
            request = new HttpPost(urlStr);
            if (params != null && params.size() > 0) {
                request.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
            }

            // 设置头文件
            request.addHeader("User-Agent", USER_AGENT);
            request.addHeader("Accept-Encoding", ACCEPT_ENCODING);
            response = httpClient.execute(request);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                success = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (httpClient != null) {
                httpClient.getConnectionManager().shutdown();
            }
        }

        return success;
    }

}
