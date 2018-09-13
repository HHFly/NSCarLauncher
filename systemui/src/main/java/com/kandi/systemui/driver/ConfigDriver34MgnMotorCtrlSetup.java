package com.kandi.systemui.driver;

import android.os.RemoteException;

import com.driverlayer.kdos_driverServer.IECarDriver;

public class ConfigDriver34MgnMotorCtrlSetup {
	private IECarDriver R_service;
	
	public ConfigDriver34MgnMotorCtrlSetup(IECarDriver R_service) {
		this.R_service = R_service;
	}
	
	/**
	 * 获取电机控制机参数
	 * @param param		参数数组
	 * @return 			成功返回0，失败返回负值错误代码
	 * 
	 * 参数数组定义
	 *		param[0]	最高转数限定				(单位 RPM)
	 *		param[1]	怠速转矩限定				(单位 牛/米, -600 ~ 600)
	 *		param[2]	SOC							(单位 千分比)
	 */
	//int R_service.getMotorControlerParam(out int[] param);
	
	 /**
	 * 设置电机控制机参数
	 * @param param		参数数组
	 * @return 			成功返回0，失败返回负值错误代码
	 * 
	 * 参数数组定义
	 *		param[0]	最高转数限定				(单位 RPM)
	 *		param[1]	怠速转矩限定				(单位 牛/米, -600 ~ 600)
	 *		param[2]	SOC							(单位 0.1%)
	 */
	//int R_service.setMotorControlerParam(in int[] param);
		
	private int[] param = new int[3]; 

	//call by View.onResume()
	public int retrieveBcuParam()  throws RemoteException {
		return this.R_service.getMotorControlerParam(param);
	}
	
	//call by View.SaveButton.onClick()
	public int commitBcuParam()  throws RemoteException {
		return this.R_service.setMotorControlerParam(param);
	}

	
	//param[0]	最高转数限定				(单位 RPM)
	public int getMaxRpmLimit() {
		return param[0];
	}
	public void setMaxRpmLimit(int rpm) {
		param[0] = rpm;
	}
	
	//param[1]	怠速转矩限定				(单位 牛/米, -600 ~ 600)
	public int getIdleTorqueLimit() {
		return param[1];
	}
	public void setIdleTorqueLimit(int t) {
		param[1] = t;
	}

	//param[2]	SOC							(单位 0.1%)
	public float getSOC() {
		return param[2] * 0.1f;
	}
	
	public void setSOC(float soc) {
		param[2] = (int)(soc * 10);
	}

}
