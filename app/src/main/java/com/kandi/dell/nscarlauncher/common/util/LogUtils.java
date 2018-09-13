package com.kandi.dell.nscarlauncher.common.util;

import android.support.annotation.IntDef;
import android.util.Log;

import java.io.EOFException;
import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Set;

import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okio.Buffer;

/**
 * 打印日志
 * Created by lenovo on 2017/8/24.
 */

public class LogUtils {
    private static final String TAG = "LogUtils";

    /**
     * 日志类型
     */
    @IntDef({
            LogType.FLAG_LOG_D,
            LogType.FLAG_LOG_E,
            LogType.FLAG_LOG_I,
            LogType.FLAG_LOG_V,
            LogType.FLAG_LOG_W,
            LogType.FLAG_LOG_WTF
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface LogType {
        int FLAG_LOG_E = 0;
        int FLAG_LOG_D = 1;
        int FLAG_LOG_I = 2;
        int FLAG_LOG_V = 3;
        int FLAG_LOG_W = 4;
        int FLAG_LOG_WTF = 5;
    }

    /**
     * 是否开启日志打印
     */
    private static boolean isOpenLog = false;
    /**
     * 默认日志类型
     */
    private static int mNormalFlag = LogType.FLAG_LOG_E;

    /**
     * 初始化配置
     *
     * @param isOpenLog 是否开启日志打印
     */
    public static void init(boolean isOpenLog) {
        init(isOpenLog, LogType.FLAG_LOG_E);
    }

    /**
     * 初始化配置
     *
     * @param isOpenLog 是否开启日志打印
     * @param flag      日志类型
     */
    public static void init(boolean isOpenLog, @LogType int flag) {
        LogUtils.isOpenLog = isOpenLog;
        LogUtils.mNormalFlag = flag;
    }

    /**
     * 格式化打印request
     *
     * @param request
     */
    public static void logFormat(Object obj, String method, Request request) {
        if (isOpenLog) {
            logFormat(obj, method, request.toString());
            //打印头信息
            Headers headers = request.headers();
            for (int i = 0, count = headers.size(); i < count; i++) {
                String name = headers.name(i);
                String value = headers.value(i);
                if (!"Content-Type".equalsIgnoreCase(name) && !"Content-Length".equalsIgnoreCase(name)) {
                    logFormat(obj, method, "[name]" + name + "--[value]" + value);
                }
            }
            //打印body
            RequestBody body = request.body();
            if (body != null) {
                Buffer buffer = new Buffer();
                try {
                    body.writeTo(buffer);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                //编码设为UTF-8
                Charset UTF8 = Charset.forName("UTF-8");
                Charset charset = UTF8;
                MediaType contentType = body.contentType();
                if (contentType != null) {
                    charset = contentType.charset(UTF8);
                }

                if (isPlaintext(buffer)) {
                    logFormat(obj, method, buffer.readString(charset));
                    logFormat(obj, method, request.method());
                }
            }

        }
    }

    /**
     * Returns true if the body in question probably contains human readable text. Uses a small sample
     * of code points to detect unicode control characters commonly used in binary file signatures.
     */
    private static boolean isPlaintext(Buffer buffer) {
        try {
            Buffer prefix = new Buffer();
            long byteCount = buffer.size() < 64 ? buffer.size() : 64;
            buffer.copyTo(prefix, 0, byteCount);
            for (int i = 0; i < 16; i++) {
                if (prefix.exhausted()) {
                    break;
                }
                int codePoint = prefix.readUtf8CodePoint();
                if (Character.isISOControl(codePoint) && !Character.isWhitespace(codePoint)) {
                    return false;
                }
            }
            return true;
        } catch (EOFException e) {
            return false; // Truncated UTF-8 sequence.
        }
    }


    /**
     * 格式化输出日志
     *
     * @param obj
     * @param method
     * @param map
     */
    public static void logFormatToMap(Object obj, String method, Map<String, String> map) {
        if (isOpenLog) {
            String className = obj.getClass().getSimpleName();
            logFormatToMap(className, method, map);
        }
    }

    /**
     * 格式化输出日志
     *
     * @param className
     * @param method
     * @param map
     */
    public static void logFormatToMap(String className, String method, Map<String, String> map) {
        if (isOpenLog) {
            Set<String> keys = map.keySet();
            for (String key : keys) {
                String value = map.get(key);
                String msg = key + ":" + value;
                logFormat(className, method, msg);
            }
        }
    }


    /**
     * 格式化输出日志
     *
     * @param obj
     * @param method
     * @param desc
     */
    public static void logFormat(Object obj, String method, String desc) {
        if (isOpenLog) {
            String className = obj.getClass().getSimpleName();
            logFormat(className, method, desc);
        }
    }

    /**
     * 格式化输出日志
     *
     * @param className
     * @param method
     * @param desc
     */
    public static void logFormat(String className, String method, String desc) {
        if (isOpenLog) {
            String msg = "[className]" + className + "|||[method]" + method + "|||[desc]" + desc;
            log(msg);
        }
    }

    /**
     * 打印日志
     *
     * @param msg
     */
    public static void log(CharSequence msg) {
        log(msg, mNormalFlag);
    }

    /**
     * 打印日志log
     *
     * @param msg
     * @param flag
     */
    private static void log(CharSequence msg, int flag) {
        switch (flag) {
            default:
            case LogType.FLAG_LOG_E: {
                Log.e(TAG, msg.toString());
                break;
            }
            case LogType.FLAG_LOG_D: {
                Log.d(TAG, msg.toString());
                break;
            }
            case LogType.FLAG_LOG_I: {
                Log.i(TAG, msg.toString());
                break;
            }
            case LogType.FLAG_LOG_V: {
                Log.v(TAG, msg.toString());
                break;
            }
            case LogType.FLAG_LOG_W: {
                Log.w(TAG, msg.toString());
                break;
            }
            case LogType.FLAG_LOG_WTF: {
                Log.wtf(TAG, msg.toString());
                break;
            }
        }
    }
}
