package com.kandi.dell.nscarlauncher.base;

/**
 * Created by ronda on 17-7-28/28.
 */

public interface AppConst {
    //todo======================SharedPreferences相关==================================
    /**
     * 天气图标
     */
    String WEATHER_ICON_ID = "weather_icon_id";
    /**
     * 天气图标
     */
    String WEATHER = "weather";

    /**
     * 温度
     */
    String TEMPERATURE = "temperature";

    /**
     * 获取weather的时间(小时),即（实时天气查询回调 onWeatherLiveSearched()的时间）
     */
    String WEATHER_HOUR = "weather_hour";

    /**
     * 上次保存的非0的音量值
     */
    String LAST_NONZERO_VOLUME = "last_nonzero_volume";

    /**
     * mic是否静音
     */
    String MIC_MUTE = "mic_mute";

    /**
     * 语法识别时，构建云端语法的grammarId的键
     */
    String KEY_GRAMMAR_ABNF_ID = "key_grammar_abnf_id";
}
