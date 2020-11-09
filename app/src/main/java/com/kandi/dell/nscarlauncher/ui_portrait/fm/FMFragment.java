package com.kandi.dell.nscarlauncher.ui_portrait.fm;

import android.media.AudioManager;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.kandi.dell.nscarlauncher.R;
import com.kandi.dell.nscarlauncher.app.App;
import com.kandi.dell.nscarlauncher.base.fragment.BaseFragment;
import com.kandi.dell.nscarlauncher.common.util.NumParseUtils;
import com.kandi.dell.nscarlauncher.common.util.SPUtil;
import com.kandi.dell.nscarlauncher.common.util.TimeUtils;
import com.kandi.dell.nscarlauncher.common.util.ToastUtils;
import com.kandi.dell.nscarlauncher.ui.fm.FMAdapter;
import com.kandi.dell.nscarlauncher.ui.home.androideunm.FragmentType;
import com.kandi.dell.nscarlauncher.ui.home.androideunm.HandleKey;

import java.util.ArrayList;

public class FMFragment extends BaseFragment {
    private FmRulerView mRule;
    private FMAdapter mAdapter ;

    private  ArrayList<Float> mData =new ArrayList<>();
    public boolean isPlay,isSearch;
    public float channel = 93.0f;// 默认初始的波段
    public String FMCHANNEL ="channel";
    public String FMCHANNELLIST ="channellist";
    public boolean mIsAuto =false; //是否处于自动搜索
    @Override
    public int getContentResId() {
        return R.layout.fragment_fm_por;
    }

    @Override
    public void findView() {
        mRule=getView(R.id.rule);
    }

    @Override
    public void setListener() {
        setClickListener(R.id.iv_return);
        setClickListener(R.id.iv_back);
        setClickListener(R.id.iv_search);
        setClickListener(R.id.iv_fm_left);
        setClickListener(R.id.iv_fm_right);
        setClickListener(R.id.iv_fav);
        mRule.setOnValueChangeListener(new SizeViewValueChangeListener() {
            @Override
            public void onValueChange(float value) {


                if (TimeUtils.isFastDoubleClick()) {
                    return;
                }
                if(isSearch){
                    ToastUtils.show(R.string.搜索中);

                    return;
                }
                channel=value;
                setTvText(R.id.tv_fm_Hz,String.format("%.1f",value));
                Log.i("testtest","===1111==setChannel====onValueChange-->"+String.format("%.1f",value));
                if(mData.toString().contains(""+value)){
                    getView(R.id.iv_fav).setSelected(true);
                }else{
                    getView(R.id.iv_fav).setSelected(false);
                }
                if(!mIsAuto){
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Log.i("testtest","=======hello==test2222=====");
                                System.out.println("App.get().getRadio().SetRadioFreq():" + channel + "----" + App.get().getRadio().SetRadioFreq(channel)); // 变换广播频道
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();

                }
                SPUtil.getInstance(getContext(),FMCHANNEL).putFloat(FMCHANNEL,channel);

            }
        });
    }

    @Override
    public void initView() {
        setmType(FragmentType.FM);
        initData();
        openFm();
        changeChannel(channel);
    }

    @Override
    public void Resume() {

        if (isSecondResume) {
                openFm();
                changeChannel(channel);
        }
    }
    /*初始化数据*/
    private void  initData(){

        /*初始化数据*/
        initFMList();
        /*初始化recleview*/
        initRvAdapter(mData);
        /*初始化channel*/
        channel= SPUtil.getInstance(getContext(),FMCHANNEL).getFloat(FMCHANNEL,93.0f);
    }
    // 初始化一些音乐频道
    public void initFMList() {
        String list=  SPUtil.getInstance(getContext(),FMCHANNELLIST).getString(FMCHANNELLIST,"").replace("[","").replace("]","");
        if(!"".equals(list)){
            String[] arr = list.split(",");

            for(int i =0;i<arr.length;i++){
                mData.add(NumParseUtils.parseFloat(arr[i]));
            }
        }else {
            mData.add(87.5f);
            mData.add(91.8f);
            mData.add(93.0f);
            mData.add(105.5f);
            mData.add(106.8f);
            mData.add(107.0f);

        }
    }
    @Override
    public void onClick(View v) {
        if (TimeUtils.isFastDoubleClick()) {
            return;
        }
        switch (v.getId()){
            case R.id.iv_return:
                App.get().getCurActivity().hideFragment();
                break;
            case R.id.iv_back:
                App.get().getCurActivity().hideFragment();
                closeFm();
                SPUtil.getInstance(getContext(),FMCHANNEL).putFloat(FMCHANNEL,channel);
                break;
            case R.id.iv_search:
                if(isSearch){
                    ToastUtils.show(R.string.搜索中);

                    return;
                }
                if(!mIsAuto) {
                    mIsAuto=true;
                    mData.clear();
                    changeChannel(88.0f);
                    showLoadingDialog();
                    new Thread(new SeachThread()).start();
                }
                break;
            case R.id.iv_fm_left:
                leftFm(channel);
                break;
            case R.id.iv_fm_right:
                rightFm(channel);
                break;
            case R.id.iv_fav:
                if(!getView(R.id.iv_fav).isSelected()){
                    getView(R.id.iv_fav).setSelected(true);
                    mData.add(channel);
                    initRvAdapter(mData);
                    SPUtil.getInstance(getContext(),FMCHANNELLIST).putString(FMCHANNELLIST,mData.toString());
                }else{
                    getView(R.id.iv_fav).setSelected(false);
                    mData.remove(channel);
                    initRvAdapter(mData);
                    SPUtil.getInstance(getContext(),FMCHANNELLIST).putString(FMCHANNELLIST,mData.toString());
                }
                break;
        }
    }
    /*播放*/
    public   void  play(){
        if (!isPlay) {
            openFm();
        } else {
            App.get().getCurActivity().hideFragment();
            closeFm();
            SPUtil.getInstance(getContext(),FMCHANNEL).putFloat(FMCHANNEL,channel);
        }
    }
    /**
     * 搜台要在开启子线程
     */
    private class SeachThread implements Runnable {

        @Override
        public void run() {
            try {
                Log.i("testtest","=======hello==test2222==RadioFreqSeekUp===");
                while(true){
                    if(App.get().getRadio().RadioFreqSeekUp()<0){
                        Log.i("testtest","=======RadioFreqSeekUp()<0===");
//                        hideLoadingDialog();
//                        myHandler.sendMessage(myHandler.obtainMessage(REFRESH));
                        myHandler.sendMessageDelayed(myHandler.obtainMessage(REFRESH),200);
                        mIsAuto =false;
                        break;
                    }else {
                        float seek =App.get().getRadio().GetRadioFreq();
                        Log.i("testtest","=======seek==="+seek + "    channel"+channel);
                        if(channel>seek){
                            Log.i("testtest","=======channel>seek===");
                            hideLoadingDialog();
                            myHandler.sendMessage(myHandler.obtainMessage(REFRESH));
                            mIsAuto =false;
                            break;
                        }
                        channel =seek;
                        mData.add(channel);
                        myHandler.sendMessage(myHandler.obtainMessage(VIEWFRESH));
                        if(!mIsAuto){
                            break;
                        }
                        Thread.sleep(1000);
                    }

                }
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    /*左调频*/
    public  void  leftFm(final Float channels){
        if(isSearch){
            ToastUtils.show(R.string.搜索中);

            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                channel=channels;
                if(!isPlay){
                    openFm();
                }
                try {
                    if(!isSearch) {
                        isSearch = true;
                        if (App.get().getRadio().RadioFreqSeekDown() >= 0) {

                            channel = App.get().getRadio().GetRadioFreq();
                            myHandler.sendMessage(myHandler.obtainMessage(VIEWFRESH));
                            isSearch = false;

                        } else {
                            isSearch = false;
                        }
                    }else {
//                        Toast.makeText(getActivity(),"正在搜索", Toast.LENGTH_SHORT).show();\
//                        Log.d("FM", "run: is Search" );
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

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
            System.out.println("radio.SetRadioFreq():" + channel + "----" + App.get().getRadio().SetRadioFreq(channel)); // 变换广播频道
        } catch (RemoteException e) {
            e.printStackTrace();
        }
//    SPUtil.getInstance(getContext(),FMCHANNEL).putFloat(FMCHANNEL,channel);
    }
    /*变换频道
     **/
    public void  changeChannelView(){
        setTvText(R.id.tv_fm_Hz,String.format("%.1f",channel));
        if(mRule!=null) {
            mRule.setChannel(channel);
        }
        SPUtil.getInstance(getContext(),FMCHANNEL).putFloat(FMCHANNEL,channel);

    }
    /*变换频道
     **/
    public void  changeChannel(){
        setTvText(R.id.tv_fm_Hz,String.format("%.1f",channel));
        if(mRule!=null) {
            mRule.setChannel(channel);
        }
        try {
            System.out.println("radio.SetRadioFreq():" + channel + "----" + App.get().getRadio().SetRadioFreq(channel)); // 变换广播频道
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        SPUtil.getInstance(getContext(),FMCHANNEL).putFloat(FMCHANNEL,channel);
    }
    /*右调频*/
    public  void  rightFm( final Float channels){

        if(isSearch){
            ToastUtils.show(R.string.搜索中);

            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                channel=channels;
//        Log.d("Fm","Channel   "+String.valueOf(channel));
                if(!isPlay){
                    openFm();
                }
                try {
                    if(!isSearch) {
                        isSearch = true;
                        if (App.get().getRadio().RadioFreqSeekUp() >= 0) {

                            channel = App.get().getRadio().GetRadioFreq();
                            myHandler.sendMessage(myHandler.obtainMessage(VIEWFRESH));
//                Log.d("Fm","Channel   "+String.valueOf(channel));
                            isSearch = false;
                        } else {
                            isSearch = false;
                        }
                    }else {
//                        Toast.makeText(getActivity(),"正在搜索", Toast.LENGTH_SHORT).show();
//                        Log.d("FM", "run: is Search" );
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }).start();

    }
    //    打开收音机
    //    打开收音机
    public void openFm(){

        if (App.get().getAudioManager().requestAudioFocus(afChangeListener, 14, AudioManager.AUDIOFOCUS_GAIN) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            new Thread() {
                public void run() {
                    try {
                        System.out.println("radio.OpenLocalRadio():" + App.get().getRadio().OpenLocalRadio());
                        App.get().getCurActivity().myHandler.sendEmptyMessage(HandleKey.FMOPEN);
//                        homePagerActivity.getHomePagerOneFragment().pagerOneHnadler.sendMessage(homePagerActivity.getHomePagerOneFragment().pagerOneHnadler.obtainMessage(HandleKey.OPEMFM));
//
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
        if (App.get().getAudioManager().abandonAudioFocus(afChangeListener) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            new Thread() {
                public void run() {
                    try {
                        System.out.println("radio.CloseLocalRadio():" + App.get().getRadio().CloseLocalRadio());
                        App.get().getCurActivity().myHandler.sendEmptyMessage(HandleKey.FMCOLSE);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                ;
            }.start();
        }
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

                                System.out.println("radio.CloseLocalRadio():" + App.get().getRadio().CloseLocalRadio());
                                App.get().getCurActivity().myHandler.sendEmptyMessage(HandleKey.FMCOLSE);
                                isPlay=false;
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
                                System.out.println("radio.OpenLocalRadio():" + App.get().getRadio().OpenLocalRadio());
//                                Log.d("Fm","Channel open  "+String.valueOf(channel));
                                System.out.println("radio.SetRadioFreq():" + channel + "----" + App.get().getRadio().SetRadioFreq(channel)); // 开机初始化为频道93.0
//                                Log.d("Fm","Channel  set "+String.valueOf(channel));
                                App.get().getCurActivity().myHandler.sendEmptyMessage(HandleKey.FMOPEN);
                                isPlay=true;
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
                                System.out.println("radio.CloseLocalRadio():" + App.get().getRadio().CloseLocalRadio());
                                App.get().getCurActivity().myHandler.sendEmptyMessage(HandleKey.FMCOLSE);
                                isPlay=false;
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
                                System.out.println("radio.CloseLocalRadio():" + App.get().getRadio().CloseLocalRadio());
                                App.get().getCurActivity().myHandler.sendEmptyMessage(HandleKey.FMCOLSE);
                                isPlay=false;
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        };
                    }.start();
                    break;
            }
        }
    };
    private final  int VIEWFRESH =0;
    private final  int VIEWCHANGE =1;
    private final  int REFRESH =2;
    public final static   int NEXT =3;
    public final  static int LAST =4;
    public Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case VIEWCHANGE:
                    changeChannel();
                    break;
                case VIEWFRESH:
                    changeChannelView();
                    break;
                case REFRESH:
                    hideLoadingDialog();
                    initRvAdapter(mData);
                    SPUtil.getInstance(getContext(),FMCHANNELLIST).putString(FMCHANNELLIST,mData.toString());
                    changeChannel();
                    break;
                case NEXT:
                    rightFm(channel);
                    break;
                case LAST:
                    leftFm(channel);
                    break;
                default:
                    break;
            }
        };
    };
    /**
     * 初始化adapter
     *
     *
     */
    private void initRvAdapter( ArrayList<Float> data) {
        if (mAdapter == null) {
            RecyclerView rv = getView(R.id.recyclerView);
            mAdapter =new FMAdapter(data);

            if (rv != null) {
                GridLayoutManager linearLayoutManager = new GridLayoutManager(getContext(),2);
                linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                rv.setLayoutManager(linearLayoutManager);
                rv.setAdapter(mAdapter);
            }
            mAdapter.setOnItemClickListener(new FMAdapter.OnItemClickListener() {
                @Override
                public void onClickFM(Float data) {
                    changeChannel(data);

                }

            });

        }else {
            mAdapter.notifyData(data,true);
        }
    }
}
