package com.kandi.systemui.driver;

import android.os.RemoteException;
import android.util.Log;

import com.driverlayer.kdos_driverServer.IECarDriver;

public class EnergyInfoDriver {
	public final int MAX_BATTARY_SIZE=4;
	
	private IECarDriver R_service;
	
	public EnergyInfoDriver(IECarDriver R_service) {
		this.R_service = R_service;
		
	}
	
	private EnergyInfoDetailsDriver[] battaryDetialInfo = new EnergyInfoDetailsDriver[MAX_BATTARY_SIZE];
	/**
	 * 获取电池组详细信息
	 * @param index 电池组序号 （0～3）
	 * @return 对应序号的电池组详细信息对象
	 */
	public EnergyInfoDetailsDriver getBattaryDetailInfo(int index) {
		return battaryDetialInfo[index];
//		try {
//			eiddList.get(index).retriveGeneralBatteryInfo();
//		} catch (RemoteException e) {
//			e.printStackTrace();
//		}
//		return eiddList.get(index);
	}
	
	/**
	 * 获取整车概要信息
	 * @param array_info	整形数据采用数组方式顺序排列上传。
	 * @return				获取成功返回0，失败返回负值错误代码
	 * 
	 * 整车概要信息数据顺序：
	 * 	array_info[0]	整车SOC,
	 * 	array_info[1]	汽车电池总电压(0~400.0V),分辨率100mV
	 * 	array_info[2]	电流环采样电流(-1600.0~1600.0A),分辨率100mA
	 * 	array_info[3]	剩余里程,KM
	 * 	array_info[4]	控制器转速(范围：0—65536 rpm),
	 * 	array_info[5]	输出扭矩(范围：0—65536Nm),
	 * 	array_info[6]	正极绝缘电阻(0—200000 KOhm),
	 * 	array_info[7]	负极绝缘电阻(0—200000 KOhm),
	 * 	array_info[8]	电池就位数(0-2),
	 * 	array_info[9]	整车电池就位掩码(short，1:电池已就位),
	 * 	
	 * 	array_info[10]	最大电压单体所在电池编号,
	 * 	array_info[11]	最大电压单体编号,
	 * 	array_info[12]	最大电压单体电压值(mV,整型),
	 * 
	 * 	array_info[13]	最小电压单体所在电池编号,
	 * 	array_info[14]	最小电压单体编号,
	 * 	array_info[15]	最小电压单体电压值(mV,整型),
	 * 
	 * 	array_info[16]	最高温度所在电池编号,
	 * 	array_info[17]	最高温度单体编号,
	 * 	array_info[18]	最高温度单体温度(摄氏度,整型),
	 * 
	 * 	array_info[19]	最低温度所在电池编号,
	 * 	array_info[20]	最低温度单体编号,
	 * 	array_info[21]	最低温度单体温度(摄氏度,整型),
	 * 
	 */
	//int R_service.getGeneral_Car(out int[] array_info);
	
	
	
	private int[] generalInfo = new int[22];
	
	//* @return				获取成功返回0，失败返回负值错误代码
	/**
	 * 从驱动服务获取整车概要信息（包括：电池就位数、整车SOC、剩余里程等信息）
	 * 获取整车概要信息
	 * @return				获取成功返回0，失败返回负值错误代码
	 * @throws RemoteException
	 */
	public int retreveGeneralInfo() throws RemoteException {
		int ret = R_service.getGeneral_Car(generalInfo);
		if(ret == 0) {
			Log.i("EnergyInfoDriver", ".getBattaryNum="+getBattaryCabinNum());
			//for(int i=0; i < getBattaryCabinNum(); i++) {
			for(int i=0; i < MAX_BATTARY_SIZE; i++) {
				battaryDetialInfo[i] = new EnergyInfoDetailsDriver(R_service, i);
				battaryDetialInfo[i].retriveGeneralBatteryInfo();
				
			}
		}
		return ret;
	}
	
	/**
	 * @return array_info[8]	电池就位数(0-4)
	 */
	public int getBattaryCabinNum() {
		return generalInfo[8];
	}
	
	/**
	 * @return array_info[9] 整车电池就位掩码(short，1:电池已就位), bit0-3对应电池ABCD
	 */
	public int getBattaryCabinMask() {
		return generalInfo[9];
	}
	
	/**
	 * @return 电池是否就位， 整车电池就位掩码(short，1:电池已就位),
	 */
	public boolean isBattarySet(int index) {
		return (generalInfo[9] & (1<<index)) != 0;
	}

	/**
	 * @return array_info[0]	整车SOC
	 */
	public int getSOC() {
		return generalInfo[0];
	}
	
	/**
	 * @return array_info[3]	剩余里程,KM
	 */
	public int getRemainMileage() {
		return generalInfo[3];
	}

//	/**
//	 * @return 正极绝缘电阻(KOhm)
// 	 * 	array_info[6]	正极绝缘电阻(0—200000 KOhm),
//	 */
//	public int getInsulatedResP() {
//		return generalInfo[6];
//	}
//
//	/**
//	 * ？？？
//	 * @return 负极绝缘电阻 (KOhm)
//	 * 	array_info[7]	负极绝缘电阻(0—200000 KOhm),
//
//	 */
//	public int getInsulatedResN() {
//		return generalInfo[7];
//	}
	
	/**
	 * 获取历史趋势图数据
	 * @param nTrendType	曲线类型：1:电流(A)/2:电量(%)/3:发动机功率(kw)
	 * @param nDataLen		所获取采样数量，从当前时间算起的历史数据
	 * @param nInterval		采样间隔ms
	 * @param nValueArr		数据值数组，数组长度 >= nDataLen
	 * @return				实际返回的采样量
	 */
	//int R_service.Ecoc_getTrendData(int nTrendType, int nDataLen, int nInterval,out int[] nValueArr);		

	double dbgShift=0;
	/**
	 * 例：获取15分钟30采样点电流趋势图数据
	 * int nTrendType = 1	//曲线类型：1:电流(A)
	 * int time = 15;		//获取15分钟曲线
	 * int samples = 10;	//10采样点
	 * int[] nValueArr = new int[samples];	
	 * int len = getTrendData( nTrendType, samples,  time*60*1000/samples, nValueArr);
	 */
	public int getTrendData(int nTrendType, int nDataLen, int nInterval, int[] nValueArr) throws RemoteException {
		/*
		//debug data
		final double pi=3.1415926;
		double a;
		switch(nTrendType) {
		case 1:	//I
			a=10;
			break;
		case 2:	//%
			a=100;
			break;
		case 3:	//P
			a=1000;
			break;
		default:
			a=5;
		}
		for(int i=0; i<nDataLen;i++) {
			nValueArr[i]=(int)( (Math.sin(pi*i*10000/nInterval+dbgShift)+1)*a/2.0+0.5);
		}
		dbgShift+=pi/10;
		return nDataLen;
		/*/
		return R_service.Ecoc_getTrendData( nTrendType, nDataLen, nInterval, nValueArr);
		//*/
	}

	
}
