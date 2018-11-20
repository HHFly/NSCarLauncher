package com.kandi.dell.nscarlauncher.ui.setting.fragment;

import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.GridLayout;

import com.kandi.dell.nscarlauncher.R;
import com.kandi.dell.nscarlauncher.base.fragment.BaseFragment;
import com.kandi.dell.nscarlauncher.common.util.SPUtil;
import com.kandi.dell.nscarlauncher.ui.setting.adapter.RecyclerViewGridAdapter;
import com.kandi.dell.nscarlauncher.ui.setting.model.WallPaperInfo;

import java.util.ArrayList;
import java.util.List;

public class SetWallpaperFragment extends BaseFragment {
    /**
     * 显示的数据
     */
    private List<WallPaperInfo> mDatas;
    RecyclerView recyclerView;
    public String PicIndex ="picindex";
    private int[] imgBgArrays = new int[]{R.mipmap.bg1,R.mipmap.bg2,R.mipmap.bg3,
            R.mipmap.bg4,R.mipmap.bg5,R.mipmap.bg6,
            R.mipmap.bg7,R.mipmap.bg8,R.mipmap.bg9};
    private int[] imgBgSmallArrays = new int[]{R.mipmap.bg1_small,R.mipmap.bg2_small,R.mipmap.bg3_small,
            R.mipmap.bg4_small,R.mipmap.bg5_small,R.mipmap.bg6_small,
            R.mipmap.bg7_small,R.mipmap.bg8_small,R.mipmap.bg9_small};

    @Override
    public int getContentResId() {
        return R.layout.fragment_set_wallpaper;
    }

    @Override
    public void findView() {
        recyclerView = getView(R.id.recyclerView);
    }

    @Override
    public void setListener() {
        setClickListener(R.id.iv_return);
    }

    @Override
    public void initView() {
        initData();
        //创建适配器adapter对象 参数1.上下文 2.数据加载集合
        RecyclerViewGridAdapter recyclerViewGridAdapter = new RecyclerViewGridAdapter(getContext(), mDatas);
        //4.设置适配器
        recyclerView.setAdapter(recyclerViewGridAdapter);
        //布局管理器对象 参数1.上下文 2.规定显示的行数
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3);
        //通过布局管理器可以控制条目排列的顺序 true反向显示 false正常显示(默认)
        gridLayoutManager.setReverseLayout(false);
        //设置RecycleView显示的方向是水平还是垂直
        //GridLayout.HORIZONTAL水平 GridLayout.VERTICAL默认垂直
        gridLayoutManager.setOrientation(GridLayout.HORIZONTAL);
        //设置布局管理器， 参数linearLayoutManager对象
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerViewGridAdapter.setOnItemClickListener(new RecyclerViewGridAdapter.OnItemClickListener() {

            @Override
            public void onClickData(WallPaperInfo data) {
                SPUtil.getInstance(getContext(),PicIndex).putInt(PicIndex,data.getImgId());
                //选择完成后通知到主页进行替换背景
                Intent intent = new Intent();
                intent.setAction("com.changeBg");
                getActivity().sendBroadcast(intent);
                DisplayFragment.hideFragment();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_return:
                DisplayFragment.hideFragment();
                break;
        }
    }

    public void initData(){
        mDatas = new ArrayList<WallPaperInfo>();
        for(int i=0;i<imgBgArrays.length;i++){
            WallPaperInfo info = new WallPaperInfo();
            info.imgId = imgBgArrays[i];
            info.smallImgId = imgBgSmallArrays[i];
            mDatas.add(info);
        }
    }
}
