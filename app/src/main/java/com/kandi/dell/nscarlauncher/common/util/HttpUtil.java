package com.kandi.dell.nscarlauncher.common.util;

import android.os.Handler;
import android.os.Looper;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by eric on 2017/12/12.
 */

public class HttpUtil {
    private OkHttpClient client = new OkHttpClient();
    private String mUrl;
    private Map<String, String> mParam;
    private HttpResponse mHttpResponse;
    private Handler mHandler = new Handler(Looper.getMainLooper());

    public HttpUtil(HttpResponse response){
        this.mHttpResponse = response;
    }

    public interface HttpResponse{
        void onSuccess(Object obj);
        void onFail(String error);
    }

    public void sendPostHttp(String url, Map<String, String> param){
        sendHttp(url,param,true);
    }

    public void sendGetHttp(String url, Map<String, String> param){
        sendHttp(url,param,false);
    }

    private void sendHttp(String url, Map<String, String> param, boolean isPost){
        this.mUrl = url;
        this.mParam = param;
        run(isPost);
    }

    private void run(boolean isPost){
        Request request = createRequest(isPost);
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (mHttpResponse != null){
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mHttpResponse.onFail("请求错误");
                        }
                    });
                }
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (mHttpResponse != null){
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (!response.isSuccessful()){
                                mHttpResponse.onFail("请求失败");
                            } else {
                                try {
                                    mHttpResponse.onSuccess(response.body().string());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    mHttpResponse.onFail("结果转换失败");
                                }
                            }
                        }
                    });
                }
            }
        });
    }

    private Request createRequest(boolean isPost){
        Request request;
        if (isPost){
            MultipartBody.Builder requestBodyBuilder = new MultipartBody.Builder();
            requestBodyBuilder.setType(MultipartBody.FORM);
            Iterator<Map.Entry<String,String>> iterator = mParam.entrySet().iterator();
            while ((iterator.hasNext())){
                Map.Entry<String,String> entry = iterator.next();
                requestBodyBuilder.addFormDataPart(entry.getKey(),entry.getValue());
            }
            request = new Request.Builder().url(mUrl)
                    .post(requestBodyBuilder.build())
                    .build();
        } else {
            String urlString = mUrl+"?"+MapParamToString(mParam);
            request = new Request.Builder().url(urlString)
                    .build();
        }
        return request;
    }

    private String MapParamToString(Map<String, String> param){
        StringBuilder stringBuilder = new StringBuilder();
        Iterator<Map.Entry<String,String>> iterator = param.entrySet().iterator();
        while (iterator.hasNext()){
            Map.Entry<String,String> entry = iterator.next();
            stringBuilder.append(entry.getKey()+"="+entry.getValue()+"&");
        }
        String str = stringBuilder.toString().substring(0,stringBuilder.length()-1);
        return str;
    }

}
