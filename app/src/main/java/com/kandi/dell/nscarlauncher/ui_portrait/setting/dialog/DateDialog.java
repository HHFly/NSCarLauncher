package com.kandi.dell.nscarlauncher.ui_portrait.setting.dialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.kandi.dell.nscarlauncher.R;
import com.kandi.dell.nscarlauncher.ui_portrait.setting.model.SetDate_WheelMain;
import com.kandi.dell.nscarlauncher.ui_portrait.setting.widgets.SetDate_JudgeDate;
import com.kandi.dell.nscarlauncher.ui_portrait.setting.widgets.SetDate_ScreenInfo;

/**
 * 设置界面日期设置弹出Dialog框
 * @author justin
 *
 */
public class DateDialog extends Dialog{
	private Button return_bt;
	private TextView toptitle;
	SetDate_WheelMain wheelMain;
	Activity activity = null;
	private DialogDismissListener listener;
	Context context;
	
	public DateDialog(Activity activity,DialogDismissListener listener) {
		super(activity, R.style.my_dialog);
		this.activity = activity;
		this.listener = listener;
	}
	
	private void initView(){
		toptitle = (TextView) findViewById(R.id.toptitle);
		toptitle.setText(context.getResources().getString(R.string.设置日期));
		
		return_bt = (Button) findViewById(R.id.timeset_return);
		String dataformat = Settings.System.getString(getContext().getContentResolver(), Settings.System.DATE_FORMAT);
		if("".equals(dataformat) || dataformat == null){
			Settings.System.putString(getContext().getContentResolver(), Settings.System.DATE_FORMAT,"yyyy-MM-dd");
		}
		return_bt.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				java.util.Date dt=null;  
				SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd hh:mm");
				try {
					dt=df.parse(wheelMain.getTime());
					Calendar cal=Calendar.getInstance();  
					cal.setTime(dt); 
//					SetDate_SystemDateTime.setDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				if(listener != null){
					String date = wheelMain.getTime();
					Calendar calendar = Calendar.getInstance();
					if (SetDate_JudgeDate.isDate(date, "yyyy-MM-dd HH:mm")) {
						try {
							calendar.setTime(df.parse(date));
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					listener.onDismiss(date, 0, calendar);
				}
				
				dismiss();
			}
		});
		
		final View timepickerview = (View) findViewById(R.id.timePicker1);
		SetDate_ScreenInfo screenInfo = new SetDate_ScreenInfo(activity);
		wheelMain = new SetDate_WheelMain(timepickerview);
		wheelMain.screenheight = screenInfo.getHeight();
		
		String str ="";
		SimpleDateFormat Cur_Time = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		str = Cur_Time.format(new java.util.Date()); 
		
		Calendar calendar = Calendar.getInstance();
		if (SetDate_JudgeDate.isDate(str, "yyyy-MM-dd HH:mm")) {
			try {
				calendar.setTime(Cur_Time.parse(str));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
	    int min = calendar.get(Calendar.MINUTE);
		wheelMain.initDateTimePicker(year, month, day,hour,min,context);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.set_system_datetime);
		context = getContext();
		initView();
		
	}
}
