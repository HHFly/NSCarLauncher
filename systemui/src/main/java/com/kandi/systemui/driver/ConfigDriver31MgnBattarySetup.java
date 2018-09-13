package com.kandi.systemui.driver;

import android.os.RemoteException;

import com.driverlayer.kdos_driverServer.IECarDriver;

public class ConfigDriver31MgnBattarySetup {
	private IECarDriver R_service;
	
	public ConfigDriver31MgnBattarySetup(IECarDriver R_service) {
		this.R_service = R_service;
	}
	
	/**
	 * 获取当前电池充电设置参数
	 * @param param		参数数组
	 * @return 			成功返回0，失败返回负值错误代码
	 * 
	 * 参数数组定义
	 *		param[0]	绝缘报警值					(单位 K-Ohm)
	 *		param[1]	绝缘故障值					(单位 K-Ohm)
	 *		param[2]	电池总电压低压报警值		(单位 100mV)
	 *		param[3]	电池总电压放电低压故障值	(单位 100mV)
	 *		param[4]	降流开启单体电压			(单位 mV)
	 *		param[5]	充电停止单体电压			(单位 mV)
	 *		param[6]	充电停止温度				(单位:摄氏度)
	 */
	//int R_service.getBettaryParam(out int[] param);
	
	/**
	 * 设置当前电池充电设置参数
	 * @param param		参数数组
	 * @return 			成功返回0，失败返回负值错误代码
	 * 
	 * 参数数组定义
	 *		param[0]	充电停止总压			(单位 0.1V)
	 *		param[1]	充电停止单体电压			(单位 1mV)
	 *		param[2]	充电停止温度			(单位 1度)
	 *		param[3]	充电停止电流			(单位 0.1A)
	 *		param[4]	降流单体电压			(单位 1mV)
	 *		param[5]	降流延时时间			(单位 1S)
	 *		
	 *		param[6]	Step0_Charger_temp			(单位 1度)
	 *		param[7]	Step0_Charger_cellv			(单位:mV)
	 *		param[8]	Step0_Charger_current		(单位：0.1A)
	 *		param[9]	Step1_Charger_temp			(单位 1度)
	 *		param[10]	Step1_Charger_cellv			(单位:mV)
	 *		param[11]	Step1_Charger_current		(单位：0.1A)
	 *		param[12]	Step2_Charger_temp			(单位 1度)
	 *		param[13]	Step2_Charger_cellv			(单位:mV)
	 *		param[14]	Step2_Charger_current		(单位：0.1A)
	 *		
	 *		param[15]	Step0_Dis_temp		(单位 1度)
	 *		param[16]	Step0_Dis_cellw		(单位:mV)
	 *		param[17]	Step0_Dis_celle		(单位：mV)
	 *		param[18]	Step1_Dis_temp		(单位 1度)
	 *		param[19]	Step1_Dis_cellw		(单位:mV)
	 *		param[20]	Step1_Dis_celle		(单位：mV)
	 *		param[21]	Step2_Dis_temp		(单位 1度)
	 *		param[22]	Step2_Dis_cellw		(单位:mV)
	 *		param[23]	Step1_Dis_celle		(单位：mV)
	 */
	//int R_service.setBettaryParam(in int[] param);
	
	
	private int[] param = new int[24]; 

	//call by View.onResume()
	public int retrieveBattaryParam()  throws RemoteException {
		return this.R_service.getBattaryParam(param);
	}
	
	//call by View.SaveButton.onClick()
	public int commitBattaryParam()  throws RemoteException {
		return this.R_service.setBattaryParam(param);
	}

	//param[0]	充电停止总压			(单位 0.1V)
	public int getChargerStopVol() {
		return param[0];
	}
	
	//param[1]	充电停止单体电压			(单位 1mV)
	public int getChargerStopCellVol() {
		return param[1];
	}
	
	//	param[2]	充电停止温度			(单位 1度)
	public int getChargerStopTemp() {
		return param[2];
	}
	//	param[3]	充电停止电流			(单位 0.1A)
	public int getChargerStopCurr() {
		return param[3];
	}
	//	param[4]	降流单体电压			(单位 1mV)
	public int getReduceCell() {
		return param[4];
	}
	//	param[5]	降流延时时间			(单位 1S)
	public int getReduceTime() {
		return param[5];
	}
	//	param[6]	Step0_Charger_temp			(单位 1度)
	public int getChargerLowTemp() {
		return param[6];
	}
	//	param[7]	Step0_Charger_cellv			(单位:mV)
	public int getChargerLowCell() {
		return param[7];
	}
	//	param[8]	Step0_Charger_current		(单位：0.1A)
	public int getChargerLowCurr() {
		return param[8];
	}
	//	param[9]	Step1_Charger_temp			(单位 1度)
	public int getChargerNormalTemp() {
		return param[9];
	}
	//	param[10]	Step1_Charger_cellv			(单位:mV)
	public int getChargerNormalCell() {
		return param[10];
	}
	//	param[11]	Step1_Charger_current		(单位：0.1A)
	public int getChargerNormalCurr() {
		return param[11];
	}
	//	param[12]	Step2_Charger_temp			(单位 1度)
	public int getChargerHighTemp() {
		return param[12];
	}
	//	param[13]	Step2_Charger_cellv			(单位:mV)
	public int getChargerHighCell() {
		return param[13];
	}
	//	param[14]	Step2_Charger_current		(单位：0.1A)
	public int getChargerHighCurr() {
		return param[14];
	}
	//	param[15]	Step0_Dis_temp		(单位 1度)
	public int getDischargerLowTemp() {
		return param[15];
	}
	//	param[16]	Step0_Dis_cellw		(单位:mV)
	public int getDischargerLowCellw() {
		return param[16];
	}
	//	param[17]	Step0_Dis_celle		(单位：mV)
	public int getDischargerLowCelle() {
		return param[17];
	}
	//	param[18]	Step1_Dis_temp		(单位 1度)
	public int getDischargerNormalTemp() {
		return param[18];
	}
	//	param[19]	Step1_Dis_cellw		(单位:mV)
	public int getDischargerNormalCellw() {
		return param[19];
	}
	//	param[20]	Step1_Dis_celle		(单位：mV)
	public int getDischargerNormalCelle() {
		return param[20];
	}
	//	param[21]	Step2_Dis_temp		(单位 1度)
	public int getDischargerHighTemp() {
		return param[21];
	}
	//	param[22]	Step2_Dis_cellw		(单位:mV)
	public int getDischargerHighCellw() {
		return param[22];
	}
	//	param[23]	Step1_Dis_celle		(单位：mV)
	public int getDischargerHighCelle() {
		return param[23];
	}

}
