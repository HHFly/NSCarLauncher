package com.kandi.dell.nscarlauncher.ui_portrait.setting.adapter;

import android.content.Context;
import android.content.res.Configuration;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kandi.dell.nscarlauncher.R;
import com.kandi.dell.nscarlauncher.ui_portrait.setting.model.LanguageBean;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CarModeAdapter extends RecyclerView.Adapter<CarModeAdapter.LaViewHolder>{

    private List<LanguageBean> mList;
    private int mposition = -1;
    private Context mContext;

    public CarModeAdapter(Context context) {
        this.mContext = context;
        initData();
    }

    @Override
    public LaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.language_item,parent,false);
        LaViewHolder holder = new LaViewHolder(view);
        //获取初始化数据
        return holder;
    }

    @Override
    public void onBindViewHolder(final LaViewHolder holder, final int position) {
        LanguageBean mLanguageBean = mList.get(position);
        holder.language_name.setText(mLanguageBean.getName());
        holder.itemView.setSelected(mposition == position);
        holder.language_checkbox.setSelected(mposition == position);
        /*
        添加选中的打勾显示
         */
        holder.language_checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //将点击的位置传出去
                mposition = position;
                //在点击监听里最好写入setVisibility(View.VISIBLE);这样可以避免效果会闪
                holder.language_checkbox.setSelected(true);
                //刷新界面 notify 通知Data 数据set设置Changed变化
                //在这里运行notifyDataSetChanged 会导致下面的onBindViewHolder 重新加载一遍
                notifyDataSetChanged();
                //通过调用can接口修改运动模式
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    //自动帮我们写的ViewHolder，参数：View布局对象
    public class LaViewHolder extends RecyclerView.ViewHolder {
        TextView language_name;
        ImageView language_checkbox;
        public LaViewHolder(View itemView) {
            super(itemView);
            language_name = (TextView) itemView.findViewById(R.id.language_name);
            language_checkbox = (ImageView) itemView.findViewById(R.id.language_checkbox);
        }
    }


    public void initData(){
        mList = new ArrayList<LanguageBean>();
        LanguageBean bean = new LanguageBean();
        bean.setId(0);
        bean.setName(mContext.getString(R.string.经济));
        mList.add(bean);
        bean = new LanguageBean();
        bean.setId(1);
        bean.setName(mContext.getString(R.string.运动));
        mList.add(bean);
    }
}
