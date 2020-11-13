package com.kandi.dell.nscarlauncher.candriver;

import android.os.RemoteException;

import com.driverlayer.os_driverServer.IECarDriver;


public class EmsDriver {
	private IECarDriver R_service;

	public EmsDriver(IECarDriver R_service) {
		this.R_service = R_service;
	}

	private int[] nTemp = new int[12];

	/**
	 * 读取能量管理概要信息
	 * @param ;
	 * @return
	 *
	 * param 定义：
	 * 	param[0]		剩余电量；
	 * 	param[1]		总电压
	 * 	param[2]		总电流
	 * 	param[3]		单体最高
	 *	param[4]		单体最低
	 *	param[5]		最高温度
	 *	param[6]		最低温度
	 *	param[7]		正绝缘阻值
	 *	param[8]		负绝缘阻值
	 *	param[9]		电池箱数
	 *	param[10]		soc
	 *  param[11]		剩余里程
	 */
	public int retreveEmsInfo() throws RemoteException {
		int ret = R_service.getPowerManager(nTemp);
		return ret;
	}

	public int getSocPer(){
		return nTemp[0];
	}

	public int getEms_cV(){
		return nTemp[1];
	}

	public int getEms_cA(){
		return nTemp[2];
	}

	public int getEms_highestCell(){
		return nTemp[3];
	}

	public int getEms_lowestCell(){
		return nTemp[4];
	}

	public int getEms_highestT(){
		return nTemp[5];
	}

	public int getEms_lowestT(){
		return nTemp[6];
	}

	public int getEms_IR(){
		return nTemp[7];//正绝缘
	}

	public int getEms_batnum(){
		return nTemp[9];
	}
}
