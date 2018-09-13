package com.kandi.systemui.driver;

import android.os.RemoteException;

import com.driverlayer.kdos_driverServer.IECarDriver;

public class ConfigDriver33MgnBcuSetup {
	private IECarDriver R_service;
	
	public ConfigDriver33MgnBcuSetup(IECarDriver R_service) {
		this.R_service = R_service;
	}
	
	/**
	 * 获取电池组控制单元(BCU)参数
	 * @param param		参数数组
	 * @return 			成功返回0，失败返回负值错误代码
	 * 
	 * 参数数组定义
	 *		param[0]	电池粘连检测周期			(单位 秒)
	 *		param[1]	电流环选型					(#电流环编号 0~255)
	 *		param[2]	最小电池数目				(0~255)
	 *		param[3]	协议使能					(位掩码 bit0:康迪协议，bit1:万向协议，bit3:大有协议. 0:禁用，1:使能)
	 *		param[4]	RTC低功耗使能				(0:禁用，1:使能)
	 *		param[5]	RTC低功耗CAN使能			(0:禁用，1:使能)
	 *		param[6]	电流环自动校准使能			(0:禁用，1:使能)
	 *		param[7]	均衡使能					(0:禁用，1:使能)
	 */
	//int R_service.getBCUParam(out int[] param);
	
	/**
	 * 设置电池组控制单元(BCU)参数
	 * @param param		参数数组
	 * @return 			成功返回0，失败返回负值错误代码
	 * 
	 * 参数数组定义
	 *		param[0]	电池粘连检测周期			(单位 秒)
	 *		param[1]	电流环选型					(#电流环编号 0~255)
	 *		param[2]	最小电池数目				(0~255)
	 *		param[3]	协议使能					(位掩码 bit0:康迪协议，bit1:万向协议，bit3:大有协议. 0:禁用，1:使能)
	 *		param[4]	RTC低功耗使能				(0:禁用，1:使能)
	 *		param[5]	RTC低功耗CAN使能			(0:禁用，1:使能)
	 *		param[6]	电流环自动校准使能			(0:禁用，1:使能)
	 *		param[7]	均衡使能					(0:禁用，1:使能)
	 *
	 *		param[0]	电池粘连检测周期			(单位 秒)
	 *		param[1]	电流环选型					(#电流环编号 0~255)
	 *		param[2]	最小电池数目				(0~255)
	 *		param[3]	协议使能					(位掩码 bit0:康迪协议，bit1:万向协议，bit3:大有协议. 0:禁用，1:使能)
	 *		param[4]	RTC低功耗使能				(0:禁用，1:使能)
	 *		param[5]	RTC低功耗CAN使能			(0:禁用，1:使能)
	 *		param[6]	电流环自动校准使能			(0:禁用，1:使能)
	 *		param[7]	均衡使能					(0:禁用，1:使能)
	 *		param[8]	清除电池粘连标志				(0:禁用，1:使能)
	 *		param[9]	绝缘报警值					(单位 KR)
	 *		param[10]	绝缘故障值					(单位 KR)	
	 */
	//int R_service.setBCUParam(in int[] param);
	
	private int[] param = new int[11]; 

	//call by View.onResume()
	public int retrieveBcuParam()  throws RemoteException {
		return this.R_service.getBCUParam(param);
	}
	
	//call by View.SaveButton.onClick()
	public int commitBcuParam()  throws RemoteException {
		return this.R_service.setBCUParam(param);
	}

	
	//param[0]	电池粘连检测周期			(单位 秒)
	public int getBattStickTestCycle() {
		return param[0];
	}
	public void setBattStickTestCycle(int t) {
		param[0] = t;
	}
	
	//param[1]	电流环选型					(#电流环编号 0~255)
	public int getCurrencyLoopType() {
		return param[1];
	}
	public void setCurrencyLoopType(int i) {
		param[1] = i;
	}

	//param[2]	最小电池数目				(0~255)
	public int getMinBattaryQty() {
		return param[2];
	}
	public void setMinBattaryQty(int qty) {
		param[2] = qty;
	}

	//param[3]	协议使能 (位掩码 bit0:康迪协议，bit1:万向协议，bit3:大有协议. 0:禁用，1:使能)
	public enum eBcuProtocol {
		PROTOCAL_KANGDI, PROTOCAL_WANXIANG, PROTOCAL_DAYOU
	}
	public boolean isBcuProtocolEnabled(eBcuProtocol proto) {
		switch(proto) {
		case PROTOCAL_KANGDI:
			return (param[3] & 0x01) != 0;
		case PROTOCAL_DAYOU:
			return (param[3] & 0x02) != 0;
		case PROTOCAL_WANXIANG:
			return (param[3] & 0x04) != 0;
		default:
			return false;
		}
	}
	public void setBcuProtocolEnabled(int val) {
		param[3] = val;
	}
	
	//param[4]	RTC低功耗使能				(0:禁用，1:使能)
	public boolean isRtcLowPowerEnabled() {
		return (param[4] != 0);
	}
	public void setRtcLowPowerEnabled(boolean isEnabled) {
		param[4] = isEnabled?1:0;
	}
	
	//param[5]	RTC低功耗CAN使能			(0:禁用，1:使能)
	public boolean isRtcLowPowerCanEnabled() {
		return (param[5] != 0);
	}
	public void setRtcLowPowerCanEnabled(boolean isEnabled) {
		param[5] = isEnabled?1:0;
	}
	
	//param[6]	电流环自动校准使能			(0:禁用，1:使能)
	public boolean isCurrencyLoopAutoCalEnabled() {
		return (param[6] != 0);
	}
	public void setCurrencyLoopAutoCalEnabled(boolean isEnabled) {
		param[6] = isEnabled?1:0;
	}

	//param[7]	均衡使能					(0:禁用，1:使能)
	public boolean isBalanceEnabled() {
		return (param[7] != 0);
	}
	public void setBalanceEnabled(boolean isEnabled) {
		param[7] = isEnabled?1:0;
	}
	
	//param[8]	清除电池粘连标志				(0:禁用，1:使能)
	public boolean isClearBatterEnabled() {
		return (param[8] != 0);
	}
	public void setClearBatterEnabled(boolean isEnabled) {
		param[8] = isEnabled?1:0;
	}
	//param[9]	绝缘报警值					(单位 KR)
	public int getInsulationwVal() {
		return param[9];
	}
	public void setInsulationwVal(int val) {
		param[9] = val;
	}
	//param[10]	绝缘故障值					(单位 KR)
	public int getInsulationeVal() {
		return param[10];
	}
	public void setInsulationeVal(int val) {
		param[10] = val;
	}

	
	/**
	 * BCU继电器粘连状态清除
	 * @param param		预留，默认0
	 * @return 			成功返回0，失败返回负值错误代码
	 * 
	 */
	//int R_service.resetBCURelayStick(int param);
	
	public int resetBCURelayStick() throws RemoteException {
		return this.R_service.resetBCURelayStick(0);
	}
	
}
