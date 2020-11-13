/*ver:0.0.1
 *bref:用于存放车载设备的数据信息。
 */

package com.nushine.nshlbus.com.driverlayer.os_driverInfo;

import org.apache.log4j.Logger;

import com.nushine.nshlbus.app.App;
import com.nushine.nshlbus.com.driverlayer.os_driverServer.DriverCfg.Global_Cfg;
import com.nushine.nshlbus.com.driverlayer.os_driverApp.DriverAC.AC_MODE_CONTROL;
import com.nushine.nshlbus.com.driverlayer.os_driverApp.DriverAC.AC_AIRMODE_CONTROL;
import com.nushine.nshlbus.com.driverlayer.os_driverApp.DriverAC.AC_AIRCYCLEMODE_CONTROL;
import com.nushine.nshlbus.com.driverlayer.os_driverApp.DriverAC.AC_POWER_CONTROL;

import com.nushine.nshlbus.com.driverlayer.os_driverApp.DriverBcm.BCM_BOOLEAN;
import com.nushine.nshlbus.com.driverlayer.os_driverApp.DriverBcm.BCM_CAR_EXLIGTH_CONTROL;
import com.nushine.nshlbus.com.driverlayer.os_driverApp.DriverBcm.BCM_DOOR_CONTROL;
import com.nushine.nshlbus.com.driverlayer.os_driverApp.DriverBcm.BCM_WINDOWS_CONTROL;
import com.nushine.nshlbus.com.driverlayer.os_driverApp.DriverVCU.VCU_DNR_CONTROL;
import com.nushine.nshlbus.com.driverlayer.os_driverApp.DriverVCU.VCU_DRIVERMODE_CONTROL;
import com.nushine.nshlbus.com.driverlayer.os_driverApp.DriverVCU.VCU_ENERGY_CONTROL;


//信息库，包含整个系统所有信息，同时负责上传信息给UI和接收UI控制
public class DriverInfomation{
	Logger log = Logger.getLogger(DriverInfomation.class);
	public InformationBufferDefine m_DriverInfo;

	public int[]  m_uiGetAirCondition;		//空调信息
	public int[] m_uiGetCarStatus;	   //保存车门车窗的状态信息

	/*空调设置相关数据定义*/
	int m_ACbak[]={0xf0,0,0};	//模式，温度，风量备份
	int m_CycleBak=0xf0;		//内外循环备份
	final int Close_AcContorl = 9;	//空调关闭时风量设置为9
	//按钮事件标志
	final int AC_Change = 0x01;		//AC设置标志
	final int Ptc_Change = 0x02;	//PTC设置标志
	final int Cycle_Change = 0x04;	//循环模式设置标志
	final int Mode_Change = 0x08;	//模式设置标志
	final int Temp_Change = 0x10;	//温度设置标志
	final int Fl_Change = 0x20;		//风量设置标志
	final int invalide = 0xffff;	//无效值定义

	//控制设备类型定义
	public enum CAR_MACHINE_TYPE{
		CAR_RESERVED,
		CAR_DOOR,
		CAR_WINDOWS,
		CAR_STOREBOX,
		CAR_CHAGERTOP,
		CAR_LAMP,
		CAR_DOUBLELAMP,
		CAR_FRONTFOGLAMP,
		CAR_READLAMP,
		CAR_BATDOOR,
		CAR_REARFOGLAMP,
		CAR_MACHINE_TOTAL
	}

	public DriverInfomation() {
		super();
		App.get().setM_myinfo(this);
	}
	public DriverInfomation(Global_Cfg driver_cfg){
		App.get().setM_myinfo(this);
		m_DriverInfo = new InformationBufferDefine(driver_cfg);
		m_uiGetCarStatus = new int[16];
		m_uiGetAirCondition = new int[7];
	}

	//空调信息打包准备
	public void loacalAirConditionInfo(){

		for(int i=0;i<m_uiGetAirCondition.length;i++){
			m_uiGetAirCondition[i]=0;
		}

		m_uiGetAirCondition[0] = m_DriverInfo.m_AC.m_ACInfo.m_getACcontrol.m_innairtemp;
		//m_uiGetAirCondition[1] = m_DriverInfo.m_Bcu.m_bcuinfo.m_aircontrol.m_getouttemp;
		//m_uiGetAirCondition[2] = m_DriverInfo.m_Bcu.m_bcuinfo.m_aircontrol.m_gethumid;
		m_uiGetAirCondition[3] = m_DriverInfo.m_AC.m_ACInfo.m_BckgetACcontrol.m_airtemp;

		if(m_DriverInfo.m_AC.m_ACInfo.m_BckgetACcontrol.m_ACmode==AC_MODE_CONTROL.MODE_REFRIGERATE){
			m_uiGetAirCondition[4]=0x01;
		}
		else if(m_DriverInfo.m_AC.m_ACInfo.m_BckgetACcontrol.m_ACmode==AC_MODE_CONTROL.MODE_HEAT){
			m_uiGetAirCondition[4]=0x04;
		}
		else if(m_DriverInfo.m_AC.m_ACInfo.m_BckgetACcontrol.m_ACmode==AC_MODE_CONTROL.MODE_REFRIGERATEANDHEAT){
			m_uiGetAirCondition[4]=0x05;
		}

		if(m_DriverInfo.m_AC.m_ACInfo.m_BckgetACcontrol.m_airspeed>=Close_AcContorl){
			m_uiGetAirCondition[4]&=(~0x10);
		}else{
			m_uiGetAirCondition[4]|=0x10;
		}

		if(m_DriverInfo.m_AC.m_ACInfo.m_BckgetACcontrol.m_airmode==AC_AIRMODE_CONTROL.AIRMODE_FACE){
			m_uiGetAirCondition[5]=0x01;
		}
		else if(m_DriverInfo.m_AC.m_ACInfo.m_BckgetACcontrol.m_airmode==AC_AIRMODE_CONTROL.AIRMODE_FACEANDFOOT){
			m_uiGetAirCondition[5]=0x02;
		}
		else if(m_DriverInfo.m_AC.m_ACInfo.m_BckgetACcontrol.m_airmode==AC_AIRMODE_CONTROL.AIRMODE_FOOT){
			m_uiGetAirCondition[5]=0x03;
		}
		else if(m_DriverInfo.m_AC.m_ACInfo.m_BckgetACcontrol.m_airmode==AC_AIRMODE_CONTROL.AIRMODE_FOOTANDDEFROST){
			m_uiGetAirCondition[5]=0x04;
		}
		else if(m_DriverInfo.m_AC.m_ACInfo.m_BckgetACcontrol.m_airmode==AC_AIRMODE_CONTROL.AIRMODE_DEFROST){
			m_uiGetAirCondition[5]=0x05;
		}

		if(m_DriverInfo.m_AC.m_ACInfo.m_BckgetACcontrol.m_aircyclemode==AC_AIRCYCLEMODE_CONTROL.AIRCYCLEMODE_INSIDE){
			m_uiGetAirCondition[5]|=0x10;
		}
		else{
			m_uiGetAirCondition[5]&=(~0x10);
		}

		m_uiGetAirCondition[6]=m_DriverInfo.m_AC.m_ACInfo.m_BckgetACcontrol.m_airspeed;

	}

	//车身控制
	public void DriverSetCarAction(int m_type,int m_num,int m_action){
		@SuppressWarnings("unused")
		final int ON=0X01,OFF=0X02;
		if(m_type==CAR_MACHINE_TYPE.CAR_DOOR.ordinal()){
			if(m_action==ON){
				m_DriverInfo.m_Bcm.m_BcmInfo.m_setcarcontrol.m_cardoor_lockstatus[0]=BCM_DOOR_CONTROL.DOOR_UNLOCK;
				log.debug("Car Body: Lock\r\n");
			}else{
				m_DriverInfo.m_Bcm.m_BcmInfo.m_setcarcontrol.m_cardoor_lockstatus[0]=BCM_DOOR_CONTROL.DOOR_LOCK;
				log.debug("Car Body: UnLock\r\n");
			}
		}else if(m_type==CAR_MACHINE_TYPE.CAR_STOREBOX.ordinal()){
			if(m_action==ON){
				m_DriverInfo.m_Bcm.m_BcmInfo.m_setcarcontrol.m_cardoor_lockstatus[1]=BCM_DOOR_CONTROL.DOOR_UNLOCK;
				log.debug("Car Trunk or Boot: Open\r\n");
			}else{
				m_DriverInfo.m_Bcm.m_BcmInfo.m_setcarcontrol.m_cardoor_lockstatus[1]=BCM_DOOR_CONTROL.DOOR_LOCK;
				log.debug("Car Trunk or Boot: Close\r\n");
			}
		}else if(m_type==CAR_MACHINE_TYPE.CAR_CHAGERTOP.ordinal()){
			if(m_action==ON){
				m_DriverInfo.m_Bcm.m_BcmInfo.m_setcarcontrol.m_cardoor_lockstatus[2]=BCM_DOOR_CONTROL.DOOR_UNLOCK;
				log.debug("Car ChargerTop: Open\r\n");
			}else{
				m_DriverInfo.m_Bcm.m_BcmInfo.m_setcarcontrol.m_cardoor_lockstatus[2]=BCM_DOOR_CONTROL.DOOR_LOCK;
				log.debug("Car ChargerTop: Close\r\n");
			}
		}else if(m_type==CAR_MACHINE_TYPE.CAR_WINDOWS.ordinal()){
			m_num-=1;
			if(m_num>=0&&m_num<=6){
				if(m_action==BCM_WINDOWS_CONTROL.WINDOWS_UP.ordinal()){
					m_DriverInfo.m_Bcm.m_BcmInfo.m_setcarcontrolbak.m_carwindow_seting[m_num]=BCM_WINDOWS_CONTROL.WINDOWS_UP;
					m_DriverInfo.m_Bcm.m_BcmInfo.m_setcarcontrol.m_carwindow_seting[m_num]=BCM_WINDOWS_CONTROL.WINDOWS_UP;
					log.debug("Car Windows: "+m_num+" Up"+"\r\n");
				}else if(m_action==BCM_WINDOWS_CONTROL.WINDOWS_DOWN.ordinal()){
					m_DriverInfo.m_Bcm.m_BcmInfo.m_setcarcontrolbak.m_carwindow_seting[m_num]=BCM_WINDOWS_CONTROL.WINDOWS_DOWN;
					m_DriverInfo.m_Bcm.m_BcmInfo.m_setcarcontrol.m_carwindow_seting[m_num]=BCM_WINDOWS_CONTROL.WINDOWS_DOWN;
					log.debug("Car Windows: "+m_num+" Down"+"\r\n");
				}else if(m_action==BCM_WINDOWS_CONTROL.WINDOWS_PAUSE.ordinal()){
					m_DriverInfo.m_Bcm.m_BcmInfo.m_setcarcontrol.m_carwindow_seting[m_num]=BCM_WINDOWS_CONTROL.WINDOWS_INVALIDE;
					log.debug("Car Windows: "+m_num+" Pause"+"\r\n");
				}
				else{
					m_DriverInfo.m_Bcm.m_BcmInfo.m_setcarcontrol.m_carwindow_seting[m_num]=BCM_WINDOWS_CONTROL.WINDOWS_INVALIDE;
					log.debug("Car Windows: "+m_num+" INVALIDE"+"\r\n");
				}
			}
		}
		else if(m_type==CAR_MACHINE_TYPE.CAR_LAMP.ordinal()){
			if(m_action==BCM_CAR_EXLIGTH_CONTROL.LIGHT_FAR.ordinal()){
				m_DriverInfo.m_Bcm.m_BcmInfo.m_setcarcontrol.m_ex_carlamp=BCM_CAR_EXLIGTH_CONTROL.LIGHT_FAR;
				log.debug("Car HeadLight: Far\r\n");
			}else if(m_action == BCM_CAR_EXLIGTH_CONTROL.LIGHT_NEAR.ordinal()){
				m_DriverInfo.m_Bcm.m_BcmInfo.m_setcarcontrol.m_ex_carlamp=BCM_CAR_EXLIGTH_CONTROL.LIGHT_NEAR;
				log.debug("Car HeadLight: Near\r\n");
			}else if(m_action == BCM_CAR_EXLIGTH_CONTROL.LIGTH_CLOSE.ordinal()){
				m_DriverInfo.m_Bcm.m_BcmInfo.m_setcarcontrol.m_ex_carlamp=BCM_CAR_EXLIGTH_CONTROL.LIGTH_CLOSE;
				log.debug("Car HeadLight: Close\r\n");
			}
		}else if(m_type==CAR_MACHINE_TYPE.CAR_DOUBLELAMP.ordinal()){
			if(m_action==ON){
				m_DriverInfo.m_Bcm.m_BcmInfo.m_setcarcontrol.m_doublelamp=BCM_BOOLEAN.BOOLEAN_TRUE;
				log.debug("Car DoubleLamp: Open\r\n");
			}else{
				m_DriverInfo.m_Bcm.m_BcmInfo.m_setcarcontrol.m_doublelamp=BCM_BOOLEAN.BOOLEAN_FALSE;
				log.debug("Car DoubleLamp: Close\r\n");
			}
		}else if(m_type==CAR_MACHINE_TYPE.CAR_FRONTFOGLAMP.ordinal()){
			if(m_action==ON){
				m_DriverInfo.m_Bcm.m_BcmInfo.m_setcarcontrol.m_frontfoglamp=BCM_BOOLEAN.BOOLEAN_TRUE;
				log.debug("Car FrontFogLamp: Open\r\n");
			}else{
				m_DriverInfo.m_Bcm.m_BcmInfo.m_setcarcontrol.m_frontfoglamp=BCM_BOOLEAN.BOOLEAN_FALSE;
				log.debug("Car FogLamp: Close\r\n");
			}
		}
		else if(m_type==CAR_MACHINE_TYPE.CAR_REARFOGLAMP.ordinal()){
			if(m_action==ON){
				m_DriverInfo.m_Bcm.m_BcmInfo.m_setcarcontrol.m_rearfoglamp=BCM_BOOLEAN.BOOLEAN_TRUE;
				log.debug("Car RearFogLamp: Open\r\n");
			}else{
				m_DriverInfo.m_Bcm.m_BcmInfo.m_setcarcontrol.m_rearfoglamp=BCM_BOOLEAN.BOOLEAN_FALSE;
				log.debug("Car FogLamp: Close\r\n");
			}
		}
		else if(m_type==CAR_MACHINE_TYPE.CAR_READLAMP.ordinal()){
			if(m_action==ON){
				m_DriverInfo.m_Bcm.m_BcmInfo.m_setcarcontrol.m_readlamp=BCM_BOOLEAN.BOOLEAN_TRUE;
				log.debug("Car LittleLamp: Open\r\n");
			}else{
				m_DriverInfo.m_Bcm.m_BcmInfo.m_setcarcontrol.m_readlamp=BCM_BOOLEAN.BOOLEAN_FALSE;
				log.debug("Car LittleLamp: Close\r\n");

			}
		}
		else if(m_type==CAR_MACHINE_TYPE.CAR_BATDOOR.ordinal()){
			if(m_action==BCM_WINDOWS_CONTROL.WINDOWS_UP.ordinal()){
				m_DriverInfo.m_Bcm.m_BcmInfo.m_setcarcontrol.m_BatDoor=BCM_WINDOWS_CONTROL.WINDOWS_UP;
				log.debug("Car BatDoor: Up\r\n");
			}else if(m_action==BCM_WINDOWS_CONTROL.WINDOWS_DOWN.ordinal()){
				m_DriverInfo.m_Bcm.m_BcmInfo.m_setcarcontrol.m_BatDoor=BCM_WINDOWS_CONTROL.WINDOWS_DOWN;
				log.debug("Car BatDoor: Down\r\n");
			}else if(m_action==BCM_WINDOWS_CONTROL.WINDOWS_PAUSE.ordinal()){
				m_DriverInfo.m_Bcm.m_BcmInfo.m_setcarcontrol.m_BatDoor=BCM_WINDOWS_CONTROL.WINDOWS_PAUSE;
				log.debug("Car BatDoor: Pause\r\n");
			}
		}
		m_DriverInfo.m_Bcm.Change_BCMControlSendCycle();
	}

	//行车模式设置
	public void DriverSetCarWorkMode(short m_mode){
		if(m_mode==1){
			m_DriverInfo.m_Vcu.m_VCUInfo.m_setVCUControl.m_mode = VCU_DRIVERMODE_CONTROL.DRIVEMODE_ENERGYSAVE;
			log.debug("Car work mode:Energysave\r\n");
		}
		else if(m_mode==2){
			m_DriverInfo.m_Vcu.m_VCUInfo.m_setVCUControl.m_mode = VCU_DRIVERMODE_CONTROL.DRIVEMODE_SPORT;
			log.debug("Car work mode:Sport\r\n");
		}
		else if(m_mode==3){
			m_DriverInfo.m_Vcu.m_VCUInfo.m_setVCUControl.m_mode = VCU_DRIVERMODE_CONTROL.DRIVEMODE_HOTSPORT;
			log.debug("Car work mode:HotSport\r\n");
		}
		else if(m_mode==4){
			m_DriverInfo.m_Vcu.m_VCUInfo.m_setVCUControl.m_mode = VCU_DRIVERMODE_CONTROL.DRIVEMODE_NEDC;
			log.debug("Car work mode:INVALIDE\r\n");
		}
		m_DriverInfo.m_Vcu.m_VCUInfo.m_setVCUControl.m_energymode = m_mode;
		m_DriverInfo.m_Vcu.Change_VCUControlSendCycle();
	}

	public int DriverGetCarWorkMode(){
		int temp;
		if(m_DriverInfo.m_Vcu.m_VCUInfo.m_getVCUControl.m_mode == VCU_DRIVERMODE_CONTROL.DRIVEMODE_ENERGYSAVE){
			temp=1;
		}
		else if(m_DriverInfo.m_Vcu.m_VCUInfo.m_getVCUControl.m_mode == VCU_DRIVERMODE_CONTROL.DRIVEMODE_SPORT){
			temp=2;
		}
		else if(m_DriverInfo.m_Vcu.m_VCUInfo.m_getVCUControl.m_mode == VCU_DRIVERMODE_CONTROL.DRIVEMODE_HOTSPORT){
			temp=3;
		}else if(m_DriverInfo.m_Vcu.m_VCUInfo.m_getVCUControl.m_mode == VCU_DRIVERMODE_CONTROL.DRIVEMODE_NEDC){
			temp=4;
		}else{
			temp=0;
		}

		return temp;
	}

	public void DriverSetAirPara(int m_openAc,int m_openPtc, int m_mode,int m_temp,int m_windSpeed){
		int temp_change=0;
		log.debug("Air-Condition Seting:\r\n");

		if((m_openAc!=invalide)||(m_openPtc!=invalide)){
			AC_MODE_CONTROL m_acmode;

			if((m_openAc!=invalide)){
				m_acmode=AC_MODE_CONTROL.MODE_REFRIGERATE;

			}else if((m_openPtc!=invalide)){
				m_acmode=AC_MODE_CONTROL.MODE_HEAT;
			}else{
				m_acmode=AC_MODE_CONTROL.MODE_INVALIDE;
			}

			if(m_acmode!=AC_MODE_CONTROL.MODE_INVALIDE){
				if(m_acmode==AC_MODE_CONTROL.MODE_REFRIGERATE){
					temp_change = AC_Change;
					AC_Control_Action(m_openAc,m_openPtc,m_mode,m_temp,m_windSpeed,temp_change);
					return;
				}
				else if(m_acmode==AC_MODE_CONTROL.MODE_HEAT){
					temp_change = Ptc_Change;
					AC_Control_Action(m_openAc,m_openPtc,m_mode,m_temp,m_windSpeed,temp_change);
					return;

				}
			}
		}

//		if(m_openPtc!=invalide){
//			temp_change = Ptc_Change;
//			AC_Control_Action(m_openAc,m_openPtc,m_mode,m_temp,m_windSpeed,temp_change);
//			return;
//		}

		if(m_mode!=invalide){
			AC_AIRMODE_CONTROL m_airctl;
			if((m_mode&0x0f)!=0){
				if(m_mode==1){
					m_airctl = AC_AIRMODE_CONTROL.AIRMODE_FACE;
				}else if(m_mode==2){
					m_airctl = AC_AIRMODE_CONTROL.AIRMODE_FACEANDFOOT;
				}else if(m_mode==3){
					m_airctl = AC_AIRMODE_CONTROL.AIRMODE_FOOT;
				}else if(m_mode==4){
					m_airctl = AC_AIRMODE_CONTROL.AIRMODE_FOOTANDDEFROST;
				}else if(m_mode==5){
					m_airctl = AC_AIRMODE_CONTROL.AIRMODE_DEFROST;
				}else{
					m_airctl = AC_AIRMODE_CONTROL.AIRMODE_INVALIDE;
				}

				if(m_airctl!=m_DriverInfo.m_AC.m_ACInfo.m_getACcontrol.m_airmode){
					temp_change = Mode_Change;
					AC_Control_Action(m_openAc,m_openPtc,m_mode,m_temp,m_windSpeed,temp_change);
					return;
				}
			}else{
				AC_AIRCYCLEMODE_CONTROL mode=AC_AIRCYCLEMODE_CONTROL.AIRCYCLEMODE_INVALIDE;
				if((m_mode&0x10)==0x10){
					mode=AC_AIRCYCLEMODE_CONTROL.AIRCYCLEMODE_INSIDE;
				}else if((m_mode&0x10)==0){
					mode=AC_AIRCYCLEMODE_CONTROL.AIRCYCLEMODE_OUTSIDE;
				}

				if(mode!=m_DriverInfo.m_AC.m_ACInfo.m_getACcontrol.m_aircyclemode){
					temp_change = Cycle_Change;
					AC_Control_Action(m_openAc,m_openPtc,m_mode,m_temp,m_windSpeed,temp_change);
					m_CycleBak = m_mode&0x10;
				}
				return;
			}
		}

		if((m_temp!=invalide)&&(m_temp!=m_DriverInfo.m_AC.m_ACInfo.m_getACcontrol.m_airtemp)){
			temp_change = Temp_Change;
			AC_Control_Action(m_openAc,m_openPtc,m_mode,m_temp,m_windSpeed,temp_change);
			return;
		}

		if((m_windSpeed!=invalide)&&((m_windSpeed!=m_DriverInfo.m_AC.m_ACInfo.m_getACcontrol.m_airspeed))){
			temp_change = Fl_Change;
			AC_Control_Action(m_openAc,m_openPtc,m_mode,m_temp,m_windSpeed,temp_change);
			return;
		}
	}

	private void AC_Control_Action(int m_openAc,int m_openPtc,int m_mode,int m_temp,int m_windSpeed,int temp_change){
		if(m_windSpeed>0&&m_windSpeed<=8){//风扇开启
			log.debug("1.Air-Condition: Fs Open\r\n");
		}else{
			m_windSpeed = Close_AcContorl;
			log.debug("1.Air-Condition: FS Close\r\n");
		}

		if(m_openAc==0X02&&m_openPtc==0X02){
			m_DriverInfo.m_AC.m_ACInfo.m_setACcontrol.m_ACmode=AC_MODE_CONTROL.MODE_REFRIGERATEANDHEAT;
			log.debug("1.Air-Condition: ACMode AC+PTC Open\r\n");
		}else if(m_openAc==0X02){
			m_DriverInfo.m_AC.m_ACInfo.m_setACcontrol.m_ACmode=AC_MODE_CONTROL.MODE_REFRIGERATE;
			log.debug("1.Air-Condition: ACMode AC Open\r\n");
		}else if(m_openPtc==0X02){
			m_DriverInfo.m_AC.m_ACInfo.m_setACcontrol.m_ACmode=AC_MODE_CONTROL.MODE_HEAT;
			log.debug("1.Air-Condition: ACMode Ptc Open\r\n");
		}else {
			m_DriverInfo.m_AC.m_ACInfo.m_setACcontrol.m_ACmode=AC_MODE_CONTROL.MODE_INVALIDE;
		}



		if(m_openAc==0x02){
			m_DriverInfo.m_AC.m_ACInfo.m_setACcontrol.m_ACopen=AC_POWER_CONTROL.POWER_ON;
			log.debug("1.Air-Condition: PowerStatus Power On\r\n");
		}
		else if(m_openAc==0x01){
			m_DriverInfo.m_AC.m_ACInfo.m_setACcontrol.m_ACopen=AC_POWER_CONTROL.POWER_OFF;
			log.debug("1.Air-Condition: PowerStatus Power Off\r\n");
		}
		else{
			m_DriverInfo.m_AC.m_ACInfo.m_setACcontrol.m_ACopen=AC_POWER_CONTROL.POWER_INVALIDE;
			log.debug("1.Air-Condition: PowerStatus Invalide\r\n");
		}

		if((m_mode&0x0f)==0x01){
			m_DriverInfo.m_AC.m_ACInfo.m_setACcontrol.m_airmode=AC_AIRMODE_CONTROL.AIRMODE_FACE;
			log.debug("2.Air-Condition: WindMode Face\r\n");
		}
		else if((m_mode&0x0f)==0x02){
			m_DriverInfo.m_AC.m_ACInfo.m_setACcontrol.m_airmode=AC_AIRMODE_CONTROL.AIRMODE_FACEANDFOOT;
			log.debug("2.Air-Condition: WindMode FaceFoot\r\n");
		}
		else if((m_mode&0x0f)==0x03){
			m_DriverInfo.m_AC.m_ACInfo.m_setACcontrol.m_airmode=AC_AIRMODE_CONTROL.AIRMODE_FOOT;
			log.debug("2.Air-Condition: WindMode Foot\r\n");
		}
		else if((m_mode&0x0f)==0x04){
			m_DriverInfo.m_AC.m_ACInfo.m_setACcontrol.m_airmode=AC_AIRMODE_CONTROL.AIRMODE_FOOTANDDEFROST;
			log.debug("2.Air-Condition: WindMode FootDefrost\r\n");
		}
		else if((m_mode&0x0f)==0x05){
			m_DriverInfo.m_AC.m_ACInfo.m_setACcontrol.m_airmode=AC_AIRMODE_CONTROL.AIRMODE_DEFROST;
			log.debug("2.Air-Condition: WindMode Defrost\r\n");
		}

		if((m_mode&0x10)==0x10){
			m_DriverInfo.m_AC.m_ACInfo.m_setACcontrol.m_aircyclemode=AC_AIRCYCLEMODE_CONTROL.AIRCYCLEMODE_INSIDE;
			log.debug("2.Air-Condition: AirCycleMode Inside\r\n");
		}
		else{
			m_DriverInfo.m_AC.m_ACInfo.m_setACcontrol.m_aircyclemode=AC_AIRCYCLEMODE_CONTROL.AIRCYCLEMODE_OUTSIDE;
			log.debug("2.Air-Condition: AirCycleMode Outside\r\n");
		}

		m_DriverInfo.m_AC.m_ACInfo.m_setACcontrol.m_airtemp = (short) m_temp;
		log.debug("3.Air-Condition temperature:"+m_temp+"\r\n");
		m_DriverInfo.m_AC.m_ACInfo.m_setACcontrol.m_airspeed = (short) m_windSpeed;
		log.debug("4.Air-Condition Air Speed:"+m_windSpeed+"\r\n");
		m_DriverInfo.m_AC.Change_ACControlSendCycle(temp_change);
	}

	public int DriverGetSportLevel(){
		int temp;
		if(m_DriverInfo.m_Vcu.m_VCUInfo.m_getVCUControl.m_sportenergy == VCU_ENERGY_CONTROL.ENERGY_L){
			temp=0;
		}else if(m_DriverInfo.m_Vcu.m_VCUInfo.m_getVCUControl.m_sportenergy == VCU_ENERGY_CONTROL.ENERGY_M){
			temp=1;
		}else if(m_DriverInfo.m_Vcu.m_VCUInfo.m_getVCUControl.m_sportenergy == VCU_ENERGY_CONTROL.ENERGY_H){
			temp=2;
		}else if(m_DriverInfo.m_Vcu.m_VCUInfo.m_getVCUControl.m_sportenergy == VCU_ENERGY_CONTROL.ENERGY_INVALIDE){
			temp=3;
		}else{
			temp=0;
		}

		return temp;
	}

	public int DriverGetEcoLevel(){
		int temp;
		if(m_DriverInfo.m_Vcu.m_VCUInfo.m_getVCUControl.m_ecoenergy == VCU_ENERGY_CONTROL.ENERGY_L){
			temp=0;
		}else if(m_DriverInfo.m_Vcu.m_VCUInfo.m_getVCUControl.m_ecoenergy == VCU_ENERGY_CONTROL.ENERGY_M){
			temp=1;
		}else if(m_DriverInfo.m_Vcu.m_VCUInfo.m_getVCUControl.m_ecoenergy == VCU_ENERGY_CONTROL.ENERGY_H){
			temp=2;
		}else if(m_DriverInfo.m_Vcu.m_VCUInfo.m_getVCUControl.m_ecoenergy == VCU_ENERGY_CONTROL.ENERGY_INVALIDE){
			temp=3;
		}else{
			temp=0;
		}

		return temp;
	}

	//读取车身设备的状态信息
	public void loacalBcmConditionInfo(){
		///
		for(int i=0;i<m_DriverInfo.m_Bcm.m_BcmInfo.m_getcarcontrol.m_carwindow_seting.length;i++){
			if(m_DriverInfo.m_Bcm.m_BcmInfo.m_getcarcontrol.m_carwindow_seting[i]==BCM_WINDOWS_CONTROL.WINDOWS_UP){
				m_uiGetCarStatus[i]=1;
			}
			else if(m_DriverInfo.m_Bcm.m_BcmInfo.m_getcarcontrol.m_carwindow_seting[i]==BCM_WINDOWS_CONTROL.WINDOWS_DOWN){
				m_uiGetCarStatus[i]=2;
			}
			else if(m_DriverInfo.m_Bcm.m_BcmInfo.m_getcarcontrol.m_carwindow_seting[i]==BCM_WINDOWS_CONTROL.WINDOWS_PAUSE){
				m_uiGetCarStatus[i]=3;
			}
			else{//测试 原来0
				m_uiGetCarStatus[i]=3;
			}
		}

		for(int i=0;i<m_DriverInfo.m_Bcm.m_BcmInfo.m_getcarcontrol.m_cardoor_lockstatus.length;i++){
			if(m_DriverInfo.m_Bcm.m_BcmInfo.m_getcarcontrol.m_cardoor_lockstatus[i]==BCM_DOOR_CONTROL.DOOR_UNLOCK){
				m_uiGetCarStatus[i+7]=1;
			}
			else{
				m_uiGetCarStatus[i+7]=2;
			}
		}

		if(m_DriverInfo.m_Bcm.m_BcmInfo.m_getcarcontrol.m_ex_carlamp==BCM_CAR_EXLIGTH_CONTROL.LIGHT_NEAR){
			m_uiGetCarStatus[10]=2;
		}
		else if(m_DriverInfo.m_Bcm.m_BcmInfo.m_getcarcontrol.m_ex_carlamp==BCM_CAR_EXLIGTH_CONTROL.LIGHT_FAR){
			m_uiGetCarStatus[10]=1;
		}
		else if(m_DriverInfo.m_Bcm.m_BcmInfo.m_getcarcontrol.m_ex_carlamp==BCM_CAR_EXLIGTH_CONTROL.LIGTH_CLOSE){
			m_uiGetCarStatus[10]=3;
		}
		else{//
			m_uiGetCarStatus[10]=3;
		}

		if(m_DriverInfo.m_Bcm.m_BcmInfo.m_getcarcontrol.m_readlamp==BCM_BOOLEAN.BOOLEAN_TRUE){
			m_uiGetCarStatus[11]=1;
		}
		else{
			m_uiGetCarStatus[11]=2;
		}

		if(m_DriverInfo.m_Bcm.m_BcmInfo.m_getcarcontrol.m_frontfoglamp==BCM_BOOLEAN.BOOLEAN_TRUE){
			m_uiGetCarStatus[12]=1;
		}
		else{
			m_uiGetCarStatus[12]=2;
		}

		if(m_DriverInfo.m_Bcm.m_BcmInfo.m_getcarcontrol.m_rearfoglamp==BCM_BOOLEAN.BOOLEAN_TRUE){
			m_uiGetCarStatus[13]=1;
		}
		else{
			m_uiGetCarStatus[13]=2;
		}

		if(m_DriverInfo.m_Bcm.m_BcmInfo.m_getcarcontrol.m_doublelamp==BCM_BOOLEAN.BOOLEAN_TRUE){
			m_uiGetCarStatus[14]=1;
		}
		else{
			m_uiGetCarStatus[14]=2;
		}

		if(m_DriverInfo.m_Bcm.m_BcmInfo.m_getcarcontrol.m_BatDoor==BCM_WINDOWS_CONTROL.WINDOWS_UP){
			m_uiGetCarStatus[15]=1;
		}
		else if(m_DriverInfo.m_Bcm.m_BcmInfo.m_getcarcontrol.m_BatDoor==BCM_WINDOWS_CONTROL.WINDOWS_DOWN){
			m_uiGetCarStatus[15]=2;
		}
		else if(m_DriverInfo.m_Bcm.m_BcmInfo.m_getcarcontrol.m_BatDoor==BCM_WINDOWS_CONTROL.WINDOWS_PAUSE){
			m_uiGetCarStatus[15]=3;
		}
		else{//
			//m_uiGetCarStatus[15]=0;
			m_uiGetCarStatus[15]=3;
		}
	}
}

