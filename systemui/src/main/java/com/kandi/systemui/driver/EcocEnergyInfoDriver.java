package com.kandi.systemui.driver;

import android.os.RemoteException;

import com.driverlayer.os_driverServer.IECarDriver;

public class EcocEnergyInfoDriver {
	public final int MAX_BATTARY_SIZE=4;
	
	private IECarDriver R_service;
	
	public EcocEnergyInfoDriver(IECarDriver R_service) {
		this.R_service = R_service;
		
	}
	
	
	/**
	 * 获取整车概要信息
	 * @param array_info	整形数据采用数组方式顺序排列上传。
	 * @return				获取成功返回0，失败返回负值错误代码
	 * 
	 * 整车概要信息数据顺序：
	 * 	array_info[0]	整车SOC,
	 * 	array_info[1]	剩余里程,KM
	 * 	array_info[2]	控制器转速(范围：0—65536 rpm),
	 * 	array_info[3]	正极绝缘电阻(0—200000 KOhm),
	 * 	array_info[4]	负极绝缘电阻(0—200000 KOhm),
	 * 	array_info[5]	电池就位数(0-4), 
	 * 	array_info[6]	剩余电量,1KWH, 
	 * 	array_info[7]	车速,1km/h,  
	 * 	array_info[8]	充电线连接状态:0未连接，1连接, 
	 * 	array_info[9]	充电状态:0:停止；1:启动 2:故障停止    
	 */
	//int Ecoc_getGeneral_Car(out int[] array_info);
	

	
	private int[] array_info = new int[12];
	
	//* @return				获取成功返回0，失败返回负值错误代码
	/**
	 * 从驱动服务获取整车概要信息（包括：电池就位数、整车SOC、剩余里程等信息）
	 * 获取整车概要信息
	 * @return				获取成功返回0，失败返回负值错误代码
	 * @throws RemoteException
	 */
	public int retreveGeneralInfo() throws RemoteException {
		if(R_service!=null) {
			int ret = R_service.getPowerManager(array_info);
			return ret;
		}
		return -1;
	}
	
	/**
	 * @return array_info[0]	整车SOC
	 */
	public float getSOC() {
		return array_info[10]/10.0f;
	}
	
	/////////////////////////////////////////
	//模拟器无法模拟充电线连接状态及充电状态，用广播事件切换
	////
	
	public  static int debugChargeGunState=0;
	public  static int debugChargingState=0;
	
	/**
	 * @return array_info[8]	充电线连接状态:0未连接，1连接, 
	 */
	public int getChargeGunState() {
		//return array_info[8];
		return debugChargeGunState;
	}

	/**
	 * @return array_info[9]	充电状态:0:停止；1:启动 2:故障停止
	 */
	public int getCargingState() {
		//return array_info[9];
		return debugChargingState;
	}
	public void setChargeGunState(int state) {
		debugChargeGunState= state;
	}
	
	public void setChargingState(int state) {
		debugChargingState= state;
	}
	
}
