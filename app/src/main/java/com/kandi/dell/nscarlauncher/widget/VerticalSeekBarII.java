package com.kandi.dell.nscarlauncher.widget;

import android.widget.SeekBar;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;


public class VerticalSeekBarII extends SeekBar {

    public VerticalSeekBarII(Context context) {
        super(context);
    }

    public VerticalSeekBarII(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public VerticalSeekBarII(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    public interface OnBarIIChangeListener {

        void onStartTracking();

        void onStopTracking();
    }
    private OnBarIIChangeListener mOnBarIIChangeListener;
    void onStartTrackingTouch() {
        if (mOnBarIIChangeListener != null) {
            mOnBarIIChangeListener.onStartTracking();
        }
    }

    void onStopTrackingTouch() {
        if (mOnBarIIChangeListener != null) {
            mOnBarIIChangeListener.onStopTracking();
        }
    }
    public void setmOnBarIIChangeListener(OnBarIIChangeListener mOnBarIIChangeListener) {
        this.mOnBarIIChangeListener = mOnBarIIChangeListener;
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(h, w, oldh, oldw);
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(heightMeasureSpec, widthMeasureSpec);
        setMeasuredDimension(getMeasuredHeight(), getMeasuredWidth());
    }

    protected void onDraw(Canvas c) {
        c.rotate(-90);
        c.translate(-getHeight(),0);

        super.onDraw(c);
    }
    @Override
    public synchronized void setProgress(int progress) {
        super.setProgress(progress);
        onSizeChanged(getWidth(), getHeight(), 0, 0);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        super.onTouchEvent(event);
        if (!isEnabled()) {
            return false;
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                onStartTrackingTouch();
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_UP:
                int i=0;
                i=getMax() - (int)(getMax() * event.getY() / getHeight());
                setProgress(i);
//                Log.i("Progress",getProgress()+"");
                onSizeChanged(getWidth(),getHeight(), 0, 0);
                onStopTrackingTouch();
                break;

            case MotionEvent.ACTION_CANCEL:
                onStopTrackingTouch();
                break;
        }
        return true;
    }


}
