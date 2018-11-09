package com.kandi.dell.nscarlauncher.ui.setting.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.kandi.dell.nscarlauncher.R;
import com.kandi.dell.nscarlauncher.ui.setting.model.WallPaperInfo;

import java.util.List;

public class RecyclerViewGridAdapter extends RecyclerView.Adapter<RecyclerViewGridAdapter.GridViewHolder> {

    private Context mContext;
    //泛型是RecyclerView所需的Bean类
    private List<WallPaperInfo> wallPaperInfo;
    WallPaperInfo datainfo;
    //构造方法，一般需要接收两个参数 1.上下文 2.集合对象（包含了我们所需要的数据）
    public RecyclerViewGridAdapter(Context context, List<WallPaperInfo> date) {
        mContext = context;
        wallPaperInfo = date;
    }
    @Override
    public RecyclerViewGridAdapter.GridViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //转换一个ViewHolder对象，决定了item的样式，参数1.上下文 2.XML布局资源 3.null
        View itemView = View.inflate(mContext, R.layout.item_wallpaper, null);
        //创建一个ViewHodler对象
        GridViewHolder gridViewHolder = new GridViewHolder(itemView);
        //把ViewHolder传出去
        return gridViewHolder;
    }
    //当ViewHolder和数据绑定是回调
    @Override
    public void onBindViewHolder(RecyclerViewGridAdapter.GridViewHolder holder, int position) {
        //从集合里拿对应的item的数据对象
        datainfo = wallPaperInfo.get(position);
        //给Holder里面的控件对象设置数据
        holder.setData(datainfo);
    }
    //决定RecyclerView有多少条item
    @Override
    public int getItemCount() {
        //数据不为null，有几条数据就显示几条数据
        if (wallPaperInfo != null && wallPaperInfo.size() > 0) {
            return wallPaperInfo.size();
        }
        return 0;
    }
    //自动帮我们写的ViewHolder，参数：View布局对象
    public class GridViewHolder extends RecyclerView.ViewHolder {
        private final ImageView mImageView;
        public GridViewHolder(View itemView) {
            super(itemView);
            mImageView = (ImageView) itemView.findViewById(R.id.paper);
        }
        public void setData(final WallPaperInfo data) {
            //给imageView设置图片数据
            mImageView.setBackgroundResource(data.getImgId());
            mImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(onItemClickListener!=null){
                        onItemClickListener.onClickData(data);
                    }
                }
            });
        }
    }

    private OnItemClickListener onItemClickListener;
    public void setOnItemClickListener(OnItemClickListener listener) {
        onItemClickListener = listener;
    }

    public interface OnItemClickListener {
        /**
         * 点击
         */
        void onClickData(WallPaperInfo data);
    }
}