package com.kandi.dell.nscarlauncher.ui.phone.mvp.mpl;

import com.google.gson.Gson;
import com.kandi.dell.nscarlauncher.common.util.HttpUtil;
import com.kandi.dell.nscarlauncher.common.util.NumberUtil;
import com.kandi.dell.nscarlauncher.ui.phone.mvp.MvpMainView;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by eric on 2017/12/12.
 */

public class MainPresenter {
    /**
     * 中国电信号码格式验证 手机段： 133,153,180,181,189,177,1700,173,199
     **/
    private static final String CHINA_TELECOM_PATTERN = "(^1(33|53|77|73|99|8[019])\\d{8}$)|(^1700\\d{7}$)";

    /**
     * 中国联通号码格式验证 手机段：130,131,132,155,156,185,186,145,176,1709
     **/
    private static final String CHINA_UNICOM_PATTERN = "(^1(3[0-2]|4[5]|5[56]|7[6]|8[56])\\d{8}$)|(^1709\\d{7}$)";

    /**
     * 中国移动号码格式验证
     * 手机段：134,135,136,137,138,139,150,151,152,157,158,159,182,183,184,187,188,147,178,1705
     **/
    private static final String CHINA_MOBILE_PATTERN = "(^1(3[4-9]|4[7]|5[0-27-9]|7[8]|8[2-478])\\d{8}$)|(^1705\\d{7}$)";

    private static final String mUrl = "http://ws.webxml.com.cn/WebServices/MobileCodeWS.asmx/getMobileCodeInfo";
    private MvpMainView mvpMainView;
    private String phoneInfo;
    private Gson gson = new Gson();
    public MainPresenter(MvpMainView mvpMainView){
        this.mvpMainView = mvpMainView;
    }

    public String getPhone() {
        return phoneInfo;
    }

    public void searchPhoneInfo(String phoneString){
         if (!NumberUtil.isCellPhone(phoneString)){
             return;
         }
        //http request method
        sendHttp(phoneString);
    }

    private void sendHttp(final String phoneString){
        Map<String,String> map = new HashMap<String, String>();
        map.put("mobileCode",phoneString);
        map.put("userId","");
        HttpUtil httpUtil = new HttpUtil(new HttpUtil.HttpResponse() {
            @Override
            public void onSuccess(Object obj) {
                String json = obj.toString();
                if(json.contains("没有此号码记录")){
                    return;
                }
                int index = json.indexOf(phoneString)+phoneString.length()+1;
                json = json.substring(index,json.lastIndexOf(" "));
                phoneInfo = json + "," + isChinaMobilePhoneNum(phoneString);
                mvpMainView.updateView();
            }

            @Override
            public void onFail(String error) {
                phoneInfo = "";
                mvpMainView.updateView();
            }
        });
        httpUtil.sendGetHttp(mUrl,map);
    }

    /**
     * 查询电话属于哪个运营商
     *
     * @param tel 手机号码
     * @return 0：不属于任何一个运营商，1:移动，2：联通，3：电信
     */
    public String isChinaMobilePhoneNum(String tel) {
        boolean b1 = tel == null || tel.trim().equals("") ? false : match(CHINA_MOBILE_PATTERN, tel);
        if (b1) {
            return "移动";
        }
        b1 = tel == null || tel.trim().equals("") ? false : match(CHINA_UNICOM_PATTERN, tel);
        if (b1) {
            return "联通";
        }
        b1 = tel == null || tel.trim().equals("") ? false : match(CHINA_TELECOM_PATTERN, tel);
        if (b1) {
            return "电信";
        }
        return "";
    }

    public boolean match(String regEx,String tel){
        // 编译正则表达式
        Pattern pattern = Pattern.compile(regEx);
        Matcher match=pattern.matcher(tel);
        boolean firstMath = false;
        while (match.find()){
            if(match.start() == 0){
                firstMath = true;
                break;
            }
        }
        return firstMath;
    }

}
