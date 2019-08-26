package com.kandi.dell.nscarlauncher.base.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;

public class MkViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private MkViewHolder.OnItemClickListener mListener;

    public MkViewHolder(View view) {
        super(view);
        view.setOnClickListener(this);
    }

    public MkViewHolder(View view, MkViewHolder.OnItemClickListener listener) {
        this(view);
        this.mListener = listener;
    }

    public <T extends View> T get(int id) {
        return get(this.itemView, id);
    }

    private static <T extends View> T get(View view, int id) {
        SparseArray<View> viewHolder = (SparseArray) view.getTag();
        if (viewHolder == null) {
            viewHolder = new SparseArray();
            view.setTag(viewHolder);
        }

        View childView = (View) viewHolder.get(id);
        if (childView == null) {
            childView = view.findViewById(id);
            viewHolder.put(id, childView);
        }

        return (T) childView;
    }

    public void onClick(View v) {
        if (this.mListener != null) {
            this.mListener.onItemClick(this);
        }

    }

    public void setOnItemClickListener(MkViewHolder.OnItemClickListener listener) {
        this.mListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(MkViewHolder var1);
    }
}

