package com.kandi.dell.nscarlauncher.ui.home.model;

import com.kandi.dell.nscarlauncher.R;

import java.util.HashMap;

public class WeatherData {
    // 初始化天气图标
    public HashMap initWeatherMap() {
      HashMap  mWeatherMap = new HashMap<>();
        //刚好是高德地图中返回的所有天气列表： http://lbs.amap.com/api/android-sdk/guide/map-tools/weather-code
//        mWeatherMap.put("多云", R.mipmap.weather_01);
//        mWeatherMap.put("暴雪", R.mipmap.weather_02);
//        mWeatherMap.put("暴雨", R.mipmap.weather_03);
//        mWeatherMap.put("暴雨-大暴雨", R.mipmap.weather_04);
//        mWeatherMap.put("大暴雨", R.mipmap.weather_05);
//        mWeatherMap.put("大暴雨-特大暴雨", R.mipmap.weather_06);
//        mWeatherMap.put("大雪-暴雪", R.mipmap.weather_07);
//        mWeatherMap.put("大雨-暴雨", R.mipmap.weather_08);
//        mWeatherMap.put("大雪", R.mipmap.weather_09);
//        mWeatherMap.put("大雨", R.mipmap.weather_10);
//        mWeatherMap.put("冻雨", R.mipmap.weather_11);
//        mWeatherMap.put("雷阵雨", R.mipmap.weather_12);
//        mWeatherMap.put("雷阵雨并伴有冰雹", R.mipmap.weather_13);
//        mWeatherMap.put("晴", R.mipmap.weather_14);
//        mWeatherMap.put("沙尘暴", R.mipmap.weather_15);
//        mWeatherMap.put("特大暴雨", R.mipmap.weather_16);
//        mWeatherMap.put("雾", R.mipmap.weather_17);
//        mWeatherMap.put("小雪-中雪", R.mipmap.weather_18);
//        mWeatherMap.put("小雨-中雨 ", R.mipmap.weather_19);
//        mWeatherMap.put("小雪", R.mipmap.weather_20);
//        mWeatherMap.put("小雨", R.mipmap.weather_21);
//        mWeatherMap.put("阴", R.mipmap.weather_22);
//        mWeatherMap.put("雨夹雪", R.mipmap.weather_23);
//        mWeatherMap.put("阵雪", R.mipmap.weather_24);
//        mWeatherMap.put("阵雨", R.mipmap.weather_25);
//        mWeatherMap.put("中雪-大雪", R.mipmap.weather_26);
//        mWeatherMap.put("中雨-大雨", R.mipmap.weather_27);
//        mWeatherMap.put("中雪", R.mipmap.weather_28);
//        mWeatherMap.put("中雨", R.mipmap.weather_29);
//        mWeatherMap.put("浮尘", R.mipmap.weather_30);
//        mWeatherMap.put("扬沙", R.mipmap.weather_31);
//        mWeatherMap.put("强沙尘暴", R.mipmap.weather_32);
//        mWeatherMap.put("飑", R.mipmap.weather_33);
//        mWeatherMap.put("龙卷风", R.mipmap.weather_34);
//        mWeatherMap.put("弱高吹雪", R.mipmap.weather_35);
//        mWeatherMap.put("轻雾", R.mipmap.weather_17);
//        mWeatherMap.put("霾", R.mipmap.weather_37);


        mWeatherMap.put("暴雪", R.mipmap.home_icon_weather_01);
        mWeatherMap.put("暴雨", R.mipmap.home_icon_weather_02);
        mWeatherMap.put("暴雨到大暴雨", R.mipmap.home_icon_weather_03);
        mWeatherMap.put("飑", R.mipmap.home_icon_weather_04);
        mWeatherMap.put("大暴雨", R.mipmap.home_icon_weather_05);

        mWeatherMap.put("大暴雨到特大暴雨", R.mipmap.home_icon_weather_06);
        mWeatherMap.put("大雪", R.mipmap.home_icon_weather_07);
        mWeatherMap.put("大雪到暴雪", R.mipmap.home_icon_weather_08);
        mWeatherMap.put("大雨", R.mipmap.home_icon_weather_09);
        mWeatherMap.put("大雨到暴雨", R.mipmap.home_icon_weather_10);


        mWeatherMap.put("冻雨", R.mipmap.home_icon_weather_11);
        mWeatherMap.put("多云", R.mipmap.home_icon_weather_12);
        mWeatherMap.put("风", R.mipmap.home_icon_weather_13);
        mWeatherMap.put("浮尘", R.mipmap.home_icon_weather_14);
        mWeatherMap.put("雷阵雨", R.mipmap.home_icon_weather_15);

        mWeatherMap.put("雷阵雨伴有冰雹", R.mipmap.home_icon_weather_16);
        mWeatherMap.put("龙卷风", R.mipmap.home_icon_weather_17);
        mWeatherMap.put("霾", R.mipmap.home_icon_weather_18);
        mWeatherMap.put("强沙尘暴", R.mipmap.home_icon_weather_19);
        mWeatherMap.put("轻雾", R.mipmap.home_icon_weather_20);

        mWeatherMap.put("晴", R.mipmap.home_icon_weather_21);
        mWeatherMap.put("弱高吹雪", R.mipmap.home_icon_weather_22);
        mWeatherMap.put("沙尘暴", R.mipmap.home_icon_weather_23);
        mWeatherMap.put("特大暴雨", R.mipmap.home_icon_weather_24);
        mWeatherMap.put("雾", R.mipmap.home_icon_weather_25);

        mWeatherMap.put("小雪", R.mipmap.home_icon_weather_26);
        mWeatherMap.put("小雪到中雪", R.mipmap.home_icon_weather_27);
        mWeatherMap.put("小雨", R.mipmap.home_icon_weather_28);
        mWeatherMap.put("小雨到中雨", R.mipmap.home_icon_weather_29);
        mWeatherMap.put("扬尘", R.mipmap.home_icon_weather_30);

        mWeatherMap.put("阴", R.mipmap.home_icon_weather_31);
        mWeatherMap.put("雨夹雪", R.mipmap.home_icon_weather_32);
        mWeatherMap.put("阵雪", R.mipmap.home_icon_weather_33);
        mWeatherMap.put("阵雨", R.mipmap.home_icon_weather_34);
        mWeatherMap.put("中雪", R.mipmap.home_icon_weather_35);

        mWeatherMap.put("中雪到大雪", R.mipmap.home_icon_weather_36);
        mWeatherMap.put("中雨", R.mipmap.home_icon_weather_37);
        mWeatherMap.put("中雨到大雨", R.mipmap.home_icon_weather_38);
        return  mWeatherMap;
    }
}
