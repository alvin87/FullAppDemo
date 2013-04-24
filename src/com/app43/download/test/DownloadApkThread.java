package com.app43.download.test;

import cn.com.pcgroup.common.android.config.Env;

import com.alvin.api.utils.LogOutputUtils;
import com.alvin.api.utils.SettingsUtils;
import com.app43.appclient.bean.ApkDownloadTask;
import com.app43.appclient.bean.App;
import com.app43.appclient.module.db.DBApp_download;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

public class DownloadApkThread extends Thread {

    final static String TAG = DownloadApkThread.class.getSimpleName();
    private static DownloadApkThread instance;
    private static ProgressListener progressListener;
    // private static TaskListener taskListener;
    private final static String USER_AGENT = Env.client + " " + Env.versionName;
    private final static String ACCEPT_ENCODING = "gzip, deflate";
    private final static int BUFFER = 8192;
    // 网络超时设置
    private static int CONNECT_TIMEOUT = 20;
    private static int DATA_TIMEOUT = 40;
    private static PowerManager.WakeLock wakeLock;// 保持cpu运转状态
    private static Map<String, ApkDownloadTask> queueMap = new HashMap<String, ApkDownloadTask>();// 根据appName添加下载任务
    private static Map<String, App> nameAppMap = new HashMap<String, App>();// 根据appName把app写入到数据库
    private static ServiceDownload serviceDownloadContext;
    private static boolean canDownload = true;
    private static String currentTaskName = "";// 当前下载的apk
    private static String cancleTaskName;// 点击取消的apk
    // private static HttpDownloadAdapter httpDownloadAdapter;
    private static DownloadNotify httpDownloadAdapter;
    private static DBApp_download dbApp_download = null;
    // private static BroadProgressThread broadProgressThread;
    private static long timer = 0, oldTime = 0;// 用来判断时间
    private static long space = 3000;// 3000毫秒
    // private static List<String> fullyDownload = new ArrayList<String>();//
    // 用来记录下载完成的队列,否则在间隔3秒的时间内可能不显示已经完成的
    private static List<String> queueList = new ArrayList<String>();// 下载队列,用来调度按顺序下载
    // private static boolean finish = false;// 判断是否下载完成,是的话则执行广播
    public static boolean initFinish = false;
    private static Map<String, Long> timeAppMap = new HashMap<String, Long>();// 绑定时间戳来判断是否取消广播

    private DownloadApkThread() {
        LogOutputUtils.e(TAG, "开启下载线程");
        initFinish = false;
        start();

    }

    public static DownloadApkThread getDownloadApkThread(
            ServiceDownload mContext) {

        if (dbApp_download == null) {
            dbApp_download = new DBApp_download(mContext,
                    SettingsUtils.DATABASEVERSION);
        }

        if (serviceDownloadContext == null) {
            serviceDownloadContext = mContext;
        }

        if (instance == null) {
            instance = new DownloadApkThread();
        }
        // if (broadProgressThread == null) {
        // broadProgressThread = BroadProgressThread.getBroadProgressThread(
        // mContext, instance);
        // }
        if (httpDownloadAdapter == null) {
            httpDownloadAdapter = new DownloadNotify() {

                @Override
                public void notify(int progress, Long lCurrentTime) {

                    // writeToDb(nameAppMap.get(currentTaskName), progress);
                    if (progress == 100) {
                        while (timer - oldTime < 3000) {
                            timer = System.currentTimeMillis();
                        }
                        updateDb(currentTaskName, lCurrentTime, progress);
                        ShowNotifycation.finishNotify(currentTaskName);
                        sendBroadcast(currentTaskName, progress, lCurrentTime);
                        return;
                    }
                    timer = System.currentTimeMillis();

                    if (timer - oldTime >= 3000) {
                        updateDb(currentTaskName, lCurrentTime, progress);
                        ShowNotifycation.showProgressNotify(currentTaskName,
                                progress);
                        sendBroadcast(currentTaskName, progress, lCurrentTime);
                        oldTime = timer;
                    }
                    if (progressListener != null) {
                        progressListener.progressChange(currentTaskName,
                                progress);
                    }
                }
            };
        }
        return instance;
    }

    public static void sendBroadcast(String apkName, int iprogress,
            Long lCurrntTime) {
        Intent intent = new Intent();
        intent.setAction(SettingsUtils.PROGRESS_BROADCAST_RECEIVE);
        intent.putExtra(SettingsUtils.APK_NAME, apkName);
        intent.putExtra(SettingsUtils.PROGRESS, iprogress);
        intent.putExtra(SettingsUtils.CURRENT_TIME, lCurrntTime);
        serviceDownloadContext.sendBroadcast(intent);
    }

    private void addQueue(ApkDownloadTask apkDownloadTask, App app,
            Long lCurrentTime) {
        LogOutputUtils.e(TAG, "添加任务" + app.getTitle());
        queueList.add(apkDownloadTask.getApkName());
        queueMap.put(apkDownloadTask.getApkName(), apkDownloadTask);
        nameAppMap.put(app.getTitle(), app);
        timeAppMap.put(app.getTitle(), lCurrentTime);
    }

    private void removeQueue(String apkName) {
        if (queueList.contains(apkName)) {
            queueList.remove(apkName);
        }
        if (queueMap.containsKey(apkName)) {
            queueMap.remove(apkName);
        }
        if (nameAppMap.containsKey(apkName)) {
            nameAppMap.remove(apkName);
        }
        if (timeAppMap.containsKey(apkName)) {
            timeAppMap.remove(apkName);
        }
    }

    /**
     * 作用: 添加下载任务 供下载服务调用
     * 
     * @throws Exception
     */
    public void addDownloadTask(final App app, Long lCurrntTime)
            throws Exception {
        synchronized (instance) {

            ApkDownloadTask apkDownloadTask = new ApkDownloadTask();
            apkDownloadTask.setUrl(app.getDownloadUrl());
            apkDownloadTask.setApkName(app.getTitle());
            apkDownloadTask.setHttpDownloadAdapter(httpDownloadAdapter);
            apkDownloadTask.setAppend(app.isAppend());
            apkDownloadTask.setCurrentTime(app.getDownDate());
            if (apkDownloadTask.getUrl() == null
                    || apkDownloadTask.getUrl().trim().equals("")) {
                throw new Exception("The download url is Null : "
                        + app.getTitle());
            }
            addQueue(apkDownloadTask, app, lCurrntTime);
            LogOutputUtils.e("taskListener", "run");
            // if (taskListener != null) {
            instance.notify();
        }

    }

    @Override
    public void run() {
        super.run();
        while (true) {
            LogOutputUtils.i(TAG, "开始下载");
            LogOutputUtils.i(TAG, "有多少个" + queueList.size());
            while (queueList.size() > 0) {
                LogOutputUtils.i(TAG, "有多少个" + queueList.size() + "开始下载"
                        + queueList.get(0));
                acquireWakeLock(serviceDownloadContext);
                currentTaskName = queueList.get(0);
                ApkDownloadTask task = queueMap.get(currentTaskName);
                if (task.getHttpDownloadAdapter() != null) {
                    File file = new File(SettingsUtils.getRootPath(),
                            task.getApkName() + ".apk");
                    try {
                        boolean isAppend = task.isAppend() == 1 ? true : false;
                        LogOutputUtils.i("下载线程", task.getUrl() + "续传"
                                + isAppend);
                        oldTime = System.currentTimeMillis();
                        download(task.getUrl(), file, isAppend,
                                task.getHttpDownloadAdapter(), currentTaskName,
                                task.getCurrentTime());
                        if (canDownload) {
                            removeQueue(currentTaskName);
                        }

                        releaseWakeLock();
                        if (queueMap.isEmpty()) {
                            serviceDownloadContext.killServiceProcess();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            LogOutputUtils.i(TAG, "已经循环完毕");
            initFinish = true;
            try {
                synchronized (instance) {
                    LogOutputUtils.i("taskListener", "wait()");
                    instance.wait();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 作用: 取消队列中的下载任务包括正在下载的任务 供下载服务调用
     */
    public void removeDownloadTask(String appName) {
        cancleTaskName = appName;

        // 本地线程对取消任务的处理
        // if (queueMap.containsKey(cancleTaskName)) {
        // queueMap.remove(appName);
        // }
        removeQueue(appName);
        LogOutputUtils.e(TAG, "正在下载的" + currentTaskName);
        LogOutputUtils.e(TAG, "下载线程接收到通知取消任务" + appName + "是否正在下载的"
                + currentTaskName.equals(appName));
        if (currentTaskName.equals(appName)) {

            canDownload = false;
        }

        // 广播线程对取消任务的处理
        // if (taskListener != null) {
        // taskListener.removeTask(cancleTaskName);
        // }
    }

    /**
     * 进度变化通知接口 供广播线程调用
     */
    public interface ProgressListener {
        public void progressChange(String appName, int progress);
    }

    public void addProgressListener(ProgressListener listener) {
        if (progressListener == null) {
            progressListener = listener;
        }
    }

    /**
     * 下载任务变化通知接口 供广播线程调用
     */
    public interface TaskListener {
        public void addTask(App app);

        public void removeTask(String cancleAppName);
    }

    // public void addTaskListener(TaskListener listener) {
    // if (taskListener == null) {
    // taskListener = listener;
    // }
    // }

    /**
     * 通知接口
     */

    public interface NotifyListerer {
        public void addNotify();

        public void removeNotify();
    }

    // 设置cpu运转状态
    public static void acquireWakeLock(Context context) {
        // System.out.println("##### OfflineService: acquireWakeLock");
        if (wakeLock != null) {
            return;
        }
        PowerManager powerManager = (PowerManager) (context
                .getSystemService(Context.POWER_SERVICE));
        // 各种锁的类型对CPU 、屏幕、键盘的影响：
        // PARTIAL_WAKE_LOCK:保持CPU 运转，屏幕和键盘灯有可能是关闭的。
        // SCREEN_DIM_WAKE_LOCK：保持CPU 运转，允许保持屏幕显示但有可能是灰的，允许关闭键盘灯
        // SCREEN_BRIGHT_WAKE_LOCK：保持CPU 运转，允许保持屏幕高亮显示，允许关闭键盘灯
        // FULL_WAKE_LOCK：保持CPU 运转，保持屏幕高亮显示，键盘灯也保持亮度
        wakeLock = powerManager
                .newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
        wakeLock.acquire();
    }

    private static void releaseWakeLock() {
        // System.out.println("##### OfflineService: releaseWakeLock");
        if (wakeLock != null && wakeLock.isHeld()) {
            wakeLock.release();
            wakeLock = null;
        }
    }

    /**
     * 下载指定URL的文件
     * 
     * @param urlStr
     *            要下载的文件URL
     * @param dest
     *            下载后保存文件的地址
     * @param append
     *            是否使用断点续传
     * @param adapter
     *            下载监控适配器
     * @throws IOException
     */
    public long download(String urlStr, File dest, boolean append,
            DownloadNotify adapter, String apkString, Long lcurrentTiem)
            throws Exception {
        LogOutputUtils.e(TAG, "Download file : " + " ||append:" + append
                + urlStr + "存到:" + dest.getAbsolutePath());
        canDownload = true;
        long fileSize = -1;
        int progress = 0;
        LogOutputUtils.e(TAG, "file.exist: " + dest.exists());
        // 清除目标文件
        if (!append && dest.exists() && dest.isFile()) {
            dest.delete();
        }

        // 预处理断点续传
        if (append && dest.exists() && dest.exists()) {
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(dest);
                fileSize = fis.available();
            } catch (IOException e) {
                LogOutputUtils.i(TAG,
                        "Get local file size fail: " + dest.getAbsolutePath());
                throw e;
            } finally {
                if (fis != null) {
                    fis.close();
                }
            }
        }

        HttpGet request = new HttpGet(urlStr);
        request.addHeader("User-Agent", USER_AGENT);
        request.addHeader("Accept-Encoding", ACCEPT_ENCODING);
        if (fileSize > 0) {
            request.addHeader("RANGE", "bytes=" + fileSize + "-");
        }
        HttpParams params = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(params,
                CONNECT_TIMEOUT * 1000);
        HttpConnectionParams.setSoTimeout(params, DATA_TIMEOUT * 1000);

        HttpClient httpClient = new DefaultHttpClient(params);

        InputStream is = null;
        FileOutputStream os = null;
        try {
            long time = System.currentTimeMillis();
            HttpResponse response = httpClient.execute(request);
            long time2 = System.currentTimeMillis() - time;
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK
                    || response.getStatusLine().getStatusCode() == HttpStatus.SC_PARTIAL_CONTENT) {
                is = response.getEntity().getContent();
                long remoteFileSize;
                if (response.getStatusLine().getStatusCode() == HttpStatus.SC_PARTIAL_CONTENT) {
                    remoteFileSize = response.getEntity().getContentLength()
                            + fileSize;
                } else {
                    remoteFileSize = response.getEntity().getContentLength();
                }
                Header contentEncoding = response
                        .getFirstHeader("Content-Encoding");
                if (contentEncoding != null
                        && contentEncoding.getValue().equalsIgnoreCase("gzip")) {
                    is = new GZIPInputStream(is);
                }
                os = new FileOutputStream(dest, append);
                byte buffer[] = new byte[BUFFER];
                int readSize = 0;
                while (canDownload && (readSize = is.read(buffer)) > 0) {
                    os.write(buffer, 0, readSize);
                    os.flush();
                    fileSize += readSize;
                    if (adapter != null) {
                        if (remoteFileSize > 0) {
                            // System.out.println("##### fileSize = " + fileSize
                            // + ", remoteSize = "
                            // + remoteFileSize);
                            int tempProgress = (int) (fileSize * 100 / remoteFileSize);
                            if (tempProgress > progress) {
                                progress = tempProgress;
                                adapter.notify(progress, lcurrentTiem);
                            }
                        } else {
                            if (progress == 0) {
                                progress = -1;
                                adapter.notify(progress, lcurrentTiem);
                            }
                        }
                    }

                }
                if (canDownload) {
                    if (adapter != null && progress < 100) {
                        adapter.notify(100, lcurrentTiem);
                    }
                } else {

                    // if (adapter != null && progress < 100) {
                    // adapter.notify(Settings.CANCLE_DOWNLOAD,lcurrentTiem);
                    // }
                }

            }
            if (fileSize < 0) {
                fileSize = 0;
            }
            long time3 = System.currentTimeMillis() - time;
            LogOutputUtils.i(TAG, "Download time = " + time2 / 1000.0 + "/"
                    + time3 / 1000.0 + "\tsize=" + Math.round(fileSize / 1.024)
                    / 1000 + "k\turl=" + urlStr);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (canDownload) {
                try {
                    cancalStream(is, os, httpClient, "下载完成");
                } catch (IOException e) {
                    LogOutputUtils.i(TAG, "正常下载关闭流异常");
                    e.printStackTrace();
                }
            } else {
                new Thread(new CloseStream(is, os, httpClient)).start();
            }

        }

        if (!canDownload) {
            LogOutputUtils.i(TAG, "Download file stop: " + urlStr);
            // throw new Exception("Download file stop: " + urlStr);
        }

        if (fileSize < 0) {
            throw new Exception("Download file fail: " + urlStr);
        }

        return fileSize;
    }

    // 写进度到数据库
    // private static void writeToDb(App app, int progress) {
    // dbApp_download.insertProgress(app, progress);
    // }

    public interface DownloadNotify {
        public void notify(int progress, Long lCurrentTime);
    }

    public class CloseStream implements Runnable {

        InputStream is;
        FileOutputStream os;
        HttpClient httpClient;

        public CloseStream(InputStream is, FileOutputStream os,
                HttpClient httpClient) {
            this.is = is;
            this.os = os;
            this.httpClient = httpClient;
        }

        @Override
        public void run() {
            try {
                cancalStream(is, os, httpClient, "点击取消");
            } catch (IOException e) {
                LogOutputUtils.i(TAG, "取消关闭流异常");
                e.printStackTrace();
            }
        }

    }

    private void cancalStream(InputStream is, FileOutputStream os,
            HttpClient httpClient, String where) throws IOException {
        LogOutputUtils.i(TAG, where + "关闭流");
        long startTime = System.currentTimeMillis();
        if (os != null) {
            os.close();
        }
        Long time5 = System.currentTimeMillis();
        LogOutputUtils.i(TAG, "关闭os流耗时" + (time5 - startTime));
        if (is != null) {
            is.close();
        }
        Long time6 = System.currentTimeMillis();
        LogOutputUtils.i(TAG, "关闭is流耗时" + (time6 - time5));
        httpClient.getConnectionManager().shutdown();
        LogOutputUtils.i(TAG, "关闭http连接耗时"
                + (System.currentTimeMillis() - time6));
    }

    private static void updateDb(String sApkName, long time, int iProgress) {
        if ((!timeAppMap.containsKey(sApkName))
                || (timeAppMap.get(sApkName) - time != 0)) {
            // 如果下载adapter没有此apk名字或者时间戳不匹配则只负责显示

            return;
        }

        LogOutputUtils.e(
                TAG,
                iProgress
                        + "progress"
                        + "更新"
                        + dbApp_download.updateAppProgress(sApkName, time,
                                iProgress));
    }

}
