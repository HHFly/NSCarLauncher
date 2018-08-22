package com.example.dell.nscarlauncher.ui.application;

import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.view.View;

import com.example.dell.nscarlauncher.R;
import com.example.dell.nscarlauncher.base.adapter.AutoViewHolder;
import com.example.dell.nscarlauncher.base.adapter.BaseListRvAdapter;
import com.example.dell.nscarlauncher.base.adapter.BaseRvAdapter;

import java.util.List;

public class AppAdapter extends BaseListRvAdapter<ResolveInfo> {
private PackageManager packageManager;
    public AppAdapter(List<ResolveInfo> data, PackageManager packageManager) {
        super(data);
        this.packageManager=packageManager;
    }

    @Override
    public int getItemResId() {
        return R.layout.item_apk;
    }

    @Override
    public void bindBodyData(AutoViewHolder holder, int bodyPos, final ResolveInfo data) {
        holder.imageDrawable(R.id.img,data.activityInfo.loadIcon(packageManager));
        holder.text(R.id.name,data.activityInfo.loadLabel(packageManager).toString());
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
        void onClick(ResolveInfo data);
    }
}
