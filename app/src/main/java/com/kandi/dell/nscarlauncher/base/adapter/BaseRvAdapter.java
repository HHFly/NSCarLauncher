package com.kandi.dell.nscarlauncher.base.adapter;

import android.content.Context;
import android.support.annotation.ColorRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.StringRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kandi.dell.nscarlauncher.app.App;

import java.util.List;

/**
 * RecyclerAdapter的基类
 */

public abstract class BaseRvAdapter extends RecyclerView.Adapter {

    /**
     * Application上下文
     */
    private Context mContext;

    public BaseRvAdapter() {
        mContext = App.get();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return customOnCreateViewHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        customBindView((AutoViewHolder) holder, position);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position, List payloads) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position);
        } else {
            customBindLocalRefresh((AutoViewHolder) holder, position, payloads);
        }
    }

    /**
     * 创建ViewHolder
     *
     * @param parent
     * @param viewType
     * @return
     */
    public abstract AutoViewHolder customOnCreateViewHolder(ViewGroup parent, int viewType);

    /**
     * 绑定view
     *
     * @param holder
     * @param position
     */
    public abstract void customBindView(AutoViewHolder holder, int position);

    /**
     * 局部刷新
     *
     * @param holder
     * @param position
     */
    public abstract void customBindLocalRefresh(AutoViewHolder holder, int position, List payloads);

    /**
     * 获取字符串资源
     *
     * @param resId
     * @return
     */
    public String getString(@StringRes int resId) {
        return getContext().getString(resId);
    }

    /**
     * 获取颜色资源
     *
     * @param resId
     * @return
     */
    public int getColor(@ColorRes int resId) {
        return getContext().getResources().getColor(resId);
    }

    /**
     * 获取Application上下文
     *
     * @return
     */
    public Context getContext() {
        return mContext;
    }

    /**
     * 创建实例
     *
     * @param parent
     * @param resId
     * @return
     */
    public View inflater(ViewGroup parent, @LayoutRes int resId) {
        return LayoutInflater.from(parent.getContext()).inflate(resId, parent, false);
    }
    /**
     * 点击Item监听
     */
    private OnItemClickListener onBaseItemClickListener;
    /**
     * 点击Item监听
     */
    public interface OnItemClickListener<Model> {
        /**
         * 点击Item监听
         *
         * @param view     点击的View
         * @param position 点击对应的position
         * @param data     点击返回的Item数据源
         * @param type     类型：用于区分点击的是哪个东西(自己设置)
         */
        void onItemClick(View view, int position, Model data, int type);
    }

    /**
     * 设置点击Item监听
     *
     * @param listener
     */
    public void setOnItemClickListener(OnItemClickListener listener) {
        onBaseItemClickListener = listener;
    }

    /**
     * 调用点击监听器
     *
     * @param view     点击的View
     * @param position 点击对应的position
     * @param data     点击返回的Item数据源
     * @param type     类型：用于区分点击的是哪个东西(自己设置)
     */
    public void useOnItemClick(View view, int position, Object data, int type) {
        if (onBaseItemClickListener != null) {
            onBaseItemClickListener.onItemClick(view, position, data, type);
        }
    }

    /**
     * 调用点击监听器
     *
     * @param view     点击的View
     * @param position 点击对应的position
     * @param data     点击返回的Item数据源
     */
    public void useOnItemClick(View view, int position, Object data) {
        useOnItemClick(view, position, data, -1);
    }
}
