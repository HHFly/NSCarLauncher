package com.kandi.dell.nscarlauncher.ui.phone;

import android.content.Context;
import android.media.AudioManager;
import android.os.Handler;
import android.os.IKdAudioControlService;
import android.os.IKdBtService;
import android.os.Message;
import android.os.RemoteException;
import android.os.SystemProperties;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kandi.dell.nscarlauncher.R;
import com.kandi.dell.nscarlauncher.app.App;
import com.kandi.dell.nscarlauncher.base.fragment.BaseFragment;
import com.kandi.dell.nscarlauncher.ui.bluetooth.FlagProperty;
import com.kandi.dell.nscarlauncher.ui.home.androideunm.FragmentType;
import com.kandi.dell.nscarlauncher.ui.phone.model.PhoneBookInfo;
import com.kandi.dell.nscarlauncher.ui.phone.model.PhoneRecordInfo;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;



public class PhoneFragment extends BaseFragment implements ViewPager.OnPageChangeListener {
    public final static int                    PHONE_OVER     = 1;//结束
    public final static int                    PHONE_CONTINUE = 2;//
    public final static int                    PHONE_START    = 3;
    public final static int                    PHONE_END      = 4;
    public final static int                    DELETE_CLIKE   = 5;
    public final static int                    BOOKREFRESH   = 6;
    public final static int                    RECORDREFRESH  =7;
    public static boolean flag_phone; //是否通话
    private static int phone_call_time;//通话时间
    static String phone_continue_show = "";//通话时间
    private ViewPager viewPager;
    private Fragment mCurFragment;//当前页
    private ArrayList<Fragment> mFragments;
    private static PNumFragment pNumFragment;//电话页
    private static PMemberFragment pMemberFragment;//通讯录
    private static PRecordFragment pRecordFragment;//通话记录
    private static ArrayList<PhoneBookInfo> phoneBookInfos =new ArrayList<>();//通讯录
    private static ArrayList<PhoneRecordInfo> phoneRecordInfos =new ArrayList<>();//通讯记录
    private static  String number;
    static TextView tv_phone_number,tv_phone_info,tv_keep_calltext,tv_other_phine;
    static ImageView bt_call,bt_stop;
    static LinearLayout ll_other,ll_calling_key,ll_calling_controll;
    static RelativeLayout rl_call;
    static AudioManager audioManager;
    static IKdAudioControlService audioservice ;
    static IKdBtService btservice;

    public static RelativeLayout NullView ;//空界面
    private int callIndex;

    @Override
    public void setmType(int mType) {
        super.setmType(FragmentType.PHONE);
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void Resume() {
        requestAudioFocus();
    }

    @Override
    public int getContentResId() {
        return R.layout.fragment_phone;
    }

    @Override
    public void findView() {
        tv_other_phine=getView(R.id.tv_other_phine);
        ll_calling_controll =getView(R.id.ll_calling_controll);
        ll_calling_key =getView(R.id.ll_calling_key);
        tv_phone_number=getView(R.id.call_number);
        tv_phone_info=getView(R.id.call_time);
        ll_other=getView(R.id.ll_other_phone);
        tv_keep_calltext= getView(R.id.fragment_phone_keep_calltext);
        bt_call=getView(R.id.call_start);
        bt_stop=getView(R.id.call_stop);
        rl_call =getView(R.id.item_phone_calling);
        viewPager=getView(R.id.viewPager1);
        NullView =getView(R.id.bt_phone_null);
    }

    @Override
    public void setListener() {
        setClickListener(R.id.call_stop);
        setClickListener(R.id.iv_call);
        setClickListener(R.id.ll_other_phone);
        setClickListener(R.id.call_start);
        setClickListener(R.id.call_key);
        setClickListener(R.id.tv_calling_key_hide);
        setClickListener(R.id.rl_1);
        setClickListener(R.id.rl_2);
        setClickListener(R.id.rl_3);
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
        viewPager.addOnPageChangeListener(this);
    }

    @Override
    public void initView() {
        initData();
        getService();
        viewPager.setAdapter(new MyAdapter(getFragmentManager()));
//        viewPager.setOffscreenPageLimit(PageCount-1);
        viewPager.setOffscreenPageLimit(3);
        setViewSelected(R.id.rl_1,true);

    }
    /*获取全局模块*/
    private void  getService(){
        if(audioservice==null) {
            audioservice = App.get().getAudioservice();
        }
        if(audioManager==null) {
            audioManager = App.get().getAudioManager();
        }
        if(btservice==null) {
            btservice = App.get().getBtservice();
        }
    }
    /*打电话*/
   public static void callphone(String num){
       number=num;
       new Thread() {
           public void run() {
               myHandler.sendMessage(myHandler.obtainMessage(PHONE_START));
           }
       }.start();

   }
   /*挂电话*/
    public static void hangDownphone(){

        new Thread() {
            public void run() {
                myHandler.sendMessage(myHandler.obtainMessage(PHONE_END));
            }
        }.start();

    }
    /*添加电话号码*/
    private void addphone(String num){
        viewPager.setCurrentItem(0);
        pNumFragment.setNumber(num);
    }
    /*电话DTMF*/
    private void addphoneDtmf(String num){
        try {

            btservice.btSendDtmf(num);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
    /*选择tab*/
    private void tabSelected(int i){
        setViewSelected(R.id.rl_1,false);
        setViewSelected(R.id.rl_2,false);
        setViewSelected(R.id.rl_3,false);
        switch (i){
            case 1 :
                setViewSelected(R.id.rl_1,true);
                break;
            case 2 :
                setViewSelected(R.id.rl_2,true);
                break;
            case 3 :
                setViewSelected(R.id.rl_3,true);
                break;
        }
    }
    @Override
    public void onPageScrolled(int i, float v, int i1) {

    }

    @Override
    public void onPageSelected(int i) {
        switch (i) {
            case 0:
                tabSelected(1);
                break;
            case 1:
                tabSelected(2);
                pMemberFragment.refresh();
                break;
            case 2:
                tabSelected(3);
                pRecordFragment.refresh();
                break;

        }
    }
    /*获取通话记录*/
    public  static  void getPhoneRecord(){
        try {
            getPhoneRecordStr(btservice.getCallHistoryJsonString());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
    // 使蓝牙获取到的通话记录存储起来
    public static void getPhoneRecordStr(String str) {
        if (str != null && str.compareTo("") != 0) {
            if(phoneRecordInfos==null){
                phoneRecordInfos =new ArrayList<>();
            }
            phoneRecordInfos.clear();
            try {
                JSONArray jsonArray = new JSONArray(str);
                if(jsonArray.length()==0){
                    return;
                }
                int len = jsonArray.length();
                for (int i = 0; i < len; i++) {
                    PhoneRecordInfo info = new PhoneRecordInfo();
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String call_time = changeTimeToStandard(jsonObject.getString("callTime"));
                    String name = jsonObject.getString("name");
                   String number = jsonObject.getString("callNumber");
                   info.setName(name);
                   info.setNumber(number);
                   info.setCall_time(call_time);
                   phoneRecordInfos.add(info);
                }
                if(pRecordFragment!=null) {
                    pRecordFragment.setmData(phoneRecordInfos);
                    myHandler.sendMessage(myHandler.obtainMessage(RECORDREFRESH));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    // 使蓝牙获取到的电话本存储起来
    public static void getPhoneBook() {
        try {
            getPhoneBookStr(btservice.getContactsJsonString());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
    // 使蓝牙获取到的电话本存储起来
    public static void getPhoneBookStr(String str) {
        // subStringPrintf(str, 1024);
//        System.out.println("++++" + str);
        if (str != null && str.compareTo("") != 0) {
            if(phoneBookInfos==null){
                phoneBookInfos= new ArrayList<>();
            }
            phoneBookInfos.clear();
            try {
                JSONArray jsonArray = new JSONArray(str);
                if(jsonArray.length()==0){
                    return;
                }
                int len = jsonArray.length();
                for (int i = 0; i < len; i++) {
                    PhoneBookInfo info = new PhoneBookInfo();
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    info.setName( jsonObject.getString("Name"));
                    JSONArray object = jsonObject.getJSONArray("TypeAndNumber");
                    if (object.length() <= 0) {

                    } else {
                      String number= object.getJSONObject(0).getString("phone").replace(" ", "");
                        number = number.replace("-", "");
                        info.setNumber(number);
//                        System.out.println(info.name + " : " + info.number);
                       phoneBookInfos.add(info);

                    }
                }
                if(pMemberFragment!=null) {
                    pMemberFragment.setmData(phoneBookInfos);
                    myHandler.sendMessage(myHandler.obtainMessage(BOOKREFRESH));

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    // 改时间为标准格式
    private static String changeTimeToStandard(String time) { // 20160807T183300
        String result = "";
        if (time.length() >= 15) {
            result += time.substring(0, 4) + "-";
            result += time.substring(4, 6) + "-";
            result += time.substring(6, 8) + " ";
            result += time.substring(9, 11) + ":";
            result += time.substring(11, 13) + ":";
            result += time.substring(13, 15);
        }
        return result;
    }
    // 电话接通开始
    public static void phoneStart() {
        if (FlagProperty.flag_phone_ringcall) { // 来电显示电话号码

//            bt_call.setVisibility(View.INVISIBLE);
//            bt_back.setVisibility(View.INVISIBLE);
//            bt_stop.setVisibility(View.VISIBLE);
            FlagProperty.flag_phone_ringcall = false; // 使来电状态置为结束
        }
        if (!FlagProperty.is_one_oper) { // 打电话过程中挂断蓝牙再连接
//            bt_call.setVisibility(View.INVISIBLE);
//            bt_back.setVisibility(View.INVISIBLE);
//            bt_stop.setVisibility(View.VISIBLE);
        }
        if(tv_phone_number!=null) {
            tv_phone_number.setText(getName(FlagProperty.phone_number));
        }
        phone_call_time = 0;
        flag_phone = true;
        if(rl_call!=null) {
            rl_call.setVisibility(View.VISIBLE);//显示界面
        }
        if(ll_other!=null){
            ll_other.setVisibility(View.INVISIBLE);
        }
        new CountThread().start();
    }
    // 通话开始开启一个计时线程
    public static class CountThread extends Thread {
        @Override
        public void run() {
            while (flag_phone) {
                phone_continue_show = timeToString(phone_call_time);
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
    // 打电话持续时间转化为hh:mm:ss格式
    public static String timeToString(long time) {
        int hour = (int) (time / 3600);
        int minute = (int) ((time % 3600) / 60);
        int second = (int) (time % 60);
        return getTwoNumbers(hour) + ":" + getTwoNumbers(minute) + ":" + getTwoNumbers(second);
    }
    // 时间格式化标准
    public static String getTwoNumbers(int num) {
        if (num < 10) {
            return "0" + num;
        } else {
            return "" + num;
        }
    }
    public static Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            try {
                switch (msg.what) {
                    case PHONE_OVER:
                        tv_phone_info.setText("");
                        rl_call.setVisibility(View.GONE);
                        ll_calling_controll.setVisibility(View.VISIBLE);
                        ll_calling_key.setVisibility(View.GONE);

                        break;
                    case PHONE_CONTINUE:
                        tv_phone_info.setText(phone_continue_show);
                        break;
                    case PHONE_START:

                        if(!"".equals(number)) {
                            btservice.btDial(number);
                            rl_call.setVisibility(View.VISIBLE);
                        }

                        break;
                    case PHONE_END:
                        btservice.btHungupCall();
                        
                        break;
                    case DELETE_CLIKE:
                        if(pNumFragment!=null){
                            pNumFragment.subString();
                        }
                        break;
                    case BOOKREFRESH:
                        if(pMemberFragment!=null) {
                            pMemberFragment.refresh();
                        }
                        break;
                    case RECORDREFRESH:
                        if(pRecordFragment!=null) {
                            pRecordFragment.refresh();
                        }
                        break;
                    default:
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    };
    // 手机端切换通话
    public static void changeCallInPhone(int index) {
        if (index == 1) {
            tv_phone_number.setText(FlagProperty.phone_number_one);
            tv_keep_calltext.setText(FlagProperty.phone_number_two);
        } else if (index == 2) {
            tv_phone_number.setText(FlagProperty.phone_number_two);
            tv_keep_calltext.setText(FlagProperty.phone_number_one);
        }
    }

    // 电话接通结束
    public static void phoneStop(Context context) {
        Log.d("kondi", "BtPhone abandon audioFocus");
        if (audioManager == null) {
            audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        }
        if (audioManager.abandonAudioFocus(PhoneFragment.afChangeListener) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {

        }
        if (tv_phone_info != null) {
            tv_phone_info.setText("通话结束");
            bt_stop.setVisibility(View.VISIBLE);
            bt_call.setVisibility(View.VISIBLE);

            new CallOverThread().start();
        }

    }
    // 通话结束开启一个线程
    public static class CallOverThread extends Thread {
        @Override
        public void run() {
//            try {
//                sleep(500);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
            myHandler.sendMessage(myHandler.obtainMessage(PHONE_OVER));
        }
    }
    // 显示第三方通话来电
    public static void showKeepCall(String number) {
        // number = getName(number);
//        FlagProperty.phone_number_one = tv_phone_number.getText().toString().trim();
//        FlagProperty.phone_number_two = number;
//        tv_keep_calltext.setText(tv_phone_number.getText().toString().trim());
//        tv_phone_number.setText(number);
//
//        ll_other.setVisibility(View.VISIBLE);
    }
    // 显示第三方通话来电
    public static void showCalling(String number) {
        String phone = getName(number);
        tv_keep_calltext.setText(phone);
        tv_other_phine.setText(R.string.电话接入);
        ll_other.setVisibility(View.VISIBLE);
    }
    // 挂断第三方通话来电
    public static void showCallhangup() {
        ll_other.setVisibility(View.INVISIBLE);
    }
    // 接听第三方通话来电
    public static void showCallhAnswer() {
        String phone = getName(FlagProperty.phone_number_one);
        String phonetwo =getName(FlagProperty.phone_number_two);
        tv_phone_number.setText(phonetwo);
        tv_keep_calltext.setText(phone);
        tv_other_phine.setText(R.string.保持通话);
    }
    //保留第三方通话

    public static void showCallhKeep() {
        String phone = getName(FlagProperty.phone_number_one);
        String phonetwo =getName(FlagProperty.phone_number_two);
        tv_phone_number.setText(phone);
        tv_keep_calltext.setText(phonetwo);
        tv_other_phine.setText(R.string.保持通话);
    }
    // 当三方通话中断掉其中一方时
    public static void hideThirdCallShow(int index) {
        if (index == 1) {

            tv_phone_number.setText(FlagProperty.phone_number_two);
        } else if (index == 2) {
            tv_phone_number.setText(FlagProperty.phone_number_one);
        }
        ll_other.setVisibility(View.INVISIBLE);
    }
    // 根据号码显示电话薄中姓名
    public static String getName(String number) {
        System.out.println("number:" + number);
        for (int i = 0; i < phoneBookInfos.size(); i++) {
//            System.out.println("" + (i + 1) + ":" + DialogPhoneBook.datas.get(i).number + "结果"
//                    + (DialogPhoneBook.datas.get(i).number.compareTo(number) == 0));
            if (phoneBookInfos.get(i).getNumber().compareTo(number) == 0) {
                return phoneBookInfos.get(i).getName() + "(" + phoneBookInfos.get(i).getNumber() + ")";
            }
        }
        return number;
    }
    /*音频焦点管理*/
    public static AudioManager.OnAudioFocusChangeListener afChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        public void onAudioFocusChange(int focusChange) {
            if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {

            } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {

            } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
            }

        }
    };
    // 电话拨打
    public static void phoneCall(String number) {
        if (tv_phone_number==null) return;
        tv_phone_number.setText(getName(number));
        tv_phone_info.setText("正在呼叫...");
        bt_call.setVisibility(View.GONE);

    }
    @Override
    public void onPageScrollStateChanged(int i) {

    }
    private void   initData() {
        mFragments = new ArrayList<>();
        if(pNumFragment==null) {
            pNumFragment = new PNumFragment();
        }
        if(pMemberFragment==null){
         pMemberFragment =new PMemberFragment();}
         if(pRecordFragment==null){
         pRecordFragment =new PRecordFragment();}
        mFragments.add(pNumFragment);
        mFragments.add(pMemberFragment);
        mFragments.add(pRecordFragment);

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
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.call_key:
                setViewVisibility(R.id.ll_calling_key,true);
                setViewInVisibility(R.id.ll_calling_controll,false);
                break;
            case R.id.tv_calling_key_hide:
                setViewVisibility(R.id.ll_calling_key,false);
                setViewInVisibility(R.id.ll_calling_controll,true);
                break;
            case R.id.rl_1:
                tabSelected(1);
                viewPager.setCurrentItem(0);
                break;
            case R.id.rl_2:
                tabSelected(2);
                viewPager.setCurrentItem(1);
                break;
            case R.id.rl_3:
                tabSelected(3);
                viewPager.setCurrentItem(2);
                break;
            case  R.id.ll_other_phone:
//                changeCall();
                break;
            case R.id.iv_call:
                callphone(pNumFragment.getNumber());
                break;
            case R.id.call_stop:
                hangDownphone();
                break;
            case R.id.call_start:

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
                
        }
    }

    private void changeCall() {
        new Thread() {
            public void run() {
                try {
                    if (callIndex == 1) {
                        System.out.println(
                                "change call" + callIndex + ": " + btservice.btHoldActiveAndAcceptWaiting(2));
                    } else if (callIndex == 2) {
                        System.out.println(
                                "change call" + callIndex + ":" + btservice.btHoldActiveAndAcceptWaiting(1));
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            ;
        }.start();
    }

    /*判断蓝牙连接*/
    private void requestAudioFocus() {



        if (SystemProperties.get("sys.kd.btacconnected").compareTo("yes") == 0) {

            FlagProperty.flag_bluetooth = true;
            getPhoneRecord();
            getPhoneBook();
        } else {
            setNullViewGone(true);
        }
    }

    public static  void setNullViewGone(boolean isShow){
        if(NullView!=null) {
            NullView.setVisibility(isShow ? View.VISIBLE : View.GONE);
        }

    }
}
