package com.example.dell.nscarlauncher.ui.fm;

import android.media.AudioManager;
import android.os.IKdAudioControlService;
import android.os.RemoteException;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.os.IFmService;
import com.example.dell.nscarlauncher.R;
import com.example.dell.nscarlauncher.app.App;
import com.example.dell.nscarlauncher.base.fragment.BaseFragment;
import com.example.dell.nscarlauncher.common.util.SPUtil;
import com.example.dell.nscarlauncher.ui.home.HomePagerActivity;
import com.example.dell.nscarlauncher.widget.RadioRulerView;


import java.util.ArrayList;

public class FMFragment extends BaseFragment implements RadioRulerView.OnValueChangeListener{
    private FMAdapter mAdapter ;
    private RadioRulerView mRule;
    public static ArrayList<String> fm_list = new ArrayList<String>();
    private HomePagerActivity homePagerActivity;

    private IFmService radio;  //收音机
    public static   float channel = 93.0f;// 默认初始的波段
    private IKdAudioControlService audioservice ;
    AudioManager audioManager;
   public static String FMCHANNEL ="channel";
   private  boolean mIsAuto =true; //是否自动搜索

    public void setHomePagerActivity(HomePagerActivity homePagerActivity) {
        this.homePagerActivity = homePagerActivity;
    }

    public static float getChannel() {
        return channel;
    }

    @Override
    public void onUnFirstResume() {


    }

    @Override
    public void Resume() {
        if (isSecondResume) {
            if(radio!=null&&audioservice!=null&&audioManager!=null){
                openFm();
                changeChannel(channel);
            }else {
                getService();
                openFm();
                changeChannel(channel);
            }
        }
    }

    @Override
    public int getContentResId() {
        return R.layout.fragment_fm;
    }

    @Override
    public void findView() {
        mRule=getView(R.id.rule);
        mRule.setMaxLineCount(200);
    }

    @Override
    public void setListener() {
        setClickListener(R.id.iv_back);
        setClickListener(R.id.iv_search);
        setClickListener(R.id.iv_fm_left);
        setClickListener(R.id.iv_fm_right);
        mRule.setOnValueChangeListener(this);
    }

    @Override
    public void initView() {
        initData();
        openFm();
        changeChannel(channel);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_back:
                homePagerActivity.hideFragment();
                closeFm();
               SPUtil.getInstance(getContext(),FMCHANNEL).putFloat(FMCHANNEL,channel);
                break;
            case R.id.iv_search:
                if(mIsAuto) {
                    mRule.setAutoSearchFM(mIsAuto);
                    mRule.startAutoSeachFM();
                }
                mIsAuto=!mIsAuto;
                break;
            case R.id.iv_fm_left:
                leftFm();
                break;
            case R.id.iv_fm_right:
                rightFm();
                break;
        }
    }
    /*左调频*/
    public  void  leftFm(){

        if(channel>88.0f) {
            channel = (float) (channel * 10-1)/10f;
            changeChannel(channel);

        }
    }
    /*右调频*/
    public  void  rightFm(){
//        channel= SPUtil.getInstance(getContext(),FMCHANNEL).getFloat(FMCHANNEL,93.0f);
        if(channel<108.0f) {
            channel = (float) (channel * 10+1)/10f;
            changeChannel(channel);
        }
    }
    /*初始化数据*/
    private void  initData(){
        /*初始化模块*/
        getService();
        /*初始化数据*/
        initFMList();
        /*初始化recleview*/
        initRvAdapter(fm_list);
        /*初始化channel*/
        channel= SPUtil.getInstance(getContext(),FMCHANNEL).getFloat(FMCHANNEL,93.0f);
    }

    /*获取全局模块*/
    private void  getService(){
      if(radio==null) {
          radio = App.get().getRadio();
      }
      if(audioservice==null) {
          audioservice = App.get().getAudioservice();
      }
      if(audioManager==null) {
          audioManager = App.get().getAudioManager();
      }
    }

    /**
     * 初始化adapter
     *
     *
     */
    private void initRvAdapter( ArrayList<String> data) {
        if (mAdapter == null) {
            RecyclerView rv = getView(R.id.recyclerView);
            mAdapter =new FMAdapter(data);

            if (rv != null) {
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
                linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                rv.setLayoutManager(linearLayoutManager);
                rv.setAdapter(mAdapter);
            }
            mAdapter.setOnItemClickListener(new FMAdapter.OnItemClickListener() {
                @Override
                public void onClickFM(String data) {
                  changeChannel(Float.valueOf(data));

                }

            });

        }else {
            mAdapter.notifyDataSetChanged();
        }
    }
//    打开收音机
public void openFm(){

            if (audioManager.requestAudioFocus(afChangeListener, 14, AudioManager.AUDIOFOCUS_GAIN) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                new Thread() {
                    public void run() {
                        try {
                            System.out.println("radio.OpenLocalRadio():" + radio.OpenLocalRadio());
//                        System.out.println("radio.SetRadioFreq():" + channel + "----" + radio.SetRadioFreq(channel)); // 开机初始化为频道93.0
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    ;
                }.start();
            }

    }
//    关闭收音机
    public void closeFm(){
        if (audioManager.abandonAudioFocus(afChangeListener) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            new Thread() {
                public void run() {
                    try {
                        System.out.println("radio.CloseLocalRadio():" + radio.CloseLocalRadio());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                ;
            }.start();
        }
    }

    // 初始化一些音乐频道
    public void initFMList() {
        fm_list.add("89.0");
        fm_list.add("91.8");
        fm_list.add("93.0");
        fm_list.add("95.0");
        fm_list.add("96.8");
        fm_list.add("98.8");
        fm_list.add("103.2");
    }
        /*滑动监听回调*/
    @Override
    public void onValueChange(float value) {
        channel=value;
        setTvText(R.id.tv_fm_Hz,String.format("%.1f",value));
        try {
            System.out.println("radio.SetRadioFreq():" + channel + "----" + radio.SetRadioFreq(channel)); // 变换广播频道
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        SPUtil.getInstance(getContext(),FMCHANNEL).putFloat(FMCHANNEL,channel);
    }

    /*变换频道
    **/
public void  changeChannel(float value){
    setTvText(R.id.tv_fm_Hz,String.format("%.1f",value));
    channel=value;
    if(mRule!=null) {
        mRule.setChannel(channel);
    }
    try {
        System.out.println("radio.SetRadioFreq():" + channel + "----" + radio.SetRadioFreq(channel)); // 变换广播频道
    } catch (RemoteException e) {
        e.printStackTrace();
    }
    SPUtil.getInstance(getContext(),FMCHANNEL).putFloat(FMCHANNEL,channel);
}
/*音频焦点处理*/
    AudioManager.OnAudioFocusChangeListener afChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int i) {
            switch (i){
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT://这说明你临时失去了音频焦点，但是在不久就会再返回来。此时，你必须终止所有的音频播放，但是保留你的播放资源，因为可能不久就会返回来。
                    new Thread() {
                        public void run() {
                            try {
                                System.out.println("radio.CloseLocalRadio():" + radio.CloseLocalRadio());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        };
                    }.start();       //后面添加的关闭
                    break;
                case AudioManager.AUDIOFOCUS_GAIN://你已经获得音频焦点
                    new Thread() {
                        public void run() {
                            try {
                                System.out.println("radio.OpenLocalRadio():" + radio.OpenLocalRadio());
                                System.out.println("radio.SetRadioFreq():" + channel + "----" + radio.SetRadioFreq(channel)); // 开机初始化为频道93.0
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        };
                    }.start();
                    break;
                case  AudioManager.AUDIOFOCUS_LOSS://你已经失去音频焦点很长时间了，必须终止所有的音频播放。因为长时间的失去焦点后，不应该在期望有焦点返回，这是一个尽可能清除不用资源的好位置。
                    new Thread() {
                        public void run() {
                            try {
                                System.out.println("radio.CloseLocalRadio():" + radio.CloseLocalRadio());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        };
                    }.start();
                    break;
                case  AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK://这说明你已经临时失去了音频焦点，但允许你安静的播放音频（低音量），而不是完全的终止音频播放。目前所有的情况下，oFocusChange的时候停止mediaPlayer */
                    new Thread() {
                        public void run() {
                            try {
                                System.out.println("radio.CloseLocalRadio():" + radio.CloseLocalRadio());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        };
                    }.start();
                    break;
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        SPUtil.getInstance(getContext(),FMCHANNEL).putFloat(FMCHANNEL,channel);
    }

}
