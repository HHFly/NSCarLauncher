package com.example.dell.nscarlauncher.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.SeekBar;

public class VerticalSeekBar extends SeekBar {
	
	public static int progress = 0;
	public float mCurrentPos = 0;
	public float mPreviousPos = 0;
	int addValue = 0;
	boolean flag_move;

	public VerticalSeekBar(Context context) {
		super(context);
	}

	public VerticalSeekBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public VerticalSeekBar(Context context, AttributeSet attrs) {
		super(context, attrs);
	}


	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (!isEnabled()) {
			return false;
		}

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			//System.out.println("event.getY(): " + getDownProgress(event.getY()) + " -- " + progress);
			if (Math.abs(getDownProgress(event.getY()) - progress) > 15) {
				progress = getDownProgress(event.getY());
				setProgress(progress);
				onSizeChanged(getWidth(), getHeight(), 0, 0);
			}else{
				mPreviousPos = event.getY();
				flag_move = true;
			}
			break;
		case MotionEvent.ACTION_MOVE:
			if (flag_move) {
				mCurrentPos = event.getY();
				addValue = (int) ((mPreviousPos -mCurrentPos)/getHeight()*getMax());
				if (progress + addValue < 0) {
					setProgress(0);
				}else if (progress + addValue > 100) {
					setProgress(100);
				}else{
					setProgress(progress + addValue);
				}
				onSizeChanged(getWidth(), getHeight(), 0, 0);
			}
			break;
		case MotionEvent.ACTION_UP:
			if (flag_move) {
				if (progress + addValue < 0) {
					progress = 0;
				}else if (progress + addValue > 100) {
					progress = 100;
				}else{
					progress += addValue;
				}
				flag_move = false;
			}
			break;

		case MotionEvent.ACTION_CANCEL:
			break;
		}
		return true;
	}
	

	public int getDownProgress(float pos){
		int num = (int)(100 - (pos -25.0)/1.8);
		if (num < 0) {
			return 0;
		}else if (num > 100) {
			return 100;
		}else{
			return num;
		}
	}
}