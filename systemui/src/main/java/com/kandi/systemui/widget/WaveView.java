package com.kandi.systemui.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WaveView extends View {

    /*画布宽度*/
    private int width;
    /*画布高度*/
    private int height;
    /*sin曲线画笔*/
    private Paint paint;
    /*圆的画笔*/
    private Paint textPaint;
    /*文本画笔*/
    private Paint circlePaint;
    /*sin曲线的路径*/
    private Path path;
    /*sin曲线 1/4个周期的宽度*/
    private int cycle = 60;
    /*sin曲线振幅的高度*/
    private int waveHeight = 20;
    /*sin曲线的起点*/
    private Point startPoint;
    /*当前进度*/
    private int progress;
    /*x轴平移量*/
    private int translateX = 40;
    /*是否启用了动画设置进度*/
    private boolean openAnimate = false;
    /*是否自增长*/
    private boolean autoIncrement = true;

    private boolean mStarted = false;
    private Handler mHandler;

    private List<Bubble> bubbles = new ArrayList<Bubble>();
    private Random random = new Random();

    private boolean starting = false;
    private boolean isPause = false;

    public WaveView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);

    }

    public WaveView(Context context) {
        super(context);
        init(context);

    }

    public void setPause(boolean pause) {
        isPause = pause;
    }

    private void init(Context context) {
        path = new Path();
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStrokeWidth(dip2px(context, 5));
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.parseColor("#619BFD"));

        circlePaint = new Paint();
        circlePaint.setStrokeWidth(dip2px(context, 5));
        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setAntiAlias(true);
        circlePaint.setColor(Color.parseColor("#3A6ADB"));

        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(dip2px(context, 20));
        textPaint.setColor(Color.BLACK);
        mHandler = new Handler() {
            @Override
            public void handleMessage(android.os.Message msg) {
                if (msg.what == 0) {
                    invalidate();
                    if (mStarted) {
                        // 不断发消息给自己，使自己不断被重绘
                        mHandler.sendEmptyMessageDelayed(0, 160L);
                    }
                }
            }
        };
    }
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        // 关闭硬件加速，防止异常unsupported operation exception
        this.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //裁剪画布为圆形
        Path circlePath = new Path();
        circlePath.addCircle(width / 2, height / 2, width / 2, Path.Direction.CW);
        canvas.clipPath(circlePath);
        canvas.drawPaint(circlePaint);
        canvas.drawCircle(width / 2, height / 2, width / 2, circlePaint);
        //以下操作都是在这个圆形画布中操作

        //根据进度改变起点坐标的y值
        startPoint.y = (int) (height - (progress / 100.0 * height));
        //startPoint.x在onDraw方法后值会变，故采用下面方法
        startPoint.x = -200 + count*translateX;
        if(startPoint.x == 0){
            count = 0;
        }else{
            count++;
        }
        //起点
        path.moveTo(startPoint.x, startPoint.y);
        int j = 1;
        //循环绘制正弦曲线 循环一次半个周期
        for (int i = 1; i <= 8; i++) {
            if (i % 2 == 0) {
                //波峰
                path.quadTo(startPoint.x + (cycle * j), startPoint.y + waveHeight,
                        startPoint.x + (cycle * 2) * i, startPoint.y);
            } else {
                //波谷
                path.quadTo(startPoint.x + (cycle * j), startPoint.y - waveHeight,
                        startPoint.x + (cycle * 2) * i, startPoint.y);
            }
            j += 2;
        }
        //绘制封闭的曲线
        path.lineTo(width, height);//右下角
        path.lineTo(startPoint.x, height);//左下角
        path.lineTo(startPoint.x, startPoint.y);//起点
        path.close();
        if(progress > 0){
            canvas.drawPath(path, paint);
        }

//        drawText(canvas, textPaint, progress + "%");
//        //判断是不是平移完了一个周期
//        if (startPoint.x + translateX >= 0) {
//            //满了一个周期则恢复默认起点继续平移
//            startPoint.x = -cycle * 4;
//        }
//        //每次波形的平移量 40
//        startPoint.x += translateX;
        if (autoIncrement) {
            if (progress >= 100) {
                progress = 0;
            } else {
                progress++;
            }
        }
        path.reset();
        canvas.restore();
        onDrawBubble(canvas);
        //启动绘制
        postInvalidateDelayed(350);
    }
    int count = 0;

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //获取view的宽度
        width = getViewSize(400, widthMeasureSpec);
        //获取view的高度
        height = getViewSize(400, heightMeasureSpec);
        //默认从屏幕外先绘制3/4个周期 使得波峰在圆中间
        startPoint = new Point(-cycle * 3, height / 2);
    }


    private int getViewSize(int defaultSize, int measureSpec) {
        int viewSize = defaultSize;
        //获取测量模式
        int mode = MeasureSpec.getMode(measureSpec);
        //获取大小
        int size = MeasureSpec.getSize(measureSpec);
        switch (mode) {
            case MeasureSpec.UNSPECIFIED: //如果没有指定大小，就设置为默认大小
                viewSize = defaultSize;
                break;
            case MeasureSpec.AT_MOST: //如果测量模式是最大取值为size
                //我们将大小取最大值,你也可以取其他值
                viewSize = size;
                break;
            case MeasureSpec.EXACTLY: //如果是固定的大小，那就不要去改变它
                viewSize = size;
                break;
        }
        return viewSize;
    }

    /**
     * 绘制文字
     *
     * @param canvas 画布
     * @param paint  画笔
     * @param text   画的文字
     */
    private void drawText(Canvas canvas, Paint paint, String text) {
        //画布的大小
        Rect targetRect = new Rect(0, 0, width, height);
        Paint.FontMetricsInt fontMetrics = paint.getFontMetricsInt();
        int baseline = (targetRect.bottom + targetRect.top - fontMetrics.bottom - fontMetrics.top) / 2;
        // 下面这行是实现水平居中，drawText对应改为传入targetRect.centerX()
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(text, targetRect.centerX(), baseline, paint);
    }

    /**
     * 根据手机的分辨率从 dip 的单位 转成为 px(像素)
     */
    public int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 设置振幅高度
     *
     * @param waveHeight 振幅
     */
    public void setWaveHeight(int waveHeight) {
        this.waveHeight = waveHeight;
        invalidate();
    }

    /**
     * 设置sin曲线 1/4个周期的宽度
     *
     * @param cycle 1/4个周期的宽度
     */
    public void setCycle(int cycle) {
        this.cycle = cycle;
        invalidate();
    }

    /**
     * 设置当前进度
     *
     * @param progress 进度
     */
    public void setProgress(int progress) {
        if (progress > 100 || progress < 0)
            throw new RuntimeException(getClass().getName() + "请设置[0,100]之间的值");
        this.progress = progress;
        autoIncrement = false;
        invalidate();
    }

    /**
     * 设置x轴移动量
     *
     * @param translateX 默认40
     */
    public void setTranslateX(int translateX) {
        this.translateX = translateX;
    }

    /**
     * 通过动画设置当前进度
     *
     * @param progress 进度 <=100
     * @param duration 动画时长
     */
    public void setProgress(final int progress, int duration) {
        if (progress > 100 || progress < 0)
            throw new RuntimeException(getClass().getName() + "请设置[0,100]之间的值");
        autoIncrement = false;
        openAnimate = true;
        ValueAnimator progressAnimator = ValueAnimator.ofInt(0, progress);
        progressAnimator.setDuration(duration);
        progressAnimator.setTarget(progress);
        progressAnimator.setInterpolator(new LinearInterpolator());
        progressAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                WaveView.this.progress = (int) animation.getAnimatedValue();
                if (WaveView.this.progress == progress)
                    openAnimate = false;
                invalidate();
            }
        });
        progressAnimator.start();
    }

    public int getProgress() {
        return progress;
    }

    /**
     * @category 开始波动
     */
    public void startWave() {
        if (!mStarted) {
            mStarted = true;
            this.mHandler.sendEmptyMessage(0);
        }
    }

    /**
     * @category 停止波动
     */
    public void stopWave() {
        if (mStarted) {

            mStarted = false;
            this.mHandler.removeMessages(0);
        }
    }
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }
/*气泡
**/
private int minspeed =10;
private int waterLine ;
private void onDrawBubble(Canvas canvas) {


    isPause = false;
    width = getWidth();
    height = getHeight();
    if (!starting) {
        starting = true;
        new Thread() {
            public void run() {
                while (true) {
                    if(isPause){
                        continue;
                    }
                    if(!starting){
                        break;
                    }
                    try {
                        Thread.sleep(random.nextInt(3) * 300);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    Bubble bubble = new Bubble();
                    int radius = random.nextInt(10);
                    while (radius == 0) {
                        radius = random.nextInt(10);
                    }

                    float speedY = random.nextFloat()*20;
                    while (speedY < 1) {
                        speedY = random.nextFloat()*20;
                    }
                    if(speedY<minspeed){
                        speedY=minspeed;
                    }
                    bubble.setRadius(radius);
                    bubble.setSpeedY(speedY);

                    bubble.setX(width /2);
                    bubble.setY(height);
                    float speedX = random.nextFloat()-0.5f;
                    while (speedX == 0) {
                        speedX = random.nextFloat()-0.5f;
                    }
                    bubble.setSpeedX(speedX* random.nextInt(20));
                    bubbles.add(bubble);
                }
            };
        }.start();
    }
    waterLine =(int) (height - (progress / 100.0 * height));
    Paint paint = new Paint();
    // 绘制渐变正方形
//		Shader shader = new LinearGradient(0, 0, 0, height, new int[] {
//				getResources().getColor(R.color.blue_bright),
//				getResources().getColor(R.color.blue_light),
//				getResources().getColor(R.color.blue_dark) },
//				null, Shader.TileMode.REPEAT);
//		paint.setShader(shader);
//		canvas.drawRect(0, 0, width, height, paint);
//		paint.reset();
		paint.setColor(Color.parseColor("#8EB8FF"));
		paint.setAlpha(200);
    List<Bubble> list = new ArrayList<Bubble>(bubbles);
    for (Bubble bubble : list) {
        if ((bubble.getY()-bubble.getRadius()-bubble.getSpeedY() -waveHeight/2)<waterLine ||isOverRange(bubble) ) {
            bubbles.remove(bubble);

        } else {

            int i = bubbles.indexOf(bubble);
            if (bubble.getX() + bubble.getSpeedX() <= bubble.getRadius()) {
                bubble.setX(bubble.getRadius());
            } else if (bubble.getX() + bubble.getSpeedX() >= width
                    - bubble.getRadius()) {
                bubble.setX(width - bubble.getRadius());
            } else {
                bubble.setX(bubble.getX() + bubble.getSpeedX());
            }
            bubble.setY(bubble.getY() - bubble.getSpeedY());
            bubbles.set(i, bubble);
            canvas.drawCircle(bubble.getX(), bubble.getY(),
                    bubble.getRadius(), paint);
        }
    }
    if(bubbles.size()>1000){
        starting=false;
    }

}
private Boolean isOverRange(Bubble bubble){
    //点击位置x坐标与圆心的x坐标的距离
    int distanceX = (int) Math.abs(width/2-bubble.getX());
    //点击位置y坐标与圆心的y坐标的距离
    int distanceY = (int) Math.abs(height/2-bubble.getY());
    //点击位置与圆心的直线距离
    int distanceZ = (int) Math.sqrt(Math.pow(distanceX,2)+Math.pow(distanceY,2));
    if(bubble.getY()>height*0.75){
        if(width / 2<distanceZ){
            return true;
        }
        return false;
    }
    if(width / 2<distanceZ+bubble.getRadius()*2+10){
       return true;
    }
    return false;
}


    private class Bubble {
        /** 气泡半径 */
        private int radius;
        /** 上升速度 */
        private float speedY;
        /** 平移速度 */
        private float speedX;
        /** 气泡x坐标 */
        private float x;
        /** 气泡y坐标 */
        private float y;

        /**
         * @return the radius
         */
        public int getRadius() {
            return radius;
        }

        /**
         * @param radius
         *            the radius to set
         */
        public void setRadius(int radius) {
            this.radius = radius;
        }

        /**
         * @return the x
         */
        public float getX() {
            return x;
        }

        /**
         * @param x
         *            the x to set
         */
        public void setX(float x) {
            this.x = x;
        }

        /**
         * @return the y
         */
        public float getY() {
            return y;
        }

        /**
         * @param y
         *            the y to set
         */
        public void setY(float y) {
            this.y = y;
        }

        /**
         * @return the speedY
         */
        public float getSpeedY() {
            return speedY;
        }

        /**
         * @param speedY
         *            the speedY to set
         */
        public void setSpeedY(float speedY) {
            this.speedY = speedY;
        }

        /**
         * @return the speedX
         */
        public float getSpeedX() {
            return speedX;
        }

        /**
         * @param speedX
         *            the speedX to set
         */
        public void setSpeedX(float speedX) {
            this.speedX = speedX;
        }

    }

}
