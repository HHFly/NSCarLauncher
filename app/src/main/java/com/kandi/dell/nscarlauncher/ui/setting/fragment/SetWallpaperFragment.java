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
    private int[] imgBgArrays = new int[]{R.color.dfbackground,R.color.red_fo3a53,R.color.color_main_blue,
            R.mipmap.ic_f_fm_bg,R.mipmap.home_icon_weather_05,R.mipmap.home_icon_weather_06,
            R.mipmap.home_icon_weather_07,R.mipmap.home_icon_weather_08,R.mipmap.home_icon_weather_09,
            R.mipmap.home_icon_weather_10,R.mipmap.home_icon_weather_11,R.mipmap.home_icon_weather_12,
            R.mipmap.home_icon_weather_07,R.mipmap.home_icon_weather_08,R.mipmap.home_icon_weather_09,
            R.mipmap.home_icon_weather_10,R.mipmap.home_icon_weather_11,R.mipmap.home_icon_weather_12};

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
            mDatas.add(info);
        }
    }
}
