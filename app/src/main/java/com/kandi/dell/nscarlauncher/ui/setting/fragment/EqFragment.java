package com.kandi.dell.nscarlauncher.ui.setting.fragment;

import android.media.MediaPlayer;
import android.media.audiofx.Equalizer;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import com.kandi.dell.nscarlauncher.R;
import com.kandi.dell.nscarlauncher.app.App;
import com.kandi.dell.nscarlauncher.base.fragment.BaseFragment;
import com.kandi.dell.nscarlauncher.common.util.JsonUtils;
import com.kandi.dell.nscarlauncher.common.util.SPUtil;
import com.kandi.dell.nscarlauncher.ui.fm.FMAdapter;
import com.kandi.dell.nscarlauncher.ui.music.Service.PlayerService;
import com.kandi.dell.nscarlauncher.ui.setting.SetFragment;
import com.kandi.dell.nscarlauncher.ui.setting.adapter.EqAdapter;
import com.kandi.dell.nscarlauncher.ui.setting.model.EqData;
import com.kandi.dell.nscarlauncher.widget.EqSeekBarView;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class EqFragment extends BaseFragment {
    private MediaPlayer mMediaPlayer;
    private Equalizer mEqualizer;
    private LinearLayout mLayout;
    private EqAdapter mAdapter ;
    private ArrayList<EqData> mData =new ArrayList<>();
    private ArrayList<EqSeekBarView> mView =new ArrayList<>();
    public static  int postion=1;
    @Override
    public int getContentResId() {
        return R.layout.fragment_set_eq;
    }

    @Override
    public void findView() {
        mLayout =getView(R.id.ll_seekbar);

    }

    @Override
    public void setListener() {
        setClickListener(R.id.iv_return);
    }

    @Override
    public void initView() {
        setEqualize();
        /*初始化数据*/
        initEqList();

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
            case R.id.iv_return:
                SetFragment.hideFragment();

               
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
                   mAdapter.getData().get(0).setSelect(true);
                    mAdapter.notifyDataSetChanged();
                }
            });
            mView.add(eqSeekBarView);
            mLayout.addView(eqSeekBarView);
        }
        String set =SPUtil.getInstance(getContext(),"EQ").getString("EQSet");
        Equalizer.Settings settings = JsonUtils.fromJson(set, Equalizer.Settings.class);
        if(settings!=null) {
            mEqualizer.setProperties(settings);
        }
    }
    private  void refreshSeekbar(){

        short bands = mEqualizer.getNumberOfBands();

        for (short i = 0; i < bands; i++) {
            final short band = i;

           mView.get(i).refreshSeekbar(band,mEqualizer);
        }
    }
    /**
     * 初始化adapter
     *
     *
     */
    private void initRvAdapter( ArrayList<EqData> data) {
        if (mAdapter == null) {
            RecyclerView rv = getView(R.id.rv_eq_nameList);
            mAdapter =new EqAdapter(data);

            if (rv != null) {
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
                linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                rv.setLayoutManager(linearLayoutManager);
                rv.setAdapter(mAdapter);
            }
            mAdapter.setOnItemClickListener(new EqAdapter.OnItemClickListener() {
                @Override
                public void onClickMode(EqData data) {
                            postion=data.getPosition()+1;
                        mEqualizer.usePreset(data.getPosition());
                        refreshSeekbar();
                        mAdapter.DataClear();
                        data.setSelect(true);
                       mAdapter.notifyDataSetChanged();
                }

                @Override
                public void onClickCutsom(EqData data) {
                    String set =SPUtil.getInstance(getContext(),"EQ").getString("EQSet");
                    Equalizer.Settings settings = JsonUtils.fromJson(set, Equalizer.Settings.class);
                    if(settings!=null) {
                        mEqualizer.setProperties(settings);
                    }
                    postion=0;
                    refreshSeekbar();
                    mAdapter.DataClear();
                    data.setSelect(true);
                    mAdapter.notifyDataSetChanged();
                }

            });

        }else {
            mAdapter.notifyData(data,true);
        }
    }


}
