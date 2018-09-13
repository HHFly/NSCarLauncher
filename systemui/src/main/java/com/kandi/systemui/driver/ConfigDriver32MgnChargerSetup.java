package com.kandi.systemui.driver;

import android.os.RemoteException;

import com.driverlayer.kdos_driverServer.IECarDriver;

public class ConfigDriver32MgnChargerSetup {
	private IECarDriver R_service;
	
	public ConfigDriver32MgnChargerSetup(IECarDriver R_service) {
		this.R_service = R_service;
	}
	
	/**
	 * 获取充电机参数
	 * @param param		参数数组
	 * @return 			成功返回0，失败返回负值错误代码
	 * 
	 * 参数数组定义
	 *		param[0]	充电机最高充电电压			(单位 100mV)
	 *		param[1]	充电机最高充电电压			(单位 100mV)
	 *		param[2]	均衡使能					(0:禁用，1:使能)
	 */
	//int R_service.getBattaryChargingParam(out int[] param);

	/**
	 * 设置充电机参数
	 * @param param		参数数组
	 * @return 			成功返回0，失败返回负值错误代码
	 * 
	 * 参数数组定义
	 *		param[0]	充电机最高充电电压			(单位 100mV)
	 *		param[1]	充电机最高充电电流			(单位 100mA)
	 *		param[2]	均衡使能					(0:禁用，1:使能)
	 */
	//int R_service.setBattaryChargingParam(in int[] param);
	
	private int[] param = new int[3]; 

	//call by View.onResume()
	public int retrieveBattaryChargingParam()  throws RemoteException {
		return this.R_service.getBattaryChargingParam(param);
	}
	
	//call by View.SaveButton.onClick()
	public int commitBattaryChargingParam()  throws RemoteException {
		return this.R_service.setBattaryChargingParam(param);
	}

	//param[0]	充电机最高充电电压			(单位 100mV)
	public float getMaxChargingVotage() {
		return param[0]/10.0f;
	}
	public void setMaxChargingVotage(float v) {
		param[0] = (int)(v*10);
	}
	
	//param[1]	充电机最高充电电流			(单位 100mA)
	public float getMaxChargingAmp() {
		return param[1]/10.0f;
	}
	public void setMaxChargingAmp(float i) {
		param[1] = (int)(i*10);
	}

	//param[2]	均衡使能					(0:禁用，1:使能)
	public boolean isBalanceEnabled() {
		return param[2] == 1;
	}
	public void setBalanceEnabled(boolean isEnabled) {
		param[2] = isEnabled?1:0;
	}

}
