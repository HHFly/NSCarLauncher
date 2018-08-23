package com.example.dell.nscarlauncher.ui.application;

import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.view.View;

import com.example.dell.nscarlauncher.R;
import com.example.dell.nscarlauncher.base.adapter.AutoViewHolder;
import com.example.dell.nscarlauncher.base.adapter.BaseListRvAdapter;
import com.example.dell.nscarlauncher.base.adapter.BaseRvAdapter;

import java.util.List;

public class AppAdapter extends BaseListRvAdapter<AppInfo> {
private PackageManager packageManager;
    public AppAdapter(List<AppInfo> data, PackageManager packageManager) {
        super(data);
        this.packageManager=packageManager;
    }

    @Override
    public int getItemResId() {
        return R.layout.item_apk;
    }

    @Override
    public void bindBodyData(AutoViewHolder holder, int bodyPos, final AppInfo data) {
        holder.imageDrawable(R.id.apk_img,data.getAppIcon());

        holder.text(R.id.apk_name,data.getAppLabel());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onItemClickListener != null) {
                        onItemClickListener.onClick(data);

                }
            }
        });
    }

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        onItemClickListener = listener;
    }

    public interface OnItemClickListener {
        /**
         * 确认支付
         *
         * @param data
         */
        void onClick(AppInfo data);
    }
}
