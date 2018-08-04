package com.example.dell.nscarlauncher.base.adapter;

import android.support.annotation.LayoutRes;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * RecyclerView列表适配器
 * 拥有顶部的列表
 * Created by lenovo on 2017/9/23.
 */

public abstract class BaseHasTopListRvAdapter<TopData, BodyData> extends BaseRvAdapter {
    protected final int VIEW_TYPE_TOP = 0;
    protected final int VIEW_TYPE_BODY = 1;

    /**
     * 顶部数据源
     */
    private TopData mTopData;
    /**
     * 数据源
     */
    private List<BodyData> mBodyData;

    public BaseHasTopListRvAdapter() {
        this(null, null);
    }

    public BaseHasTopListRvAdapter(TopData topData) {
        this(topData, null);
    }

    public BaseHasTopListRvAdapter(List<BodyData> bodyData) {
        this(null, bodyData);
    }
    public BaseHasTopListRvAdapter(TopData topData, ArrayList<BodyData> bodyData) {
        this.mTopData = topData;
        this.mBodyData = new ArrayList<>();
        if (bodyData != null) {
            mBodyData.addAll(bodyData);
        }
    }
    public BaseHasTopListRvAdapter(TopData topData, List<BodyData> bodyData) {
        this.mTopData = topData;
        this.mBodyData = new ArrayList<>();
        if (bodyData != null) {
            mBodyData.addAll(bodyData);
        }
    }

    @Override
    public AutoViewHolder customOnCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case VIEW_TYPE_TOP: {
                view = inflater(parent, getTopItemResId());
                break;
            }
            default:
            case VIEW_TYPE_BODY: {
                view = inflater(parent, getBodyItemResId());
                break;
            }
        }
        return new AutoViewHolder(view);
    }

    @Override
    public int getItemViewType(int position) {
        if (position < getTopItemCount()) {
            return VIEW_TYPE_TOP;
        }
        return VIEW_TYPE_BODY;
    }

    @Override
    public int getItemCount() {
        return getTopItemCount() + getBodyItemCount();
    }

    @Override
    public void customBindView(AutoViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        switch (viewType) {
            case VIEW_TYPE_TOP: {
                TopData topData = getTopData();
                if (topData != null) {
                    bindTopData(holder, position, topData);
                }
                break;
            }
            default:
            case VIEW_TYPE_BODY: {
                int bodyPos = position - getTopItemCount();
                BodyData bodyData = getDataItem(bodyPos);
                if (bodyData != null) {
                    bindBodyData(holder, bodyPos, bodyData);
                }
                break;
            }
        }
    }


    @Override
    public void customBindLocalRefresh(AutoViewHolder holder, int position, List payloads) {

    }

    /**
     * 获取顶部Item资源文件
     *
     * @return
     */
    @LayoutRes
    public abstract int getTopItemResId();

    /**
     * 获取列表数据Item资源文件
     *
     * @return
     */
    @LayoutRes
    public abstract int getBodyItemResId();

    /**
     * 绑定顶部数据
     *
     * @param holder
     */
    public abstract void bindTopData(AutoViewHolder holder, int topPos, TopData data);

    /**
     * 绑定列表数据
     *
     * @param holder
     * @param bodyPosition 列表索引
     */
    public abstract void bindBodyData(AutoViewHolder holder, int bodyPosition, BodyData data);

    /**
     * 刷新顶部数据
     *
     * @param data
     */
    public void notifyData(TopData data) {
        mTopData = data;
        notifyDataSetChanged();
    }

    /**
     * 刷新数据
     *
     * @param data
     */
    public void notifyData(List<BodyData> data, boolean isRefresh) {
        if (isRefresh) {
            refresh(data);
        } else {
            loadMore(data);
        }
    }

    /**
     * 刷新数据
     *
     * @param topData   头部数据
     * @param bodyData  列表数据
     * @param isRefresh 分页数据是刷新还是加载更多
     */
    public void notifyData(TopData topData, List<BodyData> bodyData, boolean isRefresh) {
        notifyData(topData, bodyData, isRefresh, true);
    }
    public void notifyData(TopData topData, ArrayList<BodyData> bodyData, boolean isRefresh) {
        notifyData(topData, bodyData, isRefresh, true);
    }
    /**
     * 刷新数据
     *
     * @param topData     头部数据
     * @param bodyData    列表数据
     * @param isRefresh   分页数据是刷新还是加载更多
     * @param isNotifyTop 是否刷新头部数据
     */
    public void notifyData(TopData topData, List<BodyData> bodyData, boolean isRefresh, boolean isNotifyTop) {
        if (isNotifyTop) {
            mTopData = topData;
        }
        notifyData(bodyData, isRefresh);
    }

    /**
     * 刷新数据
     *
     * @param data
     */
    public void refresh(List<BodyData> data) {
        resetBodyData();
        if (data != null) {
            getBodyData().addAll(data);
        }
        notifyDataSetChanged();
    }

    /**
     * 加载更多
     *
     * @param data
     */
    public void loadMore(List<BodyData> data) {
        if (mBodyData == null) {
            mBodyData = new ArrayList<>();
        }
        if (data != null) {
            mBodyData.addAll(data);
        }
        notifyDataSetChanged();
    }

    /**
     * 重置数据源
     */
    private void resetBodyData() {
        if (mBodyData == null) {
            mBodyData = new ArrayList<>();
        } else {
            mBodyData.clear();
        }
    }

    /**
     * 获取顶部Item数量
     *
     * @return
     */
    public int getTopItemCount() {
        if (getTopItemResId() == 0) {
            return 0;
        } else {
            return 1;
        }
    }

    /**
     * 获取列表数据Item数量
     *
     * @return
     */
    public int getBodyItemCount() {
        if (getBodyData() == null) {
            return 0;
        }
        return getBodyData().size();
    }

    /**
     * 是否为bodyItem中的第一项
     *
     * @return
     */
    public boolean isFirstBody(int bodyPos) {
        return bodyPos == 0;
    }

    /**
     * 是否为bodyItem中的最后一项
     *
     * @return
     */
    public boolean isLastBody(int bodyPos) {
        return bodyPos == getBodyItemCount() - 1;
    }


    /**
     * 获取顶部数据源
     *
     * @return
     */
    public TopData getTopData() {
        return mTopData;
    }

    /**
     * 获取列表数据源
     *
     * @return
     */
    public List<BodyData> getBodyData() {
        return mBodyData;
    }

    /**
     * 获取Item数据
     *
     * @param position
     * @return
     */
    public BodyData getDataItem(int position) {
        if (getBodyData() == null) {
            return null;
        }
        return getBodyData().get(position);
    }

    /**
     * 移除列表数据某一项
     *
     * @param bodyPosition
     */
    public void removeBody(int bodyPosition) {
        int count = getBodyItemCount();
        if (bodyPosition >= 0 && bodyPosition < count) {
            try {
                getBodyData().remove(bodyPosition);

                //列表数据索引对应的真实索引
                int truePos = bodyPosition + getTopItemCount();
                notifyItemRemoved(truePos);
                notifyItemRangeChanged(truePos, count - truePos);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
