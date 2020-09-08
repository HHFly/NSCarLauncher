package com.kandi.dell.nscarlauncher.ui_portrait.setting.dialog;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.kandi.dell.nscarlauncher.R;
import com.kandi.dell.nscarlauncher.ui_portrait.setting.model.SetDate_WheelMain;
import com.kandi.dell.nscarlauncher.ui_portrait.setting.widgets.SetDate_JudgeDate;
import com.kandi.dell.nscarlauncher.ui_portrait.setting.widgets.SetDate_ScreenInfo;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

public class DateTimeDialog extends Dialog {

    private Button return_bt;
    SetDate_WheelMain wheelMain;
    DateFormat dateFormat;
    Activity activity = null;
    Context context;

    public DateTimeDialog(Activity activity,DialogDismissListener listener) {
        super(activity, R.style.my_dialog);
        this.activity = activity;
        this.listener = listener;
    }

    private void initView(){
        dateFormat = android.text.format.DateFormat.getDateFormat(getContext());
        return_bt = (Button) findViewById(R.id.timeset_return);
        return_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                java.util.Date dt=null;
                SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd HH:mm");
                try {
                    dt=df.parse(wheelMain.getTime());
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(dt);
//					SetDate_SystemDateTime.setTime(cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE));
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                if(listener != null){
                    String date = wheelMain.getTime();
                    Calendar calendar = Calendar.getInstance();
                    SimpleDateFormat Cur_Time = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    if (SetDate_JudgeDate.isDate(date, "yyyy-MM-dd HH:mm")) {
                        try {
                            calendar.setTime(Cur_Time.parse(date));
                        } catch (ParseException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                    listener.onDismiss(wheelMain.getTime(), 0, calendar);
                }

                dismiss();

            }
        });

        final View timepickerview = (View) findViewById(R.id.timePicker1);
        SetDate_ScreenInfo screenInfo = new SetDate_ScreenInfo(activity);
        wheelMain = new SetDate_WheelMain(timepickerview,true);
        wheelMain.screenheight = screenInfo.getHeight();

        String str ="";
        SimpleDateFormat Cur_Time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        str = Cur_Time.format(new java.util.Date());

        Calendar calendar = Calendar.getInstance();
        if (SetDate_JudgeDate.isDate(str, "yyyy-MM-dd HH:mm:ss")) {
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
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.set_system_datetime);
        context = getContext();
        this.initView();
    }


    public String getDate(){
        Log.i("hdt", wheelMain.getTime());
        return wheelMain.getTime();
    }



    @Override
    protected void onStop() {
        super.onStop();
    }



    private DialogDismissListener listener;
}