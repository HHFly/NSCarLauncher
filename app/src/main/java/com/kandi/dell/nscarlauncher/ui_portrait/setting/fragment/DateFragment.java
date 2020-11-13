package com.kandi.dell.nscarlauncher.ui_portrait.setting.fragment;

import android.content.ContentResolver;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;

import com.kandi.dell.nscarlauncher.R;
import com.kandi.dell.nscarlauncher.base.fragment.BaseFragment;
import com.kandi.dell.nscarlauncher.candriver.DriverServiceManger;
import com.kandi.dell.nscarlauncher.common.util.SPUtil;
import com.kandi.dell.nscarlauncher.ui.bluetooth.FlagProperty;
import com.kandi.dell.nscarlauncher.ui_portrait.home.HomePagerActivity;
import com.kandi.dell.nscarlauncher.ui_portrait.setting.dialog.DateDialog;
import com.kandi.dell.nscarlauncher.ui_portrait.setting.dialog.DateTimeDialog;
import com.kandi.dell.nscarlauncher.ui_portrait.setting.dialog.DialogDismissListener;

import android.support.v7.widget.SwitchCompat;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DateFragment extends BaseFragment {
    private HomePagerActivity homePagerActivity;
//    private CheckBox checkBox ;
    private SwitchCompat is_auto,cb_date;
    private RelativeLayout auto_layout,data_layout,time_layout,set24_layout;
    private boolean isAuto;//保存自动确定时间状态
    private TextView text_data,text_time;
    private DateTimeDialog timeDialog;
    private DateDialog dateDialog;
    boolean isbreak = false;
    Thread thread;
    private final int UPDATE_PANNEL = 1;
    SimpleDateFormat Cur_Time;
    public void setHomePagerActivity(HomePagerActivity homePagerActivity) {
        this.homePagerActivity = homePagerActivity;
    }
    @Override
    public int getContentResId() {
        return R.layout.fragment_set_date_portrait;
    }

    @Override
    public void findView() {
        auto_layout = getView(R.id.auto_layout);
        data_layout = getView(R.id.data_layout);
        time_layout = getView(R.id.time_layout);
        set24_layout = getView(R.id.set24_layout);
        is_auto = getView(R.id.is_auto);
        text_data = getView(R.id.text_data);
        text_time = getView(R.id.text_time);
        cb_date = getView(R.id.cb_date);
//        checkBox =getView(R.id.cb_date);
    }

    @Override
    public void setListener() {
        setClickListener(R.id.auto_layout);
//        setClickListener(R.id.rl_date);
        setClickListener(R.id.bt_back);
//        setClickListener(R.id.cb_date);
        setClickListener(R.id.set24_layout);
    }

    @Override
    public void initView() {
//        checkBox.setChecked(FlagProperty.isHourdate);
        String autotimestate = Settings.Global.getString(homePagerActivity.getActivity().getContentResolver(), Settings.Global.AUTO_TIME);
        if("1".equals(autotimestate)){
            isAuto = false;
            enableDateTimeSet(isAuto);
        }else{
            isAuto = true;
            enableDateTimeSet(isAuto);
        }

        String timeforstate = android.provider.Settings.System.getString(homePagerActivity.getActivity().getContentResolver(), Settings.System.TIME_12_24);
        if("24".equals(timeforstate)){
            cb_date.setChecked(true);
        }else{
            android.provider.Settings.System.putString(homePagerActivity.getActivity().getContentResolver(), Settings.System.TIME_12_24, "12");
            cb_date.setChecked(false);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        this.refreshPannelStatus();
    }

    @Override
    public void Resume() {
        isbreak = false;
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!isbreak){
                    try{
                        myHandler.sendEmptyMessage(UPDATE_PANNEL);
                        Thread.sleep(1000);
                    }catch (Exception e){
                    }
                }
            }
        });
        thread.setName("DateFragment");
        thread.start();
    }

    @Override
    public void Pause() {
        isbreak = true;
    }

    @Override
    public void onDestroy() {
        isbreak = true;
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.auto_layout:
                isAuto = !isAuto;
                Settings.Global.putString(homePagerActivity.getActivity().getContentResolver(), Settings.Global.AUTO_TIME, isAuto?"1":"0");
                //刷新界面
                enableDateTimeSet(isAuto);
                //需要刷新数据
                break;
            case R.id.data_layout:
                if(dateDialog == null){
					dateDialog = new DateDialog(homePagerActivity.getActivity(), new DialogDismissListener() {

						@Override
						public void onDismiss(String info, int what, Object obj) {
							Cur_Time = new SimpleDateFormat(Settings.System.getString(homePagerActivity.getActivity().getContentResolver(),Settings.System.DATE_FORMAT));
							Calendar calendar = (Calendar) obj;
                            text_data.setText(Cur_Time.format(calendar.getTime()));
						}
					});
				}
				dateDialog.show();
                break;
            case R.id.time_layout:
                if(timeDialog == null){
					timeDialog = new DateTimeDialog(homePagerActivity.getActivity(), new DialogDismissListener() {

						@Override
						public void onDismiss(String info, int what, Object obj) {
							Cur_Time = new SimpleDateFormat(SPUtil.getInstance(homePagerActivity.getActivity()).getString("shared_time_format", "HH:mm"));
							Calendar calendar = (Calendar) obj;
                            text_time.setText(Cur_Time.format(calendar.getTime()));
						}
					});
				}
				timeDialog.show();
                break;
            case R.id.bt_back:
                homePagerActivity.getSetFragment().hideFragment();
                break;
            case R.id.set24_layout:
                ContentResolver cv = homePagerActivity.getActivity().getContentResolver();
                boolean is24Checked = cb_date.isChecked();
                if(!is24Checked){
                    android.provider.Settings.System.putString(cv, Settings.System.TIME_12_24, "24");
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                    String dateStr = sdf.format(new java.util.Date());
                    text_time.setText(dateStr);
                }else{
                    android.provider.Settings.System.putString(cv, Settings.System.TIME_12_24, "12");
                    SimpleDateFormat sdf = new SimpleDateFormat("a hh:mm");
                    String dateStr = sdf.format(new java.util.Date());
                    text_time.setText(dateStr);
                }
                SPUtil.getInstance(homePagerActivity.getActivity()).putString("shared_time_format", !is24Checked ? "HH:mm":"a hh:mm");
                cb_date.setChecked(!is24Checked);
                break;
        }
    }

    public void enableDateTimeSet(boolean selected){
        auto_layout.setSelected(true);
        data_layout.setSelected(selected);
        time_layout.setSelected(selected);
        is_auto.setChecked(!selected);
        if(selected){
            data_layout.setOnClickListener(this);
            time_layout.setOnClickListener(this);
        }else{
            data_layout.setOnClickListener(null);
            time_layout.setOnClickListener(null);
        }
    }

    public Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_PANNEL:
                    refreshPannelStatus();
                    break;
                default:
                    break;
            }
        };
    };

    public void refreshPannelStatus(){
        Cur_Time = new SimpleDateFormat(Settings.System.getString(homePagerActivity.getActivity().getContentResolver(),Settings.System.DATE_FORMAT));
        Calendar calendar = Calendar.getInstance();;
        text_data.setText(Cur_Time.format(calendar.getTime()));
        Cur_Time = new SimpleDateFormat(SPUtil.getInstance(homePagerActivity.getActivity()).getString("shared_time_format", "HH:mm"));
        text_time.setText(Cur_Time.format(calendar.getTime()));
    }
}
