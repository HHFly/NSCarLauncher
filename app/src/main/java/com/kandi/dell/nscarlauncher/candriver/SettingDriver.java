package com.kandi.dell.nscarlauncher.candriver;

import android.os.RemoteException;

import com.driverlayer.os_driverServer.IECarDriver;


public class SettingDriver {
	private IECarDriver R_service;

	public SettingDriver(IECarDriver R_service) {
		this.R_service = R_service;
	}

	/**
	 * 读取车辆模式参数
	 * @param
	 * @return			返回车辆模式 0：经济模式；1：运动模式；//2：暴躁模式；3：NEDC模式
	 */
	public int getCarWorkMode() throws RemoteException {
		return R_service.getCar_WorkMode();
	}

	/**
	 * 设置车辆模式参数
	 * @param   motor_mode 1：经济模式；2：运动模式；//3：暴躁模式；4：NEDC模式
	 */
	public int setCarWorkMode(int motor_mode) throws RemoteException {
		return R_service.setCar_WorkMode(motor_mode);
	}

	/**
	 * 设置制动回收开关
	 * @param  isOnOff   1:运动模式,0:经济模式；
	 */
	public int setAutoCycleSwitch(int isOnOff) throws RemoteException {
		return R_service.setCar_WorkMode(isOnOff);
	}
}
