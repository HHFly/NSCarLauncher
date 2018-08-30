package com.example.dell.nscarlauncher.ui.setting.activity;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.CompoundButton;

import com.example.dell.nscarlauncher.R;
import com.example.dell.nscarlauncher.base.Activity.BaseActivity;
import com.example.dell.nscarlauncher.ui.music.adapter.MusicAdapter;
import com.example.dell.nscarlauncher.ui.setting.adapter.WifiAdpter;
import com.example.dell.nscarlauncher.ui.setting.model.WifiInfo;

import java.util.List;

public class WifiActivity extends BaseActivity implements CompoundButton.OnCheckedChangeListener {
    private WifiAdpter mAdapter;
    @Override
    public int getContentViewResId() {
        return R.layout.activity_wifiset;
    }

    @Override
    public void initView() {

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()){
            case R.id.isopen:
                if(isChecked){
                    setViewVisibility(R.id.recyclerView_wifi,true);
                }else {
                    setViewVisibility(R.id.recyclerView_wifi,false);
                }
                break;

            default:
                break;
        }
    }
    /**
     * 初始化adapter
     *
     *
     */
    private void initRvAdapter( List<WifiInfo> data) {
        if (mAdapter == null) {
            RecyclerView rv = getView(R.id.recyclerView_music);
            mAdapter =new WifiAdpter(data);

            if (rv != null) {
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getBaseContext());
                rv.setLayoutManager(linearLayoutManager);
                rv.setAdapter(mAdapter);
            }
            mAdapter.setOnItemClickListener(new WifiAdpter.OnItemClickListener() {


                @Override
                public void onClickMusic(WifiInfo data, int Pos) {

                }
            });

        }else {
            mAdapter.notifyData(data,true);
        }

    }
}
