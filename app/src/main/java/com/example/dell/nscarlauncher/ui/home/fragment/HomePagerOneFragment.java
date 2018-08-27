package com.example.dell.nscarlauncher.ui.home.fragment;

import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.provider.Settings;
import android.support.annotation.DrawableRes;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.weather.LocalWeatherForecastResult;
import com.amap.api.services.weather.LocalWeatherLive;
import com.amap.api.services.weather.LocalWeatherLiveResult;
import com.amap.api.services.weather.WeatherSearch;
import com.amap.api.services.weather.WeatherSearchQuery;
import com.example.dell.nscarlauncher.R;
import com.example.dell.nscarlauncher.app.App;
import com.example.dell.nscarlauncher.base.AppConst;
import com.example.dell.nscarlauncher.base.fragment.BaseFragment;
import com.example.dell.nscarlauncher.common.util.JumpUtils;
import com.example.dell.nscarlauncher.common.util.SPUtil;
import com.example.dell.nscarlauncher.common.util.TimeUtils;
import com.example.dell.nscarlauncher.ui.fm.FMFragment;
import com.example.dell.nscarlauncher.ui.home.HomePagerActivity;
import com.example.dell.nscarlauncher.ui.home.androideunm.FragmentType;
import com.example.dell.nscarlauncher.ui.home.androideunm.HandleKey;
import com.example.dell.nscarlauncher.ui.home.model.WeatherData;
import com.example.dell.nscarlauncher.widget.PlayControllView;
import com.example.dell.nscarlauncher.widget.WaveView;
import com.white.lib.utils.ToastUtil;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.example.dell.nscarlauncher.ui.fm.FMFragment.FMCHANNEL;

public class HomePagerOneFragment extends BaseFragment  implements WeatherSearch.OnWeatherSearchListener {
    private WaveView circleView;
    private static TextView tv_w_time;
    private static TextView tv_w_date;
    private static TextView tv_w_week;
    private HomePagerActivity homePagerActivity;
    private HashMap<String, Integer> mWeatherMap; // 天气类型与对应的图标
    //定位客户端,以及参数
    private AMapLocationClient mLocationClient;
    private AMapLocationClientOption mLocationOption;
    //thread flag
    private volatile boolean timeFlag = true;
    private volatile boolean weatherFlag = true;
  // 播发控制
    private PlayControllView fmPaly ;
    //fragment
    private  FMFragment fmFragment;
    private float channel;

    public void setFragment(HomePagerActivity homePagerActivity,FMFragment fmFragment) {
        this.homePagerActivity = homePagerActivity;
        this.fmFragment =fmFragment;
    }

    @Override
    public int getContentResId() {
        return R.layout.fragment_home1;
    }

    @Override
    public void findView() {
        circleView =getView(R.id.wave_view);
//        fmPaly=getView(R.id.fm_playcontroll);
        tv_w_time =getView(R.id.tv_w_time);
        tv_w_date =getView(R.id.tv_w_date);
        tv_w_week=getView(R.id.tv_w_week);

    }

    @Override
    public void setListener() {
        setClickListener(R.id.FM);
        setClickListener(R.id.bt_music);
        setClickListener(R.id.music);
        setClickListener(R.id.rl_air);

    }
    private void setFmPalyListen(){
        fmPaly.setOnItemClickListener(new PlayControllView.OnItemClickListener() {
            @Override
            public void onClickLeft() {
                if(fmFragment!=null){
                    fmFragment.leftFm();
                }
            }

            @Override
            public void onClickCenter(boolean isPlay) {
                FmPaly(isPlay);

            }

            @Override
            public void onClickRight() {
                if(fmFragment!=null){
                    fmFragment.rightFm();

                }
            }
        });
    }
    private void  FmPaly(boolean isPlay){
        if(isPlay){
            if(fmFragment!=null){
                fmFragment.openFm();

            }
        }else {
            if(fmFragment!=null){
                fmFragment.closeFm();

            }
        }
    }
    @Override
    public void onResume() {
        super.onResume();

    }
    @Override
    public void onDestroy() {
        super.onDestroy();

    }
    @Override
    public void initView() {

        init_time();
        initLocation();
        circleView.setProgress(50);
        setFmMHZ();
    }
    private void setFmMHZ(){
        channel= SPUtil.getInstance(getContext(),FMCHANNEL).getFloat(FMCHANNEL,93.0f);
        setTvText(R.id.tv_fm_hz,String.valueOf(channel));
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.FM:
                if(homePagerActivity!=null){
                    homePagerActivity.jumpFragment(FragmentType.FM);
                }
                break;
            case R.id.bt_music:
                if(homePagerActivity!=null){
                    homePagerActivity.jumpFragment(FragmentType.BTMUSIC);
                }
                break;
            case R.id.music:
                if(homePagerActivity!=null){
                    homePagerActivity.jumpFragment(FragmentType.MUSIC);
                }
                break;
            case R.id.rl_air:
                JumpUtils.actAPK(getActivity(),FragmentType.AIRCONTROLL);
                    break;
        }
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
                        App.pagerOneHnadler.sendMessage(msgtimedata);
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }



    public static class PagerOneHnadler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            TimeUtils timeUtils =new TimeUtils();
            switch (msg.what){
                case HandleKey.TIME:
                    tv_w_time.setText(timeUtils.getHour());
                    tv_w_date.setText(timeUtils.getDate());
                    tv_w_week.setText(timeUtils.getDayOfWeek());
                    break;
                case HandleKey.WEATHAER:
                    break;
            }
            super.handleMessage(msg);

        }
    }
    /*刷新布局*/
    public void  freshlayout(FMFragment fmFragment){
        setFmMHZ();
    }
    // 初始化定位
    private void initLocation() {
        //初始化定位
        mLocationClient = new AMapLocationClient(getContext());
        //设置定位回调监听
        mLocationClient.setLocationListener(mLocationListener);

        //初始化定位参数
        mLocationOption = new AMapLocationClientOption();

        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Battery_Saving);//可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
        mLocationOption.setGpsFirst(false);//可选，设置是否gps优先，只在高精度模式下有效。默认关闭
        mLocationOption.setHttpTimeOut(30000);//可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
        mLocationOption.setInterval(2000);//可选，设置定位间隔。默认为2秒
        mLocationOption.setNeedAddress(true);//可选，设置是否返回逆地理地址信息。默认是true
        mLocationOption.setOnceLocation(true);//可选，设置是否单次定位。默认是false
        mLocationOption.setOnceLocationLatest(false);//可选，设置是否等待wifi刷新，默认为false.如果设置为true,会自动变为单次定位，持续定位时不要使用. 提高首次定位精度.
        AMapLocationClientOption.setLocationProtocol(AMapLocationClientOption.AMapLocationProtocol.HTTP);//可选， 设置网络请求的协议。可选HTTP或者HTTPS。默认为HTTP
        mLocationOption.setSensorEnable(false);//可选，设置是否使用传感器。默认是false
        mLocationOption.setWifiScan(true); //可选，设置是否开启wifi扫描。默认为true，如果设置为false会同时停止主动刷新，停止以后完全依赖于系统刷新，定位位置可能存在误差
        mLocationOption.setLocationCacheEnable(true); //可选，设置是否使用缓存定位，默认为true


        //设置定位参数
        mLocationClient.setLocationOption(mLocationOption);

        //mLocationClient.startLocation();//启动定位
    }

    public AMapLocationListener mLocationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {
            if (aMapLocation != null) {
                //获取到结果后,就停止定位

//                KLog.d("mLocationListener 回调 --> errorCode: " + aMapLocation.getErrorCode() + ", mLocationClient: " + mLocationClient);

                if (aMapLocation.getErrorCode() == 0) {
                    String city = aMapLocation.getCity();
                    setTvText(R.id.tv_w_location,city);
                    initWeather(city);



                    Settings.System.putString(getActivity().getContentResolver(), "poi_city", city);

//                    KLog.e("onLocationChanged --> city: " + city);
                } else {
                    //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                    Log.e("Liu", "location Error, ErrCode:" + aMapLocation.getErrorCode() + ", errInfo:" + aMapLocation.getErrorInfo());
                    if (aMapLocation.getErrorCode() == 19) {
                        Toast.makeText(getActivity(), "定位失败，由于手机没插sim卡且WIFI功能被关闭", Toast.LENGTH_SHORT).show();
                    }


                    // 定位失败, 则5s后再次重新定位
//                    mLocationClient.stopLocation();
//                    mHandler.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            KLog.e("再次启动定位");
//                            mLocationClient.startLocation();//启动定位
//                        }
//                    }, 5000);
                }
            }
        }
    };

    /**
     * 初始化天气
     *
     * @param city 城市名称或adcode （根据定位获取当前位置）
     */
    private void initWeather(String city) {

//        KLog.d("initWeather city: " + city);
        WeatherData data =new WeatherData();
        if (mWeatherMap == null) {
            mWeatherMap= data.initWeatherMap();
        }
        if (!TextUtils.isEmpty(city)) {
            String temp =SPUtil.getInstance(getContext(),AppConst.TEMPERATURE).getString(AppConst.TEMPERATURE,"20");

            setTvText(R.id.tv_w_temperature,temp);
            @DrawableRes int weather = SPUtil.getInstance(getContext(),AppConst.WEATHER_ICON_ID).getInt(AppConst.WEATHER_ICON_ID, R.mipmap.weather_01);
            setIvImage(R.id.iv_w_weather,weather);


            //检索参数为城市(可以写名称或adcode)和天气类型，实况天气为WEATHER_TYPE_LIVE、天气预报为WEATHER_TYPE_FORECAST
            WeatherSearchQuery weatherSearchQuery = new WeatherSearchQuery(city, WeatherSearchQuery.WEATHER_TYPE_LIVE);
            WeatherSearch weatherSearch = new WeatherSearch(getContext());
            weatherSearch.setOnWeatherSearchListener(this);
            weatherSearch.setQuery(weatherSearchQuery);
            weatherSearch.searchWeatherAsyn(); //异步搜索

            Log.d("Liu_Weather", "开始请求天气信息");
        }
    }

    //==============OnWeatherSearchListener两个回调方法===================

    /**
     * 实时天气查询回调
     */
    @Override
    public void onWeatherLiveSearched(LocalWeatherLiveResult weatherLiveResult, int rCode) {
        Log.d("Liu_Weather", " 实时天气查询回调 onWeatherLiveSearched: rCode: " + rCode);

        if (rCode == AMapException.CODE_AMAP_SUCCESS) {
            if (weatherLiveResult != null && weatherLiveResult.getLiveResult() != null) {
                LocalWeatherLive weatherlive = weatherLiveResult.getLiveResult();

                String windDirection = weatherlive.getWindDirection();// 获取风向
                String windPower = weatherlive.getWindPower();//获取风的级数
                String humidity = weatherlive.getHumidity();//获取湿度（百分数）
                String adCode = weatherlive.getAdCode();
                String city = weatherlive.getCity();
                String reportTime = weatherlive.getReportTime();// 发布时间

                String temperature = weatherlive.getTemperature();//获取温度
                setTvText(R.id.tv_w_temperature,temperature);
                String weather = weatherlive.getWeather(); // 获取天气名称： 晴,多云,阴,阵雨,雷阵雨....
                Integer weatherImgResId = mWeatherMap.get(weather);//根据天气名称获取对应的图片资源的id
                setIvImage(R.id.iv_w_weather,weatherImgResId);



                Log.d("Liu_Weather", "具体天气信息: weather: " + weather + "temperature: " + temperature + ", windDirection: " + windDirection + ", windPower: "
                        + windPower + ", humidity: " + humidity + ", adCode: " + adCode + ", city: " + city + ",reportTime: " + reportTime);


                //持久化存储
                SPUtil.getInstance(getContext(),AppConst.TEMPERATURE).putString(AppConst.TEMPERATURE, temperature);
                SPUtil.getInstance(getContext(),AppConst.WEATHER_ICON_ID).putInt(AppConst.WEATHER_ICON_ID, weatherImgResId);
                SPUtil.getInstance(getContext(),AppConst.WEATHER_HOUR).putInt(AppConst.WEATHER_HOUR, Calendar.getInstance(Locale.CHINA).get(Calendar.HOUR_OF_DAY)); // HOUR_OF_DAY 24小时制

                if (mLocationClient != null) {
//                    KLog.d("onWeatherLiveSearched, then close the LocationClient");
                    mLocationClient.stopLocation();
                }
            } else {
//                KLog.e(getResources().getString(R.string.no_result));
                ToastUtil.show(getContext(), "");
            }
        } else {
//            Log.d("Liu_Weather", ToastUtil.getErrorMsgByCode(rCode));
            //ToastUtil.showerror(getApplicationContext(), rCode);
        }
    }

    @Override
    public void onWeatherForecastSearched(LocalWeatherForecastResult localWeatherForecastResult, int i) {

    }

}
