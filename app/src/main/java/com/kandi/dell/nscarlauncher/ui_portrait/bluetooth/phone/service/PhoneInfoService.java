package com.kandi.dell.nscarlauncher.ui_portrait.bluetooth.phone.service;

import android.os.IKdBtService;
import android.os.RemoteException;

import com.kandi.dell.nscarlauncher.app.App;
import com.kandi.dell.nscarlauncher.common.util.FirstLetterUtil;
import com.kandi.dell.nscarlauncher.common.util.StringUtils;
import com.kandi.dell.nscarlauncher.ui.phone.model.PhoneBookInfo;
import com.kandi.dell.nscarlauncher.ui.phone.model.PhoneRecordInfo;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;
import java.util.regex.Pattern;


public class PhoneInfoService {
    IKdBtService btservice;
    public ArrayList<PhoneRecordInfo> phoneRecordInfos;
    public ArrayList<PhoneBookInfo> phoneBookInfos;

    public ArrayList<PhoneRecordInfo> getPhoneRecordInfos() {
        if(phoneRecordInfos==null||phoneRecordInfos.size()==0){
            getPhoneRecord();
        }
        return phoneRecordInfos;
    }

    public ArrayList<PhoneBookInfo> getPhoneBookInfos() {
        if(phoneBookInfos==null||phoneBookInfos.size()==0){
            getPhoneBook();
        }
        return phoneBookInfos;
    }

    public PhoneInfoService() {
        this.btservice = App.get().getBtservice();

    }
    public void getInfo(){
        getPhoneRecord();
        getPhoneBook();
    }
    /*获取通话记录*/
    public void getPhoneRecord(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    if(btservice != null){
                        String PhoneRecordStr=  btservice.getCallHistoryJsonString();
                        getPhoneRecordStr(PhoneRecordStr);
                    }
//            LogUtils.log(PhoneRecordStr);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }
    // 使蓝牙获取到的通话记录存储起来
    public void getPhoneRecordStr(String str) {
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

                    info.setName(StringUtils.replaceBlank(name));
                    info.setNumber(number);
                    info.setCall_time(call_time);
                    phoneRecordInfos.add(info);
                }
                App.get().getCurActivity().getPhoneFragment().myHandler.sendEmptyMessage(7);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    // 使蓝牙获取到的电话本存储起来
    public void getPhoneBook() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if(btservice != null){
                        String  ContactsJsonString =btservice.getContactsJsonString();
                        getPhoneBookStr(ContactsJsonString);
                    }
//            LogUtils.log(ContactsJsonString);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }
    // 使蓝牙获取到的电话本存储起来
    public void getPhoneBookStr(String str) {
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
                    info.setFirstLetter(FirstLetterUtil.getFirstLetter(getDuoYin(jsonObject.getString("Name"))));
                    if (object.length() <= 0) {

                    } else {
                        String number= object.getJSONObject(0).getString("phone").replace(" ", "");
                        number = number.replace("-", "");
                        info.setNumber(number);
//                        System.out.println(info.name + " : " + info.number);
                        phoneBookInfos.add(info);
                    }
                }

                if(phoneBookInfos != null){
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try{
                                Collections.sort(phoneBookInfos, new Comparator<PhoneBookInfo>() {
                                    @Override
                                    public int compare(PhoneBookInfo o1, PhoneBookInfo o2) {
                                        Collator collator = Collator.getInstance(Locale.CHINA);
                                        String name1 = getDuoYin(o1.getName());
                                        String name2 = getDuoYin(o2.getName());
                                        if (isStartWithLetter(name1)) {
                                            name1 = "9" + name1;
                                        }
                                        if (isStartWithLetter(name2)) {
                                            name2 = "9" + name2;
                                        }
                                        return collator.getCollationKey(name1).compareTo(collator.getCollationKey(name2));
                                    }
                                });
                               App.get().getCurActivity().getPhoneFragment().myHandler.sendEmptyMessage(6);
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    StringBuffer stringBuffer;
    String[] arrays = new String[]{"贾","单","沈","仇","解","翟","查","曾","晟","乐","区","冯","繁","长","石","柏","朴","缪","媛"};
    String[] toarrays = new String[]{"甲","善","深","求","谢","宅","乍","增","成","月","欧","逢","婆","帐","时","摆","普","秒","圆"};
    private String getDuoYin(String inputString){
        stringBuffer = new StringBuffer(inputString);
        for(int i=0;i<arrays.length;i++){
            if(stringBuffer.indexOf(arrays[i]) == 0){
                stringBuffer.replace(0,1,toarrays[i]);
                break;
            }
        }
        return stringBuffer.toString();
    }
    private String regex = "^[a-zA-Z].*$";

    private Pattern pattern = Pattern.compile(regex);

    public boolean isStartWithLetter(String str){
        if (str != null) {
            if (pattern.matcher(str).matches()) {
                return true;
            }
        }
        return false;
    }

    // 改时间为标准格式
    private String changeTimeToStandard(String time) { // 20160807T183300
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
}
