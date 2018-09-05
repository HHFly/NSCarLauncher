package com.example.dell.nscarlauncher.ui.video;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.dell.nscarlauncher.R;
import com.example.dell.nscarlauncher.base.fragment.BaseFragment;
import com.example.dell.nscarlauncher.ui.bluetooth.FlagProperty;
import com.example.dell.nscarlauncher.ui.home.HomePagerActivity;
import com.example.dell.nscarlauncher.ui.home.androideunm.FragmentType;
import com.example.dell.nscarlauncher.ui.music.DialogLocalMusic;
import com.example.dell.nscarlauncher.ui.music.Service.PlayerService;
import com.example.dell.nscarlauncher.ui.music.adapter.MusicAdapter;
import com.example.dell.nscarlauncher.ui.music.model.Mp3Info;

import java.util.List;

import fm.jiecao.jcvideoplayer_lib.JCFullScreenActivity;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard;

public class VideoFragment extends BaseFragment {
    private VideoAdapter mAdapter;
    private List<Mp3Info> mData;
   public static DialogLocalMusic dialogLocalMusic;
    private final static int  VIEWFRESH =1;

    @Override
    public void setmType(int mType) {
        super.setmType(FragmentType.VIDEO);
    }

    @Override
    public int getContentResId() {
        return R.layout.fragment_video;
    }

    @Override
    public void findView() {

    }

    @Override
    public void setListener() {
        setClickListener(R.id.video_local_1);
        setClickListener(R.id.video_local_2);
        setClickListener(R.id.video_local_return);
    }

    @Override
    public void initView() {
        dialogLocalMusic  = new DialogLocalMusic(new DialogLocalMusic.ThreadCallback() {
           @Override
           public void threadEndLisener() {

           }

           @Override
           public void videoEndListener() {
               myHandler.sendMessage(myHandler.obtainMessage(VIEWFRESH));

           }
       });
       dialogLocalMusic.ScanVideo(getContext(),false);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.video_local_1:
                changeData(1);
                break;
            case R.id.video_local_2:
                changeData(2);
                break;
            case R.id.video_local_return:
                HomePagerActivity.hideFragment();
                break;
        }
    }

    /*初始化本地音乐数据*/
    private  void getMusicData(){
        mData =DialogLocalMusic.SDVideoData;

        selectMode(1);
        if(mData==null||mData.size()==0){
            mData =DialogLocalMusic.USBVideoData;
            if(mData!=null||mData.size()!=0) {

                selectMode(2);
            }
        }

        initRvAdapter(mData);

    }
    private void  changeData(int mode){
        mData =mode==1?DialogLocalMusic.SDVideoData:DialogLocalMusic.USBVideoData;
        selectMode(mode);
        initRvAdapter(mData);
    }
    public Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {

                case VIEWFRESH:
                    getMusicData();
                    break;
                default:
                    break;
            }
        };
    };
    private void  selectMode(int type){
        setViewSelected(R.id.video_local_1,false);
        setViewSelected(R.id.video_local_2,false);
        switch (type){
            case 1:
                setViewSelected(R.id.video_local_1,true);
                break;
            case 2 :
                setViewSelected(R.id.video_local_2,true);
                break;
        }
    }
    /**
     * 初始化adapter
     *
     *
     */
    private void initRvAdapter( List<Mp3Info> data) {
        if (mAdapter == null) {
            RecyclerView rv = getView(R.id.recyclerView_videolocoal);
            mAdapter =new VideoAdapter(data);

            if (rv != null) {
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
                rv.setLayoutManager(linearLayoutManager);
                rv.setAdapter(mAdapter);
            }
            mAdapter.setOnItemClickListener(new VideoAdapter.OnItemClickListener() {

                @Override
                public void onClickMusic(Mp3Info data, int Pos) {
                    JCFullScreenActivity.startActivity(getContext(),
                            data.url,
                            JCVideoPlayerStandard.class,
                            data.title);

                }


            });

        }else {
            mAdapter.notifyData(data,true);
        }
//        setViewVisibilityGone(R.id.item_music_null,data==null||data.size()==0);

        setViewVisibilityGone(R.id.rl_video_local_nodata,data==null||data.size()==0);
    }
}
