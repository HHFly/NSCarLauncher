package com.kandi.systemui.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by jiangzn on 17/2/3.
 */
public class RoundLightBarView extends View {
    private int mTotalWidth, mTotalHeight;
    private int mCenterX, mCenterY;
    //底色画笔
    private Paint mCirclePaint;
    //进度条画笔
    private Paint mProgressPaint;
    //圆点画笔
    private Paint mbitmapPaint;
    private Matrix mMatrix;             // 矩阵,用于对图片进行一些操作
    private float[] pos;                // 当前点的实际位置
    private float[] tan;                // 当前点的tangent值,用于计算图片所需旋转的角度


    private int mCircleR;


    private Context mContext;
    //距离外围的边距
    private float interval ;

    private int startAngle = 1;

    //球
    private Bitmap mLititleBitmap;  // 圆点图片

    public RoundLightBarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }

    public RoundLightBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        interval  =10;

        //初始化画笔
        initPaint();
        //初始化bitmap
        initBitmap();
    }

    private void initBitmap() {
        mMatrix=new Matrix();
        pos = new float[2];
        tan = new float[2];
//        mLititleBitmap= ((BitmapDrawable) getResources()
//                .getDrawable(R.mipmap.white_round))
//                .getBitmap();
    }

    private void initPaint() {

        //画黑底的深色圆画笔
        mCirclePaint = new Paint();
        //抗锯齿
        mCirclePaint.setAntiAlias(true);
        // 防抖动
        mCirclePaint.setDither(true);
        // 开启图像过滤，对位图进行滤波处理。
        mCirclePaint.setFilterBitmap(true);
        mCirclePaint.setColor(Color.parseColor("#272D42"));
        //空心圆
        mCirclePaint.setStyle(Paint.Style.STROKE);
        //圆半径
        mCircleR = 4;
        mCirclePaint.setStrokeWidth(mCircleR);

        //画彩色圆弧的画笔
        mProgressPaint = new Paint();
        //抗锯齿
        mProgressPaint.setAntiAlias(true);
        // 防抖动
        mProgressPaint.setDither(true);
        // 开启图像过滤，对位图进行滤波处理。
        mProgressPaint.setFilterBitmap(true);
        mProgressPaint.setColor(Color.parseColor("#0090FF"));
        //空心圆
        mProgressPaint.setStyle(Paint.Style.STROKE);
        //设置笔刷样式为原型
        mProgressPaint.setStrokeCap(Paint.Cap.ROUND);
        //设置圆弧粗
        mProgressPaint.setStrokeWidth(mCircleR);
        //将绘制的内容显示在第一次绘制内容之上
        mProgressPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));


        //圆点画笔
        mbitmapPaint = new Paint();
//        mbitmapPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));
        mbitmapPaint.setColor(Color.parseColor("#0090FF"));
        mbitmapPaint.setStyle(Paint.Style.FILL);
        mbitmapPaint.setAntiAlias(true);
        mbitmapPaint.setMaskFilter(new BlurMaskFilter(8, BlurMaskFilter.Blur.SOLID));
        setLayerType(View.LAYER_TYPE_SOFTWARE, mbitmapPaint);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //canvas去锯齿
        canvas.setDrawFilter(
                new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        //画底色圆
        canvas.drawCircle(mCenterX, mCenterY, mCenterX - mCircleR - interval, mCirclePaint);
        //画进度条
        int colorSweep[] = {Color.TRANSPARENT, Color.parseColor("#0090FF"),Color.parseColor("#78A9FC"),Color.parseColor("#78A9FC")};

        //设置渐变色
        sweepGradient = new SweepGradient(mCenterX, mCenterY, colorSweep, null);
        //按照圆心旋转
        Matrix matrix = new Matrix();
        matrix.setRotate(startAngle, mCenterX, mCenterY);
        sweepGradient.setLocalMatrix(matrix);

        mProgressPaint.setShader(sweepGradient);

        canvas.drawArc(
                new RectF(0 + mCircleR + interval, 0 + mCircleR + interval,
                        mTotalWidth - mCircleR - interval, mTotalHeight - mCircleR - interval),
                2 + startAngle, 240, false, mProgressPaint);

        startAngle++;
        if (startAngle == 360) {
            startAngle = 1;
        }

        //绘制白色小星星
        Path orbit = new Path();
        //通过Path类画一个90度（180—270）的内切圆弧路径
        orbit.addArc(
                new RectF(0 + mCircleR + interval, 0 + mCircleR + interval,
                        mTotalWidth - mCircleR - interval, mTotalHeight - mCircleR - interval)
                , 2 + startAngle, 240);
        // 创建 PathMeasure
        PathMeasure measure = new PathMeasure(orbit, false);
        measure.getPosTan(measure.getLength() * 1, pos, tan);
//        mMatrix.reset();
//        mMatrix.postScale(1,1);
//        mMatrix.postTranslate(pos[0] - mLititleBitmap.getWidth()  , pos[1] - mLititleBitmap.getHeight()  );
//        mMatrix.postTranslate(pos[0] - mLititleBitmap.getWidth()  , pos[1] - mLititleBitmap.getHeight()  );   // 将图片绘制中心调整到与当前点重合
//        canvas.drawBitmap(mLititleBitmap, mMatrix, mbitmapPaint);//绘制球



        //绘制实心小圆圈
//        canvas.drawCircle(124.17865f, 16.125025f, 10, mbitmapPaint);
        canvas.drawCircle(pos[0], pos[1], 10, mbitmapPaint);
        canvas.restore();
        //启动绘制
//        postInvalidateDelayed(10);
        invalidate();
    }

    SweepGradient sweepGradient;

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mTotalWidth = w;
        mTotalHeight = h;
        mCenterX = mTotalWidth / 2;
        mCenterY = mTotalHeight / 2;


    }

}
