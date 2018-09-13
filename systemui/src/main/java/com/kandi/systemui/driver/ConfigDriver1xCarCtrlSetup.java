package com.kandi.systemui.driver;

import android.os.RemoteException;

import com.driverlayer.kdos_driverServer.IECarDriver;

public class ConfigDriver1xCarCtrlSetup {
	private IECarDriver R_service;
	
	public ConfigDriver1xCarCtrlSetup(IECarDriver R_service) {
		this.R_service = R_service;
	}
	

	/**
	 * 设置EPS助力模式
	 * @param EPS_mode  包含3个模式，助力轻（0x01）,助力中（0x02）,助力重（0x03）;
	 * @return			 设置成功返回0，失败返回负值错误代码
	 */
	//int setCar_EPSassistance(int EPS_mode);
	
	/**
	 * 读取EPS助力模式	
	 * @param null																	
	 * @return			返回EPS助力模式 1:助力轻,2:助力中,3:助力重；
	 */
	//int getCar_EPSassistance();	
	

	public enum ePowerAssist { 	ASSISTED_LOW, ASSISTED_MIDDLE, ASSISTED_HIGH };
	
	public ePowerAssist getPowerAssistedSteering() throws RemoteException {
		switch(R_service.getCar_EPSassistance()) {
		case 0x01:
			return ePowerAssist.ASSISTED_LOW;
		case 0x03:
			return ePowerAssist.ASSISTED_HIGH;
		case 0x02:
		default:
			return ePowerAssist.ASSISTED_MIDDLE;
		}
	}
	
	public void setPowerAssistedSteering(ePowerAssist assist) throws RemoteException {
		switch(assist) {
		case ASSISTED_LOW:
			R_service.setCar_EPSassistance(0x01);
			break;
		case ASSISTED_MIDDLE:
			R_service.setCar_EPSassistance(0x02);
			break;
		case ASSISTED_HIGH:
			R_service.setCar_EPSassistance(0x03);
			break;
		}
	}


	/**
	 * 设置汽车运动模式
	 * @param motor_mode 包含2个模式，经济模式（0x01）,运动模式（0x02）;
	 * @return			 设置成功返回0，失败返回负值错误代码
	 */
	//int setCar_WorkMode(int motor_mode);
	
	/**
	 * 读取车辆模式参数	
	 * @param null																	
	 * @return			返回车辆模式 1:运动模式,0:经济模式；
	 */
	//int getCar_WorkMode();
	
	public enum eDrvVMode { 	DRVMODE_SPORT, DRVMODE_ECO };
	
	public eDrvVMode getDrvMode() throws RemoteException {
		switch(R_service.getCar_WorkMode()) {
		case 0x01:
			return eDrvVMode.DRVMODE_SPORT;
		case 0x02:
		default:
			return eDrvVMode.DRVMODE_ECO;
		}
	}
	
	public void setDrvMode(eDrvVMode mode) throws RemoteException {
		switch(mode) {
		case DRVMODE_SPORT:
			R_service.setCar_WorkMode(0x01);
			break;
		case DRVMODE_ECO:
			R_service.setCar_WorkMode(0x02);
			break;
		}
	}
	
	boolean isTouchCtrlOn = true;	//for debug
	public boolean isTouchCtrlOn() {
		return isTouchCtrlOn;
	}
	public void setTouchCtrlOn(boolean flag) {
		this.isTouchCtrlOn = flag;
	}
}
