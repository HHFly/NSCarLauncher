
package com.nushine.nshlbus.com.driverlayer.os_driverServer;
import java.io.File;
import java.util.Arrays;

import org.apache.log4j.Logger;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Environment;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.driverlayer.os_driverServer.IECarDriver;
import com.nushine.nshlbus.MainActivity;
import com.nushine.nshlbus.com.driverlayer.os_driverInfo.DriverInfomation;
import com.nushine.nshlbus.com.driverlayer.os_driverTime.DriverTimeEngine;
import com.vwcs.log.LogInit;
import com.vwcs.log.LogInit.Notify_Level;

/**
 *
 * @author chenzheng_java
 * @description 提供服务的service
 *
 */
public class DriverManager extends Service {
    Logger log = Logger.getLogger(DriverManager.class);
    DriverCfg m_cfg ;//配置文件初始化
    public DriverInfomation m_Info;
    private final static String TAG = "RemoteService";
    private String m_ver="";
    private MyBinder binder;
    private SensorManager sm;
    private Sensor ligthSensor;
    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "执行了OnBind");
        if(binder==null){
            binder = new MyBinder();
        }
        return binder;
    }

    /*
     *@brif: 驱动服务程序的事件通过广播消息的方式发送：
     *@para: key： "KD_CAST_EVENT"；
     *		 value：定义见下表 ,
     * **************************************************************************
     * ********************************表1  驱动服务程序广播消息定义***********************
     * **************************************************************************
     * 【类别】    ** 【广播Key】  **  【广播Value】      			**   【备注】       				*
     * BCM状态   **  key+0  **  0:表示在线；1：表示离线		**	boolean					*
     * **************************************************************************
     */
    public CheckEvent[] m_check = {
            new CheckBCMStatus()};

    public enum DRIEVENT{
        DRI_BCMSTATUS		//BCM状态事件
        }
    /**
     * @author hujj_kd
     *    事件类：包含每个事件的基本功能，例如广播发送，	超时判断机制
     * @version v1.0
     */
    public abstract class CheckEvent{
        protected boolean m_event=false;
        protected boolean m_eventbak=false;
        protected long old_EnventTime= DriverTimeEngine.GetSysTem_ms();
        protected int  timeout_cycle=100;	/**事件推送周期默认100ms*/
        protected abstract void Check();
        /**
         * @param
         * @return 上一时刻 - 下一时刻>=time_cycle = true:(超时)
         * 		         上一时刻 - 下一时刻<time_cycle = false:(未超时)
         */
        protected boolean EventCheck(){
            if(DriverTimeEngine.CheckTimeOut(old_EnventTime,DriverTimeEngine.GetSysTem_ms(),timeout_cycle)){
                old_EnventTime = DriverTimeEngine.GetSysTem_ms();
                return true;
            }else{
                return false;
            }
        }
        protected void SendBroadcast(int num,boolean value){	//boolean型广播发送
            //广播对象
            int i=0;
            if(value){
                i=1;
            }
            String m_dribroadcast = "com.driverlayer.kdos_driverserver";	//广播名称
            Intent intent=new Intent(m_dribroadcast);
            String m_key="KD_CAST_EVENT";	/**事件的Key值*/
            intent.putExtra(m_key+num, value);
            sendBroadcast(intent);
        }
        protected void SendBroadcast(int num,int value){	//整形广播发送
            //广播对象
            String m_dribroadcast = "com.driverlayer.kdos_driverserver";	//广播名称
            Intent intent=new Intent(m_dribroadcast);
            String m_key="KD_CAST_EVENT";	/**事件的Key值*/
            intent.putExtra(m_key+num, value);
            sendBroadcast(intent);
        }
        protected void SendBroadcast(int num,int[] value){	//整形数组广播发送
            //广播对象
            String m_dribroadcast = "com.driverlayer.kdos_driverserver";	//广播名称
            Intent intent=new Intent(m_dribroadcast);
            String m_key="KD_CAST_EVENT";	/**事件的Key值*/
            intent.putExtra(m_key+num, value);
            sendBroadcast(intent);
        }
        protected void SendBroadcast(String broadcast){	//整形数组广播发送
            //广播对象
            Intent intent=new Intent(broadcast);
            sendBroadcast(intent);
        }
    }
    //数据状态改变
    class CheckBCMStatus extends CheckEvent{
        int bak6 = 0;
        int bak7 = 0;
        @Override
        public void Check() {
            // TODO Auto-generated method stub
            if((m_Info.m_DriverInfo.m_Bcm.m_BcmInfo.m_getcarcontrol.m_byte6 != bak6 | m_Info.m_DriverInfo.m_Bcm.m_BcmInfo.m_getcarcontrol.m_byte7 != bak7) && EventCheck()){
                bak6 = m_Info.m_DriverInfo.m_Bcm.m_BcmInfo.m_getcarcontrol.m_byte6;
                bak7 = m_Info.m_DriverInfo.m_Bcm.m_BcmInfo.m_getcarcontrol.m_byte7;
                SendBroadcast(DRIEVENT.DRI_BCMSTATUS.ordinal(),m_eventbak);
            }
        }
    }

    public boolean UnonBind(Intent intent) {
        Log.i(TAG, "执行了UnBind");
        return super.onUnbind(intent);
    }

    /**
     * @brif   实现远程AIDL接口
     * @return 无
     */
    private class MyBinder extends IECarDriver.Stub{

        @Override
        public String getVersion() throws RemoteException {
            // TODO Auto-generated method stub
            return m_ver;
        }

        @Override
        public int getCar_Status(int[] param) throws RemoteException {
            param[0] = m_Info.m_DriverInfo.m_Bcm.m_BcmInfo.m_getcarcontrol.m_byte6;
            param[1] = m_Info.m_DriverInfo.m_Bcm.m_BcmInfo.m_getcarcontrol.m_byte7;
            param[2] = m_Info.m_DriverInfo.m_Bcm.m_BcmInfo.m_getcarcontrol.m_rearfog_light;
            param[3] = m_Info.m_DriverInfo.m_Bcm.m_BcmInfo.m_getcarcontrol.m_main_light1;
            param[4] = m_Info.m_DriverInfo.m_Bcm.m_BcmInfo.m_getcarcontrol.m_main_light2;
            return 0;
        }

        @Override
        public int setCar_Status(int[] param) throws RemoteException {
            m_Info.m_DriverInfo.m_Bcm.m_BcmInfo.m_setcarcontrol.m_byte6 = param[0];
            m_Info.m_DriverInfo.m_Bcm.m_BcmInfo.m_setcarcontrol.m_byte7 = param[1];
            m_Info.m_DriverInfo.m_Bcm.m_BcmInfo.m_setcarcontrol.m_rearfog_light = (byte)param[2];
            m_Info.m_DriverInfo.m_Bcm.m_BcmInfo.m_setcarcontrol.m_main_light1 = (byte)param[3];
            m_Info.m_DriverInfo.m_Bcm.m_BcmInfo.m_setcarcontrol.m_main_light2 = (byte)param[4];
            m_Info.m_DriverInfo.m_Bcm.Change_BCMControlSendCycle();
            return 0;
        }

    }
    /**
     * @brif   初始化启动服务程序
     * @return 无
     */
    private void DriverInit(){
        m_cfg = new DriverCfg();//配置文件初始化
        m_Info = new DriverInfomation(m_cfg.m_gcfg);
        m_ver = getVersion();
        //日志配置
        LogInit m_cfglog=new LogInit(log);
        String path = Environment.getExternalStorageDirectory() + File.separator
                + "VWCS_App" + File.separator + "VWCS_Driver" + File.separator + "logs" + File.separator
                + "driver_log.txt";
        m_cfglog.setLogOutPath(path);
        m_cfglog.setLogRootLevel(Notify_Level.LEVEL_OFF);
        m_cfglog.setLogFileSize(10);//单个日志文件10M
        m_cfglog.setLogFileBackNum(50);//滚动50个文件
        m_cfglog.setLogFlushMode(false);
        m_cfglog.cfgLog();
        //m_cfg.setDefaultcfg(path, Notify_Level.LEVEL_DEBUG);//日志文件输出默认配置
        Logger log = Logger.getLogger(MainActivity.class);
        log.info("DriverServer Start!!!");
    }

    /**
     * @brif   获取版本号
     * @return 当前应用的版本号
     */
    public String getVersion() {
        try {
            PackageManager manager = this.getPackageManager();
            PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
            String version = info.versionName;
            return version;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        DriverInit();
        Temperature_Init();
        InitManager_Thread();
//		SocketThreadManager.sharedInstance();
        super.onCreate();
    }
    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        //System.exit(0);
        super.onDestroy();
    }

    @Override
    public void onStart(Intent intent,int startId){
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    private void InitManager_Thread(){
        new Thread(){
            public void run(){
                long old_NormalTime=DriverTimeEngine.GetSysTem_ms();
                final short m_NormalRate=250;
                while(true){
                    try {
                        sleep(10);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    for(CheckEvent ck:m_check){
                        ck.Check(); //判断各个事件状态情况
                    }
                }
            }
        }.start();
    }

    public void Temperature_Init(){
        //获取SensorManager对象
        sm = (SensorManager)getSystemService(SENSOR_SERVICE);
        //获取Sensor对象
        ligthSensor = sm.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        sm.registerListener(new TempSensorListener(), ligthSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public class TempSensorListener implements SensorEventListener {

        public void onSensorChanged1(SensorEvent event) {
            //获取精度
            //float acc = event.accuracy;
        }

        @Override
        public void onSensorChanged(SensorEvent arg0) {
            // TODO Auto-generated method stub
            //获取采样值
            float sample = arg0.values[0];
            m_Info.m_DriverInfo.m_SensorTemp.CalculationTemp(sample);
        }

        @Override
        public void onAccuracyChanged(Sensor arg0, int arg1) {
            // TODO Auto-generated method stub

        }

    }
}

