package com.example.dell.nscarlauncher.base.adapter;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mark.app.base.recylerview.MkViewHolder;

/**
 * Created by hui on 2018/4/14.
 */

public abstract class BaseMkRvAdapter extends RecyclerView.Adapter<AutoViewHolder> {

    BaseMkRvAdapter.RvAdapterCallback mCallback;
    protected MkViewHolder.OnItemClickListener mItemClickListener;

    public BaseMkRvAdapter() {
    }

    public void setCallback(BaseMkRvAdapter.RvAdapterCallback callback) {
        this.mCallback = callback;
    }

    protected void callback(Bundle bundle) {
        if(this.mCallback != null) {
            this.mCallback.notifWithBundle(bundle);
        }

    }

    public void setOnItemClickListener(MkViewHolder.OnItemClickListener listener) {
        this.mItemClickListener = listener;
    }

    public AutoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    public void onBindViewHolder(AutoViewHolder holder, int position) {
        if(this.mItemClickListener != null) {
            holder.setOnItemClickListener(this.mItemClickListener);
        }

    }

    public int getItemCount() {
        return 0;
    }

    public View inflater(ViewGroup parent, @LayoutRes int resId) {
        return LayoutInflater.from(parent.getContext()).inflate(resId, parent, false);
    }

    public interface RvAdapterCallback {
        void notifWithBundle(Bundle var1);
    }
}
