package com.example.dell.nscarlauncher.base.model;

import java.util.List;

/**
 * 分页的基类
 * Created by lenovo on 2017/9/8.
 */

public class PagingBaseModel<T extends BaseModel> extends BaseModel {
    /**
     * 当前页数据
     */
    private List<T> data;

    /**
     * 当前页
     */
    private int currentPage;
    /**
     * 总记录数
     */
    private int total;
    /**
     * 总页数
     */
    private int pages;
    /*
    * 时间戳*/
    private long timestamp;
    /**
     * 设置分页数据
     *
     * @param currentPage
     * @param isLast
     */
    public void setPagingInfo(int currentPage, boolean isLast) {
        setCurrentPage(currentPage);
        if (isLast) {
            //达到最后一页
            setPages(currentPage);
        } else {
            setPages(currentPage + 1);
        }
    }

    /**
     * 设置分页数据
     *
     * @param currentPage
     * @param data
     */
    public void setPagingInfo(int currentPage, List data,long timestamp) {
        setCurrentPage(currentPage);
        setTimestamp(timestamp);
        int size = data == null ? 0 : data.size();
        if (size == 0) {
            //达到最后一页
            setPages(currentPage);
        } else {
            setPages(currentPage + 1);
        }
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * 是否为最后一页
     *
     * @return
     */
    public boolean isLastPage() {
        return getCurrentPage() >= getPages();
    }

    /**
     * 加载更多
     *
     * @param listener
     */
    public void loadMore(PagingListener listener) {
        if (listener != null) {
            if (isLastPage()) {
                listener.lastPage();
            } else {
                listener.loadMore(getNext());
            }
        }
    }

    /**
     * 获取下一页
     *
     * @return
     */
    public int getNext() {
        return getCurrentPage() + 1;
    }

    /**
     * 分页监听
     */
    public interface PagingListener {
        /**
         * 最后一页
         */
        void lastPage();

        /**
         * 加载更多
         *
         * @param nextPage
         */
        void loadMore(int nextPage);
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }
}
