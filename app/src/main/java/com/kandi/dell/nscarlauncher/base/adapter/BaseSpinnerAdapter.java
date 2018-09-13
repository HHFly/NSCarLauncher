package com.kandi.dell.nscarlauncher.base.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.kandi.dell.nscarlauncher.R;
import com.kandi.dell.nscarlauncher.base.model.BaseModel;

import java.util.ArrayList;
import java.util.List;

/**
 * 商家分类Spinner
 * Created by lenovo on 2017/10/12.
 */

public abstract class BaseSpinnerAdapter<T> extends BaseAdapter {

    private List<SpinnerModel> mData;

    private int mContentResId = R.layout.spinner_item_80;

    public BaseSpinnerAdapter(List<T> data) {
        mData = new ArrayList<>();
        if (data != null) {
            int count = data.size();
            for (int i = 0; i < count; i++) {
                mData.add(getSpinnerModelItem(data.get(i)));
            }
        }
    }

    public BaseSpinnerAdapter(List<T> data, int contentResId) {
        this.mData = new ArrayList<>();
        if (data != null) {
            int count = data.size();
            for (int i = 0; i < count; i++) {
                mData.add(getSpinnerModelItem(data.get(i)));
            }
        }
        this.mContentResId = contentResId;
    }

    @Override
    public int getCount() {
        if (mData == null) {
            return 0;
        }
        return mData.size();
    }

    @Override
    public SpinnerModel getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            convertView = inflater.inflate(mContentResId, parent, false);
        }
        SpinnerModel item = getItem(position);
        TextView tv = (TextView) convertView.findViewById(R.id.item_tv);
        tv.setText(item.getName());
        return convertView;
    }

    public int getPositionById(long id) {
        if (mData != null) {
            int count = mData.size();
            for (int i = 0; i < count; i++) {
                SpinnerModel item = mData.get(i);
                if (item.getId() == id) {
                    return i;
                }
            }
        }
        return -1;
    }

    /**
     * 转换模型
     *
     * @param data
     * @return
     */
    public abstract SpinnerModel getSpinnerModelItem(T data);

    public static class SpinnerModel extends BaseModel {
        private long id;
        private String name;

        public SpinnerModel() {
        }

        public SpinnerModel(long id, String name) {
            this.id = id;
            this.name = name;
        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
