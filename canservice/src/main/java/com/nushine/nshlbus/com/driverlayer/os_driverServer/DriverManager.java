
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
import com.nushine.nshlbus.com.driverlayer.os_driverApp.DriverMFL.MFL_ASSISTANCE_CONTROL;
import com.nushine.nshlbus.com.driverlayer.os_driverApp.DriverBcm.BCM_STATUS;
import com.nushine.nshlbus.com.driverlayer.os_driverApp.DriverBcm.BCM_DOOR_CONTROL;
import com.nushine.nshlbus.com.driverlayer.os_driverApp.DriverBcm.BCM_WINDOWS_CONTROL;
import com.nushine.nshlbus.com.driverlayer.os_driverApp.DriverBcm.BCM_CAR_EXLIGTH_CONTROL;
import com.nushine.nshlbus.com.driverlayer.os_driverApp.DriverBcm.BCM_BOOLEAN;
import com.nushine.nshlbus.com.driverlayer.os_driverApp.DriverAC.AC_STATUS;

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
    private byte[] vinInit;
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
     * 	档位         **  key+0   **  0:隐藏；1:弹出    			**  boolean					*
     * 空调面板    **  key+1   **  0:正常；1:离线    			**  boolean					*
     * 充电插入    **  key+2   **  0:拔出；1:插入    			**  boolean					*
     * 充电起停    **  key+3   **  0:停止；1:启动 2:故障停止    	**  int						*
     *  故障         **  key+4   **  0:解除；1:发生    			**  boolean，详细故障通过接口读取	*
     *  告警         **  key+5   **  0:解除；1:发生    			**  boolean，详细告警通过接口读取	*
     * 	车门         **  key+6   **  0:解锁；1:锁闭    			**  boolean					*
     * 	车窗         **  key+7   **  1:上升；2:下降；3:暂停		**  int[],数组下标表示车窗编号		*
     * 后备箱        **  key+8   **  0:锁闭；1:解锁    			**  boolean					*
     * 充电盖        **  key+9   **  0:锁闭；1:解锁    			**  boolean					*
     *	大灯         **  key+10  **  1:远光灯 ；2:近光灯；3：关闭       **  int						*
     * 	双跳         **  key+11  **  0:锁闭；1:解锁    			**  boolean					*
     * 前雾灯         **  key+12  **  0:关闭；1:打开    			**  boolean					*
     * 	小灯         **  key+13  **  0:关闭；1:打开    			**  boolean					*
     * 助力转向    **  key+14  **  1:低助力；2:中助力；3:高助力     **  int						*
     * 电池舱门    **  key+15  **  1:上升；2:下降；3：暂停    	**  int						*
     * 前雾灯        **  key+16  **  0:关闭；1:打开    			**  boolean					*
     * 参数设置    **  key+17  **  0:设置异常；1:设置完成    	    **  boolean					*
     * BCM状态   **  key+18  **  0:表示在线；1：表示离线		**	boolean					*
     * **************************************************************************
     */
    public CheckEvent[] m_check = {
            new CheckAC_Enable(),
            new CheckInsert_Charger(),
            new CheckStart_Charger(),
            new CheckDoorLockStatus(),
            new CheckWindowsStatus(),
            new CheckTrunkBootStatus(),
            new CheckChargerTopStatus(),
            new CheckHeadLightStatus(),
            new CheckDoubleLampStatus(),
            new CheckFrontFrogLampStatus(),
            new CheckLittleLampStatus(),
            new CheckGearFrogLampStatus(),
            new CheckBcm_CommStatus(),
            new CheckMFLStatus()};

    public enum DRIEVENT{
        DRI_AIR_ENABLE,		//空调面板使能或者禁止状态变化事件
        DRI_INSERT_CHARGER, //充电抢插入状态变化事件
        DRI_CHARGER_ONOFF,	//充电机启动或者停止状态变化事件
        DRI_BCMSTATUS,		//BCM状态事件
        DRI_MFLSTATUS       //多功能方向盘事件,
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

    //多功能方向盘事件
    class CheckMFLStatus extends CheckEvent{
        MFL_ASSISTANCE_CONTROL[] m_mfl_bak = new MFL_ASSISTANCE_CONTROL[8];
        MFL_ASSISTANCE_CONTROL[] m_mfl_init = new MFL_ASSISTANCE_CONTROL[8];
        String ACTION_WHEEL_VOLUMEADD = "com.kangdi.BroadCast.WheelVolumeAdd";//多功能方向盘音量+
        String ACTION_WHEEL_VOLUMEREDUCE = "com.kangdi.BroadCast.WheelVolumeReduce";//多功能方向盘音量-
        String ACTION_WHEEL_MODE = "com.kangdi.BroadCast.WheelMode";//多功能方向盘模式
        String ACTION_WHEEL_VOICE = "com.kangdi.BroadCast.WheelVoice";//多功能方向盘语音输入
        String ACTION_WHEEL_MUTE = "com.kangdi.BroadCast.Mute";//多功能方向盘静音
        String ACTION_WHEEL_MUSIC_PREV = "com.kangdi.BroadCast.WheelMusicPrev";//多功能方向盘音乐上一首
        String ACTION_WHEEL_MUSIC_NEXT = "com.kangdi.BroadCast.WheelMusicNext";//多功能方向盘音乐下一首
        String ACTION_WHEEL_CALL = "com.kangdi.BroadCast.WheelCall";//多功能方向盘接听
        String ACTION_WHEEL_HANGUP = "com.kangdi.BroadCast.WheelHangup";//多功能方向盘挂断
        String[] mfl_status = new String[] { ACTION_WHEEL_VOLUMEADD,
                ACTION_WHEEL_MODE, ACTION_WHEEL_VOLUMEREDUCE,
                ACTION_WHEEL_VOICE, ACTION_WHEEL_MUSIC_NEXT, ACTION_WHEEL_CALL,
                ACTION_WHEEL_MUSIC_PREV, ACTION_WHEEL_MUTE, ACTION_WHEEL_HANGUP };
        long oldtime;
        long nowtime;
        int[] report;
        String MFL_Broad = "";
        boolean flag = false;
        CheckMFLStatus(){
            for(int i=0;i<m_mfl_bak.length;i++){
                m_mfl_bak[i] = MFL_ASSISTANCE_CONTROL.FORCE_INVALIDE;
            }
            for(int i=0;i<m_mfl_init.length;i++){
                m_mfl_init[i] = MFL_ASSISTANCE_CONTROL.FORCE_INVALIDE;
            }
        }
        @Override
        protected void Check() {
            // TODO Auto-generated method stub
            timeout_cycle=80;
            if(EventCheck()){
                report = new int[m_mfl_bak.length];
                for(int i=0; i<m_mfl_bak.length;i++){
                    report[i] = m_Info.m_DriverInfo.m_MFL.m_MFLinfo.m_getMFLcontrol.m_mfl_status[i].ordinal();//[音量+，mode，音量-，*语音输入*，下一首，挂断/接听，上一首，*静音*]
                }
                if(!flag){
                    if(m_mfl_bak[4].ordinal() == 1 || m_mfl_bak[5].ordinal() == 1 || m_mfl_bak[6].ordinal() == 1 || m_mfl_bak[7].ordinal() == 1){
                        oldtime = DriverTimeEngine.GetSysTem_ms();
                        flag = true;
                    }else{
                        if(!Arrays.equals(m_Info.m_DriverInfo.m_MFL.m_MFLinfo.m_getMFLcontrol.m_mfl_status,m_mfl_init)){
                            if(report[4] == 0 && report[5] == 0 && report[6] == 0 && report[7] == 0){
//								SendBroadcast(DRIEVENT.DRI_MFLSTATUS.ordinal(),report);
                                for(int i=0;i<report.length;i++){
                                    if(report[i] != 0){
                                        if(i == 5){
                                            if(report[i] == 2){
                                                MFL_Broad = mfl_status[8];
                                            }else{
                                                MFL_Broad = mfl_status[i];
                                            }
                                        }else{
                                            MFL_Broad = mfl_status[i];
                                        }
                                        SendBroadcast(MFL_Broad);
                                    }
                                }
                            }
                        }
                    }
                }else{
                    if(report[4] == 0 && report[5] == 0 && report[6] == 0 && report[7] == 0){
                        nowtime = DriverTimeEngine.GetSysTem_ms();
                        for(int i=0; i<m_mfl_bak.length;i++){
                            report[i] = m_mfl_bak[i].ordinal();//[音量+，mode，音量-，*语音输入*，下一首，挂断/接听，上一首，*静音*]
                        }
                        if((nowtime-oldtime)>=2000){
                            for(int i=4;i<=6;i++){
                                if(m_mfl_bak[i].ordinal() == 1){
                                    report[i] = 2;
                                }
                            }
//							SendBroadcast(DRIEVENT.DRI_MFLSTATUS.ordinal(),report);
                            for(int i=0;i<report.length;i++){
                                if(report[i] != 0){
                                    if(i == 5){
                                        if(report[i] == 2){
                                            MFL_Broad = mfl_status[8];
                                        }else{
                                            MFL_Broad = mfl_status[i];
                                        }
                                    }else{
                                        MFL_Broad = mfl_status[i];
                                    }
                                    SendBroadcast(MFL_Broad);
                                }
                            }
                        }else{
                            for(int i=4;i<=6;i++){
                                if(m_mfl_bak[i].ordinal() == 1){
                                    report[i] = 1;
                                }
                            }
//							SendBroadcast(DRIEVENT.DRI_MFLSTATUS.ordinal(),report);
                            for(int i=0;i<report.length;i++){
                                if(report[i] != 0){
                                    if(i == 5){
                                        if(report[i] == 2){
                                            MFL_Broad = mfl_status[8];
                                        }else{
                                            MFL_Broad = mfl_status[i];
                                        }
                                    }else{
                                        MFL_Broad = mfl_status[i];
                                    }
                                    SendBroadcast(MFL_Broad);
                                }
                            }
                        }
                        flag = false;
                    }
                }
                m_mfl_bak = m_Info.m_DriverInfo.m_MFL.m_MFLinfo.m_getMFLcontrol.m_mfl_status.clone();
            }
        }

    }

    //BCM掉线事件广播
    class CheckBcm_CommStatus extends CheckEvent{

        @Override
        public void Check() {
            // TODO Auto-generated method stub
            if(m_Info.m_DriverInfo.m_Bcm.m_BCMStatus == BCM_STATUS.BCMSTATUS_NORMAL){
                m_event = false;
            }else{
                m_event = true;
            }

            if(m_event!=m_eventbak && EventCheck()){
                m_eventbak = m_event;
                SendBroadcast(DRIEVENT.DRI_BCMSTATUS.ordinal(),m_eventbak);
            }
        }
    }

    //空调面板隐藏条件判断
    class CheckAC_Enable extends CheckEvent{
        @Override
        public void Check() {
            timeout_cycle = 50;
            // TODO Auto-generated method stub
            //if(m_Info.m_DriverInfo.m_AC.m_ACInfo.AC_Change_Flag){
            //	m_event = !m_event;
            //}
            if(m_Info.m_DriverInfo.m_AC.m_ACInfo.AC_Change_Flag && EventCheck()){
                m_Info.loacalAirConditionInfo(); //空调信息反馈
                if(m_Info.m_DriverInfo.m_AC.m_ACInfo.m_ACstatus == AC_STATUS.AIRSTATUS_ERR){
                    SendBroadcast(DRIEVENT.DRI_AIR_ENABLE.ordinal(),true);
                }
                else{
                    SendBroadcast(DRIEVENT.DRI_AIR_ENABLE.ordinal(),false);
                }

                m_eventbak = m_event;
                m_Info.m_DriverInfo.m_AC.m_ACInfo.AC_Change_Flag = false;
            }
        }
    }

    //充电枪插入判断
    class CheckInsert_Charger extends CheckEvent{
        @Override
        public void Check() {
            // TODO Auto-generated method stub
            if(m_Info.m_DriverInfo.m_Vcu.m_VCUInfo.m_status.m_ChgGun ==1){
                m_event = true;
            }else{
                m_event = false;
            }
            if(m_event!=m_eventbak && EventCheck()){
                m_eventbak = m_event;
                SendBroadcast(DRIEVENT.DRI_INSERT_CHARGER.ordinal(),m_eventbak);
            }
        }
    }
    //充电机启动事件
    class CheckStart_Charger extends CheckEvent{
        byte old=0;
        @Override
        public void Check() {
            // TODO Auto-generated method stub
            if(m_Info.m_DriverInfo.m_Vcu.m_VCUInfo.m_status.m_Chging !=old && EventCheck()){
                old = m_Info.m_DriverInfo.m_Vcu.m_VCUInfo.m_status.m_Chging;
                SendBroadcast(DRIEVENT.DRI_CHARGER_ONOFF.ordinal(),old);
            }
        }
    }
    //车身锁改变
    class CheckDoorLockStatus extends CheckEvent{
        @Override
        public void Check() {
            // TODO Auto-generated method stub
            if(m_Info.m_DriverInfo.m_Bcm.m_BcmInfo.m_getcarcontrol.m_cardoor_lockstatus[0] ==BCM_DOOR_CONTROL.DOOR_LOCK){
                m_event = true;
            }else{
                m_event = false;
            }
            if(m_event!=m_eventbak  && EventCheck()){
                m_eventbak = m_event;
                SendBroadcast(DRIEVENT.DRI_BCMSTATUS.ordinal(),m_eventbak);
            }
        }
    }
    //车窗改变
    class CheckWindowsStatus extends CheckEvent{
        BCM_WINDOWS_CONTROL[] m_carwindow_bak=new BCM_WINDOWS_CONTROL[7];
        int[] report;
        CheckWindowsStatus(){
            for(int i=0;i<m_carwindow_bak.length;i++){
                m_carwindow_bak[i] = BCM_WINDOWS_CONTROL.WINDOWS_PAUSE;
            }
        }
        @Override
        public void Check() {
            timeout_cycle = 100;
            // TODO Auto-generated method stub
            if(!Arrays.equals(m_Info.m_DriverInfo.m_Bcm.m_BcmInfo.m_getcarcontrol.m_carwindow_seting,m_carwindow_bak)&& EventCheck()){
                m_carwindow_bak = m_Info.m_DriverInfo.m_Bcm.m_BcmInfo.m_getcarcontrol.m_carwindow_seting.clone();
                report = new int[m_carwindow_bak.length];
                for(int i=0; i<m_carwindow_bak.length;i++){
                    report[i] = m_carwindow_bak[i].ordinal();
                }
                SendBroadcast(DRIEVENT.DRI_BCMSTATUS.ordinal(),report);
            }
        }
    }
    //后备箱改变
    class CheckTrunkBootStatus extends CheckEvent{
        @Override
        public void Check() {
            // TODO Auto-generated method stub
            if(m_Info.m_DriverInfo.m_Bcm.m_BcmInfo.m_getcarcontrol.m_cardoor_lockstatus[1] ==BCM_DOOR_CONTROL.DOOR_LOCK){
                m_event = true;
            }else{
                m_event = false;
            }
            if(m_event!=m_eventbak  && EventCheck()){
                m_eventbak = m_event;
                SendBroadcast(DRIEVENT.DRI_BCMSTATUS.ordinal(),m_eventbak);
            }
        }
    }
    //充电盖
    class CheckChargerTopStatus extends CheckEvent{
        @Override
        public void Check() {
            // TODO Auto-generated method stub
            if(m_Info.m_DriverInfo.m_Bcm.m_BcmInfo.m_getcarcontrol.m_cardoor_lockstatus[2] ==BCM_DOOR_CONTROL.DOOR_LOCK){
                m_event = true;
            }else{
                m_event = false;
            }
            if(m_event!=m_eventbak  && EventCheck()){
                m_eventbak = m_event;
                SendBroadcast(DRIEVENT.DRI_BCMSTATUS.ordinal(),m_eventbak);
            }
        }
    }
    //大灯改变
    class CheckHeadLightStatus extends CheckEvent{
        BCM_CAR_EXLIGTH_CONTROL old=BCM_CAR_EXLIGTH_CONTROL.LIGTH_CLOSE;
        @Override
        public void Check() {
            // TODO Auto-generated method stub
            if(m_Info.m_DriverInfo.m_Bcm.m_BcmInfo.m_getcarcontrol.m_ex_carlamp !=old && EventCheck()){
                old =m_Info.m_DriverInfo.m_Bcm.m_BcmInfo.m_getcarcontrol.m_ex_carlamp;
                SendBroadcast(DRIEVENT.DRI_BCMSTATUS.ordinal(),old.ordinal());
            }
        }
    }
    //双跳改变
    class CheckDoubleLampStatus extends CheckEvent{
        @Override
        public void Check() {
            // TODO Auto-generated method stub
            if(m_Info.m_DriverInfo.m_Bcm.m_BcmInfo.m_getcarcontrol.m_doublelamp ==BCM_BOOLEAN.BOOLEAN_TRUE){
                m_event = true;
            }else{
                m_event = false;
            }
            if(m_event!=m_eventbak  && EventCheck()){
                m_eventbak = m_event;
                SendBroadcast(DRIEVENT.DRI_BCMSTATUS.ordinal(),m_eventbak);
            }
        }
    }
    //前灯改变
    class CheckFrontFrogLampStatus extends CheckEvent{
        @Override
        public void Check() {
            // TODO Auto-generated method stub
            if(m_Info.m_DriverInfo.m_Bcm.m_BcmInfo.m_getcarcontrol.m_frontfoglamp ==BCM_BOOLEAN.BOOLEAN_TRUE){
                m_event = true;
            }else{
                m_event = false;
            }
            if(m_event!=m_eventbak  && EventCheck()){
                m_eventbak = m_event;
                SendBroadcast(DRIEVENT.DRI_BCMSTATUS.ordinal(),m_eventbak);
            }
        }
    }

    //后雾灯改变
    class CheckGearFrogLampStatus extends CheckEvent{
        @Override
        public void Check() {
            // TODO Auto-generated method stub
            if(m_Info.m_DriverInfo.m_Bcm.m_BcmInfo.m_getcarcontrol.m_rearfoglamp ==BCM_BOOLEAN.BOOLEAN_TRUE){
                m_event = true;
            }else{
                m_event = false;
            }
            if(m_event!=m_eventbak  && EventCheck()){
                m_eventbak = m_event;
                SendBroadcast(DRIEVENT.DRI_BCMSTATUS.ordinal(),m_eventbak);
            }
        }
    }

    //小灯改变
    class CheckLittleLampStatus extends CheckEvent{
        @Override
        public void Check() {
            // TODO Auto-generated method stub
            if(m_Info.m_DriverInfo.m_Bcm.m_BcmInfo.m_getcarcontrol.m_readlamp ==BCM_BOOLEAN.BOOLEAN_TRUE){
                m_event = true;
            }else{
                m_event = false;
            }
            if(m_event!=m_eventbak  && EventCheck()){
                m_eventbak = m_event;
                SendBroadcast(DRIEVENT.DRI_BCMSTATUS.ordinal(),m_eventbak);
            }
        }
    }
    //电池舱门改变
    class CheckBatDoorStatus extends CheckEvent{
        BCM_WINDOWS_CONTROL old = BCM_WINDOWS_CONTROL.WINDOWS_PAUSE;
        @Override
        public void Check() {
            // TODO Auto-generated method stub
            if(m_Info.m_DriverInfo.m_Bcm.m_BcmInfo.m_getcarcontrol.m_BatDoor !=old && EventCheck()){
                old = m_Info.m_DriverInfo.m_Bcm.m_BcmInfo.m_getcarcontrol.m_BatDoor;
                SendBroadcast(DRIEVENT.DRI_BCMSTATUS.ordinal(),old.ordinal());
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
        public int set_OneKeyOpenWindow(int buttonState) throws RemoteException {
            // TODO Auto-generated method stub
            for(int i=0;i<m_Info.m_DriverInfo.m_Bcm.m_BcmInfo.m_setcarcontrol.m_carwindow_seting.length;i++){
                m_Info.DriverSetCarAction(2, i+1, 2);
            }
            m_Info.m_DriverInfo.m_Bcm.m_BcmInfo.m_setcarcontrol.OneKeyOpenWindow = true;
            return 0;
        }
        @Override
        public int setCar_WorkMode(int motor_mode) throws RemoteException {
            // TODO Auto-generated method stub
            m_Info.DriverSetCarWorkMode((short)motor_mode);
            return 0;
        }

        public int getCar_WorkMode() throws RemoteException {
            // TODO Auto-generated method stub
            int temp;
            temp = m_Info.DriverGetCarWorkMode();
            log.debug("UI Read CarWorkMode:"+temp+"\r\n");
            return temp;
        }

        @Override //空调设置
        public int setAirCon_Para(int m_openAc,int m_openPtc, int m_mode, int m_temp,
                                  int m_windSpeed) throws RemoteException {
            // TODO Auto-generated method stub
            m_Info.DriverSetAirPara(m_openAc,m_openPtc, m_mode, m_temp, m_windSpeed);
            return 0;
        }

        @Override	//空调读取
        public int GetAirCon_Status(int[] status) //空调信息读取
                throws RemoteException {
            int error=0;
            // TODO Auto-generated method stub
            if(m_Info.m_DriverInfo.m_AC.m_ACInfo.m_ACstatus!=AC_STATUS.AIRSTATUS_NORMAL){
                error=1;
            }

            int length=status.length;
            if(length>=m_Info.m_uiGetAirCondition.length){
                length=m_Info.m_uiGetAirCondition.length;
            }

            System.arraycopy(m_Info.m_uiGetAirCondition, 0, status, 0, length);
            status[0]=m_Info.m_DriverInfo.m_AC.m_ACInfo.m_getACcontrol.m_innairtemp;
            log.debug("Car indoor temp:"+status[0]+"\r\nCar outdoor temp:"+status[1]+"\r\nCar indoor Humidity:"+status[2]+
                    "\r\nCar settings temp:"+status[3]+"\r\nAC Mode:"+status[4]+"\r\nAC Speedmode:"+status[5]+
                    "\r\nAC Speed:"+status[6]+"\r\n");
            log.debug("ACstatus:"+error+"\r\n");
            return error;
        }

        @Override
        public void setCar_Action(int m_type, int m_num, int m_action)
                throws RemoteException {
            // TODO Auto-generated method stub
            m_Info.DriverSetCarAction(m_type, m_num, m_action);
        }

        @Override
        public String getVersion() throws RemoteException {
            // TODO Auto-generated method stub
            return m_ver;
        }

        @Override
        public int getCarState(int[] array_info) throws RemoteException {
            // TODO Auto-generated method stub
            int error=0;
            int length=array_info.length;
            // TODO Auto-generated method stub
            if(m_Info.m_DriverInfo.m_Bcm.m_BCMStatus==BCM_STATUS.BCMSTATUS_ERR){
                error=1;
            }
            if(length>=m_Info.m_uiGetCarStatus.length){
                length=m_Info.m_uiGetCarStatus.length;
            }

            System.arraycopy(m_Info.m_uiGetCarStatus, 0, array_info, 0, length);
            log.debug("UI Read Bcu Info:\r\n"+"Windown 1:"+array_info[0]+"\r\nWindown 2:"+array_info[1]+"\r\nWindown 3:"+array_info[2]+"\r\nWindown 4:"+array_info[3]+
                    "\r\nWindown 5:"+array_info[4]+"\r\nWindown 6:"+array_info[5]+"\r\nWindown 7:"+array_info[6]+"\r\nCarDoor lockStatus:"+array_info[7]+
                    "\r\nCar StoBoxStatus:"+array_info[8]+"\r\nCar ChargeBoxStatus:"+array_info[9]+"\r\nHeadLamp Status:"+array_info[10]+"\r\nLittleLamp Status:"+array_info[11]+
                    "\r\nFrontfoglampStatus:"+array_info[12]+"\r\nRearfoglampStatus:"+array_info[13]+"\r\nDounbleLamp Status:"+array_info[14]+"\r\nBatBox Status:"+array_info[15]+"\r\n");

            log.debug("BCMstatus:"+error+"\r\n");
            return error;
        }

        @Override
        public int GetTBoxStatus(int[] param) throws RemoteException {
            if (m_Info.m_DriverInfo.m_TBOX.TBOXCommStatus) {
                if (m_Info.m_DriverInfo.m_TBOX.TBOXTxStatus) {
                    param[0] = 3;
                } else {
                    param[0] = 2;
                }
            } else {
                param[0] = 1;
            }
            if(m_Info.m_DriverInfo.m_TBOX.TBOXTxAllowTimeOut){
                if (m_Info.m_DriverInfo.m_TBOX.TBOXTxAllow){
                    param[1] = 1;
                }else{
                    param[1] = 2;
                }
            }else{
                param[1] = 2;
            }
            return 0;
        }

        /**
         * 获取车架号,""没有 获取到，有数据则获取到
         */
        @Override
        public String GetCarVin() throws RemoteException {
            // TODO Auto-generated method stub
            if(Arrays.equals(m_Info.m_DriverInfo.m_Vcu.m_VCUInfo.m_VIN, vinInit)) {
                return "";
            }else {
                return new String(m_Info.m_DriverInfo.m_Vcu.m_VCUInfo.m_VIN);
            }
        }

        @Override
        public int getCar_SportEnergy(int[] param) throws RemoteException {
            param[0] = m_Info.DriverGetCarWorkMode();
            param[1] = m_Info.DriverGetEcoLevel();
            return 0;
        }

        @Override
        public int setCar_SportEnergy(int[] param) throws RemoteException {
            m_Info.m_DriverInfo.m_Vcu.m_VCUInfo.m_setVCUControl.m_energymode = (short) (param[0] | param[1]<<3);
            m_Info.m_DriverInfo.m_Vcu.Change_VCUControlSendCycle();
            return 0;
        }

        @Override
        public int getPowerManager(int[] param) throws RemoteException {
            param[0] = m_Info.m_DriverInfo.m_Vcu.m_VCUInfo.m_batinplace.m_RemainingCpct;
            param[1] = m_Info.m_DriverInfo.m_Vcu.m_VCUInfo.m_motorstatus.m_BatVolt;
            param[2] = m_Info.m_DriverInfo.m_Vcu.m_VCUInfo.m_motorstatus.m_BatCur - 16000;
            param[3] = m_Info.m_DriverInfo.m_Vcu.m_VCUInfo.m_socstatus.m_MaxCellVolt;
            param[4] = m_Info.m_DriverInfo.m_Vcu.m_VCUInfo.m_socstatus.m_MinCellVolt;
            param[5] = m_Info.m_DriverInfo.m_Vcu.m_VCUInfo.m_socstatus.m_MaxPointTemp - 40;
            param[6] = m_Info.m_DriverInfo.m_Vcu.m_VCUInfo.m_socstatus.m_MinPointTemp - 40;
            param[7] = m_Info.m_DriverInfo.m_LCG.m_LCGinfo.m_PosInsulationValue;
            param[8] = m_Info.m_DriverInfo.m_LCG.m_LCGinfo.m_NegInsulationValue;
            param[9] = m_Info.m_DriverInfo.m_Vcu.m_VCUInfo.m_batinplace.m_BatOnNum;
            param[10] = m_Info.m_DriverInfo.m_Vcu.m_VCUInfo.m_socstatus.m_BatSOC;
            param[11] = m_Info.m_DriverInfo.m_Vcu.m_VCUInfo.m_meterstatus.m_RemaingMileage;
            return 0;
        }

        @Override
        public void requestCarVin() throws RemoteException {
            m_Info.m_DriverInfo.m_MFL.Change_MFLCarVinCodeSend();
        }

        @Override
        public int getCarInfo(int[] param) throws RemoteException {
            param[0] = m_Info.m_DriverInfo.m_Vcu.m_VCUInfo.m_meterstatus.m_VehicleSpd;
            param[1] = m_Info.m_DriverInfo.m_Vcu.m_VCUInfo.m_meterstatus.m_RemaingMileage;
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
        vinInit = new byte[m_Info.m_DriverInfo.m_Vcu.m_VCUInfo.m_VIN.length];
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
        //log.info("service start ok!");
		/*
		MediaPlayer mp = new MediaPlayer();
		String mp3String = android.os.Environment.getExternalStorageDirectory() + "/VWCS_App/kaiji.wav";
		try {
			mp.setDataSource(mp3String);
		} catch (IllegalArgumentException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (SecurityException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IllegalStateException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		try {
			mp.prepare();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		mp.start();
		*/
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
                    if(DriverTimeEngine.CheckTimeOut(old_NormalTime,DriverTimeEngine.GetSysTem_ms(),m_NormalRate)){
                        old_NormalTime = DriverTimeEngine.GetSysTem_ms();
//                        m_Info.localCarSummary();//整车概要
//                        m_Info.localStringChargerSummary();//充电机概要
//                        m_Info.localStringBcuSummary();//BCU概要信息
                        m_Info.loacalBcmConditionInfo();//BCM信息
//                        m_Info.localEcocCarInfo();      //主界面信息
                    }else{
                        try {
                            sleep(10);
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
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

