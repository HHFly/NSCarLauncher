package com.kandi.dell.nscarlauncher.ui_portrait.home;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.driverlayer.kdos_driverServer.IECarDriver;
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

import com.kandi.dell.nscarlauncher.ui.home.fragment.HomePagerOneFragment;
import com.kandi.dell.nscarlauncher.ui.home.fragment.HomePagerTwoFragment;
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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.kandi.dell.nscarlauncher.ui.bluetooth.FlagProperty.staus;

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
    private  boolean timeFlag = true,isCenterlockOpen;
    SDBroadcastReceiver usbBroadcastReceiver;
    USBReceover usbReceover;
    BTBroadcoastReceiver btBroadcoastReceiver;
    CarMFLReceiver carMFLReceiver;
    private static WifiManager mWifiManager;//wifi
    public IECarDriver ieCarDriver;//车辆aidl服务
    private static final  String ACTION ="com.driverlayer.kdos_driverServer.RemoteService";
    private static final  String PACKAGE ="com.driverlayer.kd_vwcsserver";
    public   int backbox =0x02;//0x01为开启状态，0x02为关闭状态。
    public  int  centerlock =0x02;//0x01为开启状态，0x02为关闭状态。
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
        init_time();//时间
        bindIeCarService();
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
        setClickListener(R.id.iv_backbox);
        setClickListener(R.id.iv_cenlock);
        setClickListener(R.id.iv_window);
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
            case R.id.iv_backbox:
//                后备箱要注意只控制开锁，关锁不需要控制。
                if(FlagProperty.BCMStaus==0) {
                    setBackBox(true);
                }else {
                    ToastUtils.show( R.string.BCM未连接);

                }
                break;
            case R.id.iv_cenlock:

                if(FlagProperty.BCMStaus==0) {
                    isCenterlockOpen=!isCenterlockOpen;
                    setDoorLock(isCenterlockOpen);
                }else {
                    ToastUtils.show( R.string.BCM未连接);
                }
                break;
            case R.id.iv_window:
                OneKeyWindowOpen();
                break;
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

    //初始化 控件
    private void initGroupView() {
        initSeekBar();
    }
    //日期 时间模块初始化
    private void init_time() {
        ExecutorService timePool = Executors.newSingleThreadExecutor();  //采用线程池单一线程方式，防止被杀死
        timePool.execute(new Runnable() {
            @Override
            public void run() {

                while (timeFlag) {
                    try {
                        //延时一秒作用
                        Message msgtimedata = new Message();
                        msgtimedata.what =HandleKey.TIME;
                        myHandler.sendMessage(msgtimedata);
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
    public Handler myHandler = new Handler(){
        public void handleMessage(Message msg) {
            try {
                switch (msg.what) {
                    case HandleKey.TIME:
                        setTvText(R.id.tv_time, DateFormat.is24HourFormat( getApplicationContext())?TimeUtils.getHour(): TimeUtils.getHour_Min12());
                        setTvText(R.id.tv_date,TimeUtils.getDate()+"   "+TimeUtils.getDayOfWeek());
                        break;
                    case  HandleKey.POWER:
                        aidlService();
                        break;
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




    public  void showFragemnt(){
        setViewVisibility(R.id.frame_main,true);
    }
    /*隐藏fragemt*/
    public  void  hideFragment() {
        setViewVisibility(R.id.frame_main, false);
        hideLoadingDialog();
    }
    //车辆服务绑定
    private void bindIeCarService() {

        Intent intent = new Intent(ACTION);
        intent.setPackage(PACKAGE);

        try {
            getApplicationContext().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        }catch (Exception e){
//            LogUtils.log("绑定aidl服务失败");
        }

    }

    /*aidl服务*/
    private ServiceConnection serviceConnection =new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            ieCarDriver=IECarDriver.Stub.asInterface(service);
            init_carservice();//iecar 服务

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            ieCarDriver=null;
        }
    };
    //车辆服务模块初始化
    private void init_carservice() {
        ExecutorService timePool = Executors.newSingleThreadExecutor();  //采用线程池单一线程方式，防止被杀死
        timePool.execute(new Runnable() {
            @Override
            public void run() {

                while (true) {
                    try {
                        //延时5秒作用
                        Message msgtimedata = new Message();
                        msgtimedata.what = HandleKey.POWER;
                        myHandler.sendMessage(msgtimedata);
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
    /*车辆服务*/
    private  void  aidlService(){

        setCarWork();
        setCarMode();
        setCarPoewr();
        getCarState();
    }
    /*电量车程*/
    private   void setCarPoewr(){

        int[] power =new int[10];
        try {
            if(ieCarDriver!=null) {
                ieCarDriver.Ecoc_getGeneral_Car(power);
//                if (!tv_power.getText().equals(String.valueOf(power[10]))) {
//                    int i =power[10]%10;
//                    FlagProperty.CarPower =power[10]/10;
////                    setTvText(R.id.tv_t_power, String.valueOf(power[0])+"%");
//                    tv_t_power.setText(FlagProperty.CarPower +"%");
//                    iv_t_power.setImageResource(getPower(power[10]));
////                    setIvImage(R.id.iv_t_power, getPower(power[0]));
//                }
                FlagProperty.Speed =power[7];
                /*剩余里程*/

                    if (0 == power[1]) {
                        getTextView(R.id.tv_remainkon).setTextColor(Color.parseColor("#F03A53"));
                    } else {
                        getTextView(R.id.tv_remainkon).setTextColor(Color.parseColor("#FFFFFF"));
                    }
                    setTvText(R.id.tv_remainkon,String.valueOf(power[1]));

                }

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
    /*车辆模式*/
    public  void setCarMode(){
        int[] mode =new int[2];
        try {
            if(ieCarDriver!=null){
                ieCarDriver.GetTBoxStatus(mode);
                if(FlagProperty.CarMode!=mode[1]){
//                    if(homePagerOneFragment!=null){
//                        switch (mode[1]){
//                            case 0:
//                                homePagerOneFragment.tv_w_authorize.setText(R.string.默认状态);
//                                break;
//                            case 1:
//                                homePagerOneFragment.tv_w_authorize.setText(R.string.车辆已授权);
//                                break;
//                            case 2:
//                                homePagerOneFragment.tv_w_authorize.setText(R.string.车辆未授权);
//                                break;
//                        }
//                    }
                }
            }else {

            }

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /*车辆模式*/
    public  void setCarWork(){
        int work;
        try {
            if(ieCarDriver!=null) {
                work = ieCarDriver.getCar_WorkMode();


                    switch (work) {
                        case 0x01:
                            setTvText(R.id.tv_car_mode,R.string.Economic);
//                            homePagerOneFragment.tv_work.setText(R.string.Economic);
                            break;
                        case 0x02:
                            setTvText(R.id.tv_car_mode,R.string.Sport);

                            break;
                        case 0x03:
                            setTvText(R.id.tv_car_mode,R.string.Irascible);
                            break;
                        case 0x04:
                            setTvText(R.id.tv_car_mode,R.string.NEDC);

                            break;
                        default:
                            setTvText(R.id.tv_car_mode,R.string.Economic);

                            break;
                    }


            }

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /*整车控制状态*/
    public   void  getCarState(){
        int [] carState =new int[16];
        try {
            if(ieCarDriver!=null) {
                FlagProperty.BCMStaus= ieCarDriver.getCarState(carState);

                if (0x02 == carState[7]) {
                    isCenterlockOpen=false;
                    setIvImage(R.id.iv_cenlock, R.mipmap.ic_car_2);
                } else {
                    isCenterlockOpen=true;
                    setIvImage(R.id.iv_cenlock, R.mipmap.ic_lock_open);
                }

            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    /*设置后备箱状态*/
    public   void setBackBox(boolean isOpen){
        try {
            if(FlagProperty.Speed<5) {

                ieCarDriver.setCar_Action(0x03, 1, isOpen ? 0x01 : 0x02);
            }else {
                ToastUtils.show(R.string.safetip);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
    /*设置中控门锁*/
    public  void  setDoorLock(boolean isOpen){
        try {
            if(FlagProperty.Speed<5) {

                ieCarDriver.setCar_Action(0x01, 1, isOpen ? 0x01 : 0x02);
            }else {
                ToastUtils.show(R.string.safetip);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
    /*一件下窗*/
    public  void  OneKeyWindowOpen(){
        try {
            ieCarDriver.set_OneKeyOpenWindow(0);
        } catch (RemoteException e) {
            e.printStackTrace();
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
