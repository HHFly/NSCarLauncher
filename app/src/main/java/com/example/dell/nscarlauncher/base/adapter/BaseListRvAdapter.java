package com.example.dell.nscarlauncher.base.adapter;

import android.support.annotation.LayoutRes;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * RecyclerView列表适配器
 * Created by lenovo on 2017/9/23.
 */

public abstract class BaseListRvAdapter<T> extends BaseRvAdapter {
    /**
     * 数据源
     */
    private List<T> mData;


    public BaseListRvAdapter(List<T> data) {
        resetData();
        if (data != null) {
            getData().addAll(data);
        }
    }

    @Override
    public AutoViewHolder customOnCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater(parent, getItemResId());
        return new AutoViewHolder(view);
    }

    @Override
    public void customBindView(AutoViewHolder holder, int position) {
        T bodyData = getDataItem(position);
        if (bodyData != null) {
            bindBodyData(holder, position, bodyData);
        }
    }

    /**
     * 获取Item资源文件
     *
     * @return
     */
    public abstract
    @LayoutRes
    int getItemResId();

    /**
     * 绑定列表数据
     *
     * @param holder
     * @param bodyPos
     * @param data
     */
    public abstract void bindBodyData(AutoViewHolder holder, int bodyPos, T data);

    @Override
    public void customBindLocalRefresh(AutoViewHolder holder, int position, List payloads) {
        T bodyData = getDataItem(position);
        if (bodyData != null) {
            bindBodyDataLocalRefresh(holder, position, bodyData);
        }
    }

    /**
     * 局部刷新
     *
     * @param holder
     * @param bodyPos
     * @param data
     */
    public void bindBodyDataLocalRefresh(AutoViewHolder holder, int bodyPos, T data) {

    }

    /**
     * 刷新数据
     *
     * @param data
     */
    public void notifyData(List<T> data, boolean isRefresh) {
        if (isRefresh) {
            refresh(data);
        } else {
            loadMore(data);
        }
    }

    /**
     * 刷新数据
     *
     * @param data
     */
    public void refresh(List<T> data) {
        resetData();
        if (data != null) {
            getData().addAll(data);
        }
        notifyDataSetChanged();
    }

    /**
     * 加载更多
     *
     * @param data
     */
    public void loadMore(List<T> data) {
        if (getData() == null) {
            setData(new ArrayList<T>());
        }
        if (data != null) {
            getData().addAll(data);
        }
        notifyDataSetChanged();
    }

    /**
     * 重置数据源
     */
    private void resetData() {
        if (getData() == null) {
            List<T> l = new ArrayList<>();
            setData(l);
        } else {
            getData().clear();
        }
    }

    @Override
    public int getItemCount() {
        return getDataCount();
    }

    /**
     * 获取数据源数量
     *
     * @return
     */
    public int getDataCount() {
        if (getData() == null) {
            return 0;
        }
        return getData().size();
    }

    /**
     * 获取数据源
     *
     * @return
     */
    public List<T> getData() {
        return mData;
    }

    /**
     * 获取Item数据
     *
     * @param position
     * @return
     */
    public T getDataItem(int position) {
        if (getData() == null) {
            return null;
        }
        return getData().get(position);
    }

    /**
     * 设置数据源
     *
     * @param data
     */
    public void setData(List<T> data) {
        mData = data;
    }

    /**
     * 移除某一项
     *
     * @param position
     */
    public void remove(int position) {
        int count = getItemCount();
        if (position >= 0 && position < count) {
            try {
                getData().remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, count - position);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 是否为第一个
     *
     * @param bodyPos
     * @return
     */
    public boolean isFirstBody(int bodyPos) {
        return bodyPos == 0;
    }

    /**
     * 是否为第最后一个
     *
     * @param bodyPos
     * @return
     */
    public boolean isLastBody(int bodyPos) {
        return bodyPos == getDataCount() - 1;
    }
}
