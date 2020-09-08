package com.kandi.dell.nscarlauncher.ui_portrait.setting.adapter;

import android.content.Context;
import android.content.res.Configuration;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kandi.dell.nscarlauncher.R;
import com.kandi.dell.nscarlauncher.db.util.Utils;
import com.kandi.dell.nscarlauncher.ui_portrait.setting.model.LanguageBean;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LanguageAdapter extends RecyclerView.Adapter<LanguageAdapter.LaViewHolder>{

    private List<LanguageBean> mList;
    private int mposition = -1;
    private Context mContext;

    public LanguageAdapter(Context context) {
        this.mContext = context;
        initData();
    }

    @Override
    public LaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.language_item,parent,false);
        LaViewHolder holder = new LaViewHolder(view);
        Locale locale = mContext.getResources().getConfiguration().locale;
        String language = locale.getLanguage();
        if (language.endsWith("zh")){
            mposition = 0;
        } else {
            mposition = 1;
        }
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
                try {
                    if(position == 0){
                        Locale currentLocale = new Locale("zh",
                                "CN");
                        updateLanguage(currentLocale);
                    } else if (position == 1) {
                        updateLanguage(Locale.ENGLISH);
                    }
                    // changeLan("ch");
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
        bean.setName(mContext.getString(R.string.中文));
        mList.add(bean);
        bean = new LanguageBean();
        bean.setId(1);
        bean.setName(mContext.getString(R.string.English));
        mList.add(bean);
    }

    /**
     * 修改系统语言
     */
    public void updateLanguage(Locale locale) {
        Log.d("ANDROID_LAB", locale.toString());
        try {
            Object objIActMag, objActMagNative;
            Class clzIActMag = Class.forName("android.app.IActivityManager");
            Class clzActMagNative = Class.forName("android.app.ActivityManagerNative");
            Method mtdActMagNative$getDefault = clzActMagNative.getDeclaredMethod("getDefault");
            // IActivityManager iActMag = ActivityManagerNative.getDefault();
            objIActMag = mtdActMagNative$getDefault.invoke(clzActMagNative);
            // Configuration config = iActMag.getConfiguration();
            Method mtdIActMag$getConfiguration = clzIActMag.getDeclaredMethod("getConfiguration");
            Configuration config = (Configuration) mtdIActMag$getConfiguration.invoke(objIActMag);
            config.locale = locale;
            // iActMag.updateConfiguration(config);
            // 此处需要声明权限:android.permission.CHANGE_CONFIGURATION
            // 会重新调用 onCreate();
            Class[] clzParams = { Configuration.class };
            Method mtdIActMag$updateConfiguration = clzIActMag.getDeclaredMethod(
                    "updateConfiguration", clzParams);
            mtdIActMag$updateConfiguration.invoke(objIActMag, config);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
