package com.kandi.dell.nscarlauncher.ui_portrait.setting.fragment;

import android.media.MediaPlayer;
import android.media.audiofx.Equalizer;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.GridLayoutManager;
import android.view.View;
import android.widget.GridLayout;
import android.widget.LinearLayout;

import com.kandi.dell.nscarlauncher.R;
import com.kandi.dell.nscarlauncher.app.App;
import com.kandi.dell.nscarlauncher.base.fragment.BaseFragment;
import com.kandi.dell.nscarlauncher.common.util.JsonUtils;
import com.kandi.dell.nscarlauncher.common.util.SPUtil;
import com.kandi.dell.nscarlauncher.ui_portrait.home.HomePagerActivity;
import com.kandi.dell.nscarlauncher.ui_portrait.setting.adapter.EqAdapter;
import com.kandi.dell.nscarlauncher.ui_portrait.setting.model.EqData;
import com.kandi.dell.nscarlauncher.widget.EqSeekBarView;

import java.util.ArrayList;

public class EqFragment extends BaseFragment {
    private HomePagerActivity homePagerActivity;
    private MediaPlayer mMediaPlayer;
    private Equalizer mEqualizer;
    private LinearLayout mLayout;
    private EqAdapter mAdapter ;
    private ArrayList<EqData> mData =new ArrayList<>();
    private ArrayList<EqSeekBarView> mView =new ArrayList<>();
    public int postion=1;
    RecyclerView rv;
    public void setHomePagerActivity(HomePagerActivity homePagerActivity) {
        this.homePagerActivity = homePagerActivity;
    }
    @Override
    public int getContentResId() {
        return R.layout.fragment_set_eq_portrait;
    }

    @Override
    public void findView() {
        mLayout =getView(R.id.ll_seekbar);

    }

    @Override
    public void Resume() {
        if(isSecondResume) {
            refreshSeekbar();
        }
    }

    @Override
    public void setListener() {
        setClickListener(R.id.bt_back);
    }

    @Override
    public void initView() {
        setEqualize();
        /*初始化数据*/
        initEqList();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mEqualizer=null;
        mData=null;
        mView=null;
    }

    private void initEqList() {
        EqData custom =new EqData();
        custom.setSelect(false);
        custom.setPreset(getString(R.string.自定义));
        custom.setPosition((short) 100);

        mData.add(custom);
        for (int i=0; i<mEqualizer.getNumberOfPresets();i++) {
            EqData data =new EqData();

            data.setPosition((short) i);
            data.setPreset(getName(mEqualizer.getPresetName((short) i)));

            mData.add(data);
        }

        postion=  SPUtil.getInstance(getContext(),"EQ").getInt("EQPosition", 1);
        mData.get(postion).setSelect(true);
            initRvAdapter(mData);
    }
private String getName(String name){
        switch (name){
            case "Normal":
                return getString(R.string.eq关闭);
              
            case "Classical":
                return getString(R.string.古典);
            
            case "Dance":
                return getString(R.string.舞曲);
            
            case "Flat":
                return getString(R.string.柔和);
           
            case "Folk":
                return getString(R.string.民族);
           
            case "Heavy Metal":
                return getString(R.string.重金属);
           
            case "Jazz":
                return getString(R.string.爵士);
           
            case "Hip Hop":
                return getString(R.string.嘻哈);
           
            case "Pop":
                return getString(R.string.流行);
           
            case "Rock":
                return getString(R.string.摇滚);
           
            default:
                return "";
               
        }
        
}
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_back:
                homePagerActivity.getSetFragment().hideFragment();

               
        }
    }



    private void setEqualize() {

        mEqualizer =App.get().getmEqualizer();
        short bands = mEqualizer.getNumberOfBands();


        for (short i = 0; i < bands; i++) {
            final short band = i;

            EqSeekBarView eqSeekBarView =new EqSeekBarView(getContext(),band,mEqualizer);
            eqSeekBarView.setOnItemClickListener(new EqSeekBarView.OnItemClickListener() {
                @Override
                public void onClickMode() {
                    mAdapter.DataClear();
                    postion=0;
                   mAdapter.getData().get(0).setSelect(true);
                    rv.smoothScrollToPosition(0);
                    mAdapter.notifyDataSetChanged();

                }

                @Override
                public void onClickStop(short nowBandLevel,short band) {
                    short bands = mEqualizer.getNumberOfBands();
                     short[] bandLevels=new short[bands];;

                    for (short i = 0; i < bands; i++) {
                        if(i==band){
                            bandLevels[i] =nowBandLevel;
                        }else {
                            bandLevels[i]= mEqualizer.getBandLevel(i);
                        }

                    }

                    Equalizer.Settings settings =mEqualizer.getProperties();
                    settings.bandLevels=bandLevels;
                SPUtil.getInstance(getContext(),"EQ").putString("EQSet", JsonUtils.toJson(settings));
                    SPUtil.getInstance(getContext(),"EQ").putInt("EQPosition", postion );
                }
            });
            mView.add(eqSeekBarView);
            mLayout.addView(eqSeekBarView);
        }
        String set =SPUtil.getInstance(getContext(),"EQ").getString("EQSet");
        Equalizer.Settings settings = JsonUtils.fromJson(set, Equalizer.Settings.class);
        if(settings!=null) {
            try {
                mEqualizer.setProperties(settings);
            }catch (Exception e){

            }
        }
    }
    private  void refreshSeekbar(){

        short bands = mEqualizer.getNumberOfBands();

        for (short i = 0; i < bands; i++) {
            final short band = i;

           mView.get(i).refreshSeekbar(band,mEqualizer);
        }
//        Equalizer.Settings settings =mEqualizer.getProperties();
//        SPUtil.getInstance(getContext(),"EQ").putString("EQSet", JsonUtils.toJson(settings));
    }
    /**
     * 初始化adapter
     *
     *
     */
    private void initRvAdapter( ArrayList<EqData> data) {
        if (mAdapter == null) {
             rv = getView(R.id.rv_eq_nameList);
            mAdapter =new EqAdapter(data);

            if (rv != null) {
                //布局管理器对象 参数1.上下文 2.规定显示的行数
                GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 4);
                //通过布局管理器可以控制条目排列的顺序 true反向显示 false正常显示(默认)
                gridLayoutManager.setReverseLayout(false);
                //设置RecycleView显示的方向是水平还是垂直
                //GridLayout.HORIZONTAL水平 GridLayout.VERTICAL默认垂直
                gridLayoutManager.setOrientation(GridLayout.VERTICAL);
                //设置布局管理器， 参数linearLayoutManager对象
                rv.setLayoutManager(gridLayoutManager);
                rv.setAdapter(mAdapter);

            }
            mAdapter.setOnItemClickListener(new EqAdapter.OnItemClickListener() {
                @Override
                public void onClickMode(EqData data) {
                            postion=data.getPosition()+1;
                    try {
                        mEqualizer.usePreset(data.getPosition());
                    }catch (Exception e){}
                        refreshSeekbar();
                        mAdapter.DataClear();
                        data.setSelect(true);
                       mAdapter.notifyDataSetChanged();
                    SPUtil.getInstance(getContext(),"EQ").putInt("EQPosition", postion );
                }

                @Override
                public void onClickCutsom(EqData data) {
                    String set =SPUtil.getInstance(getContext(),"EQ").getString("EQSet");
                    Equalizer.Settings settings = JsonUtils.fromJson(set, Equalizer.Settings.class);
                    if(settings!=null) {
                        try {
                            mEqualizer.setProperties(settings);
                        }catch (Exception e){}

                    }

                    postion=0;
                    refreshSeekbar();
                    mAdapter.DataClear();
                    data.setSelect(true);
                    mAdapter.notifyDataSetChanged();
                    SPUtil.getInstance(getContext(),"EQ").putInt("EQPosition", postion );
                }

            });

        }else {
            mAdapter.notifyData(data,true);
        }
    }


}
