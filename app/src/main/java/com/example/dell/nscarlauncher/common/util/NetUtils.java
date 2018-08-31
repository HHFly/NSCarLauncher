package com.example.dell.nscarlauncher.common.util;

/**
 * Created by lrd on 0014,2016/9/14.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.lang.reflect.Method;

import static android.content.Context.WIFI_SERVICE;

/**
 * 跟网络相关的工具类
 */
public class NetUtils {
    private NetUtils() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    /**
     * 判断网络是否连接(网络和流量连接)
     * 其实这个方法返回的结果等价于 isMobileConnected() | isWifi
     * 只要有数据流量或WiFi连接到网络，这个方法就会返回true
     *
     * @param context
     * @return
     */
    public static boolean isConnected(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (null != cm) {
            //这里使用的是getActiveNetworkInfo()，而不是getNetworkInfo(),区别在于当流量和wifi都关闭时，前者返回的null,而后者不是
            NetworkInfo info = cm.getActiveNetworkInfo();
            if (null != info && info.isConnected()) {
                return info.getState() == NetworkInfo.State.CONNECTED;
            }
        }
        return false;
    }

    /**
     * 判断是不是手机流量连接(若是wifi和流量同时有的话,由于优先使用wifi,所以此时这里会返回false)
     * 注意：若卡SIM没有流量了，这个方法会返回false,而不是分true(这个是我估计的，没有真实测过)
     *
     * @param context
     * @return
     */
    public static boolean isMobileConnected(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                return networkInfo.getType() == ConnectivityManager.TYPE_MOBILE;
            }
        }
        return false;
    }

    /**
     * 判断是否是wifi连接
     * 注意：若仅仅是打开WiFi，而没有连接到网络的话，此方法会返回false.
     */
    public static boolean isWifi(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (cm != null) {
            NetworkInfo info = cm.getActiveNetworkInfo();
            if (info != null && info.isConnected())
                return info.getType() == ConnectivityManager.TYPE_WIFI;
        }
        return false;
    }


    /**
     * 设置手机数据流量开启与关闭
     * <p>
     * 在Android 4.4 及之前（API 7 ～ API 19）[其实也包括API 20, 只不过这个版本是watch版本,手机用不到]
     * ConnectivityManager 中有 getMobileDataEnabled() 和 setMobileDataEnabled(boolean) 这两个方法的。都是@hideLeftView, 可以通过反射方式被调用,
     * 而此时 TelephonyManager 类中是没有类似的方法的
     * <p>
     * 但是自从 Android 5.0 (API 21) 开始，
     * ConnectivityManager 中就只保留了 getMobileDataEnabled() 方法，移除了 setMobileDataEnabled() 方法。
     * 但是同时 TelephonyManager 中添加了类似这两个功能的方法：setDataEnabled(boolean) 和 getDataEnabled() 方法，名字很像，只少了一个mobile单词
     *
     * @param context
     * @param isMobileDataEnabled
     */
//    public static void setMobileDataEnabled(Context context, boolean isMobileDataEnabled) {
//
//        try {
//            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT_WATCH){
//                ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//                Method method = cm.getClass().getDeclaredMethod("setMobileDataEnabled", boolean.class);
//
//                if (null != method) {
//                    method.setAccessible(true);
//                    method.invoke(cm, isMobileDataEnabled);
//                }
//            }
//            else if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP){
//                TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
//
//                Method method = tm.getClass().getDeclaredMethod("setDataEnabled", boolean.class);
//
//                if (null != method) {
//                    method.setAccessible(true);
//                    method.invoke(tm, isMobileDataEnabled);
//                }
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    /**
     * 获取手机数据流量开关的状态
     * <p>
     * 这个方法和 isMobileConnected() 功能很相似，个人估计唯一一点不同之处就是，当sim开没有流量的时候，isMobileConnected() 会返回false，而getMobileDataEnabled()会返回true.(当然这个仅仅是个人推测，没有实验过)
     *
     * @param context
     * @return
     */
    public static boolean getMobileDataEnabled(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        try {
            Method method = cm.getClass().getDeclaredMethod("getMobileDataEnabled");

            if (null != method) {
                method.setAccessible(true);
                boolean isMobileDataEnabled = (Boolean) method.invoke(cm);

                return isMobileDataEnabled;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * 设置手机数据流量开启与关闭 （从api21开始才有的这个方法）
     * 权限：android.permission.MODIFY_PHONE_STATE  但是只能是 system app 才能申明与使用这个权限
     * <p>
     * 但是我即使声明了权限，在非root版本的小米4手机上，也用不了这个方法，会出异常 InvocationTargetException
     *
     * @param context
     * @param mobileDataEnabled
     */
    public static void setMobileDataState(Context context, boolean mobileDataEnabled) {
        try {
            TelephonyManager telephonyService = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

            Method method = telephonyService.getClass().getDeclaredMethod("setDataEnabled", boolean.class);

            if (null != method) {
                method.setAccessible(true);
                method.invoke(telephonyService, mobileDataEnabled);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取手机数据流量开关是否开启（从api21才有的这个方法）
     *
     * @param context
     * @return
     */
    public static boolean getMobileDataState(Context context) {
        try {
            TelephonyManager telephonyService = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

            Method getMobileDataEnabledMethod = telephonyService.getClass().getDeclaredMethod("getDataEnabled");

            if (null != getMobileDataEnabledMethod) {
                boolean mobileDataEnabled = (Boolean) getMobileDataEnabledMethod.invoke(telephonyService);

                return mobileDataEnabled;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * 跳转到网络设置界面
     */
    public static void openSetting(Activity activity) {
        if (android.os.Build.VERSION.SDK_INT > 10) {
            //3.0以上直接打开设置界面
            activity.startActivity(new Intent(android.provider.Settings.ACTION_SETTINGS));
        } else {
            activity.startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
        }
    }


    /**
     * 获取手机信号的强度(使用反射)
     * 分为6个等级[0,5]
     *
     * @param signalStrength
     * @return
     */
    public static int getMobileLevel(SignalStrength signalStrength) {
        int level = 0;
        try {
            //获取0-4的5种信号级别，越大信号越好,但是api23开始才能直接调用
            Method method = SignalStrength.class.getDeclaredMethod("getLevel");
            if (null != method) {
                method.setAccessible(true);
                level = (Integer) method.invoke(signalStrength);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //下面的判断是为了代码的健壮性。理论上来说getLevel()的返回值就是[0,5]
        if (level < 0) level = 0;
        if (level > 5) level = 5;
        return level;
    }

    /**
     * 获取手机信号的强度. 单位 dBm
     *
     * @param context
     * @param signalStrength
     * @return
     */
    public static int getMobiledBm(Context context, SignalStrength signalStrength) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        //Log.i("Liu", signalStrength.toString());

        String ssignal = signalStrength.toString();
        String[] parts = ssignal.split(" ");
        /*
        parts[0] = "Signalstrength:"  _ignore this, it's just the title_
        parts[1] = GsmSignalStrength
        parts[2] = GsmBitErrorRate
        parts[3] = CdmaDbm
        parts[4] = CdmaEcio
        parts[5] = EvdoDbm
        parts[6] = EvdoEcio
        parts[7] = EvdoSnr
        parts[8] = LteSignalStrength
        parts[9] = LteRsrp
        parts[10] = LteRsrq
        parts[11] = LteRssnr
        parts[12] = LteCqi
        parts[13] = gsm|lte|cdma
        parts[14] = _not really sure what this number is_
        */

        int dbm = 0;
        if (tm.getNetworkType() == TelephonyManager.NETWORK_TYPE_LTE) {
            // For Lte SignalStrength: dbm = ASU - 140. //对于LTE(4G)就是这样算的, 和3G/2G不同
            dbm = Integer.parseInt(parts[8]) - 140;
        } else {
            // For GSM Signal Strength: dbm =  (2*ASU)-113.
            if (signalStrength.getGsmSignalStrength() != 99) {
                dbm = -113 + 2 * signalStrength.getGsmSignalStrength();
            }
        }
        return dbm;
    }

    /**
     * 返回WiFi信号的格数.范围:[0,4]  (可以更改范围)
     *
     * @param context
     * @return
     */
    public static int getWifiLevel(Context context) {
        int signalLevel = 0;
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo connectionInfo = wifiManager.getConnectionInfo();
        if (connectionInfo != null) {
            //第二个参数是：信号格数的范围。例如信号范围是[0,4]的话，这里就需要写5。
            signalLevel = WifiManager.calculateSignalLevel(connectionInfo.getRssi(), 5);
        }
        return signalLevel;
    }

    /**
     * 获取网络类型
     *
     * @param context
     * @return
     */
    public static String getNetworkType(Context context) {
        // 只有4G比较好识别
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String msg = "";
        switch (tm.getNetworkType()) {
            case TelephonyManager.NETWORK_TYPE_UNKNOWN://0
                msg = "NETWORK_TYPE_UNKNOWN";
                break;
            case TelephonyManager.NETWORK_TYPE_GPRS://1
                msg = "NETWORK_TYPE_GPRS";
                break;
            case TelephonyManager.NETWORK_TYPE_EDGE://2
                msg = "NETWORK_TYPE_EDGE";
                break;
            case TelephonyManager.NETWORK_TYPE_UMTS://3
                msg = "NETWORK_TYPE_UMTS";
                break;
            case TelephonyManager.NETWORK_TYPE_CDMA://4
                msg = "NETWORK_TYPE_CDMA";
                break;
            case TelephonyManager.NETWORK_TYPE_EVDO_0://5
                msg = "NETWORK_TYPE_EVDO_0";
                break;
            case TelephonyManager.NETWORK_TYPE_EVDO_A://6
                msg = "NETWORK_TYPE_EVDO_A";
                break;
            case TelephonyManager.NETWORK_TYPE_1xRTT://7
                msg = "NETWORK_TYPE_1xRTT";
                break;
            case TelephonyManager.NETWORK_TYPE_HSDPA://8
                msg = "NETWORK_TYPE_HSDPA";
                break;
            case TelephonyManager.NETWORK_TYPE_HSUPA://9
                msg = "NETWORK_TYPE_HSUPA";
                break;
            case TelephonyManager.NETWORK_TYPE_HSPA://10
                msg = "NETWORK_TYPE_HSPA";
                break;
            case TelephonyManager.NETWORK_TYPE_IDEN://11
                msg = "NETWORK_TYPE_IDEN";
                break;
            case TelephonyManager.NETWORK_TYPE_EVDO_B://12
                msg = "NETWORK_TYPE_EVDO_B";
                break;
            case TelephonyManager.NETWORK_TYPE_LTE://13 --> 4G
                msg = "NETWORK_TYPE_LTE";
                break;
            case TelephonyManager.NETWORK_TYPE_EHRPD://14
                msg = "NETWORK_TYPE_EHRPD";
                break;
            case TelephonyManager.NETWORK_TYPE_HSPAP://15
                msg = "NETWORK_TYPE_HSPAP";
                break;
            case 16://16
                msg = "NETWORK_TYPE_GSM";
                break;
            case 17://17
                msg = "NETWORK_TYPE_TD_SCDMA";
                break;
            case 18://18
                msg = "NETWORK_TYPE_IWLAN";
                break;
            case 19://19
                msg = "NETWORK_TYPE_LTE_CA";
                break;
            default:

                break;
        }
        return msg;
    }

    /**
     * 获取当前的网络连接信息
     * 0:无网络连接
     * 1:WiFi连接
     * 2:4G流量连接
     * 3:2G/3G流量连接
     */
    private int getNetWorkType(Context context) {
        int type = 0;
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        //if (info != null && info.isAvailable()) {
        if (info != null && info.isConnected()) {
            switch (info.getType()) {
                case ConnectivityManager.TYPE_WIFI:
                    //wifi
                    WifiManager manager = (WifiManager) context.getSystemService(WIFI_SERVICE);
                    WifiInfo connectionInfo = manager.getConnectionInfo();
                    int rssi = connectionInfo.getRssi();
                    Log.i("TAG", "当前为wifi网络，信号强度=" + rssi);
                    type = 1;
                    break;
                case ConnectivityManager.TYPE_MOBILE:
                    //移动网络,可以通过TelephonyManager来获取具体细化的网络类型
                    TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

                    String netWorkStatus = "";
                    //TelephonyManager 中定义了很多NETWORK_TYPE_xx的常量, 只有4G比较好区分, 2G和3G不太好区分（好像还与运营商种类有关）
                    if (telephonyManager.getNetworkType() == TelephonyManager.NETWORK_TYPE_LTE) { // 4G网络
                        //这里只简单区分两种类型网络，认为4G网络为快速，但最终还需要参考信号值
                        netWorkStatus = "4G网络";
                        type = 2;
                    } else {
                        type = 3;
                        netWorkStatus = "2G网络";
                    }
                    //信号强度应该从PhoneStatListener#onSignalStrengthsChanged()回调监听中获取
                    // （并且4G信号的强度应该是SignalStrength#mLteSignalStrength, 而非4G的信号强度应该是SignalStrength#mGsmSignalStrength）
                    Log.i("TAG", "当前为" + netWorkStatus);
                    break;
            }
        } else {
            type = 0;
        }

        return type;
    }

}
