package com.example.dell.nscarlauncher.ui.home;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.Handler;
import android.os.IKdBtService;
import android.os.Message;
import android.os.RemoteException;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dell.nscarlauncher.R;
import com.example.dell.nscarlauncher.app.App;
import com.example.dell.nscarlauncher.base.Activity.BaseActivity;
import com.example.dell.nscarlauncher.common.util.FragmentUtils;
import com.example.dell.nscarlauncher.common.util.JumpUtils;
import com.example.dell.nscarlauncher.ui.application.AppFragment;
import com.example.dell.nscarlauncher.ui.bluetooth.BTMusicFragment;
import com.example.dell.nscarlauncher.ui.bluetooth.FlagProperty;
import com.example.dell.nscarlauncher.ui.fm.FMFragment;
import com.example.dell.nscarlauncher.ui.home.androideunm.FragmentType;
import com.example.dell.nscarlauncher.ui.home.fragment.HomePagerOneFragment;
import com.example.dell.nscarlauncher.ui.home.fragment.HomePagerThreeFragment;
import com.example.dell.nscarlauncher.ui.home.fragment.HomePagerTwoFragment;
import com.example.dell.nscarlauncher.ui.home.model.HomeModel;
import com.example.dell.nscarlauncher.ui.music.DialogLocalMusic;
import com.example.dell.nscarlauncher.ui.music.Service.PlayerService;
import com.example.dell.nscarlauncher.ui.music.fragment.MusicFragment;
import com.example.dell.nscarlauncher.ui.phone.PhoneFragment;
import com.example.dell.nscarlauncher.ui.setting.SetFragment;
import com.example.dell.nscarlauncher.widget.DialogVolumeControl;

import java.util.ArrayList;

import me.relex.circleindicator.CircleIndicator;

public class HomePagerActivity extends BaseActivity implements ViewPager.OnPageChangeListener{
    public static final int CALL_ANSWER = 4; // 接听来电
    public static final int CALL_HUNGUP = 5; // 挂断来电
    private DialogVolumeControl dialogVolumeControl ;
    private ArrayList<Fragment> mFragments;
    private ViewPager viewPager;
    private CircleIndicator indicator;//viewpager指示器
    private Fragment mCurFragment;//当前页
    private FMFragment fmFragment ;//收音机
    private BTMusicFragment btMusicFragment;//蓝牙音乐
    private MusicFragment musicFragment;//本地音乐
    private PhoneFragment phoneFragment;//电话
    private SetFragment setFragment;//设置
    private AppFragment appFragment;//应用
    private ArrayList<HomeModel> mData;
    static Dialog alertDialog;//来电弹框
    public ComingReceiver comingReceiver;
    static AudioManager audioManager;
    static IKdBtService btservice;
    @Override
    protected void onResume() {
        super.onResume();
        DialogVolumeControl.volumeResume();
        if (comingReceiver == null) {
            comingReceiver = new ComingReceiver();
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("phone.iscoming");
            intentFilter.addAction("3gphone.iscoming");
            intentFilter.addAction("phone.isgone");
            registerReceiver(comingReceiver, intentFilter);
        }
    }

    @Override
    public int getContentViewResId() {
        return R.layout.activity_homepager;
    }

    @Override
    public void initView() {
        initDa();
        viewPager.setAdapter(new MyAdapter(getSupportFragmentManager()));
//        viewPager.setOffscreenPageLimit(PageCount-1);
        viewPager.setOffscreenPageLimit(mFragments.size());
        indicator.setViewPager(viewPager);
    }

    @Override
    public void findView() {
        super.findView();
        mFragments = new ArrayList<>();
        viewPager=getView(R.id.viewPager);
        indicator =getView(R.id.indicator);
        getService();
    }
    /*获取全局模块*/
    private void  getService(){

        if(audioManager==null) {
            audioManager = App.get().getAudioManager();
        }
        if(btservice==null) {
            btservice = App.get().getBtservice();
        }


    }
    @Override
    public void setListener() {
        super.setListener();
        setClickListener(R.id.iv_power);
        setClickListener(R.id.title_iv_sound);
        setClickListener(R.id.center_img);
        viewPager.addOnPageChangeListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.title_iv_sound:
                showVolumeDialog();
                break;
            case R.id.center_img:
                hideFragment();
                break;
            case R.id.iv_power:
                JumpUtils.actAPK(this,FragmentType.CARPOWER);
                break;
        }
    }
    //初始化viewpager 数据
    private  void initDa(){
        HomePagerOneFragment homePagerOneFragment =new HomePagerOneFragment();
        homePagerOneFragment.setHomePagerActivity(this);
        HomePagerTwoFragment homePagerTwoFragment =new HomePagerTwoFragment();
        homePagerTwoFragment.setHomePagerActivity(this);
        HomePagerThreeFragment homePagerThreeFragment =new HomePagerThreeFragment();
        homePagerThreeFragment.setHomePagerActivity(this);
        mFragments.add(homePagerOneFragment);
        mFragments.add(homePagerTwoFragment);
        mFragments.add(homePagerThreeFragment);
        createFragment();

        new DialogLocalMusic(getApplicationContext());
    }

    /**
     * 初始化fragment
     */
    private void createFragment() {
      fmFragment =new FMFragment();
      fmFragment.setHomePagerActivity(this);
      btMusicFragment =new BTMusicFragment();
      musicFragment= new MusicFragment();
      phoneFragment= new PhoneFragment();
      setFragment =new SetFragment();
      appFragment=new AppFragment();
    }
    /*隐藏fragemt*/
    public  void  hideFragment(){
        setViewVisibility(R.id.frame_main,false);
    }
    /*显示fragment*/

    public void showFragemnt(){
        setViewVisibility(R.id.frame_main,true);
    }


    @Override
    public void onPageScrolled(int i, float v, int i1) {

    }

    @Override
    public void onPageSelected(int i) {

    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }
    /**
     * 选择Fragment
     *
     * @param fragment
     */
    private void switchFragment(Fragment fragment) {

        mCurFragment = FragmentUtils.selectFragment(this, mCurFragment, fragment, R.id.frame_main);
        showFragemnt();
    }
    public void  jumpFragment(@FragmentType int type ){
        switch (type){
            case  FragmentType.FM:
                switchFragment(fmFragment);
                break;
            case  FragmentType.BTMUSIC:
                switchFragment(btMusicFragment);
                break;
            case  FragmentType.MUSIC:
                switchFragment(musicFragment);
                break;
             case  FragmentType.PHONE:
                 switchFragment(phoneFragment);
                 break;
            case  FragmentType.SET:
                switchFragment(setFragment);
                break;
            case  FragmentType.APPLICATION:
                switchFragment(appFragment);
                break;
        }
    }
    /*viewpager适配器*/
    private class MyAdapter extends FragmentPagerAdapter {
        MyAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        App.get().unregistMyReceiver();
        unregisterReceiver(comingReceiver);
        if (alertDialog != null) {
            alertDialog.dismiss();
        }
        dialogVolumeControl=null;
        stopService(new Intent(this, PlayerService.class));
    }

    //    private void  initData(){
//        HomeData data =new HomeData();
//        mData= data.getData();
//        ArrayList<HomeModel> page =new ArrayList<>();
//        while (mData.size()>0){
//            HomeModel homeModel =new HomeModel(mData.get(0));
//            page.add(homeModel);
//            mData.remove(0);
//            if(page.size()==4){
//                HomePagerThreeFragment homePagerFragment =new HomePagerThreeFragment();
//                homePagerFragment.setmData(page);
//                page.clear();
//                mFragments.add(homePagerFragment);
//                PageCount++;
//            }
//        }
//        if(page.size()>0) {
//            HomePagerThreeFragment homePagerFragment = new HomePagerThreeFragment();
//            homePagerFragment.setmData(page);
//            page.clear();
//            mFragments.add(homePagerFragment);
//            PageCount++;
//        }
//
//    }
    public class ComingReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() == "phone.iscoming") {
                incomingShow(intent.getStringExtra("number"), intent.getIntExtra("index", 0));
            } else if (intent.getAction() == "phone.isgone") {
                dimissShow();
            } else if (intent.getAction() == "3gphone.iscoming") {
//                incoming3gShow(intent.getStringExtra("number"));
            }
        }
    }
    public void dimissShow() {
        if (alertDialog != null) {
            alertDialog.dismiss();
        }
    }
    // 来电显示弹出框
    public void incomingShow(String number, final int index) {
        if (audioManager.requestAudioFocus(PhoneFragment.afChangeListener, 11,
                AudioManager.AUDIOFOCUS_GAIN) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.show();
            alertDialog.setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失
            Window window = alertDialog.getWindow();
            window.setContentView(R.layout.dialog_phone_incalling);
            TextView tv_info = (TextView) window.findViewById(R.id.dialog_text);
            tv_info.setText(PhoneFragment.getName(number) + "\n来电是否接听");
            Button bt_answer = (Button) window.findViewById(R.id.dialog_btn_answer);
            Button bt_refuse = (Button) window.findViewById(R.id.dialog_btn_refuse);
            bt_answer.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    new Thread() {
                        public void run() {
                            myHandler.sendMessage(myHandler.obtainMessage(CALL_ANSWER));
                        };
                    }.start();
//                    MainKondi.changeFragment(MainKondi.FRAGMENT_PHONE); // 接听时进入电话页面
                    FlagProperty.flag_phone_incall_click = true;
                    alertDialog.dismiss();
                }
            });
            bt_refuse.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    new Thread() {
                        public void run() {
                            if (index == 1) {
                                myHandler.sendMessage(myHandler.obtainMessage(CALL_HUNGUP));
                            } else if (index == 2) {
                                try {
                                    Log.d("BlueMusicBroadcoast",
                                            "btservice.btReleaseWaitingCall():" + btservice.btReleaseWaitingCall());
                                } catch (RemoteException e) {
                                    e.printStackTrace();
                                }
                            }else{
                                myHandler.sendMessage(myHandler.obtainMessage(CALL_HUNGUP)); //默认
                            }

                        };
                    }.start();
                    alertDialog.dismiss();
                }
            });
        }
    }


    public Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            try {
                switch (msg.what) {
                    case CALL_ANSWER:
                        btservice.btAnswerCall();
                        break;
                    case CALL_HUNGUP:
                        btservice.btHungupCall();
                        break;

                    default:
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
    };

    /*
     * 显示音量*/
    private  void  showVolumeDialog(){
        if(dialogVolumeControl ==null){
            dialogVolumeControl =new DialogVolumeControl();
        }

        dialogVolumeControl.setContent(this.getActivity(),this);
        dialogVolumeControl.show(getSupportFragmentManager());
    }
    /*打开高德*/
    public void  openNavi(){
        PackageManager packageManager = getPackageManager();

        String packageName = "com.autonavi.amapauto";//高德地图
        Intent launchIntentForPackage = packageManager.getLaunchIntentForPackage(packageName);
        if (launchIntentForPackage != null)
            startActivity(launchIntentForPackage);
        else
            Toast.makeText(this, "未安装该应用", Toast.LENGTH_SHORT).show();
    }
}
