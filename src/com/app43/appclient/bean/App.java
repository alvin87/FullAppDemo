package com.app43.appclient.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 列表item josn所需的bean
 * 
 * 项目名称：app43 类名称：App 类描述： 创建人：pc 创建时间：2011-11-11 上午9:52:29 修改人：pc
 * 修改时间：2011-11-11 上午9:52:29 修改备注：
 * 
 * @version
 * 
 */
public class App implements Parcelable {
    /**
     * 切记bean里存储小对象
     */

    private int id; // app的id
    private String image; // 应用图标URL
    private String title; // 程序名
    private String packageName; // 包名
    private String summary; // 程序简介
    private String category_title; // 分类名称
    private Long category_id; // 分类ID
    private String size; // 程序大小
    private int verCode; // 版本代码
    private String verName; // 版本名
    private String contentUrl; // 焦点图终端页image
    private String downloadUrl; // 下载路径

    private String gallery_url;// gallery图片数组 暂时不用写进数据库
    private long download_times;// 下载次数 暂时不用写进数据库
    private String app_detail;// 程序内容简介 暂时不用写进数据库
    private String push_date;// 出版日期 暂时不用写进数据库
    private String company;// 公司名称 暂时不用写进数据库
    private int safe;// 是否安全认证,1:已认证.0:否

    // -------------------以上是从服务器读取的json

    private int progress;// 进度,供DBApp_download使用
    private int state;// 是否安装.1:安装,0:未安装.供DBApp_download使用

    private long downDate; // 下载日期 可以用来比较时间戳来通知显示
    private int isSelected; // 是否选中,对装机推荐界面和收藏夹有效
    private int append;// 是否续传

    // private String sysVersion; // 系统版本 暂时不用
    // private int score; // 评分等级 //暂时不用
    // private String fileName; // 文件名 暂时不用
    // private String referrence; // 推荐人 暂时不用

    public App(Parcel in) {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getApp_detail() {
        return app_detail;
    }

    public void setApp_detail(String app_detail) {
        this.app_detail = app_detail;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getCategory_title() {
        return category_title;
    }

    public void setCategory_title(String category_title) {
        this.category_title = category_title;
    }

    public Long getCategory_id() {
        return category_id;
    }

    public void setCategory_id(Long category_id) {
        this.category_id = category_id;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public int getVerCode() {
        return verCode;
    }

    public void setVerCode(int verCode) {
        this.verCode = verCode;
    }

    public String getVerName() {
        return verName;
    }

    public void setVerName(String verName) {
        this.verName = verName;
    }

    //
    // public String getReferrence() {
    // return referrence;
    // }
    //
    // public void setReferrence(String referrence) {
    // this.referrence = referrence;
    // }

    public int getSafe() {
        return safe;
    }

    public void setSafe(int safe) {
        this.safe = safe;
    }

    public String getPush_date() {
        return push_date;
    }

    public void setPush_date(String push_date) {
        this.push_date = push_date;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public long getDownload_times() {
        return download_times;
    }

    public void setDownload_times(long download_times) {
        this.download_times = download_times;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getContentUrl() {
        return contentUrl;
    }

    public void setContentUrl(String contentUrl) {
        this.contentUrl = contentUrl;
    }

    // public int getScore() {
    // return score;
    // }
    //
    // public void setScore(int score) {
    // this.score = score;
    // }

    // public int getDownloadTimes() {
    // return downloadTimes;
    // }
    //
    // public void setDownloadTimes(int downloadTimes) {
    // this.downloadTimes = downloadTimes;
    // }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    // public String getSysVersion() {
    // return sysVersion;
    // }
    //
    // public void setSysVersion(String sysVersion) {
    // this.sysVersion = sysVersion;
    // }

    // public String getReleaseDate() {
    // return releaseDate;
    // }
    //
    // public void setReleaseDate(String releaseDate) {
    // this.releaseDate = releaseDate;
    // }

    public long getDownDate() {
        return downDate;
    }

    public void setDownDate(long downDate) {
        this.downDate = downDate;
    }

    // public String getFileName() {
    // return fileName;
    // }
    //
    // public void setFileName(String fileName) {
    // this.fileName = fileName;
    // }

    public int isSelected() {
        return isSelected;
    }

    public void setSelected(int isSelected) {
        this.isSelected = isSelected;
    }

    public int isAppend() {
        return append;
    }

    public String getGallery_url() {
        return gallery_url;
    }

    public void setGallery_url(String gallery_url) {
        this.gallery_url = gallery_url;
    }

    public void setAppend(int isAppend) {
        this.append = isAppend;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(contentUrl);
        dest.writeString(downloadUrl);
        dest.writeString(category_title);
        dest.writeLong(category_id);
        dest.writeLong(downDate);
        // dest.writeInt(downloadTimes);
        // dest.writeString(fileName);
        dest.writeString(image);
        dest.writeString(summary);
        dest.writeString(packageName);
        // dest.writeString(referrence);
        // dest.writeString(releaseDate);
        // dest.writeInt(score);
        dest.writeString(size);
        dest.writeInt(verCode);
        dest.writeString(verName);
        // dest.writeString(sysVersion);
        dest.writeInt(append);
        dest.writeString(gallery_url);
        dest.writeInt(progress);
        dest.writeInt(state);
        dest.writeInt(isSelected);
        dest.writeLong(download_times);
        dest.writeString(app_detail);
        dest.writeString(company);
        dest.writeString(push_date);
        dest.writeInt(safe);
    }

    public static final Parcelable.Creator<App> CREATOR = new Parcelable.Creator<App>() {

        @Override
        public App createFromParcel(Parcel source) {
            // 在组件之间传递对象的时候要序列化相应的字段
            App app = new App();
            app.id = source.readInt();
            app.title = source.readString();
            app.contentUrl = source.readString();
            app.downloadUrl = source.readString();
            app.category_title = source.readString();
            app.category_id = source.readLong();
            app.downDate = source.readLong();
            // app.downloadTimes = source.readInt();
            // app.fileName = source.readString();
            app.image = source.readString();
            app.summary = source.readString();
            app.packageName = source.readString();
            // app.referrence = source.readString();
            // app.releaseDate = source.readString();
            // app.score = source.readInt();
            app.size = source.readString();
            app.verCode = source.readInt();
            app.verName = source.readString();
            // app.sysVersion = source.readString();
            app.append = source.readInt();
            app.gallery_url = source.readString();
            app.progress = source.readInt();
            app.state = source.readInt();
            app.isSelected = source.readInt();
            app.download_times = source.readLong();
            app.app_detail = source.readString();
            app.company = source.readString();
            app.push_date = source.readString();
            app.safe = source.readInt();
            return app;
        }

        @Override
        public App[] newArray(int size) {
            return new App[size];
        }
    };

    public App() {

    }

}
