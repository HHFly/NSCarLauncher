package com.example.dell.nscarlauncher.base.Activity;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.AnimRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dell.nscarlauncher.app.App;
import com.example.dell.nscarlauncher.base.ShowLoadingDialogListener;
import com.example.dell.nscarlauncher.base.dialog.LoadingDialog;
import com.example.dell.nscarlauncher.common.FrescoUtils;
import com.example.dell.nscarlauncher.common.ToastUtils;
import com.facebook.drawee.view.SimpleDraweeView;
import com.gyf.barlibrary.BarHide;
import com.gyf.barlibrary.ImmersionBar;
import com.white.lib.utils.location.LocationManagerUtil;
import com.zhy.autolayout.AutoLayoutActivity;

import java.io.File;
import java.util.List;



/**
 * activity基类
 */

public abstract class BaseActivity extends AutoLayoutActivity implements View.OnClickListener, ShowLoadingDialogListener {
    private final String TAG = getClass().getSimpleName();
    /**
     * 存放View
     */
    private SparseArray<View> mViews;
    /**
     * 是否非第一次执行onResume方法
     */
    private boolean isSecondResume;
    /**
     * 资源文件空值
     */
    protected final int RESID_NULL = 0;
    /**
     * 加载中对话框
     */
    private LoadingDialog mLoadingDialog;
    /**
     * 加载对话框是否显示
     */
    private boolean isLoadingShow;
    /**
     * 定位管理工具类
     */
    private LocationManagerUtil mLocationManagerUtil;
    protected ImmersionBar mImmersionBar;
    protected void superOnCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isOnCreate()) {
            if (getContentViewResId() != 0) {
                setContentView(getContentViewResId());
            }
            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                getIntentParam(bundle);
            }
            //初始化沉浸式
            if (isImmersionBarEnabled())
                initImmersionBar();
            findView();
            setListener();
            initView();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isSecondResume) {
            onUnFirstResume();
        } else {
            isSecondResume = true;
        }
    }

    /**
     * 显示加载中对话框
     */
    @Override
    public void showLoadingDialog() {
        if (mLoadingDialog == null) {
            mLoadingDialog = new LoadingDialog();
        }
        if (!isLoadingShow && !mLoadingDialog.isAdded()) {
            isLoadingShow = true;
            mLoadingDialog.show(getFragmentManager());
        }
    }
    
    /**
     * 隐藏加载中对话框
     */
    @Override
    public void hideLoadingDialog() {
        if (mLoadingDialog != null) {
//        if (mLoadingDialog.isAdded()) {
            mLoadingDialog.dismissAllowingStateLoss();
            isLoadingShow = false;
//        }
        }
    }

    @Override
    public void onClick(View v) {

    }

    /**
     * 是否调用oncreate
     *
     * @return
     */
    public boolean isOnCreate() {
        return true;
    }

    /**
     * 获取内容资源文件
     *
     * @return
     */
    @LayoutRes
    public abstract int getContentViewResId();

    /**
     * 获取Intent传递的参数
     *
     * @param bundle
     */
    public void getIntentParam(Bundle bundle) {

    }

    /**
     * 获取实例
     */
    public void findView() {

    }

    /**
     * 设置监听
     */
    public void setListener() {

    }

    /**
     * 初始化View
     */
    public abstract void initView();

    /**
     * onResume中第一次不执行的方法
     */
    public void onUnFirstResume() {
    }

    @Override
    protected void onDestroy() {
        if (getFragmentManager() != null && mLoadingDialog != null) {
            mLoadingDialog.dismissAllowingStateLoss();
            mLoadingDialog = null;
        }
        if (mLocationManagerUtil != null) {
            mLocationManagerUtil.unRegisterForThisCallback();
            mLocationManagerUtil = null;
        }
        super.onDestroy();
        if (mViews != null) {
            int count = mViews.size();
            for (int i = 0; i < count; i++) {
                int key = mViews.keyAt(i);
                View view = mViews.get(key);
                destroyView(view);
            }
            mViews.clear();
            mViews = null;
        }
        if (mImmersionBar != null)
            mImmersionBar.destroy();  //在BaseActivity里销毁
    }
    protected void initImmersionBar() {
        //在BaseActivity里初始化
        mImmersionBar = ImmersionBar.with(this);
        mImmersionBar.init();
        mImmersionBar.hideBar(BarHide.FLAG_HIDE_BAR).init();
    }
    /**
     * 是否可以使用沉浸式
     * Is immersion bar enabled boolean.
     *
     * @return the boolean
     */
    protected boolean isImmersionBarEnabled() {
        return true;
    }

    /**
     * 释放View
     *
     * @param view
     */
    private void destroyView(View view) {
        if (view != null) {
            if (view instanceof RecyclerView) {
                RecyclerView rv = (RecyclerView) view;
                rv.setAdapter(null);
            }
        }
    }

    /**
     * 该资源文件id是否为空
     *
     * @param resId
     * @return
     */

    public boolean isResIdNull(@LayoutRes int resId) {
        return resId == RESID_NULL;
    }

    /**
     * 设置View的setEnabled
     *
     * @param id
     * @param isEnable
     */
    public void setViewEnable(@IdRes int id, boolean isEnable) {
        View view = getView(id);
        if (view != null) {
            view.setEnabled(isEnable);
        }
    }

    /**
     * 设置View的setSelected
     *
     * @param id
     * @param isSelected
     */
    public void setViewSelected(@IdRes int id, boolean isSelected) {
        View view = getView(id);
        if (view != null) {
            view.setSelected(isSelected);
        }
    }
    /**
     * 设置View的setSelected
     *
     * @param view
     * @param isSelect
     */
    public void setViewSelected(View view, boolean isSelect) {
        if (view != null) {
            view.setSelected(isSelect);
        }
    }
    /**
     * 获取View
     *
     * @param id
     * @param <T>
     * @return
     */
    public <T extends View> T getView(@IdRes int id) {
        if (mViews == null) {
            mViews = new SparseArray<>();
        }
        View child = mViews.get(id);
        if (child == null) {
            child = findViewById(id);
            mViews.put(id, child);
        }
        return (T) child;
    }

    /**
     * 设置监听
     *
     * @param id
     */
    public void setClickListener(@IdRes int id) {
        View view = getView(id);
        if (view != null) {
            view.setOnClickListener(this);
        }
    }

    /**
     * 设置Edittext文本监听
     *
     * @param id
     * @param textWatcher
     */
    public void setEtTextWatcher(@IdRes int id, TextWatcher textWatcher) {
        View view = getView(id);
        if (view != null && view instanceof EditText) {
            EditText et = (EditText) view;
            et.addTextChangedListener(textWatcher);
        }
    }

    /**
     * 设置EditText文案
     *
     * @param id
     * @param text
     */
    public void setTvText(@IdRes int id, String text) {
        View view = getView(id);
        if (view != null && view instanceof TextView) {
            TextView tv = (TextView) view;
            tv.setText(text);
            if (tv instanceof EditText) {
                EditText et = (EditText) tv;
                et.setSelection(et.length());
            }
        }
    }

    /**
     * 设置TextView文案
     *
     * @param id
     * @param resId
     */
    public void setTvText(@IdRes int id, @StringRes int resId) {
        View view = getView(id);
        if (view != null && view instanceof TextView) {
            TextView tv = (TextView) view;
            tv.setText(resId);
            if (tv instanceof EditText) {
                EditText et = (EditText) tv;
                et.setSelection(et.length());
            }
        }
    }

    /**
     * 设置TextView文案
     *
     * @param id
     * @param ssb
     */
    public void setTvText(@IdRes int id, SpannableStringBuilder ssb) {
        View view = getView(id);
        if (view != null && view instanceof TextView) {
            TextView tv = (TextView) view;
            tv.setText(ssb);
            if (tv instanceof EditText) {
                EditText et = (EditText) tv;
                et.setSelection(et.length());
            }
        }
    }
    /**
     * 设置TextView文案
     *
     * @param id
     * @param resId
     */
    public void setIvImage(@IdRes int id, @DrawableRes int resId) {
        View view = getView(id);
        if (view != null && view instanceof ImageView) {
            ImageView tv = (ImageView) view;
           tv.setImageResource(resId);
        }
    }
    /**
     * 设置小图
     *
     * @param id
     * @param url
     */
    public void setSdvSmall(@IdRes int id, Uri url) {
        SimpleDraweeView sdv = getView(id);
        if (sdv != null) {
            FrescoUtils.sdvBig(sdv, url);
        }
    }
    /**
     * 设置小图
     *
     * @param id
     * @param url
     */
    public void setSdvSmall(@IdRes int id, String url) {
        SimpleDraweeView sdv = getView(id);
        if (sdv != null) {
            FrescoUtils.sdvSmall(sdv, url);
        }
    }

    /**
     * 设置中图
     *
     * @param id
     * @param url
     */
    public void setSdvInside(@IdRes int id, String url) {
        SimpleDraweeView sdv = getView(id);
        if (sdv != null) {
            FrescoUtils.sdvInside(sdv, url);
        }
    }

    /**
     * 设置大图
     *
     * @param id
     * @param url
     */
    public void setSdvBig(@IdRes int id, String url) {
        SimpleDraweeView sdv = getView(id);
        if (sdv != null) {
            FrescoUtils.sdvBig(sdv, url);
        }
    }
    /**
     * 设置大图
     *
     * @param id
     * @param url
     */
    public void setSdvBig(@IdRes int id, Uri url) {
        SimpleDraweeView sdv = getView(id);
        if (sdv != null) {
            FrescoUtils.sdvBig(sdv, url);
        }
    }
    /**
     * 设置大图
     *
     * @param id
     * @param url
     */
    public void setSdvNormal(@IdRes int id, String url, int W, int H) {
        SimpleDraweeView sdv = getView(id);
        if (sdv != null) {
            FrescoUtils.showAutoSizeOfCallback(sdv, url,W,H);
        }
    }
    /**
     * 设置是否显示
     *
     * @param id
     * @param isVisibility
     */
    public void setViewVisibility(@IdRes int id, boolean isVisibility) {
        View view = getView(id);
        if (view != null) {
            view.setVisibility(isVisibility ? View.VISIBLE : View.GONE);
        }
    }

    /**
     * 设置是否显示
     *
     * @param view
     * @param isVisibility
     */
    public void setViewVisibility(View view, boolean isVisibility) {
        if (view != null) {
            view.setVisibility(isVisibility ? View.VISIBLE : View.GONE);
        }
    }

    /**
     * 启动动画
     *
     * @param id
     * @param animId
     */
    public void startViewAnim(@IdRes int id, @AnimRes int animId) {
        View view = getView(id);
        if (view != null) {
            view.startAnimation(AnimationUtils.loadAnimation(this, animId));
        }
    }

//    /**
//     * 获取TextView文本
//     *
//     * @param id
//     * @return
//     */
//    public String getTvText(@IdRes int id) {
//        View view = getView(id);
//        if (view != null && view instanceof TextView) {
//            TextView tv = (TextView) view;
//            if (tv instanceof PhoneEditText) {
//                PhoneEditText et = (PhoneEditText) tv;
//                return et.getTextString();
//            } else {
//                return tv.getText().toString();
//            }
//        }
//        return "";
//    }

    /**
     * 获取Activity
     *
     * @return
     */
    public <T extends Activity> T getActivity() {
        return (T) this;
    }

    /**
     * 获取Application
     *
     * @return
     */
    public App getApp() {
        return App.get();
    }

    /**
     * 创建实例
     *
     * @param resId
     * @param parent
     * @param <T>
     * @return
     */
    public <T extends View> T inflater(@LayoutRes int resId, ViewGroup parent) {
        return (T) LayoutInflater.from(parent.getContext()).inflate(resId, parent, false);
    }

//    /**
//     * 压缩图片
//     *
//     * @param uri
//     */
//    public void luban(Uri uri, final DefOnUploadPicListener listener) {
//        LubanUtil.getInstance(this).setOnLubanListener(new LubanUtil.DefOnLubanListener() {
//            @Override
//            public void onSuccess(LubanResultData lubanResultData) {
//                requestUploadImage(lubanResultData.getAfterFile(), listener);
//            }
//        }).startForUri(uri);
//    }

//    /**
//     * 压缩图片
//     *
//     * @param uri
//     */
//    public void luban(List<Uri> uri, final DefOnUploadPicListener listener) {
//        LubanUtil.getInstance(this).setOnLubanListener(new LubanUtil.DefOnLubanListener() {
//            @Override
//            public void onSuccess(LubanResultData lubanResultData) {
//                requestUploadImage(lubanResultData.getAfterFile(), listener);
//            }
//        }).startForUri(uri);
//    }

//    /**
//     * 上传图片接口
//     *
//     * @param file
//     */
//    private void requestUploadImage(File file, final DefOnUploadPicListener listenr) {
//        RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
//                .addFormDataPart("uploadType", "上传图片")
//                .addFormDataPart("imageUrl", file.getName(), RequestBody.create(MediaType.parse("image/*"), file))
//                .build();
//        App.getServiceManager().getPdmService().uploadImage(body).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new DefaultObserver<String>() {
//                    @Override
//                    public void onSuccess(@io.reactivex.annotations.NonNull BaseResultEntity<String> obj) {
//                        String data = obj.getResult();
//                        if (listenr != null) {
//                            if (TextUtils.isEmpty(data)) {
//                                ToastUtils.show("图片上传成功");
//                                listenr.onLoadPicUnSuccessFinish();
//                            } else {
//                                listenr.onLoadPicFinish(data);
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onUnSuccessFinish() {
//                        super.onUnSuccessFinish();
////                        ToastUtils.show("图片上传失败");
//                        if (listenr != null) {
//                            listenr.onLoadPicUnSuccessFinish();
//                        }
//                    }
//                });
//    }
//    /**
//     * 获取Activity
//     *
//     * @return
//     */
//    public AppCompatActivity getAppCompatActivity() {
//        return this;
//    }
//    /**
//     * 检测更新
//     */
//    protected void validUpdate() {
//        requestUpdate();
//    }
//
//    /**
//     * 请求更新数据
//     */
//    private void requestUpdate() {
//        int versionCode = AppContext.versionCode();

//        App.getServiceManager().forceUpdate(versionCode, 2, type)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new DefaultObserver<Lookup>(false, false) {
//                    @Override
//                    public void onSuccess(BaseResultEntity<Lookup> obj) {
//                        Lookup data = obj.getResult();
//                        DownloadDialogUtils.updateVersion(getAppCompatActivity(), data);
//                    }
//
//                    @Override
//                    public void onAllFinish() {
//                        super.onAllFinish();
//                        hideLoadingDialog();
//                    }
//                });
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        DownloadDialogUtils.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
//
//
//    }

    public  void setProhibitEmoji(EditText et) {
        InputFilter[] filters = { getInputFilterProhibitEmoji() ,getInputFilterProhibitSP()};
        et.setFilters(filters);
    }

    private InputFilter getInputFilterProhibitEmoji() {
        InputFilter filter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end,
                                       Spanned dest, int dstart, int dend) {
                StringBuffer buffer = new StringBuffer();
                for (int i = start; i < end; i++) {
                    char codePoint = source.charAt(i);
                    if (!getIsEmoji(codePoint)) {
                        buffer.append(codePoint);
                    } else {
                        ToastUtils.show("不能含有第三方表情");
                        i++;
                        continue;
                    }
                }
                if (source instanceof Spanned) {
                    SpannableString sp = new SpannableString(buffer);
                    TextUtils.copySpansFrom((Spanned) source, start, end, null,
                            sp, 0);
                    return sp;
                } else {
                    return buffer;
                }
            }
        };
        return filter;
    }


    private   boolean getIsEmoji(char codePoint) {
        if ((codePoint == 0x0) || (codePoint == 0x9) || (codePoint == 0xA)
                || (codePoint == 0xD)
                || ((codePoint >= 0x20) && (codePoint <= 0xD7FF))
                || ((codePoint >= 0xE000) && (codePoint <= 0xFFFD))
                || ((codePoint >= 0x10000) && (codePoint <= 0x10FFFF)))
            return false;
        return true;
    }


    private InputFilter getInputFilterProhibitSP() {
        InputFilter filter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end,
                                       Spanned dest, int dstart, int dend) {
                StringBuffer buffer = new StringBuffer();
                for (int i = start; i < end; i++) {
                    char codePoint = source.charAt(i);
                    if (!getIsSp(codePoint)) {
                        buffer.append(codePoint);
                    } else {
                        ToastUtils.show("不能含有特殊字符");
                        i++;
                        continue;
                    }
                }
                if (source instanceof Spanned) {
                    SpannableString sp = new SpannableString(buffer);
                    TextUtils.copySpansFrom((Spanned) source, start, end, null,
                            sp, 0);
                    return sp;
                } else {
                    return buffer;
                }
            }
        };
        return filter;
    }
    private boolean getIsSp(char codePoint){
        if(Character.getType(codePoint)>Character.LETTER_NUMBER){

            return true;

        }

        return false;

    }

}
