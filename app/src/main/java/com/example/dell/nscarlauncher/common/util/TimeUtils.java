package com.example.dell.nscarlauncher.common.util;

import com.example.dell.nscarlauncher.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import static com.white.lib.utils.AppResUtil.getString;

/**
 * Created by Administrator on 2016/1/15.
 */
public class TimeUtils {
    Calendar calendar;

    public TimeUtils(){
        this.calendar = Calendar.getInstance();
//        this.calendar.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
    }

    //获取星期几方法
    public String getDayOfWeek(){
        String mWay = null;
        switch (calendar.get(Calendar.DAY_OF_WEEK)){
            case 1:
                mWay =getString(R.string.天);
                break;
            case 2:
                mWay =getString(R.string.一);
                break;
            case 3:
                mWay =getString(R.string.二);
                break;
            case 4:
                mWay =getString(R.string.三);
                break;
            case 5:
                mWay =getString(R.string.四);
                break;
            case 6:
                mWay =getString(R.string.五);
                break;
            case 7:
                mWay =getString(R.string.六);
                break;
        }
        return getString(R.string.星期)+mWay;
    }

    public String getDate(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");// HH:mm:ss
//获取当前时间
        Date date = new Date(System.currentTimeMillis());
        return  simpleDateFormat.format(date);
    }
    //获取十二进制 A/PM 时:分

    public String getHour(){
        String hour;
        String min ;
        hour =String.valueOf(calendar.get(Calendar.HOUR_OF_DAY)) ;
        if (calendar.get(Calendar.MINUTE) < 10)
            min = "0"+ String.valueOf(calendar.get(Calendar.MINUTE));
        else
            min = String.valueOf(calendar.get(Calendar.MINUTE));
        String hour_Min12 = hour + ":" + min;
        return hour_Min12;
    }
    //获取十二进制 A/PM 时:分
    public String getHour_Min12(){

        String hour;
        String min ;
        String dayFlag;
        if (calendar.get(Calendar.HOUR_OF_DAY)>13) {
            hour = String.valueOf(calendar.get(Calendar.HOUR_OF_DAY) - 12);
            dayFlag = "PM ";
        }
        else {
            hour = String.valueOf(calendar.get(Calendar.HOUR_OF_DAY));
            dayFlag = "AM ";
        }

        if (calendar.get(Calendar.MINUTE) < 10)
            min = "0"+ String.valueOf(calendar.get(Calendar.MINUTE));
        else
            min = String.valueOf(calendar.get(Calendar.MINUTE));

        String hour_Min12 = dayFlag + hour + ":" + min;
        //e.g: AM 11:12
        return hour_Min12;
    }

}
