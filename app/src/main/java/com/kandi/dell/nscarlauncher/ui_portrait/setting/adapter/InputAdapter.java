package com.kandi.dell.nscarlauncher.ui_portrait.setting.adapter;

import android.content.Context;
import android.content.res.Configuration;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodInfo;
import android.widget.ImageView;
import android.widget.TextView;

import com.kandi.dell.nscarlauncher.R;
import com.kandi.dell.nscarlauncher.common.util.InputMethodUtils;
import com.kandi.dell.nscarlauncher.ui_portrait.setting.model.LanguageBean;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class InputAdapter extends RecyclerView.Adapter<InputAdapter.InputViewHolder>{

    private List<InputMethodInfo> mList;
    private  String[] items ={};
    private int mposition = -1;
    private Context mContext;

    public InputAdapter(Context context) {
        this.mContext = context;
        initData();
    }

    @Override
    public InputViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.language_item,parent,false);
        InputViewHolder holder = new InputViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final InputViewHolder holder, final int position) {
        holder.language_name.setText(items[position]);
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
                InputMethodUtils.putDefaultInputMethod(mList.get(position).getId());
                //刷新界面 notify 通知Data 数据set设置Changed变化
                //在这里运行notifyDataSetChanged 会导致下面的onBindViewHolder 重新加载一遍
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    //自动帮我们写的ViewHolder，参数：View布局对象
    public class InputViewHolder extends RecyclerView.ViewHolder {
        TextView language_name;
        ImageView language_checkbox;
        public InputViewHolder(View itemView) {
            super(itemView);
            language_name = (TextView) itemView.findViewById(R.id.language_name);
            language_checkbox = (ImageView) itemView.findViewById(R.id.language_checkbox);
        }
    }


    public void initData(){
        mList= InputMethodUtils.getInputMethodManager();
        InputMethodInfo inputMethodInfo;
        ArrayList<String> item =new ArrayList<>();
        if(mList!=null){
            for(int i =0 ;i<mList.size();i++){

                item.add((String) mList.get(i).getServiceInfo().loadLabel(mContext.getPackageManager()));
            }
        }
        items =(String[])item.toArray(new String[item.size()]);
    }
}
