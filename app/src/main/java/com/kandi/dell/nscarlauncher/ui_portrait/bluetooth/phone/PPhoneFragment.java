package com.kandi.dell.nscarlauncher.ui_portrait.bluetooth.phone;

import android.media.AudioManager;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.os.SystemProperties;
import android.view.View;
import android.widget.RelativeLayout;

import com.kandi.dell.nscarlauncher.R;
import com.kandi.dell.nscarlauncher.app.App;
import com.kandi.dell.nscarlauncher.base.fragment.BaseFragment;
import com.kandi.dell.nscarlauncher.ui.bluetooth.FlagProperty;
import com.kandi.dell.nscarlauncher.ui.home.androideunm.FragmentType;
import com.kandi.dell.nscarlauncher.ui.phone.PhoneFragment;
import com.kandi.dell.nscarlauncher.ui.phone.mvp.MvpMainView;
import com.kandi.dell.nscarlauncher.ui.phone.mvp.mpl.MainPresenter;

public class PPhoneFragment extends BaseFragment implements MvpMainView{
    public final int                    PHONE_OVER     = 1;//结束
    public final int                    PHONE_CONTINUE = 2;//
    public final int                    PHONE_START    = 3;
    public final int                    PHONE_CALLING    = 10;//打出电话
    public final int                    PHONE_END      = 4;//
    public final int                    PHONE_ANSWER     = 9;//接电话
    public final int                    DELETE_CLIKE   = 5;
    public final int                    PHONE_IN =8;//来电
    public boolean flag_phone,flag_mic,flag_volume; //是否通话
    private int phone_call_time;//通话时间
    private String number,address,type;
    public RelativeLayout NullView ;//空界面
    MainPresenter mainPresenter;
    @Override
    public int getContentResId() {
        return  R.layout.fragment_phone_por;
    }

    @Override
    public void findView() {
        NullView =getView(R.id.bt_phone_null);
    }

    @Override
    public void setListener() {
        setClickListener(R.id.iv_call);
        setClickListener(R.id.call_stop);
        setClickListener(R.id.call_start);
        setClickListener(R.id.ll_1);
        setClickListener(R.id.ll_2);
        setClickListener(R.id.ll_3);
        setClickListener(R.id.ll_4);
        setClickListener(R.id.ll_5);
        setClickListener(R.id.ll_6);
        setClickListener(R.id.ll_7);
        setClickListener(R.id.ll_8);
        setClickListener(R.id.ll_9);
        setClickListener(R.id.ll_10);
        setClickListener(R.id.ll_11);
        setClickListener(R.id.ll_12);

        setClickListener(R.id.call_key);
        setClickListener(R.id.tv_calling_key_hide);
        setClickListener(R.id.call_mic);
        setClickListener(R.id.call_volume);
        setClickListener(R.id.ll2_1);
        setClickListener(R.id.ll2_2);
        setClickListener(R.id.ll2_3);
        setClickListener(R.id.ll2_4);
        setClickListener(R.id.ll2_5);
        setClickListener(R.id.ll2_6);
        setClickListener(R.id.ll2_7);
        setClickListener(R.id.ll2_8);
        setClickListener(R.id.ll2_9);
        setClickListener(R.id.ll2_10);
        setClickListener(R.id.ll2_11);
        setClickListener(R.id.ll2_12);

        setClickListener(R.id.iv_address);
        setClickListener(R.id.iv_record);
        setClickListener(R.id.iv_return);
        setClickListener(R.id.bt_blueSet);
        setClickListener(R.id.num_delete);
    }

    @Override
    public void initView() {
      requestAudioFocus();
      mainPresenter = new MainPresenter(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_call:
                callphone(number);
                break;
            case R.id.call_stop:
                hangDownphone();
                break;
            case R.id.call_start:
                answerPhone();
                phoneStart();
                break;
            case R.id.iv_address:
                App.get().getCurActivity().getPhoneFragment().switchPMFragment();
                break;
            case R.id.iv_record:
                App.get().getCurActivity().getPhoneFragment().switchPRFragment();
                break;
            case R.id.ll_1:
                addphone("1");
                break;
            case R.id.ll_2:
                addphone("2");
                break;
            case R.id.ll_3:
                addphone("3");
                break;
            case R.id.ll_4:
                addphone("4");
                break;
            case R.id.ll_5:
                addphone("5");
                break;
            case R.id.ll_6:
                addphone("6");
                break;
            case R.id.ll_7:
                addphone("7");
                break;
            case R.id.ll_8:
                addphone("8");
                break;
            case R.id.ll_9:
                addphone("9");
                break;
            case R.id.ll_10:
                addphone("*");
                break;
            case R.id.ll_11:
                addphone("0");
                break;
            case R.id.ll_12:
                addphone("#");
                break;
            case R.id.call_key:
                setViewVisibility(R.id.ll_calling_key,true);
                setViewInVisibility(R.id.ll_calling_controll,false);
                break;
            case R.id.tv_calling_key_hide:
                setViewVisibility(R.id.ll_calling_key,false);
                setViewInVisibility(R.id.ll_calling_controll,true);
                break;
            case R.id.call_mic:
                flag_mic=!flag_mic;
                setIvImage(R.id.call_mic,flag_mic?R.mipmap.ic_btphone_mic_stop:R.mipmap.ic_btphone_mic_play);


                break;
            case R.id.call_volume:
                flag_volume=!flag_volume;
                setIvImage(R.id.call_volume,flag_volume?R.mipmap.ic_btphone_volume_stop:R.mipmap.ic_btphone_vloume_play);



                break;
            case R.id.ll2_1:
                addphoneDtmf("1");
                break;
            case R.id.ll2_2:
                addphoneDtmf("2");
                break;
            case R.id.ll2_3:
                addphoneDtmf("3");
                break;
            case R.id.ll2_4:
                addphoneDtmf("4");
                break;
            case R.id.ll2_5:
                addphoneDtmf("5");
                break;
            case R.id.ll2_6:
                addphoneDtmf("6");
                break;
            case R.id.ll2_7:
                addphoneDtmf("7");
                break;
            case R.id.ll2_8:
                addphoneDtmf("8");
                break;
            case R.id.ll2_9:
                addphoneDtmf("9");
                break;
            case R.id.ll2_10:
                addphoneDtmf("*");
                break;
            case R.id.ll2_11:
                addphoneDtmf("0");
                break;
            case R.id.ll2_12:
                addphoneDtmf("#");
                break;
            case R.id.bt_blueSet:
                App.get().getCurActivity().jumpFragment(FragmentType.BTSET);
                break;
            case R.id.iv_return:
                App.get().getCurActivity().hideFragment();
                break;
            case R.id.num_delete:
                subString();
                break;
        }
    }
    public Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            try {
                switch (msg.what) {
                    case PHONE_OVER:
                        setTvText(R.id.call_number,"");
                        setTvText(R.id.call_time,"");
                        setViewVisibility(R.id.item_phone_calling,false);
                        setViewVisibility(R.id.ll_calling_key,false);
                        setViewVisibility(R.id.ll_calling_controll,false);
                        //更新通话记录
//                        new Thread(new Runnable() {
//                            @Override
//                            public void run() {
//                                try {
//                                    Thread.sleep(800);
//                                    App.get().getBtservice().btCallLogstartUpdate(1);
//                                } catch (RemoteException e) {
//                                    e.printStackTrace();
//                                } catch (InterruptedException e) {
//                                    e.printStackTrace();
//                                }
//                            }
//                        }).start();

                        flag_phone=false;


                        break;
                    case PHONE_START:

                        if(!"".equals(number)) {

                            phoneCall(number);
                        }
                        App.get().getBtservice().btDial(number);
                        break;
                    case PHONE_END:
                        App.get().getBtservice().btHungupCall();

                        break;
                    case PHONE_ANSWER:

                        App.get().getBtservice().btAnswerCall();

                        break;
                    case PHONE_CONTINUE:
                        setTvText(R.id.call_time, timeToString(phone_call_time));

                        break;
                    case PHONE_CALLING:
                        phoneStart();
                        break;
                    case PHONE_IN:
                        phoneIn();
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    };
    // 电话拨打
    public void phoneCall(String number) {
        setTvText(R.id.num,"");
        this.number = number;
        setTvText(R.id.call_number,getName(number));
        setTvText(R.id.call_time, R.string.正在呼叫);
//                            TvNum.setText("");
//                            bt_call.setVisibility(View.GONE);
        setViewVisibility(R.id.call_start,false);
        setViewVisibility(R.id.item_phone_calling,true);
//                            rl_call.setVisibility(View.VISIBLE);
        setViewVisibility(R.id.ll_calling_controll,false);
        flag_phone=true;
    }
    public void callIn(String num,String addre,String ty){
        number=num;
        address =addre;
        type =ty;
        new Thread() {
            public void run() {
                myHandler.sendMessage(myHandler.obtainMessage(PHONE_IN));
            }
        }.start();
    }
    public void phoneIn(){//来电
        if(!"".equals(number)) {
//            tv_phone_number.setText(getName(number));
            setTvText(R.id.call_number,number);
            if(address!=null&&type!=null) {
                setTvText(R.id.call_time, address + "  " + type);

            }else{
                setTvText(R.id.call_time, "");
            }
            setViewVisibility(R.id.item_phone_calling,true);
            setViewVisibility(R.id.ll_calling_controll,false);
            setViewVisibility(R.id.call_start,true);
            mainPresenter.searchPhoneInfo(number);
            flag_phone=true;
        }
    }
    // 电话接通开始
    public void phoneStart() {
        if (FlagProperty.flag_phone_ringcall) { // 来电显示电话号码

            FlagProperty.flag_phone_ringcall = false; // 使来电状态置为结束
        }
        setTvText(R.id.call_number,getName(number));
//        if(tv_phone_number!=null) {
//            if("".equals(FlagProperty.phone_number)){
//                tv_phone_number.setText(getName(number));
//            }else{
//                tv_phone_number.setText(getName(FlagProperty.phone_number));
//            }
//        }
        phone_call_time = 0;
        flag_phone = true;
        setViewVisibility(R.id.ll_calling_controll,true);
        setViewVisibility(R.id.item_phone_calling,true);
        setViewVisibility(R.id.call_start,false);
        new CountThread().start();
    }
    // 电话接通结束
    public void phoneStop() {

        myHandler.sendMessage(myHandler.obtainMessage(PHONE_OVER));

    }
    @Override
    public void updateView() {
        if(mainPresenter!=null) {
            setTvText(R.id.call_time, mainPresenter.getPhone());

        }
    }
    // 通话开始开启一个计时线程
    public class CountThread extends Thread {
        @Override
        public void run() {
            while (flag_phone) {
                myHandler.sendMessage(myHandler.obtainMessage(PHONE_CONTINUE));
                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                phone_call_time += 1;
            }
        }
    }
    // 根据号码显示电话薄中姓名
    public String getName(String number) {
//        System.out.println("number:" + number);
        if(number!=null) {
            if(App.get().getCurActivity().getPhoneInfoService().getPhoneBookInfos() != null){
                for (int i = 0; i < App.get().getCurActivity().getPhoneInfoService().getPhoneBookInfos().size(); i++) {
//            System.out.println("" + (i + 1) + ":" + DialogPhoneBook.datas.get(i).number + "结果"
//                    + (DialogPhoneBook.datas.get(i).number.compareTo(number) == 0));
                    if (App.get().getCurActivity().getPhoneInfoService().getPhoneBookInfos().get(i).getNumber().compareTo(number) == 0) {
                        return App.get().getCurActivity().getPhoneInfoService().getPhoneBookInfos().get(i).getName() + "(" + App.get().getCurActivity().getPhoneInfoService().getPhoneBookInfos().get(i).getNumber() + ")";
                    }
                }
            }else{
                return number;
            }
        }
        return number;
    }
    // 打电话持续时间转化为hh:mm:ss格式
    public String timeToString(long time) {
        int hour = (int) (time / 3600);
        int minute = (int) ((time % 3600) / 60);
        int second = (int) (time % 60);
        return getTwoNumbers(hour) + ":" + getTwoNumbers(minute) + ":" + getTwoNumbers(second);
    }
    // 时间格式化标准
    public String getTwoNumbers(int num) {
        if (num < 10) {
            return "0" + num;
        } else {
            return "" + num;
        }
    }
    public void subString(){
        number=  getTvText(R.id.num);
        if (number!=null&&number.length()>0){
            number =number.substring(0,number.length()-1);
            setTvText(R.id.num,number);
        }
    }
    /*电话DTMF*/
    private void addphoneDtmf(String num){
        try {

            App.get().getBtservice().btSendDtmf(num);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
    /*添加电话号码*/
    private void addphone(String num){
        number =  getTvText(R.id.num)+num;
       setTvText(R.id.num,number);
    }
    /*打电话*/
    public void callphone(String num){
        number=num;
        new Thread() {
            public void run() {
                myHandler.sendMessage(myHandler.obtainMessage(PHONE_START));
            }
        }.start();

    }
    /*挂电话*/
    public void hangDownphone(){

        new Thread() {
            public void run() {
                myHandler.sendMessage(myHandler.obtainMessage(PHONE_END));
                myHandler.sendMessage(myHandler.obtainMessage(PHONE_OVER));
            }
        }.start();

    }
    /*接电话*/
    public void answerPhone(){

        new Thread() {
            public void run() {
                myHandler.sendMessage(myHandler.obtainMessage(PHONE_ANSWER));
            }
        }.start();

    }
    /*判断蓝牙连接*/
    public void requestAudioFocus() {
        if (SystemProperties.get("sys.kd.btacconnected").compareTo("yes") == 0) {

            setNullViewGone(false);
        } else {
            setNullViewGone(true);
        }
    }
    public void setNullViewGone(boolean isShow){
        if(NullView!=null) {
            NullView.setVisibility(isShow ? View.VISIBLE : View.GONE);
        }

    }
    @Override
    public void Resume() {
        requestAudioFocus();
    }

    /*音频焦点管理*/
    public AudioManager.OnAudioFocusChangeListener afChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        public void onAudioFocusChange(int focusChange) {
            if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {

            } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {

            } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
            }

        }
    };
}
