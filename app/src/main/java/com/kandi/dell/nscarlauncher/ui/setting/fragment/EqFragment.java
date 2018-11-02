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
        setClickListener(R.id.tv_set_eq_close);
    }

    @Override
    public void initView() {
        setEqualize();
        /*初始化数据*/
        initEqList();
    }


    private void initEqList() {
        for (int i=0; i<mEqualizer.getNumberOfPresets();i++) {
            EqData data =new EqData();

            data.setPosition((short) i);
            data.setPreset(getName(mEqualizer.getPresetName((short) i)));
            data.setSelect("Normal".equals(mEqualizer.getPresetName((short) i)));
            mData.add(data);
        }
            initRvAdapter(mData);
    }
private String getName(String name){
        switch (name){
            case "Normal":
                return getString(R.string.普通);
              
            case "Classical":
                return getString(R.string.古典);
            
            case "Dance":
                return getString(R.string.舞曲);
            
            case "Flat":
                return getString(R.string.普通);
           
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
            case R.id.tv_set_eq_close:
                SetFragment.hideFragment();

               
        }
    }

    private void setEqualize() {

        mEqualizer =App.get().getmEqualizer();
        short bands = mEqualizer.getNumberOfBands();


        for (short i = 0; i < bands; i++) {
            final short band = i;

            EqSeekBarView eqSeekBarView =new EqSeekBarView(getContext(),band,mEqualizer);

            mView.add(eqSeekBarView);
            mLayout.addView(eqSeekBarView);
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

                        mEqualizer.usePreset(data.getPosition());
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
