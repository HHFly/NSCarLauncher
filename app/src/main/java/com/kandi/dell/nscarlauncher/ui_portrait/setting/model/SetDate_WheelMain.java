package com.kandi.dell.nscarlauncher.ui_portrait.setting.model;

import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.view.View;

import com.kandi.dell.nscarlauncher.R;
import com.kandi.dell.nscarlauncher.ui_portrait.setting.widgets.SetDate_NumericWheelAdapter;
import com.kandi.dell.nscarlauncher.ui_portrait.setting.widgets.SetDate_OnWheelChangedListener;
import com.kandi.dell.nscarlauncher.ui_portrait.setting.widgets.SetDate_WheelView;


public class SetDate_WheelMain {

	private View view;
	private SetDate_WheelView wv_year;
	private SetDate_WheelView wv_month;
	private SetDate_WheelView wv_day;
	private SetDate_WheelView wv_hours;
	private SetDate_WheelView wv_mins;
	public int screenheight;
	private boolean hasSelectTime;
	private static int START_YEAR = 1900, END_YEAR = 2100;

	public View getView() {
		return view;
	}

	public void setView(View view) {
		this.view = view;
	}

	public static int getSTART_YEAR() {
		return START_YEAR;
	}

	public static void setSTART_YEAR(int sTART_YEAR) {
		START_YEAR = sTART_YEAR;
	}

	public static int getEND_YEAR() {
		return END_YEAR;
	}

	public static void setEND_YEAR(int eND_YEAR) {
		END_YEAR = eND_YEAR;
	}

	public SetDate_WheelMain(View view) {
		super();
		this.view = view;
		hasSelectTime = false;
		setView(view);
	}
	public SetDate_WheelMain(View view,boolean hasSelectTime) {
		super();
		this.view = view;
		this.hasSelectTime = hasSelectTime;
		setView(view);
	}
	public void initDateTimePicker(int year ,int month,int day,Context context){
		this.initDateTimePicker(year, month, day, 0, 0,context);
	}
	/**
	 * @Description: TODO 寮瑰嚭鏃ユ湡鏃堕棿閫夋嫨鍣�
	 */
	public void initDateTimePicker(int year ,int month ,int day,int h,int m,Context context) {
//		int year = calendar.get(Calendar.YEAR);
//		int month = calendar.get(Calendar.MONTH);
//		int day = calendar.get(Calendar.DATE);
		// 娣诲姞澶у皬鏈堟湀浠藉苟灏嗗叾杞崲涓簂ist,鏂逛究涔嬪悗鐨勫垽鏂�
		String[] months_big = { "1", "3", "5", "7", "8", "10", "12" };
		String[] months_little = { "4", "6", "9", "11" };

		final List<String> list_big = Arrays.asList(months_big);
		final List<String> list_little = Arrays.asList(months_little);

		// 骞�
		wv_year = (SetDate_WheelView) view.findViewById(R.id.year);
		wv_year.setAdapter(new SetDate_NumericWheelAdapter(START_YEAR, END_YEAR));// 璁剧疆"骞�鐨勬樉绀烘暟鎹�
		wv_year.setCyclic(true);// 鍙惊鐜粴鍔�
		wv_year.setLabel(context.getString(R.string.year));// 娣诲姞鏂囧瓧
		wv_year.setCurrentItem(year - START_YEAR);// 鍒濆鍖栨椂鏄剧ず鐨勬暟鎹�

		// 鏈�
		wv_month = (SetDate_WheelView) view.findViewById(R.id.month);
		wv_month.setAdapter(new SetDate_NumericWheelAdapter(1, 12));
		wv_month.setCyclic(true);
		wv_month.setLabel(context.getString(R.string.month));
		wv_month.setCurrentItem(month);

		// 鏃�
		wv_day = (SetDate_WheelView) view.findViewById(R.id.day);
		wv_day.setCyclic(true);
		// 鍒ゆ柇澶у皬鏈堝強鏄惁闂板勾,鐢ㄦ潵纭畾"鏃�鐨勬暟鎹�
		if (list_big.contains(String.valueOf(month + 1))) {
			wv_day.setAdapter(new SetDate_NumericWheelAdapter(1, 31));
		} else if (list_little.contains(String.valueOf(month + 1))) {
			wv_day.setAdapter(new SetDate_NumericWheelAdapter(1, 30));
		} else {
			// 闂板勾
			if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0)
				wv_day.setAdapter(new SetDate_NumericWheelAdapter(1, 29));
			else
				wv_day.setAdapter(new SetDate_NumericWheelAdapter(1, 28));
		}
		wv_day.setLabel(context.getString(R.string.day));
		wv_day.setCurrentItem(day - 1);

		wv_hours = (SetDate_WheelView)view.findViewById(R.id.hour);
		wv_mins = (SetDate_WheelView)view.findViewById(R.id.min);
		if(hasSelectTime){
			wv_year.setVisibility(View.GONE);
			wv_month.setVisibility(View.GONE);
			wv_day.setVisibility(View.GONE);
			wv_hours.setVisibility(View.VISIBLE);
			wv_mins.setVisibility(View.VISIBLE);
			
			wv_hours.setAdapter(new SetDate_NumericWheelAdapter(0, 23));
			wv_hours.setCyclic(true);// 鍙惊鐜粴鍔�
			wv_hours.setLabel(context.getString(R.string.hour));// 娣诲姞鏂囧瓧
			wv_hours.setCurrentItem(h);
			
			
			wv_mins.setAdapter(new SetDate_NumericWheelAdapter(0, 59));
			wv_mins.setCyclic(true);// 鍙惊鐜粴鍔�
			wv_mins.setLabel(context.getString(R.string.min));// 娣诲姞鏂囧瓧
			wv_mins.setCurrentItem(m);
		}else{
			wv_hours.setVisibility(View.GONE);
			wv_mins.setVisibility(View.GONE);
			wv_hours.setAdapter(new SetDate_NumericWheelAdapter(0, 23));
			wv_hours.setCyclic(true);// 鍙惊鐜粴鍔�
			wv_hours.setLabel(context.getString(R.string.hour));// 娣诲姞鏂囧瓧
			wv_hours.setCurrentItem(h);
			
			
			wv_mins.setAdapter(new SetDate_NumericWheelAdapter(0, 59));
			wv_mins.setCyclic(true);// 鍙惊鐜粴鍔�
			wv_mins.setLabel(context.getString(R.string.min));// 娣诲姞鏂囧瓧
			wv_mins.setCurrentItem(m);
		}
		
		// 娣诲姞"骞�鐩戝惉
		SetDate_OnWheelChangedListener wheelListener_year = new SetDate_OnWheelChangedListener() {
			public void onChanged(SetDate_WheelView wheel, int oldValue, int newValue) {
				int year_num = newValue + START_YEAR;
				// 鍒ゆ柇澶у皬鏈堝強鏄惁闂板勾,鐢ㄦ潵纭畾"鏃�鐨勬暟鎹�
				if (list_big
						.contains(String.valueOf(wv_month.getCurrentItem() + 1))) {
					wv_day.setAdapter(new SetDate_NumericWheelAdapter(1, 31));
				} else if (list_little.contains(String.valueOf(wv_month
						.getCurrentItem() + 1))) {
					wv_day.setAdapter(new SetDate_NumericWheelAdapter(1, 30));
				} else {
					if ((year_num % 4 == 0 && year_num % 100 != 0)
							|| year_num % 400 == 0)
						wv_day.setAdapter(new SetDate_NumericWheelAdapter(1, 29));
					else
						wv_day.setAdapter(new SetDate_NumericWheelAdapter(1, 28));
				}
			}
		};
		// 娣诲姞"鏈�鐩戝惉
		SetDate_OnWheelChangedListener wheelListener_month = new SetDate_OnWheelChangedListener() {
			public void onChanged(SetDate_WheelView wheel, int oldValue, int newValue) {
				int month_num = newValue + 1;
				// 鍒ゆ柇澶у皬鏈堝強鏄惁闂板勾,鐢ㄦ潵纭畾"鏃�鐨勬暟鎹�
				if (list_big.contains(String.valueOf(month_num))) {
					wv_day.setAdapter(new SetDate_NumericWheelAdapter(1, 31));
				} else if (list_little.contains(String.valueOf(month_num))) {
					wv_day.setAdapter(new SetDate_NumericWheelAdapter(1, 30));
				} else {
					if (((wv_year.getCurrentItem() + START_YEAR) % 4 == 0 && (wv_year
							.getCurrentItem() + START_YEAR) % 100 != 0)
							|| (wv_year.getCurrentItem() + START_YEAR) % 400 == 0)
						wv_day.setAdapter(new SetDate_NumericWheelAdapter(1, 29));
					else
						wv_day.setAdapter(new SetDate_NumericWheelAdapter(1, 28));
				}
			}
		};
		wv_year.addChangingListener(wheelListener_year);
		wv_month.addChangingListener(wheelListener_month);

		// 鏍规嵁灞忓箷瀵嗗害鏉ユ寚瀹氶�鎷╁櫒瀛椾綋鐨勫ぇ灏�涓嶅悓灞忓箷鍙兘涓嶅悓)
		int textSize = 20;
		/*if(hasSelectTime)
			textSize = (screenheight / 100) * 3;
		else
			textSize = (screenheight / 100) * 4;*/
		wv_day.TEXT_SIZE = textSize;
		wv_month.TEXT_SIZE = textSize;
		wv_year.TEXT_SIZE = textSize;
		wv_hours.TEXT_SIZE = textSize;
		wv_mins.TEXT_SIZE = textSize;

	}

	public String getTime() {
		StringBuffer sb = new StringBuffer();
		sb.append((wv_year.getCurrentItem() + START_YEAR)).append("-")
		.append((wv_month.getCurrentItem() + 1)>10?""+(wv_month.getCurrentItem() + 1):"0"+(wv_month.getCurrentItem() + 1)).append("-")
		.append((wv_day.getCurrentItem() + 1)>10?""+(wv_day.getCurrentItem() + 1):"0"+(wv_day.getCurrentItem() + 1)).append(" ")
		.append(wv_hours.getCurrentItem()>10?""+wv_hours.getCurrentItem():"0"+wv_hours.getCurrentItem()).append(":")
		.append(wv_mins.getCurrentItem()>10?""+wv_mins.getCurrentItem():"0"+wv_mins.getCurrentItem());
		return sb.toString();
	}
}
