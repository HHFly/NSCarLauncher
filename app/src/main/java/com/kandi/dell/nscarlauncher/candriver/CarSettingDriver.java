/**
 * 
 */
package com.kandi.dell.nscarlauncher.candriver;

import com.driverlayer.os_driverServer.IECarDriver;

import android.os.RemoteException;

/**
 * @author david_gu
 *
 */
public class CarSettingDriver {

	private IECarDriver R_service;
	private int[] info = new int[16];
			
	/**
	 * 汽车门窗以及大灯状态通知
	 * @param actType	控制对象类型
	 * @param actNum	控制对象编号
	 * @param actState	动作状态设置
	 * 
	 * 设备名称 actType值 actNum编号范围	actState状态定义
	 *  中控门锁	0x01	1			0x01:开启/0x02:锁闭			*BUGFIX：0x01:锁使能锁止；0x02:锁解锁*
	 *  车窗		0x02	1~7			0x01:升/0x02:降/0x03:停止
	 *  后备箱  	0x03	1			0x01:开启/0x02:关闭			*BUGFIX：0x01:锁使能锁止；0x02:锁解锁*
	 *  充电盖  	0x04	1			0x01:开启/0x02:关闭
	 *  大灯		0x05	1			0x01:远光灯/0x02:近光灯/0x03:关闭
	 *  双跳		0x06	1			0x01:开启/0x02:关闭
	 *  前雾灯	0x07	1			0x01:开启/0x02:关闭
	 *  小灯		0x08	1			0x01:开启/0x02:关闭
	 *  电池仓门	0x0a	1			0x01:升/0x02:降/0x03：停止
	 *  后雾灯	0x0c	1			0x01:开启/0x02:关闭	 
	 *  
	 *  上述值除了状态定义中的值，其他值都无效。车窗和车门编号顺序一致，
	 *  从驾驶员侧按照之字形计数，前左/前右/后左/后右...天窗编号为7。
	 */
	//void setCar_Action(int actType, int actNum, int actState);
		
	/**
	 * 获取汽车门窗以及大灯状态
	 * @param array_info 获取车身设备状态
	 * @return 			返回值：0表示BCM正常;1表示BCM掉线
	 * 
	 *	
	 * 车身状态定义：
	 *array_info[0] 车窗1状态:0x01:升/0x02:降/0x03:停止  
	 *array_info[1]	车窗2状态:0x01:升/0x02:降/0x03:停止
	 *array_info[2]	车窗3状态:0x01:升/0x02:降/0x03:停止
	 *array_info[3]	车窗4状态:0x01:升/0x02:降/0x03:停止
	 *array_info[4] 车窗5状态:0x01:升/0x02:降/0x03:停止
	 *array_info[5]	车窗6状态:0x01:升/0x02:降/0x03:停止
	 *array_info[6]	车窗7状态:0x01:升/0x02:降/0x03:停止
	 *array_info[7]	车门锁状态：0x01:开启/0x02:锁闭		*BUGFIX：0x01:锁使能锁止；0x02:锁解锁*
	 *array_info[8] 后备箱状态：0x01:开启/0x02:关闭		*BUGFIX：0x01:锁使能锁止；0x02:锁解锁*
	 *array_info[9]	充电盖状态：0x01:开启/0x02:关闭
	 *array_info[10] 大灯状态：0x01:远光灯/0x02:近光灯/0x03:关闭
	 *array_info[11] 小灯状态：0x01:开启/0x02:关闭
	 *array_info[12]	前雾灯状态：0x01:开启/0x02:关闭
	 *array_info[13]	后雾灯状态：0x01:开启/0x02:关闭
	 *array_info[14]	双跳状态：0x01:开启/0x02:关闭
	 *array_info[15]	电池舱门状态：0x01:升/0x02:降/0x03:停止
	 *  
	 *  从驾驶员侧按照之字形计数，前左/前右/后左/后右...天窗编号为7。
	 */
	//int getCarState(out int[] array_info);
	

	public CarSettingDriver(IECarDriver R_service) {
		this.R_service = R_service;
	}

	//* @return			0表示BCM正常;1表示BCM掉线
	public int retreveCarInfo() throws RemoteException {

		return R_service.getCarState(info);
	}

	//array_info[7]	车门锁状态：0x01:开启/0x02:锁闭
	public boolean isDoorsLocked() throws RemoteException {
		return (info[7] != 0x01);
	}
	// 中控门锁 0x01 1 0x01:开启/0x02:锁闭
	public void setDoorsLocked(boolean isDoorsLocked) throws RemoteException {
		R_service.setCar_Action(0x01, 1, isDoorsLocked?0x02:0x01);	//0x01:锁使能锁止；0x02:锁解锁
	}
	
	//array_info[9]	充电盖状态：0x01:开启/0x02:关闭
	public boolean isChargeDoorOpen() throws RemoteException {
		return (info[9] == 0x01);
	}
	// 充电盖 0x04 1 0x01:开启/0x02:关闭
	public void setChargeDoorOpen(boolean isChargeDoorOpen) throws RemoteException {
		R_service.setCar_Action(0x04, 1, isChargeDoorOpen?0x01:0x02);	
	}
	
	//array_info[8] 后备箱状态：0x01:开启/0x02:关闭
	public boolean isBackDoorOpen() throws RemoteException {
		return (info[8] == 0x01);
	}
	// 后备箱 0x03 1 0x01:开启/0x02:关闭
	public void setBackDoorOpen(boolean isBackDoorOpen) throws RemoteException {
		R_service.setCar_Action(0x03, 1, isBackDoorOpen?0x02:0x01);		//0x01:锁使能锁止；0x02:锁解锁
	}

	// array_info[11] 小灯状态：0x01:开启/0x02:关闭
	public boolean isPositionLightOn() throws RemoteException {
		return (info[11] == 0x01);
	}
	// 小灯		0x08	1			0x01:开启/0x02:关闭
	public void setPositionLightOn(boolean isPositionLightOn) throws RemoteException {
		R_service.setCar_Action(0x08, 1, isPositionLightOn?0x01:0x02);
	}

	
	// array_info[12]	前雾灯状态：0x01:开启/0x02:关闭
	public boolean isFogLightOn() throws RemoteException {
		return (info[12] == 0x01);
	}
	// 前雾灯	0x07	1			0x01:开启/0x02:关闭
	public void setFogLightOn(boolean isFogLightOn) throws RemoteException {
		R_service.setCar_Action(0x07, 1, isFogLightOn?0x01:0x02);
	}

	
	// array_info[13]	后雾灯状态：0x01:开启/0x02:关闭
	public boolean isFogLightRearOn() throws RemoteException {
		return (info[13] == 0x01);
	}
	// 后雾灯	0x0c	1			0x01:开启/0x02:关闭
	public void setFogLightRearOn(boolean isFogLightOn) throws RemoteException {
		R_service.setCar_Action(0x0a, 1, isFogLightOn?0x01:0x02);
	}
	
	//array_info[14]	双跳状态：0x01:开启/0x02:关闭
	public boolean isFlashLightOn() throws RemoteException {
		return (info[14] == 0x01);
	}
	// 双跳		0x06	1			0x01:开启/0x02:关闭
	public void setFlashLightOn(boolean isFlashLightOn) throws RemoteException {
		R_service.setCar_Action(0x06, 1, isFlashLightOn?0x01:0x02);
	}
	
	
	//大灯状态：0x01:远光灯/0x02:近光灯/0x03:关闭
	public enum eMainLightState {
		OFF(0x03), NEAR_LIGHT(0x02), FAR_LIGHT(0x01);

		private int nState;
		private eMainLightState(int _nState) {
			this.nState = _nState;
		}
		
		int getIndex() {
			return nState;
		}
	}
	// array_info[10] 大灯状态：0x01:远光灯/0x02:近光灯/0x03:关闭
	public eMainLightState getMainLightState() throws RemoteException {
		switch (info[10]) {
		case 0x01:
			return eMainLightState.FAR_LIGHT;
		case 0x02:
			return eMainLightState.NEAR_LIGHT;
		case 0x03:
		default:
			return eMainLightState.OFF;
		}
	}
	// 大灯 0x05 1 0x01:远光灯/0x02:近光灯/0x03:关闭
	public void setMainLightState(eMainLightState mainLightState)
			throws RemoteException {
		R_service.setCar_Action(0x05, 1, mainLightState.getIndex());
	}

	/*
	 *array_info[0] 车窗1状态:0x01:升/0x02:降/0x03:停止  
	 *array_info[1]	车窗2状态:0x01:升/0x02:降/0x03:停止
	 *array_info[2]	车窗3状态:0x01:升/0x02:降/0x03:停止
	 *array_info[3]	车窗4状态:0x01:升/0x02:降/0x03:停止
	 *array_info[4] 车窗5状态:0x01:升/0x02:降/0x03:停止
	 *array_info[5]	车窗6状态:0x01:升/0x02:降/0x03:停止
	 *array_info[6]	车窗7状态:0x01:升/0x02:降/0x03:停止
	 *  从驾驶员侧按照之字形计数，前左/前右/后左/后右...天窗编号为7。
	*/
	public enum eWindow {
		LEFT(1), RIGHT(2),LEFT_2(3),RIGHT_2(4),LEFT_3(5),RIGHT_3(6),SKY(7);
		private int nIndex;
		private eWindow(int nIndex) {
			this.nIndex = nIndex;
		}
		public int getIndex() {
			return this.nIndex;
		}
	}
	public enum eWinDoorActionState {
		STOPPED(0x03), OPENING(0x02), CLOSING(0x01);
		private int nState;
		private eWinDoorActionState(int _nState) {
			this.nState = _nState;
		}
		public int getState() {
			return this.nState;
		}
	}
	public eWinDoorActionState getWindowAction(eWindow win) throws RemoteException {
		
		int i = win.getIndex() - 1;
		
		switch (info[i]) {
		case 0x01:
			return eWinDoorActionState.CLOSING;
		case 0x02:
			return eWinDoorActionState.OPENING;
		case 0x03:
		default:
			return eWinDoorActionState.STOPPED;
		}
	}
	// 车窗		0x02	1~7			0x01:升/0x02:降/0x03:停止
	public void setWindowAction(eWindow win, eWinDoorActionState action)
			throws RemoteException {
		R_service.setCar_Action(0x02, win.getIndex(), action.getState());
	}

	
	// array_info[15]	电池舱门状态：0x01:升/0x02:降/0x03:停止
	public eWinDoorActionState getBettaryDoorAction(eWindow win) throws RemoteException {
		switch (info[15]) {
		case 0x01:
			return eWinDoorActionState.CLOSING;
		case 0x02:
			return eWinDoorActionState.OPENING;
		case 0x03:
		default:
			return eWinDoorActionState.STOPPED;
		}
	}
	// 电池仓门	0x0a	1			0x01:升/0x02:降/0x03：停止
	public void setBettaryDoorAction(eWinDoorActionState action)
			throws RemoteException {
		R_service.setCar_Action(0x0a, 1, action.getState());
	}
	
	//一键开窗
	public void triggerOneKeyWinOpen() throws RemoteException {
			R_service.set_OneKeyOpenWindow(0);
	}
}
