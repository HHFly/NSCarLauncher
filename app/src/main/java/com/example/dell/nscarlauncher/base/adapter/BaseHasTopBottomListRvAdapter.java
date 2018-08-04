package com.example.dell.nscarlauncher.base.adapter;

import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * RecyclerView列表适配器
 * 拥有顶部的列表
 * Created by lenovo on 2017/9/23.
 */

public abstract class BaseHasTopBottomListRvAdapter<AllData, BodyData> extends BaseHasTopListRvAdapter<AllData, BodyData> {

    protected final int VIEW_TYPE_BOTTOM = 2;

    public BaseHasTopBottomListRvAdapter() {
    }

    public BaseHasTopBottomListRvAdapter(AllData allData) {
        super(allData);
    }

    public BaseHasTopBottomListRvAdapter(List<BodyData> bodyData) {
        super(bodyData);
    }

    public BaseHasTopBottomListRvAdapter(AllData allData, List<BodyData> bodyData) {
        super(allData, bodyData);
    }

    /**
     * 获取底部Item资源文件
     *
     * @return
     */
    public abstract int getBottomItemResId();

    /**
     * 绑定底部数据
     *
     * @param holder
     * @param position
     * @param data
     */
    public abstract void bindBottomData(AutoViewHolder holder, int position, AllData data);

    @Override
    public AutoViewHolder customOnCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case VIEW_TYPE_TOP: {
                view = inflater(parent, getTopItemResId());
                break;
            }
            case VIEW_TYPE_BOTTOM: {
                view = inflater(parent, getBottomItemResId());
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
    public void customBindView(AutoViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        switch (viewType) {
            case VIEW_TYPE_TOP: {
                AllData topData = getTopData();
                if (topData != null) {
                    bindTopData(holder, position, topData);
                }
                break;
            }
            case VIEW_TYPE_BOTTOM: {
                AllData bottomData = getTopData();
                if (bottomData != null) {
                    bindBottomData(holder, position, bottomData);
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
    public int getItemViewType(int position) {
        if (position < getTopItemCount()) {
            return VIEW_TYPE_TOP;
        } else if (position == getItemCount() - 1) {
            return VIEW_TYPE_BOTTOM;
        }
        return VIEW_TYPE_BODY;
    }

    @Override
    public int getItemCount() {
        int superItemCount = getSuperItemCount();
        return superItemCount + getBottomItemCount();
    }

    /**
     * 获取顶部和body的Item数量
     *
     * @return
     */
    public int getSuperItemCount() {
        return super.getItemCount();
    }

    /**
     * 获取底部Item数量
     *
     * @return
     */
    public int getBottomItemCount() {
        if (getBottomItemResId() == 0) {
            return 0;
        }
        return 1;
    }
}
