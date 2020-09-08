package com.kandi.dell.nscarlauncher.ui_portrait.setting.widgets;

import android.app.Activity;
import android.util.DisplayMetrics;

/**
 *
 * @author 
 * @email chenshi011@163.com
 *
 */
public class SetDate_ScreenInfo {
	 private Activity activity;
	 private int width;
	 private int height;
	 private float density;
	 private int densityDpi;
	 public Activity getActivity() {
		return activity;
	}

	public void setActivity(Activity activity) {
		this.activity = activity;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public float getDensity() {
		return density;
	}

	public void setDensity(float density) {
		this.density = density;
	}

	public int getDensityDpi() {
		return densityDpi;
	}

	public void setDensityDpi(int densityDpi) {
		this.densityDpi = densityDpi;
	}

	public SetDate_ScreenInfo(Activity activity){
		 this.activity = activity;
		 ini();
	 }
	 
	 private void ini(){
		 DisplayMetrics metric = new DisplayMetrics();
		 activity.getWindowManager().getDefaultDisplay().getMetrics(metric);
	     width = metric.widthPixels;  
	     height = metric.heightPixels; 
	     density = metric.density;  
	     densityDpi = metric.densityDpi;  
	 }
	 
	
}
