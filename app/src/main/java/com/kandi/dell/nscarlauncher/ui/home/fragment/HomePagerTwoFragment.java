package com.kandi.dell.nscarlauncher.ui.home.fragment;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.kandi.dell.nscarlauncher.R;
import com.kandi.dell.nscarlauncher.app.App;
import com.kandi.dell.nscarlauncher.base.fragment.BaseFragment;
import com.kandi.dell.nscarlauncher.common.util.JumpUtils;
import com.kandi.dell.nscarlauncher.common.util.SPUtil;
import com.kandi.dell.nscarlauncher.db.dao.MusicCollectionDao;
import com.kandi.dell.nscarlauncher.ui.bluetooth.FlagProperty;
import com.kandi.dell.nscarlauncher.ui.home.HomePagerActivity;
import com.kandi.dell.nscarlauncher.ui.home.androideunm.FragmentType;

import com.kandi.dell.nscarlauncher.ui.music.fragment.MusicFragment;
import com.kandi.dell.nscarlauncher.ui.music.model.MusicModel;
import com.kandi.dell.nscarlauncher.widget.MarqueTextView;
import com.kandi.dell.nscarlauncher.widget.PlayControllView;

import java.io.File;

public class HomePagerTwoFragment extends BaseFragment {
    private HomePagerActivity homePagerActivity;
    // 播发控制
    public  PlayControllView musicPaly;
    public static  int backbox =0x02;//0x01为开启状态，0x02为关闭状态。
    public static int  centerlock =0x02;//0x01为开启状态，0x02为关闭状态。
    private static ImageView iv_backbox,iv_cenlock;
    public static boolean onceLoad = true;

    public  MarqueTextView music_name;

    public   boolean isBackboxOpen,isCenterlockOpen;
    public String PicIndex ="picindex";
    @Override
    public int getContentResId() {
        return R.layout.fragment_home2;
    }

    public void setHomePagerActivity(HomePagerActivity homePagerActivity) {
        this.homePagerActivity = homePagerActivity;
    }



    @Override
    public void findView() {
        musicPaly=getView(R.id.music_playcontroll);
        iv_backbox=getView(R.id.iv_backbox);
        iv_cenlock =getView(R.id.iv_cenlock);

        music_name=getView(R.id.music_name);

    }
    @Override
    public void setListener() {
        setClickListener(R.id.rl_phone);
        setClickListener(R.id.rl_set);
        setClickListener(R.id.rl_navigation);
        setClickListener(R.id.rl_carcontroll);
        setClickListener(R.id.music);
        setClickListener(R.id.iv_backbox);
        setClickListener(R.id.iv_cenlock);
        setClickListener(R.id.iv_window);
        setClickListener(R.id.nav_home);
        setClickListener(R.id.nav_company);
        setClickListener(R.id.iv_phone_logo);
        setPalyListen();
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){

            case R.id.iv_backbox:
//                后备箱要注意只控制开锁，关锁不需要控制。
                if(FlagProperty.BCMStaus==0) {
                    HomePagerActivity.setBackBox(true);
                }else {
                    Toast.makeText(getActivity(), R.string.BCM未连接, Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.iv_cenlock:
//
                if(FlagProperty.BCMStaus==0) {
                    isCenterlockOpen=!isCenterlockOpen;
                    HomePagerActivity.setDoorLock(isCenterlockOpen);
                }else {
                    Toast.makeText(getActivity(), R.string.BCM未连接, Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.iv_window:
                HomePagerActivity.OneKeyWindowOpen();
                break;
            case R.id.rl_phone:
                HomePagerActivity.isShowPhoneAnim=true;
                homePagerActivity.jumpFragment(FragmentType.PHONE);
//                if(homePagerActivity!=null){
//                    homePagerActivity.jumpFragment(FragmentType.PHONE);
//                }
                break;
            case  R.id.iv_phone_logo:
                HomePagerActivity.isShowPhoneAnim=true;
                homePagerActivity.jumpFragment(FragmentType.PHONE);
                break;
            case R.id.rl_set:
                homePagerActivity.jumpFragment(FragmentType.SET);
                break;
            case R.id.rl_app:
                homePagerActivity.jumpFragment(FragmentType.APPLICATION);
                break;
            case R.id.rl_carcontroll:
                if(FlagProperty.BCMStaus==0) {
                    JumpUtils.actAPK(getActivity(), FragmentType.CARCONTROLL);
                    JumpUtils.act60In(getActivity());
                }else {
                    Toast.makeText(getActivity(), R.string.BCM未连接, Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.rl_navigation:
                openNavi();
                break;
            case R.id.music:
                homePagerActivity.jumpFragment(FragmentType.MUSIC);
                break;
            case R.id.nav_home:
                NavBroasd(0);
                break;
            case R.id.nav_company:
                NavBroasd(1);
                break;
        }
    }
    /*打开高德*/
    public void  openNavi(){
        PackageManager packageManager = getActivity().getPackageManager();

        String packageName = "com.autonavi.amapauto";//高德地图
        Intent launchIntentForPackage = packageManager.getLaunchIntentForPackage(packageName);
        if (launchIntentForPackage != null) {
            startActivity(launchIntentForPackage);
            JumpUtils.act80In(getActivity());
        }
        else {
            Toast.makeText(getContext(), R.string.未安装该应用, Toast.LENGTH_SHORT).show();
        }
    }

    private void setPalyListen(){
        musicPaly.setOnItemClickListener(new PlayControllView.OnItemClickListener() {
            @Override
            public void onClickLeft() {
                App.get().PauseServiceFMBTMUSic();
                isMusicFragment();
                if(homePagerActivity.getDialogLocalMusic().data.size()>0) {
                    if (  homePagerActivity.getMusicFragment().flag_play) {

                        musicPaly.center.performClick();
                        MusicPaly( homePagerActivity.getMusicFragment().flag_play);
                    }
                    if (  homePagerActivity.getMusicFragment().music_model == 2) { // 单曲循环模式不变换音乐图片
                        if( homePagerActivity.getMusicFragment().circle_image!=null)
                            homePagerActivity.getMusicFragment().circle_image.resetRoatate();
                    } else { // 其他模式
                        // circle_image.nextRoatate(getPlayDrawable(getDrawableId(DIRECTION_NEXT)));
                    }
                    musicPaly.center.performClick();
                    homePagerActivity.getMusicFragment().recoveryLast = false;
                    MusicModel.getPrevMusic(getContext(), homePagerActivity.getMusicFragment().music_model);
                }

//                HomePagerActivity.musicFragment.PreMusic();
            }

            @Override
            public void onClickCenter(boolean isPlay) {
                isMusicFragment();
                MusicPaly(isPlay);
            }

            @Override
            public void onClickRight() {
                App.get().PauseServiceFMBTMUSic();
                isMusicFragment();
//                HomePagerActivity.musicFragment.NextMusic();
                if(homePagerActivity.getDialogLocalMusic().data.size()>0) {
                    if ( homePagerActivity.getMusicFragment().flag_play) {
                        musicPaly.center.performClick();
                    }
                    if (  homePagerActivity.getMusicFragment().music_model == 2) { // 单曲循环模式不变换音乐图片
                        if( homePagerActivity.getMusicFragment().circle_image!=null)
                            homePagerActivity.getMusicFragment().circle_image.resetRoatate();
                    } else { // 其他模式
                        // circle_image.nextRoatate(getPlayDrawable(getDrawableId(DIRECTION_NEXT)));
                    }
                    musicPaly.center.performClick();
                    homePagerActivity.getMusicFragment().recoveryLast = false;
                    MusicModel.getNextMusic(getContext(),   homePagerActivity.getMusicFragment().music_model);
                }

            }
        });
    }

    private void  MusicPaly(boolean isPlay){
        if(isPlay){
            App.get().PauseServiceFMBTMUSic();
            if(onceLoad){
                getMusicData();
                String musicpath = SPUtil.getInstance(getContext(),MusicFragment.MUSICPATH).getString(MusicFragment.MUSICPATH);
                if(musicpath !=null && !musicpath.equals("")){
                    if(new File(musicpath).exists()){
                        int music_id = SPUtil.getInstance(getContext(),MusicFragment.MUSICID).getInt(MusicFragment.MUSICID,homePagerActivity.getDialogLocalMusic().musicID);
                        if(! homePagerActivity.getMusicFragment().flag_play){
                            homePagerActivity.getMusicFragment().recoveryLast = true;
                        }
                        homePagerActivity.getDialogLocalMusic().musicID = music_id;
                    }
                }
                onceLoad = false;
            }
            homePagerActivity.getMusicFragment().musicPlay(getActivity());

        }else {

            homePagerActivity.getMusicFragment().musicPause(getActivity());
        }
        setPlayControll(isPlay,2);
    }
    public void setPlayControll(boolean isPlay,int mode){
        homePagerActivity.getHomePagerOneFragment().btPaly.setPlay(false);
        homePagerActivity.getHomePagerOneFragment().fmPaly.setPlay(false);
        musicPaly.setPlay(false);

        switch (mode){
            case 1:

                break;
            case 2:
                musicPaly.setPlay(isPlay);
                break;
            default:
        }
    }
    private  void isMusicFragment(){
        if(FragmentType.MUSIC!=homePagerActivity.mCurFragment.getmType()){
            homePagerActivity.switchFragmenthide( homePagerActivity.getMusicFragment());
        }
    }


    @Override
    public void initView() {
//        MusicFragment.homePagerActivity.getDialogLocalMusic().ScanMusic(getContext(),false);
//        MusicFragment.homePagerActivity.getDialogLocalMusic().ScanVideoMusic(getContext(),false,1);
    }


    private static  void setBackbox(){
        switch (backbox){
            case 0x01:
                iv_backbox.setImageResource(R.mipmap.ic_car_1);
                break;
            case 0x02:
                iv_backbox.setImageResource(R.mipmap.ic_car_1_close);
                break;

        }
    }
    private static  void  setIv_cenlock(){
        switch (centerlock){
            case 0x01:
                iv_cenlock.setImageResource(R.mipmap.ic_car_2);
                break;
            case 0x02:
                iv_cenlock.setImageResource(R.mipmap.ic_car_1_white);
                break;

        }
    }
    public static final  int MUSIC_CLOSE =1;
    public static final  int MUSIC_OPEN =2;
    public static final  int BACKBOX =3;
    public static final  int CENTERLOCK=4;
    public  Handler myHandler = new Handler() {

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MUSIC_CLOSE:
                    musicPaly.setPlay(false);
                    break;

                case MUSIC_OPEN:
                    try{
                        music_name.setText((homePagerActivity.getDialogLocalMusic().data.get(homePagerActivity.getDialogLocalMusic().musicID)).title);
                    }catch (Exception e){
                        music_name.setText(App.get().getString(R.string.本地音乐));
                    }
                    musicPaly.setPlay(true);
                    break;
                case  BACKBOX:
                    setBackbox();
                    break;
                case  CENTERLOCK:
                    setIv_cenlock();
                    break;
                default:
                    break;
            }
        };
    };

    /*高德地图导航 0：回家
1：回公司*/
    private void NavBroasd(int type){
        Intent intent = new Intent();
        intent.setAction("AUTONAVI_STANDARD_BROADCAST_RECV");
        intent.putExtra("KEY_TYPE", 10070);
        intent.putExtra("EXTRA_TYPE", type);
        getActivity().sendBroadcast(intent);
    }

    private  void getMusicData(){
        homePagerActivity.getDialogLocalMusic().ColData = MusicCollectionDao.getAllFav(getContext());
        switch (SPUtil.getInstance(getContext(),MusicFragment.MUSICDATAMODE).getInt(MusicFragment.MUSICDATAMODE,0)){
            case 3:
                homePagerActivity.getDialogLocalMusic().transport(homePagerActivity.getDialogLocalMusic().data, homePagerActivity.getDialogLocalMusic().ColData);
                break;
            case 2:
                homePagerActivity.getDialogLocalMusic().transport(homePagerActivity.getDialogLocalMusic().data, homePagerActivity.getDialogLocalMusic().USBData);
                break;
            default:
                homePagerActivity.getDialogLocalMusic().transport(homePagerActivity.getDialogLocalMusic().data, homePagerActivity.getDialogLocalMusic().SDData);
                break;
        }
    }
}
