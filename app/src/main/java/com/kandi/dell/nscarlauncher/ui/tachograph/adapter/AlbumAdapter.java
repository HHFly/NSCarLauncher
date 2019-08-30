package com.kandi.dell.nscarlauncher.ui.tachograph.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hikvision.playerlibrary.HikLog;
import com.kandi.dell.nscarlauncher.R;
import com.kandi.dell.nscarlauncher.ui.tachograph.member.AlbumItem;

import java.util.List;

public class AlbumAdapter extends BaseAdapter {
    //布局打气筒
    private LayoutInflater inflater;
    //上下文变量
    private Context context;
    //列表项集合
    private List<AlbumItem> mItems;

    //构造方法
    public AlbumAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    public void setmItems(List<AlbumItem> mItems) {
        this.mItems = mItems;
    }

    //控件持有类
    class ViewHolder {
        ImageView ivList;
        TextView tvList;
        ImageView ivAlbum;
    }

    @Override
    public int getCount() {
        return mItems == null ? 0 : mItems.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final ViewHolder mViewHolder;
        if (view == null) {
            mViewHolder = new ViewHolder();
            view = inflater.inflate(R.layout.album_item, viewGroup, false);
            mViewHolder.ivList = view.findViewById(R.id.iv_list);
            mViewHolder.tvList = view.findViewById(R.id.tv_list);
            mViewHolder.ivAlbum = view.findViewById(R.id.iv_album);
            view.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) view.getTag();
        }
        //列表项
        AlbumItem albumItem = mItems.get(i);
        //文件路径
        String fileName = albumItem.getmFileName();
        //最后一个斜杠分隔符
        int split = fileName.lastIndexOf("/");
        //时
        String hour = fileName.substring(split + 14, split + 16);
        //分
        String minute = fileName.substring(split + 16, split + 18);
        mViewHolder.tvList.setText(String.format("%s:%s:00", hour, minute));
        HikLog.infoLog("test222","thumb = " +albumItem.getmThumbnailUrl());
        //图片加载
        RequestOptions options = new RequestOptions();
        options.placeholder(R.drawable.photo_list_mid_placeholder).fallback(R.drawable.photo_list_mid_placeholder);
        Glide.with(context)
                .load(albumItem.getmThumbnailUrl())
//                .load(R.drawable.photo_list_image_404)
                .apply(options)
                .into(mViewHolder.ivList);
        //根据文件路径判断类型设置缩略图
        if (fileName.endsWith(".jpg")) {
            mViewHolder.ivAlbum.setImageResource(R.drawable.photo_list_mid_photo);
        } else if (fileName.contains("[C]")) {
            mViewHolder.ivAlbum.setImageResource(R.drawable.photo_list_mid_video_time);
        } else {
            mViewHolder.ivAlbum.setImageResource(R.drawable.photo_list_mid_video);
        }
        //根据选中状态改变字体颜色区分是否选中
        if (albumItem.ismChecked()) {
            mViewHolder.tvList.setTextColor(ContextCompat.getColor(context, R.color.checked));
        } else {
            mViewHolder.tvList.setTextColor(ContextCompat.getColor(context, R.color.unchecked));
        }
        return view;
    }
}
