package com.example.dell.nscarlauncher.base.model;

import android.text.TextUtils;

/**
 * 分页请求参数
 * Created by lenovo on 2017/9/8.
 */

public class PagingParam extends ParamBaseModel {
    /**
     * 当前页
     */
    private Integer pageNo;
    /**
     * 当前页
     */
    private Integer currentPage;
    /**
     * 经度
     */
    private String longitude;
    /**
     * 纬度
     */
    private String latitude;
    /*
    * 时间戳
    * */
    private long timestamp;

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * 经纬度是否为空
     *
     * @return
     */
    public boolean isLngOrLatNull() {
        return TextUtils.isEmpty(longitude) || TextUtils.isEmpty(latitude);
    }

    public int getCurrentPage() {
        return pageNo;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
        this.pageNo = currentPage;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }
}
