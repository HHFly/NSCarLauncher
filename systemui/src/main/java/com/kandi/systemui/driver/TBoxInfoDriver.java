package com.kandi.systemui.driver;

import android.os.RemoteException;

import com.driverlayer.kdos_driverServer.IECarDriver;

public class TBoxInfoDriver {
	
	private IECarDriver R_service;
	
	public TBoxInfoDriver(IECarDriver R_service) {
		this.R_service = R_service;
		
	}
	private int[] array_info = new int[2];
	/**
	 * 获取TBox信息
	 * @return	获取成功返回0，失败返回负值错误代码
	 * @throws RemoteException
	 */
	public int getTBoxInfo(){
		int ret = 0;
		try {
			ret = R_service.GetTBoxStatus(array_info);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -1;
		} catch (NullPointerException e){
			e.printStackTrace();
			return -1;
		}
		return ret;
	}
	
	/**
	 * 获取TBox当前设备状态：0->默认状态;1->设备防拆;2->天线防拆;3->正常状态
	 * @return array_info[0]
	 */
	public int getTBoxStatus() {
		return array_info[0];
	}
	
	/**
	 * 获取TBox授权状态：0->默认状态;1->授权;2->未授权；
	 * @return array_info[1]
	 */
	public int getTBoxAllowStatus() {
		return array_info[1];
	}
}
