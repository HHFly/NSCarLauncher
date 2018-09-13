package com.kandi.systemui.driver;


import android.os.RemoteException;

import com.driverlayer.kdos_driverServer.IECarDriver;

public class EnergyInfoDetailsDriver{
	
	public static final int MAX_CELL_NUM = 50;
	
	private IECarDriver R_service;
	public int nBattaryNum;
	
	public EnergyInfoDetailsDriver(IECarDriver R_service, int nBattaryNum) {
		this.R_service = R_service;
		this.nBattaryNum = nBattaryNum;
	}
	
//	int debugBatCellVol;
//	int debugBatCellTemp;
	
	/**
	 * 获取电池单体电压信息
	 * @param nCellVolArr[]		返回电池单体电压信息数组，电压单位mv，单体电压值范围：0—4500mV
	 * @return					返回电池单体数量,上限为 MAX_CELL_NUM = 50;
	 */
	public int getBatCellVol(int[] nCellVolArr) throws RemoteException {
		int len = R_service.getDetial_BatCellVol(this.nBattaryNum, nCellVolArr);
		
//		//debug data
//		for(int i=0; i<len; i++) {
//			nCellVolArr[i]+= (debugBatCellVol);
//			debugBatCellVol=(debugBatCellVol+300)%2000;
//		}
				
		return len;
	}
	
	/**
	 * 获取电池详细温度信息
	 * @param nCellTempArr[]	返回电池内部温度采样值数组，温度单位：摄氏度。
	 * @return					返回电池内部温度探头数量,上限为 MAX_CELL_NUM = 50；
	 */	
	public int getBatCellTemp(int[] nCellTempArr) throws RemoteException {
		int len = R_service.getDetial_BatTemp(this.nBattaryNum, nCellTempArr);
		
//		//debug data
//		for(int i=0; i<len; i++) {
//			nCellTempArr[i]+= (debugBatCellTemp);
//			debugBatCellTemp=(debugBatCellTemp+10)%100;
//		}
		
		return len;
	}
	
	/**
	 * 获取电池概要信息
	 * @param batNum		电池编号，正常编号介于0～3，其他值无效
	 * @param array_info	返回电池概要信息中的数字信息数组；
	 * @return				返回电池概要信息中的一些字符串数据，各参数用逗号分隔；
	 * 
	 * 电池概要信息字串顺序:
	 * 	厂商名称,
	 *	电池序列号,
	 *	硬件版本号,
	 *	软件版本号,
	 * 
	 * 电池概要整形数据信息顺序:
	 *	array_info[0]	获取成功返回0，失败返回负值错误代码
	 * 	array_info[1]	电池电压范围(0~110.0V),分辨率0.1V
	 * 	array_info[2]	电池电流范围(-1600.0~1600.0A),分辨率0.1A
	 * 	array_info[3]	SOC剩余电量(0.0~100.0%),
	 * 	array_info[4]	SOH电池健康度 (0.0~100.0%),
	 * 	array_info[5]	完整充放电次数,
	 * 	array_info[6]	累计输入kwh(>=0),分辨率0.1kwh  
	 * 	array_info[7]	累计输出kwh(>=0),分辨率0.1kwh
	 * 	array_info[8]	本次输入kwh(0~65536),分辨率0.1kwh
	 * 	array_info[9]	本次输出kwh(0~65536),分辨率0.1kwh
	 * 
	 * 	array_info[10]	电池单体数量,
	 * 	array_info[11]	最高单体电压编号,
	 *	array_info[12]	最低单体电压编号,
	 *	array_info[13]	最大单体电压(单位1mV),
	 *	array_info[14]	最低单体电压(单位1mV),
	 *	array_info[15]	单体平均值	(单位1mV),
	 *
	 * 	array_info[16]	电池内部温度探头数量,
	 *	array_info[17]	最大温度探头标号,
	 *	array_info[18]	最小温度探头标号,
	 *	array_info[19]	最大温度(分辨率1摄氏度),
	 *	array_info[20]	最小温度(分辨率1摄氏度),
	 *
	 *	array_info[21]	电池加热状态(1:on/0:off),
	 *	array_info[22]	均衡状态(1:on/0:off),
	 *	array_info[23]	单体均衡启动数目,
	 *	array_info[24]	单体均衡状态掩码(int型，每bit代表一个单体均衡状态开关，1:on/0:off),
	 *	array_info[25]	电池充电状态(0：放电；1：充电；2：搁置)
	 *	array_info[26]	电池安全性(int 0:安全，1：报警，2：故障)
	 * 	array_info[27]	硬件版本 
	 * 	array_info[28]	软件版本	 
	 * 	array_info[29]	正极绝缘电阻(0—200000 KOhm),
	 * 	array_info[30]	负极绝缘电阻(0—200000 KOhm),
	 */
	//String getGeneral_Battery(int batNum,out int[] array_info);
	
	int[] array_info = new int[32];

	String sVendor;			//厂商名称
	String sBattarySN;		//电池序列号
	String sHardwareRev;	//硬件版本号
	String sSoftwareRev;	//软件版本号
	
	/**
	 * 从驱动服务读取电池信息
	 * @return 获取成功返回0，失败返回负值错误代码
	 * @throws RemoteException
	 */
	public int retriveGeneralBatteryInfo() throws RemoteException {
		String generalInfo="";
		generalInfo = R_service.getGeneral_Battery(this.nBattaryNum, array_info);
		
		int s1 =  generalInfo.indexOf(',', 0);
		int s2 =  generalInfo.indexOf(',', s1+1);
		int s3 =  generalInfo.indexOf(',', s2+1);
		
		sVendor      = ((s1 != -1)&&(s1 != 0 ))?generalInfo.substring(0, s1):"N/A";
		sBattarySN   = ((s2 != -1)&&(s1 < s2))?generalInfo.substring(s1+1, s2):"N/A";
		sHardwareRev = ((s2 != -1)&&(s2 < s3))?generalInfo.substring(s2+1, s3):"N/A";
		sSoftwareRev = ((s2 != -1)&&(s3 < generalInfo.length()))?generalInfo.substring(s3+1, generalInfo.length()):"N/A";
		
		return array_info[0]; //成功返回0，失败返回错误码
	}

	
	/**
	 * @return 获取电池厂家名称
	 */
	public String getVendor() {
		return sVendor;
	}

	/**
	 * @return	获取电池序列号
	 */
	public String getBattarySN() {
		return sBattarySN;
	}

	/**
	 * @return 获取电池硬件版本
	 */
	public String getHardwareRev() {
		return sHardwareRev;
	}
	
	/**
	 * @return 获取电池软件版本
	 */
	public String getSoftwareRev() {
		return sSoftwareRev;
	}

	/**
	 * @return 获取电池组总电压(V),范围(0~110.0V)
	 */
	public float getBattaryVoltage() {
		return array_info[1]*0.1f;	//电池电压范围(0~110.0V),分辨率100mV
	}
	
	/**
	 * @return 电池电流(A),范围(-1600.0~1600.0A)
	 */
	public float getBattaryCurrent() {
		return array_info[2]*0.1f;
	}

	/**
	 * @return SOC剩余电量(0.0~100.0%),
	 */
	public float getSOC() {
		return array_info[3] * 0.1f;
	}
	
	/**
	 * @return SOH电池健康度 (0.0~100.0%),
	 */
	public float getSOH() {
		return array_info[4] * 0.1f;
	}
	
	/**
	 * @return array_info[5]	完整充放电次数
	 */
	public int getRechargeCycle() {
		return array_info[5];
	}

	/**
	 * @return 充电状态：1：充电，0：停止n
	 */
	public int getRechargeState() {
		return array_info[25];	//电池充电状态(1：充电，0：停止)
	}
	
	/**
	 * @return 电池安全性：0:安全，1：报警，2：故障
	 */
	public int getBattSafeState() {
		return array_info[26];	//电池安全性(int 0:安全，1：报警，2：故障)
	}
	
	/**
	 * 
	 * @return 最大单体电压(单位1mV)
	 */
	public int getMaxVolt() {
		 return array_info[13];	//最大单体电压(单位1mV),
	}
	
	/**
	 * 
	 * @return 最低单体电压(单位1mV),
	 */
	public int getMinVolt() {
		 return	array_info[14];	//最低单体电压(单位1mV),
	}
	
	
	/**
	 * @return 单体平均电压值(V),
	 */
	public float getAveBattCellVol() {
		return array_info[15]*0.001f;	//单体平均值	(单位1mV)
	}
	
	/**
	 * @return 最大温度(分辨率1摄氏度),
	 */
	public int getMaxTemp() {
		return array_info[19];
	}
	
	/**
	 * @return 最大温度(分辨率1摄氏度),
	 */
	public int getMinTemp() {
		return array_info[20];
	}
	
	/**
	 * @return 正极绝缘电阻(KOhm)
 	 * array_info[29]	正极绝缘电阻(0—200000 KOhm),
	 */
	public int getInsulatedResP() {
		return array_info[29];
	}

	/**
	 * @return 负极绝缘电阻 (KOhm)
	 * array_info[30]	负极绝缘电阻(0—200000 KOhm)
	 */
	public int getInsulatedResN() {
		return array_info[30];
	}

	
}
