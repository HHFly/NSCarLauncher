package com.kandi.dell.nscarlauncher.ui_portrait.ems;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.kandi.dell.nscarlauncher.R;
import com.kandi.dell.nscarlauncher.base.fragment.BaseFragment;
import com.kandi.dell.nscarlauncher.candriver.DriverServiceManger;
import com.kandi.dell.nscarlauncher.candriver.EmsDriver;
import com.kandi.dell.nscarlauncher.ui.home.androideunm.FragmentType;
import com.kandi.dell.nscarlauncher.ui_portrait.home.HomePagerActivity;

import java.util.Timer;
import java.util.TimerTask;

public class EmsFragment extends BaseFragment{
    private HomePagerActivity homePagerActivity;
    private ImageView bat_progress;
    private TextView soc_per_txt;
    private TextView ems_cV;//总电压
    private TextView ems_cA;//总电流
    private TextView ems_highestT;//最高温度
    private TextView ems_lowestT;//最低温度
    private TextView ems_highestCell;//单体最高
    private TextView ems_lowestCell;//单体最低
    private TextView ems_IR;//绝缘电阻
    private TextView ems_batnum;//电池数量
    EmsDriver emsDriver;
    boolean isbreak = false;
    Thread thread;
    private final int UPDATE_PANNEL = 1;

    @Override
    public void setmType(int mType) {
        super.setmType(FragmentType.EMS);
    }

    public void setHomePagerActivity(HomePagerActivity homePagerActivity) {
        this.homePagerActivity = homePagerActivity;
    }

    @Override
    public void onUnFirstResume() {

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
        thread.setName("EmsFragment");
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
    public int getContentResId() {
        return R.layout.fragment_ems;
    }

    @Override
    public void findView() {
        bat_progress = getView(R.id.bat_progress);
        soc_per_txt = getView(R.id.soc_per_txt);
        ems_cV = getView(R.id.ems_cV);//总电压
        ems_cA = getView(R.id.ems_cA);//总电流
        ems_highestT = getView(R.id.ems_highestT);//最高温度
        ems_lowestT = getView(R.id.ems_lowestT);//最低温度
        ems_highestCell = getView(R.id.ems_highestCell);//单体最高
        ems_lowestCell = getView(R.id.ems_lowestCell);//单体最低
        ems_IR = getView(R.id.ems_IR);//绝缘电阻
        ems_batnum = getView(R.id.ems_batnum);//电池数量
    }

    @Override
    public void setListener() {
        setClickListener(R.id.bt_back);
    }

    @Override
    public void initView() {
        setmType(FragmentType.EMS);
//        setBatSoc(40);
        emsDriver = DriverServiceManger.getInstance().getEmsDriver();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.bt_back:
                homePagerActivity.hideFragment();
                break;
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

    public void setBatSoc(int progress){
        if(progress>=0 && progress<5){
            bat_progress.setBackgroundResource(R.mipmap.battery_bg);
        }else if(progress>=5 && progress<=30){
            bat_progress.setBackgroundResource(R.mipmap.bat_soc_low);
        }else if(progress <80){
            bat_progress.setBackgroundResource(R.mipmap.bat_soc_middle);
        }else {
            bat_progress.setBackgroundResource(R.mipmap.bat_soc_high);
        }
        soc_per_txt.setText(progress+"%");
    }

    /**
     *
     * @param cV_val 总电压
     * @param cA_val 总电流
     * @param highestT_val 最高温度
     * @param lowestT_val 最低温度
     * @param highestcell_val 单体最高
     * @param lowestcell_val 单体最低
     * @param IR_val 绝缘电阻
     * @param batnum_val 电池数量
     */
    public void setBatInfo(int cV_val,int cA_val,int highestT_val,int lowestT_val,int highestcell_val,int lowestcell_val,int IR_val,int batnum_val){
        ems_cV.setText(cV_val+"V");
        ems_cA.setText(cA_val+"A");
        ems_highestT.setText(highestT_val+"℃");
        ems_lowestT.setText(lowestT_val+"℃");
        ems_highestCell.setText(highestcell_val+"mV");
        ems_lowestCell.setText(lowestcell_val+"mV");
        ems_IR.setText(IR_val+"kΩ");
        ems_batnum.setText(batnum_val+getString(R.string.箱));
    }

    public void refreshPannelStatus(){
        if(emsDriver != null){
            try{
                emsDriver.retreveEmsInfo();
                setBatInfo(emsDriver.getEms_cV(),emsDriver.getEms_cA(),emsDriver.getEms_highestT(),emsDriver.getEms_lowestT(),
                        emsDriver.getEms_highestCell(),emsDriver.getEms_lowestCell(),emsDriver.getEms_IR(),emsDriver.getEms_batnum());
                setBatSoc(emsDriver.getSocPer());
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

}
