package com.example.dell.nscarlauncher.common.util;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;

import com.facebook.binaryresource.FileBinaryResource;
import com.facebook.cache.common.SimpleCacheKey;
import com.facebook.common.internal.Supplier;
import com.facebook.common.util.ByteConstants;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.cache.MemoryCacheParams;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import jp.wasabeef.fresco.processors.BlurPostprocessor;

/**
 * 功能：fresco工具类
 */
public class FrescoUtils {
    private static final String TAG = "FrescoUtils";

    private static boolean isInit = false;

    /**
     * 设置SimpleDraweeView图片
     *
     * @param draweeView
     * @param url
     * @return
     */
    public static void sdvBig(SimpleDraweeView draweeView, String url) {
        FrescoUtils.showAutoSize(draweeView, url, 300, 300);
    }

    /**
     * 设置SimpleDraweeView图片
     *
     * @param draweeView
     * @param url
     * @return
     */
    public static void sdvBigOfCallBack(SimpleDraweeView draweeView, String url) {
        FrescoUtils.showAutoSizeOfCallback(draweeView, url, 300, 300);
    }

    /**
     * 设置SimpleDraweeView图片
     *
     * @param draweeView
     * @param uri
     * @return
     */
    public static void sdvBig(SimpleDraweeView draweeView, Uri uri) {
        FrescoUtils.showAutoSize(draweeView, uri, 300, 300);
    }

    /**
     * 设置SimpleDraweeView图片
     *
     * @param draweeView
     * @param url
     * @return
     */
    public static void sdvInside(SimpleDraweeView draweeView, String url) {
        FrescoUtils.showAutoSize(draweeView, url, 150, 150);
    }

    /**
     * 设置SimpleDraweeView图片
     *
     * @param draweeView
     * @param uri
     * @return
     */
    public static void sdvInside(SimpleDraweeView draweeView, Uri uri) {
        FrescoUtils.showAutoSize(draweeView, uri, 150, 150);
    }

    /**
     * 设置SimpleDraweeView图片
     *
     * @param draweeView
     * @param url
     * @return
     */
    public static void sdvSmall(SimpleDraweeView draweeView, String url) {
        FrescoUtils.showAutoSize(draweeView, url, 75, 75);
    }

    /**
     * 设置SimpleDraweeView图片
     *
     * @param draweeView
     * @param uri
     * @return
     */
    public static void sdvSmall(SimpleDraweeView draweeView, Uri uri) {
        FrescoUtils.showAutoSize(draweeView, uri, 75, 75);
    }

    /**
     * 显示缩略图
     *
     * @param draweeView     目标View
     * @param url            图片地址
     * @param resizeWidthDp  图片缩放宽度
     * @param resizeHeightDp 图片缩放高度
     */
    public static void showAutoSizeBlur(final SimpleDraweeView draweeView, String url, final int resizeWidthDp, final int resizeHeightDp) {
        if (TextUtils.isEmpty(url)) {
            url = "";
        }
        showAutoSize(draweeView, Uri.parse(url), resizeWidthDp, resizeHeightDp, false, true);
    }

    /**
     * 显示缩略图
     *
     * @param draweeView     控件
     * @param url            图片url
     * @param resizeWidthDp  图片缩放宽度dp
     * @param resizeHeightDp 图片缩放高度dp
     */
    public static void showAutoSizeOfCallback(final SimpleDraweeView draweeView, final String url, final int resizeWidthDp, final int resizeHeightDp) {
        if (!TextUtils.isEmpty(url)) {
            showAutoSizeOfCallback(draweeView, Uri.parse(url), resizeWidthDp, resizeHeightDp);
        }
    }

    /**
     * 显示缩略图
     *
     * @param draweeView     控件
     * @param uri            图片Uri
     * @param resizeWidthDp  图片缩放宽度dp
     * @param resizeHeightDp 图片缩放高度dp
     */
    public static void showAutoSizeOfCallback(final SimpleDraweeView draweeView, final Uri uri, final int resizeWidthDp, final int resizeHeightDp) {
        showAutoSize(draweeView, uri, resizeWidthDp, resizeHeightDp, false);
    }

    /**
     * 显示缩略图
     *
     * @param draweeView     目标View
     * @param url            图片地址
     * @param resizeWidthDp  图片缩放宽度
     * @param resizeHeightDp 图片缩放高度
     */
    private static void showAutoSize(final SimpleDraweeView draweeView, String url, final int resizeWidthDp, final int resizeHeightDp) {
        if (TextUtils.isEmpty(url)) {
            url = "";
        }
        showAutoSize(draweeView, Uri.parse(url), resizeWidthDp, resizeHeightDp);
    }

    /**
     * 显示缩略图
     *
     * @param draweeView     目标View
     * @param uri            图片Uri地址
     * @param resizeWidthDp  图片缩放宽度
     * @param resizeHeightDp 图片缩放高度
     */
    private static void showAutoSize(final SimpleDraweeView draweeView, final Uri uri, final int resizeWidthDp, final int resizeHeightDp) {
        showAutoSize(draweeView, uri, resizeWidthDp, resizeHeightDp, false);
    }

    /**
     * 显示缩略图
     *
     * @param draweeView     控件
     * @param uri            图片Uri
     * @param resizeWidthDp  图片缩放宽度dp
     * @param resizeHeightDp 图片缩放高度dp
     * @param isAspectRatio  是否按图片原始比列缩放
     */
    private static void showAutoSize(final SimpleDraweeView draweeView, final Uri uri, final int resizeWidthDp, final int resizeHeightDp, final boolean isAspectRatio) {
        showAutoSize(draweeView, uri, resizeWidthDp, resizeHeightDp, isAspectRatio, false);
    }

    /**
     * 显示缩略图
     *
     * @param draweeView     控件
     * @param uri            图片Uri
     * @param resizeWidthDp  图片缩放宽度dp
     * @param resizeHeightDp 图片缩放高度dp
     * @param isAspectRatio  是否按图片原始比列缩放
     */
    private static void showAutoSize(final SimpleDraweeView draweeView, final Uri uri, final int resizeWidthDp, final int resizeHeightDp, final boolean isAspectRatio, final boolean isBlur) {
        if (draweeView == null) {
            LogUtils.logFormat(TAG, "showAutoSizeOfCallback", "draweeView is null");
            return;
        }
        if (uri == null) {
            //如果图片地址为空,设置默认图片
            LogUtils.logFormat(TAG, "showAutoSizeOfCallback", "uri is null");
            return;
        }

        boolean isExist = FrescoUtils.isExistPicture(draweeView.getContext(), uri.toString());
        if (isExist) {
            //如果图片已存在缓存中,不用排队
            showThumb(draweeView, uri, resizeWidthDp, resizeHeightDp, isAspectRatio, isBlur);
        } else {
            //排队加载图片
            draweeView.post(new Runnable() {
                @Override
                public void run() {
                    showThumb(draweeView, uri, resizeWidthDp, resizeHeightDp, isAspectRatio, isBlur);
                }
            });
        }
    }

    /**
     * 显示缩略图
     *
     * @param view           控件
     * @param uri            图片Uri
     * @param resizeWidthDp  图片缩放宽度dp
     * @param resizeHeightDp 图片缩放高度dp
     * @param isAspectRatio  是否按图片原始比列缩放
     */
    private static void showThumb(final SimpleDraweeView view, final Uri uri, int resizeWidthDp, int resizeHeightDp, boolean isAspectRatio) {
        showThumb(view, uri, resizeWidthDp, resizeHeightDp, isAspectRatio, false);
    }

    /**
     * 显示缩略图
     *
     * @param view           控件
     * @param uri            图片Uri
     * @param resizeWidthDp  图片缩放宽度dp
     * @param resizeHeightDp 图片缩放高度dp
     * @param isAspectRatio  是否按图片原始比列缩放
     */
    private static void showThumb(final SimpleDraweeView view, final Uri uri, int resizeWidthDp, int resizeHeightDp, boolean isAspectRatio, boolean isBlur) {
        if (view == null || uri == null) {
            return;
        }
        initialize(view.getContext());
        //将dp转换为px
        int widthPx = SizeUtils.dp2px(view.getContext(), resizeWidthDp);
        int heightPx = SizeUtils.dp2px(view.getContext(), resizeHeightDp);

        ImageRequestBuilder builder = ImageRequestBuilder.newBuilderWithSource(uri)
                .setResizeOptions(new ResizeOptions(widthPx, heightPx));

        if (isBlur) {
            builder.setPostprocessor(new BlurPostprocessor(view.getContext(), 25));
        }
        ImageRequest request = builder.build();

        BaseControllerListener controllerListener;
        if (isAspectRatio) {
            //控件比例按图片原始比例
            controllerListener = new BaseControllerListener<ImageInfo>() {
                @Override
                public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable) {
                    //初始化图片宽高比
                    float width = imageInfo.getWidth();
                    float height = imageInfo.getHeight();
                    view.setAspectRatio(width / height);
                }
            };
        } else {
            controllerListener = new BaseControllerListener<ImageInfo>();
        }

        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setImageRequest(request)
                .setOldController(view.getController())
                .setControllerListener(controllerListener)
                .build();
        view.setController(controller);
    }

    /**
     * 设置SimpleDraweeView图片
     *
     * @param draweeView
     * @param url
     * @return
     */
    public static void sdvSmallOfCusCallBack(SimpleDraweeView draweeView, String url, BaseControllerListener listener) {
        if (TextUtils.isEmpty(url)) {
            url = "";
        }
        showThumb(draweeView, Uri.parse(url), 75, 75, listener);
    }

    /**
     * 显示缩略图
     *
     * @param view           控件
     * @param uri            图片Uri
     * @param resizeWidthDp  图片缩放宽度dp
     * @param resizeHeightDp 图片缩放高度dp
     */
    private static void showThumb(final SimpleDraweeView view, final Uri uri, int resizeWidthDp, int resizeHeightDp, BaseControllerListener controllerListener) {
        if (view == null || uri == null) {
            return;
        }
        initialize(view.getContext());
        //将dp转换为px
        int widthPx = SizeUtils.dp2px(view.getContext(), resizeWidthDp);
        int heightPx = SizeUtils.dp2px(view.getContext(), resizeHeightDp);

        ImageRequestBuilder builder = ImageRequestBuilder.newBuilderWithSource(uri)
                .setResizeOptions(new ResizeOptions(widthPx, heightPx));

        ImageRequest request = builder.build();

        if (controllerListener == null) {
            controllerListener = new BaseControllerListener<ImageInfo>();
        }

        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setImageRequest(request)
                .setOldController(view.getController())
                .setControllerListener(controllerListener)
                .build();
        view.setController(controller);
    }

    /**
     * 初始化
     *
     * @param context context
     */
    public static void initialize(Context context) {
        if (isInit) {
            return;
        }
        ImagePipelineConfig config = getConfigureCaches(context);
        Fresco.initialize(context, config);
        isInit = true;
    }

    /**
     * 获取最大缓存大小
     *
     * @param context
     * @return
     */
    private static int getMaxCacheSize(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        final int maxMemory = Math.min(manager.getMemoryClass() * ByteConstants.MB, Integer.MAX_VALUE);

        if (maxMemory < 32 * ByteConstants.MB) {
            return 4 * ByteConstants.MB;
        } else if (maxMemory < 64 * ByteConstants.MB) {
            return 6 * ByteConstants.MB;
        } else {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD) {
                return 8 * ByteConstants.MB;
            } else {
                return maxMemory / 4;
            }
        }
    }

    /**
     * 获取ImagePipelineConfig
     *
     * @param context
     * @return
     */
    private static ImagePipelineConfig getConfigureCaches(Context context) {
        int maxCacheSize = getMaxCacheSize(context);
        int size = 128;
        final MemoryCacheParams bitmapCacheParams = new MemoryCacheParams(
                maxCacheSize,// 内存缓存中总图片的最大大小,以字节为单位。
                size,// 内存缓存中图片的最大数量。
                maxCacheSize,// 内存缓存中准备清除但尚未被删除的总图片的最大大小,以字节为单位。
                Integer.MAX_VALUE,// 内存缓存中准备清除的总图片的最大数量。
                Integer.MAX_VALUE);// 内存缓存中单个图片的最大大小。

        Supplier<MemoryCacheParams> mSupplierMemoryCacheParams = new Supplier<MemoryCacheParams>() {
            @Override
            public MemoryCacheParams get() {
                return bitmapCacheParams;
            }
        };
        ImagePipelineConfig.Builder builder = ImagePipelineConfig.newBuilder(context);
        builder.setBitmapMemoryCacheParamsSupplier(mSupplierMemoryCacheParams)
                .setDownsampleEnabled(true);
        return builder.build();
    }

    /**
     * 根据图片id获取Uri
     *
     * @param resId
     * @return
     */
    public static Uri getUriByResId(int resId) {
        return new Uri.Builder().scheme("res").path(String.valueOf(resId)).build();
    }

    /**
     * 判断缓存中是否有该图片
     *
     * @param url uri
     * @return boolean
     */
    private static boolean isExistPicture(Context context, String url) {
        initialize(context);

        Uri uri = Uri.parse(url);
        FileBinaryResource resource = (FileBinaryResource) Fresco.getImagePipelineFactory().getMainFileCache().getResource(new SimpleCacheKey(uri.toString()));
        if (resource == null) {
            return false;
        }
        return true;
    }
}