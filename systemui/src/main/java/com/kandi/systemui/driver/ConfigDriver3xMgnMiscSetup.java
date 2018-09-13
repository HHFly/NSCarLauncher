package com.kandi.systemui.driver;

import android.os.RemoteException;

import com.driverlayer.kdos_driverServer.IECarDriver;

public class ConfigDriver3xMgnMiscSetup {
	private IECarDriver R_service;
	
	public ConfigDriver3xMgnMiscSetup(IECarDriver R_service) {
		this.R_service = R_service;
	}
	
	/**
	 * 获取系统错误信息
	 * @param errorInfo		返回Jason格式错误信息字符串
	 * @return				实际返回的错误信息条数
	 *
	 * 系统错误信息存储在驱动层，前端UI软件通过接口调用驱动层以Jason格式打包成字符串的所有历史故障信息。
	 *
	 * Jason格式： {"ERR":[{"TIME":"yyyy-mm-dd hh:MM:ss", "CODE":"EXXXX"，"DESC":"XXXXXX"},{...},{...},...]}
	 */
	//int R_service.getError_Car(String errorInfo);

	/**
	 * 清除系统所有历史错误信息
	 * @return				被清除的错误信息数
	 */
	//int R_service.cleanError_Car();
	
	/**
	 * 获取系统警告信息
	 * @param warningInfo	返回Jason格式警告信息字符串
	 * @return				实际返回的警告信息条数
	 *
	 * 系统警告信息存储在驱动层，前端UI软件通过接口调用驱动层以Jason格式打包成字符串的所有历史警告信息。
	 *
	 * Jason格式： {"WARN":[{"TIME":"yyyy-mm-dd hh:MM:ss", "CODE":"EXXXX"，"DESC":"XXXXXX"},{...},{...},...]}
	 */
	//int R_service.getWarning_Car(String warningInfo);

	/**
	 * 清除系统所有历史警告信息
	 * @return				被清除的警告信息数
	 */
	//int R_service.cleanWarning_Car();
		
	//private String errorInfo;
	//private String warningInfo;
	
	public String[] getSystemError() throws RemoteException {
		String[] errorInfo = R_service.getError_Car();
		return errorInfo;
	}

	public String[] getSystemWarning() throws RemoteException {
		String[] warningInfo = R_service.getWarning_Car();
		return warningInfo;
	}

	public int cleanSystemError() throws RemoteException {
		return R_service.cleanError_Car();
	}
	public int cleanSystemWarning() throws RemoteException {
		return R_service.cleanWarning_Car();
	}

	
	/**
	 * 获取汽车门窗以及大灯状态
	 * @param actType	控制对象类型
	 * @param actNum	控制对象编号
	 * @return 			状态值
	 * 
	 * 设备名称 actType值 actNum编号范围	actState状态定义
	 *  助力转向	0x09	1			0x01:低助力/0x02:中助力/0x03:高助力
	 */
	//int R_service.getCarState(int actType,int actNum);
	
	/**
	 * 汽车门窗以及大灯状态通知
	 * @param actType	控制对象类型
	 * @param actNum	控制对象编号
	 * @param actState	动作状态设置
	 * 
	 * 设备名称 actType值 actNum编号范围	actState状态定义
	 *  助力转向	0x09	1			0x01:低助力/0x02:中助力/0x03:高助力
	 */
	//void R_service.setCar_Action(int actType, int actNum, int actState);

	public enum ePowerAssist { 	ASSISTED_LOW, ASSISTED_MIDDLE, ASSISTED_HIGH };
	
	/* deprecated methods
	public ePowerAssist getPowerAssistedSteering() throws RemoteException {
		switch(R_service.getCarState(0x09,1)) {
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
			R_service.setCar_Action(0x09,1,0x01);
			break;
		case ASSISTED_MIDDLE:
			R_service.setCar_Action(0x09,1,0x02);
			break;
		case ASSISTED_HIGH:
			R_service.setCar_Action(0x09,1,0x03);
			break;
		}
	}
	*/
	public boolean login(String usr, String password) throws RemoteException {
		return (usr.compareTo(password) == 0);
	}
}
