package com.kandi.dell.nscarlauncher.ui_portrait.home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
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
import com.kandi.dell.nscarlauncher.common.util.ToastUtils;

import com.kandi.dell.nscarlauncher.ui.bluetooth.FlagProperty;
import com.kandi.dell.nscarlauncher.ui.home.androideunm.FragmentType;
import com.kandi.dell.nscarlauncher.ui.home.androideunm.HandleKey;

import com.kandi.dell.nscarlauncher.ui_portrait.application.AppFragment;
import com.kandi.dell.nscarlauncher.ui_portrait.bluetooth.btmusic.BTMusicFragment;
import com.kandi.dell.nscarlauncher.ui_portrait.bluetooth.phone.PhoneFragment;
import com.kandi.dell.nscarlauncher.ui_portrait.bluetooth.phone.service.PhoneInfoService;
import com.kandi.dell.nscarlauncher.ui_portrait.fm.FMFragment;
import com.kandi.dell.nscarlauncher.ui_portrait.home.receiver.BTBroadcoastReceiver;
import com.kandi.dell.nscarlauncher.ui_portrait.home.receiver.CarMFLReceiver;
import com.kandi.dell.nscarlauncher.ui_portrait.home.receiver.SDBroadcastReceiver;
import com.kandi.dell.nscarlauncher.ui_portrait.home.receiver.USBReceover;
import com.kandi.dell.nscarlauncher.ui_portrait.music.dialog.DialogLocalMusic;
import com.kandi.dell.nscarlauncher.ui_portrait.music.fragment.MusicFragment;
import com.kandi.dell.nscarlauncher.ui_portrait.music.service.PlayerService;
import com.kandi.dell.nscarlauncher.ui_portrait.music.service.ScanService;
import com.kandi.dell.nscarlauncher.ui_portrait.airctrl.AirCtrlFragment;
import com.kandi.dell.nscarlauncher.ui_portrait.carctrl.CarCtrlFragment;
import com.kandi.dell.nscarlauncher.ui_portrait.ems.EmsFragment;
import com.kandi.dell.nscarlauncher.ui_portrait.video.VideoFragment;
import com.kandi.dell.nscarlauncher.ui_portrait.video.dialog.DialogVideo;
import com.kandi.dell.nscarlauncher.widget.PlayControllFMView;
import com.kandi.dell.nscarlauncher.widget.PlayControllView;
import com.kandi.dell.nscarlauncher.ui_portrait.setting.SetFragment;

public class HomePagerActivity extends BaseActivity {
    public ImageView gramophoneView;
    MusicFragment musicFragment;
    PhoneFragment phoneFragment;
    BTMusicFragment btMusicFragment;
    VideoFragment videoFragment;
    FMFragment fmFragment;
    AppFragment appFragment;
    public SeekBar music_progress_bar;//音乐进度条
    DialogLocalMusic dialogLocalMusicD;//音乐列表弹框
    DialogVideo dialogVideo; //视频列表弹框
    ScanService scanService;//本地数据扫描服务
    PhoneInfoService phoneInfoService;//蓝牙电话记录服务
    public CarCtrlFragment mCarCtrlFragment;
    public AirCtrlFragment mAirCtrlFragment;
    public EmsFragment mEmsFragment;
    public SetFragment mSetFragment;

    public BaseFragment mCurFragment;//当前页

    public PlayControllView btPaly;
    public PlayControllFMView fmPaly ;

    SDBroadcastReceiver usbBroadcastReceiver;
    USBReceover usbReceover;
    BTBroadcoastReceiver btBroadcoastReceiver;
    CarMFLReceiver carMFLReceiver;
    private static WifiManager mWifiManager;//wifi
    @SuppressLint("MissingSuperCall")
    @Override
    protected void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(usbBroadcastReceiver==null){
            usbBroadcastReceiver=new SDBroadcastReceiver(this);
        }
        if(usbReceover==null){
            usbReceover =new USBReceover(this);
        }
        if(btBroadcoastReceiver==null){
            btBroadcoastReceiver =new BTBroadcoastReceiver(this);
        }
        if(carMFLReceiver==null){
            carMFLReceiver=new CarMFLReceiver(this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(usbBroadcastReceiver!=null){
            this.unregisterReceiver(usbBroadcastReceiver);
        }
        if(usbReceover!=null){
            this.unregisterReceiver(usbReceover);
        }
        if(btBroadcoastReceiver!=null){
            this.unregisterReceiver(btBroadcoastReceiver);
        }
        if(carMFLReceiver!=null){
            this.unregisterReceiver(carMFLReceiver);
        }
    }

    @Override
    public int getContentViewResId() {
        return R.layout.activity_portrait;
    }

    @Override
    public void findView() {
        gramophoneView=getView(R.id.iv_music);
        music_progress_bar=getView(R.id.music_progress_bar);
        fmPaly=getView(R.id.fm_playcontroll);
        btPaly= getView(R.id.bt_playcontroll);
    }

    @Override
    public void initView() {
        createFragment();
        initGroupView();
        initNetwork();//网络
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
        setPalyListen();
    }
    private void setPalyListen(){
        //fm
        fmPaly.setOnItemClickListener(new PlayControllFMView.OnItemClickListener() {
            @Override
            public void onClickLeft() {
                getFmFragment().leftFm(getFmFragment().channel);

            }

            @Override
            public void onClickCenter(boolean isPlay) {
                getFmFragment().play();

            }

            @Override
            public void onClickRight() {
                getFmFragment().rightFm(getFmFragment().channel);
            }
        });

        //蓝牙音乐
        btPaly.setOnItemClickListener(new PlayControllView.OnItemClickListener() {
            @Override
            public void onClickLeft() {

                if(!FlagProperty.flag_bluetooth){
                    ToastUtils.show( R.string.蓝牙未连接);
                }else {
                    getBtMusicFragment().musicBack();

                }
            }

            @Override
            public void onClickCenter(boolean isPlay) {

                if(!FlagProperty.flag_bluetooth){
                    btPaly.isPlay=!isPlay;
                    ToastUtils.show( R.string.蓝牙未连接);

                    return;
                }else {

                    getBtMusicFragment().play();
                }
            }

            @Override
            public void onClickRight() {

                if(!FlagProperty.flag_bluetooth){
                    ToastUtils.show( R.string.蓝牙未连接);
                }else {
                    getBtMusicFragment().musicNext();
                }
            }
        });


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
                jumpFragment(FragmentType.AIRCONTROLL);
                break;
            case R.id.item_app:
                jumpFragment(FragmentType.APPLICATION);
                break;
            case R.id.item_phone:
                jumpFragment(FragmentType.PHONE);
                break;
            case R.id.item_power:
                jumpFragment(FragmentType.EMS);
                break;
            case R.id.item_set:
                jumpFragment(FragmentType.SET);
                break;
            case R.id.item_fm:
                jumpFragment(FragmentType.FM);
                break;
            case R.id.item_btmusic:
                jumpFragment(FragmentType.BTMUSIC);
                break;
            case R.id.item_carcontroll:
                jumpFragment(FragmentType.CARCONTROLL);
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
        getBtMusicFragment();
        getFmFragment();
        getDialogLocalMusicD();
        getDialogVideo();
        getScanService();
        getPhoneInfoService();
        getVideoFragment();
        getAppFragment();
        getAirCtrlFragment();
        getCarCtrlFragment();
        getEmsFragment();
        getSetFragment();
        getScanService().ScanAllDaTa(this);
        getScanService().addObserver(getDialogLocalMusicD());
        getScanService().addObserver(getDialogVideo());

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
//                        getView(R.id.ctl_iv_center).setBackgroundResource(R.mipmap.ic_music_play);
                        btPaly.setPlay(true);
                        break;
                    case HandleKey.BTMUSICCOLSE:
//                        getView(R.id.ctl_iv_center).setBackgroundResource(R.mipmap.ic_music_home_stop);
                        btPaly.setPlay(false);
                        break;
                    case HandleKey.FMOPEN:
//                        getView(R.id.ctl_fm_center).setBackgroundResource(R.mipmap.ic_fm_play);
                        fmPaly.setPlay(true);
                        break;
                    case HandleKey.FMCOLSE:
//                        getView(R.id.ctl_fm_center).setBackgroundResource(R.mipmap.ic_off);
                        fmPaly.setPlay(false);
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
                    switchFragment(getSetFragment());
                    break;
                case FragmentType.CARCONTROLL:
                    switchFragment(getCarCtrlFragment());
                    break;
                case FragmentType.AIRCONTROLL:
                    switchFragment(getAirCtrlFragment());
                    break;
                case FragmentType.EMS:
                    switchFragment(getEmsFragment());
                    break;
                case FragmentType.PHONE:
                    switchFragment(getPhoneFragment());
                    break;
                case FragmentType.BTMUSIC:
                    switchFragment(getBtMusicFragment());
                    break;
                case FragmentType.APPLICATION:
                    switchFragment(getAppFragment());
                    break;
                case FragmentType.VIDEO:
                    switchFragment(getVideoFragment());
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

    // 初始化无线网络信息
    private void initNetwork() {
        if (mWifiManager == null) {
            mWifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        }
    }

    // 设置当前所连接wifi的强度
    public static void setWifiLevel() {
//        Log.i("testtest","=========="+mWifiManager);
//        WifiInfo connectionInfo = mWifiManager.getConnectionInfo();
//        if (connectionInfo != null) {
//            int signalLevel = WifiManager.calculateSignalLevel(connectionInfo.getRssi(), 5);
//            mIvWifi.setImageResource(getSignalIntensity(signalLevel));
//
//        } else {
//            mIvWifi.setImageResource(R.mipmap.wifi_off);
//
//        }
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
        if (phoneInfoService == null) {
            phoneInfoService = new PhoneInfoService();
        }
        return phoneInfoService;
    }
    public CarCtrlFragment getCarCtrlFragment(){
        if(mCarCtrlFragment == null){
            mCarCtrlFragment = new CarCtrlFragment();
            mCarCtrlFragment.setHomePagerActivity(this);
        }
        return mCarCtrlFragment;
    }

    public AirCtrlFragment getAirCtrlFragment(){
        if(mAirCtrlFragment == null){
            mAirCtrlFragment = new AirCtrlFragment();
            mAirCtrlFragment.setHomePagerActivity(this);
        }
        return mAirCtrlFragment;
    }

    public EmsFragment getEmsFragment(){
        if(mEmsFragment == null){
            mEmsFragment = new EmsFragment();
            mEmsFragment.setHomePagerActivity(this);
        }
        return mEmsFragment;

    }

    public BTMusicFragment getBtMusicFragment() {
        if(btMusicFragment==null){
            btMusicFragment=new BTMusicFragment();
        }
        return btMusicFragment;
    }


    public FMFragment getFmFragment() {
        if(fmFragment==null){
            fmFragment=new FMFragment();
        }
        return fmFragment;
    }

    public AppFragment getAppFragment() {
        if(appFragment==null){
            appFragment=new AppFragment();
        }
        return appFragment;
    }

    public VideoFragment getVideoFragment() {
        if(videoFragment==null){
            videoFragment=new VideoFragment();
        }
        return videoFragment;
    }

    public SetFragment getSetFragment() {
        if(mSetFragment == null){
            mSetFragment = new SetFragment();
            mSetFragment.setHomePagerActivity(this);
        }
        return mSetFragment;
    }

    public DialogLocalMusic getDialogLocalMusicD() {
        if(dialogLocalMusicD==null){
            dialogLocalMusicD=new DialogLocalMusic(this);
        }
        return dialogLocalMusicD;
    }

    public DialogVideo getDialogVideo() {
        if(dialogVideo==null){
            dialogVideo=new DialogVideo(this);
        }
        return dialogVideo;
    }

    public ScanService getScanService() {
        if(scanService==null){
            scanService=new ScanService();
        }
        return scanService;
    }
}
