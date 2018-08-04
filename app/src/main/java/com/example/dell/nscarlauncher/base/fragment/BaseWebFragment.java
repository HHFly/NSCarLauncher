package com.example.dell.nscarlauncher.base.fragment;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.webkit.GeolocationPermissions;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.dell.nscarlauncher.BuildConfig;
import com.example.dell.nscarlauncher.common.LogUtils;


/**
 * 网页fragment
 */

public abstract class BaseWebFragment extends BaseFragment implements View.OnClickListener {
    private final String TAG = getClass().getSimpleName();

    //参数
    private String mUrl;
    private String mTitle;

    //View
    private WebView mWebView;         //webView
    private TextView mTvTitle;        //标题
    private View mViewReturn;         //返回
    private ProgressBar mPb;           //网页加载进度条
    private ProgressBar mPbLoading;   //加载中动画

    //View的id
    private int mWebViewResId;
    private int mTvTitleResId;
    private int mViewReturnResId;
    private int mPbResId;
    private int mPbLoadingResId;

    private int mPbLoadingIndeterminateDrawableAfter6;

    private boolean isLoadError;    //加载错误
    private boolean isLoadFinish = true;

    protected Runnable runnable;

    /**
     * 设置View的id
     */
    public abstract void bindViewResId();

    @Override
    public void findView() {
        bindViewResId();
        mWebView = getView(mWebViewResId);
        mTvTitle = getView(mTvTitleResId);
        mViewReturn = getView(mViewReturnResId);
        mPb = getView(mPbResId);
        mPbLoading = getView(mPbLoadingResId);
    }

    @Override
    public void setListener() {

    }

    @Override
    public void initView() {
        bindView();
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        mWebView = null;
        mTvTitle = null;
        mPb = null;
        mViewReturn = null;
        mPbLoading = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        webChromeClient = null;
        webViewClient = null;
    }

    /**
     * 设置View完成
     */
    public void bindViewFinish() {

    }

    /**
     * 设置View的属性
     */
    private void bindView() {
        if (isLoadFinish) {
            webViewSetting();
            loadUrl(mUrl);
            setTitlebarTitle(mTitle);
            setPbLoadingDrawable();
            setTitlebarReturn();
            bindViewFinish();
        }
    }

    /**
     * 设置标题栏回退
     */
    private void setTitlebarReturn() {
        if (mViewReturn != null) {
            mViewReturn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getActivity() != null) {
                        getActivity().finish();
                    }
                }
            });
        }
    }

    /**
     * 设置pbLoadding动画
     */
    private void setPbLoadingDrawable() {
        ProgressBar pbLoad = mPbLoading;
        if (pbLoad != null) {
            Drawable drawable;
            if (Build.VERSION.SDK_INT > 22) {
                drawable = getActivity().getDrawable(mPbLoadingIndeterminateDrawableAfter6);
                pbLoad.setIndeterminateDrawable(drawable);
            }
        }
    }

    /**
     * 设置标题栏标题
     */
    private void setTitlebarTitle(String title) {
        TextView tv = mTvTitle;
        if (tv != null) {
            if (title == null) {
                title = "";
            }
            tv.setText(title);
        }
    }

    /**
     * 设置webView
     */
    private void webViewSetting() {
        WebView webView = mWebView;
        if (webView == null) {
            LogUtils.logFormat(TAG, "webViewSetting", "webView is null");
            return;
        }

        //打开网页时不调用默认系统浏览器
        WebSettings settings = webView.getSettings();

        //设置UA
//        settings.setUserAgentString(AppContext.getUserAgentString());
        //将图片调整到适合WebView大小
        settings.setUseWideViewPort(true);
        //缩放到屏幕大小
        settings.setLoadWithOverviewMode(true);
        //支持js
        settings.setJavaScriptEnabled(true);
        //支持自动加载图片
        settings.setLoadsImagesAutomatically(true);
        //支持内容重新布局
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        //支持通过JS打开新窗口
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        //编码
        settings.setDefaultTextEncodingName("utf-8");
        //启动地理定位
        settings.setGeolocationEnabled(true);
        //支持获取手势焦点
        webView.requestFocusFromTouch();

        settings.setAllowFileAccess(true);
        settings.setAllowFileAccessFromFileURLs(true);
        settings.setAllowUniversalAccessFromFileURLs(true);
        settings.setDomStorageEnabled(true);

        webView.addJavascriptInterface(this, BuildConfig.WEB_INTERFACE);

        webView.setWebChromeClient(webChromeClient);
    }

    /**
     * 加载网页
     *
     * @param url
     */
    private void loadUrl(String url) {
        LogUtils.logFormat(TAG, "loadUrl", "[url]" + url);
        WebView webView = mWebView;
        if (webView == null) {
            LogUtils.logFormat(TAG, "loadUrl", "webView is null");
            return;
        }
        if (TextUtils.isEmpty(url)) {
            LogUtils.logFormat(TAG, "loadUrl", "url is null");
            return;
        }
        //开始加载网页
        webView.setWebViewClient(webViewClient);
        webView.loadUrl(url);
    }

    /**
     * 设置加载动画是否显示
     *
     * @param isVisibility
     */
    private void setPbLoadingVisibility(boolean isVisibility) {
        if (mPbLoading != null) {
            mPbLoading.setVisibility(isVisibility ? View.VISIBLE : View.GONE);
        }
    }

    /**
     * 进度条回调
     */
    private WebChromeClient webChromeClient = new WebChromeClient() {
        @Override
        public void onGeolocationPermissionsShowPrompt(final String origin, final GeolocationPermissions.Callback callback) {
            final boolean remember = true;
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("位置信息");
            builder.setMessage(origin + "允许获取您的地理位置信息吗？").setCancelable(true).setPositiveButton("允许",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog,
                                            int id) {
                            callback.invoke(origin, true, remember);
                        }
                    })
                    .setNegativeButton("不允许",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                    callback.invoke(origin, false, remember);
                                }
                            });
            AlertDialog alert = builder.create();
            alert.show();
        }

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            ProgressBar pb = mPb;
            if (pb != null) {
                if (newProgress >= 100) {
                    pb.setVisibility(View.GONE);
                } else {
                    pb.setProgress(newProgress);
                    pb.setVisibility(View.VISIBLE);
                }
            }
        }
    };

    /**
     * WebView回调
     */
    private WebViewClient webViewClient = new WebViewClient() {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            super.shouldOverrideUrlLoading(view, url);
            LogUtils.logFormat(TAG, "shouldOverrideUrlLoading", "[url]" + url);
            isLoadError = false;
            if (view != null) {
                view.loadUrl(url);
            }
            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            isLoadFinish = false;
            LogUtils.logFormat(TAG, "onPageStarted", "");
            setPbLoadingVisibility(true);
//            setShowNothing(false);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            LogUtils.logFormat(TAG, "onPageFinished", "");
            setPbLoadingVisibility(false);
            if (isLoadError) {
                //加载出错
                view.setVisibility(View.GONE);
//                setShowNothing(true);
            } else {
                //加载未出错
                view.setVisibility(View.VISIBLE);
//                setShowNothing(false);
//                accessToken();
            }
            isLoadFinish = true;
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
            LogUtils.logFormat(TAG, "onReceivedError", "");
            isLoadError = true;
        }
    };


    /**
     * 设置webView的id
     *
     * @param id
     */
    public void setWebViewResId(@IdRes int id) {
        mWebViewResId = id;
    }

    /**
     * 设置标题的id
     *
     * @param id
     */
    public void setTitleResId(@IdRes int id) {
        mTvTitleResId = id;
    }

    /**
     * 设置加载进度条id
     *
     * @param id
     */
    public void setPbResId(@IdRes int id) {
        mPbResId = id;
    }

    /**
     * 设置加载动画id
     *
     * @param id
     */
    public void setPbLoadingResId(@IdRes int id) {
        mPbLoadingResId = id;
    }

    /**
     * 设置回退id
     *
     * @param id
     */
    public void setViewReturnResId(@IdRes int id) {
        mViewReturnResId = id;
    }

    /**
     * 设置链接地址
     *
     * @param url
     */
    public void setUrl(String url) {
        mUrl = url;
    }

    /**
     * 设置标题文本
     *
     * @param title
     */
    public void setTitleText(String title) {
        mTitle = title;
    }

    /**
     * 设置加载动画6.0之后的资源文件
     *
     * @param id
     */
    public void setPbLoadingIndeterminateDrawableAfter6(@DrawableRes int id) {
        mPbLoadingIndeterminateDrawableAfter6 = id;
    }

    /**
     * 获取webView
     *
     * @return
     */
    public WebView getWebView() {
        return mWebView;
    }

    /**
     * 是否加载完毕
     *
     * @return
     */
    public boolean isLoadFinish() {
        return isLoadFinish;
    }

    @JavascriptInterface
    public void test() {
    }


}
