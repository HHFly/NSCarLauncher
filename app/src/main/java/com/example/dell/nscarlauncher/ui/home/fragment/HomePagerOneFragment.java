package com.example.dell.nscarlauncher.ui.home.fragment;

import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

import com.example.dell.nscarlauncher.R;
import com.example.dell.nscarlauncher.app.App;
import com.example.dell.nscarlauncher.base.fragment.BaseFragment;
import com.example.dell.nscarlauncher.common.util.TimeUtils;
import com.example.dell.nscarlauncher.ui.home.androideunm.HandleKey;
import com.example.dell.nscarlauncher.widget.CircleView;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HomePagerOneFragment extends BaseFragment {
    private CircleView circleView;
    private static TextView tv_w_time;
    private static TextView tv_w_date;
    private static TextView tv_w_week;

    //thread flag
    private volatile boolean timeFlag = true;
    private volatile boolean weatherFlag = true;
    @Override
    public int getContentResId() {
        return R.layout.fragment_home1;
    }

    @Override
    public void findView() {
        circleView =getView(R.id.wave_view);
        tv_w_time =getView(R.id.tv_w_time);
        tv_w_date =getView(R.id.tv_w_date);
        tv_w_week=getView(R.id.tv_w_week);
    }

    @Override
    public void setListener() {

    }

    @Override
    public void onResume() {
        super.onResume();
        init_time();
    }

    @Override
    public void initView() {

        circleView.setmWaterLevel(0.5f);
        circleView.startWave();

    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        circleView.stopWave();
        circleView = null;
    }

    //日期 时间模块初始化
    private void init_time() {
        ExecutorService timePool = Executors.newSingleThreadExecutor();  //采用线程池单一线程方式，防止被杀死
        timePool.execute(new Runnable() {
            @Override
            public void run() {

                while (timeFlag) {
                    try {
                        //延时一秒作用
                        Message msgtimedata = new Message();
                        msgtimedata.what =HandleKey.TIME;
                        App.pagerOneHnadler.sendMessage(msgtimedata);
                        Thread.sleep(1000*60);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public static class PagerOneHnadler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            TimeUtils timeUtils =new TimeUtils();
            switch (msg.what){
                case HandleKey.TIME:
                    tv_w_time.setText(timeUtils.getHour());
                    tv_w_date.setText(timeUtils.getDate());
                    tv_w_week.setText(timeUtils.getDayOfWeek());
                    break;
                case HandleKey.WEATHAER:
                    break;
            }
            super.handleMessage(msg);

        }
    }

}
