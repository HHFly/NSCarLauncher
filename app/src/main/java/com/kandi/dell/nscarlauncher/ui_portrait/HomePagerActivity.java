package com.kandi.dell.nscarlauncher.ui_portrait;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.kandi.dell.nscarlauncher.R;
import com.kandi.dell.nscarlauncher.app.App;
import com.kandi.dell.nscarlauncher.base.Activity.BaseActivity;
import com.kandi.dell.nscarlauncher.base.fragment.BaseFragment;
import com.kandi.dell.nscarlauncher.common.util.FragmentUtils;
import com.kandi.dell.nscarlauncher.common.util.IsHomeUtils;
import com.kandi.dell.nscarlauncher.common.util.TimeUtils;
import com.kandi.dell.nscarlauncher.ui.bluetooth.FlagProperty;
import com.kandi.dell.nscarlauncher.ui.home.androideunm.FragmentType;
import com.kandi.dell.nscarlauncher.ui.home.androideunm.HandleKey;

import com.kandi.dell.nscarlauncher.ui_portrait.bluetooth.phone.PhoneFragment;
import com.kandi.dell.nscarlauncher.ui_portrait.bluetooth.phone.service.PhoneInfoService;
import com.kandi.dell.nscarlauncher.ui_portrait.music.dialog.DialogLocalMusic;
import com.kandi.dell.nscarlauncher.ui_portrait.music.fragment.MusicFragment;
import com.kandi.dell.nscarlauncher.ui_portrait.music.service.PlayerService;
import com.kandi.dell.nscarlauncher.ui_portrait.music.service.ScanService;

public class HomePagerActivity extends BaseActivity {
    public ImageView gramophoneView;
    MusicFragment musicFragment;
    PhoneFragment phoneFragment;
    public SeekBar music_progress_bar;//音乐进度条
    DialogLocalMusic dialogLocalMusicD;//音乐列表弹框
    ScanService scanService;//本地数据扫描服务
    PhoneInfoService phoneInfoService;//蓝牙电话记录服务
    public BaseFragment mCurFragment;//当前页
    @SuppressLint("MissingSuperCall")
    @Override
    protected void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
    }

    @Override
    public int getContentViewResId() {
        return R.layout.activity_portrait;
    }

    @Override
    public void findView() {
        gramophoneView=getView(R.id.iv_music);
        music_progress_bar=getView(R.id.music_progress_bar
        );
    }

    @Override
    public void initView() {
        createFragment();
        initGroupView();

    }



    @Override
    public void setListener() {
        setClickListener(R.id.iv_home_music_center);
        setClickListener(R.id.iv_home_music_left);
        setClickListener(R.id.iv_home_music_right);
        setClickListener(R.id.iv_music);
        setClickListener(R.id.item_air);
        setClickListener(R.id.item_app);
        setClickListener(R.id.item_phone);
        setClickListener(R.id.item_power);
        setClickListener(R.id.item_set);
        setClickListener(R.id.item_fm);
        setClickListener(R.id.item_btmusic);
        setClickListener(R.id.item_carcontroll);
    }

    @Override
    public void onClick(View v) {
        if (TimeUtils.isFastDoubleClick()) {
            return;
        }
        switch (v.getId()){
            case R.id.iv_home_music_center:
                getMusicFragment().play();
                break;
            case R.id.iv_home_music_left:
                getMusicFragment().PreMusic();
                break;
            case R.id.iv_home_music_right:
                getMusicFragment().NextMusic();
                break;
            case R.id.iv_music:
                jumpFragment(FragmentType.MUSIC);
                break;
            case R.id.item_air:
                break;
            case R.id.item_app:
                break;
            case R.id.item_phone:
                break;
            case R.id.item_power:
                break;
            case R.id.item_set:
                jumpFragment(FragmentType.SET);
                break;
            case R.id.item_fm:
                break;
            case R.id.item_btmusic:
                break;
            case R.id.item_carcontroll:
                break;
        }
    }
    /**
     * 初始化fragment
     */
    private void createFragment() {
        App.get().setmCurActivity(this);
        getMusicFragment();
        getPhoneFragment();
        getDialogLocalMusicD();
        getScanService();
        getPhoneInfoService();
    }
    //初始化 控件
    private void initGroupView() {
        initSeekBar();
    }
    public Handler myHandler = new Handler(){
        public void handleMessage(Message msg) {
            try {
                switch (msg.what) {
                    case HandleKey.SHOW:
                        showFragemnt();

                        break;
                    case HandleKey.MUSICOPEN:
                        getView(R.id.iv_home_music_center).setBackgroundResource(R.mipmap.ic_home_music_play);
                        break;
                    case HandleKey.MUSICCOLSE:
                        getView(R.id.iv_home_music_center).setBackgroundResource(R.mipmap.ic_home_music_puase);
                        break;
                    case HandleKey.BTMUSICOPEN:
                        getView(R.id.ctl_iv_center).setBackgroundResource(R.mipmap.ic_music_play);
                        break;
                    case HandleKey.BTMUSICCOLSE:
                        getView(R.id.ctl_iv_center).setBackgroundResource(R.mipmap.ic_music_home_stop);
                        break;
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        };
    };
    public void jumpFragment(@FragmentType int type ){
        if(IsHomeUtils.isForeground(this,"HomePagerActivity"))
            switch (type) {
                case FragmentType.MUSIC:
                    switchFragment(getMusicFragment());
                    break;
                case FragmentType.SET:
//                    switchFragment(setFragment);
                    break;

            }
    }
    /**
     * 选择Fragment
     *
     * @param fragment
     */

    private void switchFragment(BaseFragment fragment) {

        mCurFragment = FragmentUtils.selectFragment(this, mCurFragment, fragment, R.id.frame_main);

        mCurFragment.Resume();

        myHandler.sendMessage(myHandler.obtainMessage(HandleKey.SHOW));
    }

    // 设置歌曲信息
    public  void setMusicInfo(String songname, String singer) {
        setTvText(R.id.tv_music_title,songname);
        setTvText(R.id.tv_music_singer,singer);

    }
    /*初始化进度条*/
    private void initSeekBar(){
        music_progress_bar.setProgress(0);
        music_progress_bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if( App.get().getCurActivity().getDialogLocalMusicD().data.size()>0&& App.get().getCurActivity().getDialogLocalMusicD().Playnow!=null) {
                    int totaltime = (int) Math.ceil( App.get().getCurActivity().getDialogLocalMusicD().Playnow.duration);
                    int num = (int) Math.ceil(Math.round((float) getMusicFragment().Progress / 100.0 * totaltime));


                    Intent i = new Intent(getActivity(), PlayerService.class);
                    i.putExtra("progress", num);
                    i.putExtra("MSG", FlagProperty.PROGRESS_CHANGE);
                    getActivity().startService(i);
                    getMusicFragment().flag_drag = false;
                    getMusicFragment().flag_first = true;
                }else {
                    music_progress_bar.setProgress(0);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                getMusicFragment().flag_drag = true;

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                getMusicFragment().Progress = progress;

            }
        });

    }




    public  void showFragemnt(){
        setViewVisibility(R.id.frame_main,true);
    }
    /*隐藏fragemt*/
    public  void  hideFragment() {
        setViewVisibility(R.id.frame_main, false);
        hideLoadingDialog();
    }

    public MusicFragment getMusicFragment() {
        if(musicFragment==null){
            musicFragment=new MusicFragment();
        }
        return musicFragment;
    }

    public PhoneFragment getPhoneFragment() {
        if(phoneFragment==null){
            phoneFragment=new PhoneFragment();
        }
        return phoneFragment;
    }
    public PhoneInfoService getPhoneInfoService() {
        if(phoneInfoService==null){
            phoneInfoService=new PhoneInfoService();
        }
        return phoneInfoService;
    }

    public DialogLocalMusic getDialogLocalMusicD() {
        if(dialogLocalMusicD==null){
            dialogLocalMusicD=new DialogLocalMusic(this);
        }
        return dialogLocalMusicD;
    }
    public ScanService getScanService() {
        if(scanService==null){
            scanService=new ScanService();
        }
        return scanService;
    }
}
