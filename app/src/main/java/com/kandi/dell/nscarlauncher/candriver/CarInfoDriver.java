package com.kandi.dell.nscarlauncher.candriver;

import com.driverlayer.os_driverServer.IECarDriver;

public class CarInfoDriver {
	private IECarDriver R_service;
	
	public CarInfoDriver(IECarDriver R_service) {
		this.R_service = R_service;
	}

	public int[] param1 = new int[5];

    public int getCar_Status() throws Exception{
		return R_service.getCar_Status(param1);
	}

	public int setCar_Status(int[] status) throws Exception{
		return R_service.setCar_Status(status);
	}

	/**
	 * 左右转向灯状态
	 * 		0:无状态；1:左转向灯；2:右转向灯
	 * @return
	 */
	public int getLeftRightLight(){
    	if((param1[0]>>7 & 0x01) == 1){
    		return 1;
		}
		if((param1[0]>>6 & 0x01) == 1){
			return 1;
		}
    	return 0;
	}

	/**
	 * 灯光总开关
	 * 		0:关闭；1:打开
	 * @return
	 */
	public int getTotalLight(){
		if((param1[0]>>5 & 0x01) == 1){
			return 1;
		}
		return 0;
	}

	/**
	 * 远近光灯
	 * 		0:关闭；1:近光灯；2:远光灯
	 * @return
	 */
	public int getNearFarLight(){
		if((param1[0]>>4 & 0x01) == 1){
			return 1;
		}
		if((param1[0]>>3 & 0x01) == 1){
			return 1;
		}
		return 0;
	}

	/**
	 * 超车灯
	 * 		0:关闭；1:打开
	 * @return
	 */
	public int getOvertakeLight(){
		if((param1[0]>>2 & 0x01) == 1){
			return 1;
		}
		return 0;
	}

	/**
	 * 后雾灯
	 * 		0:关闭；1:打开
	 * @return
	 */
	public int getRearfogLight(){
		return param1[2];
	}

	/**
	 * 主照明灯1
	 * 		0:关闭；1:打开
	 * @return
	 */
	public int getMainLight1(){
		return param1[3];
	}

	/**
	 * 主照明灯2
	 * 		0:关闭；1:打开
	 * @return
	 */
	public int getMainLight2(){
		return param1[4];
	}

	/**
	 * 雨刮状态
	 * 		0:关闭；1:间歇；2:低速；3:高速；4:自动
	 * @return
	 */
	public int getWiper(){
		if((param1[1]>>7 & 0x01) == 1){
			return 1;
		}
		if((param1[1]>>6 & 0x01) == 1){
			return 2;
		}
		if((param1[1]>>5 & 0x01) == 1){
			return 3;
		}
		if((param1[1] & 0x01) == 1){
			return 4;
		}
		return 0;
	}

	/**
	 * 喷淋开关
	 * 		0:关闭；1:打开
	 * @return
	 */
	public int getSpray(){
		if((param1[1]>>4 & 0x01) == 1){
			return 1;
		}
		return 0;
	}

	/**
	 * 自动大灯开关
	 * 		0:关闭；1:打开
	 * @return
	 */
	public int getAutolight(){
		if((param1[1]>>1 & 0x01) == 1){
			return 1;
		}
		return 0;
	}
}
