package com.kandi.systemui.driver;

import android.os.RemoteException;

import com.driverlayer.os_driverServer.IECarDriver;

public class AirConditionDriver {
	private IECarDriver R_service;
	
	public AirConditionDriver(IECarDriver R_service) {
		this.R_service = R_service;
	}
	
	
	/**
	 * 设置汽车空调工作参数
	 * 接口参数中没有被按下的按钮下发无效值0xffff;
	 * @param isOpenAc	0x01:关闭；0x02:开启
	 * 		  isOpenPtc 0x01:关闭；0x02:开启
	 * 		  
	 * @param mode  	bit0-3空调工作模式:1:吹面；2：吹面+吹脚；3：吹脚；4：吹脚+除霜;5:除霜
	 					bit4: 为1表示开启内循环，为0默认为外循环；
	 					--------------------------------------------------
	 					--bit7--bit6--bit5--bit4--bit3--bit2--bit1--bit0--
	 					-- 保留 --保留  -- 保留--内循环--[     空调工作模式选择            ]--
	 					--------------------------------------------------
	 * @param temp  	设置温度(单位摄氏度)，范围18-32摄氏度；
	 * @param windSpeed	风速，1到8，共8档； 9表示关闭风机；
	 * @return			设置成功返回0，失败返回负值错误代码 
	 */
	//int setAirCon_Para(int isOpenAc,int isOpenPtc, int mode, int temp, int windSpeed);
	
	
	/**
	 * 读取汽车空调工作参数	
	 * @param status	数组长度为3，数据定义：
	 					status[0]	车内环境温度:单位1摄氏度，范围：-40-128
					  	status[1]	车外环境温度:(同上)
						status[2]	车内湿度：0-100%，分辨率1；
						status[3]	设定湿度：单位1摄氏度，范围：-40-128	
						status[4]	0x00:AC+PTC关闭;	
					 				bit0-bit1:1开启AC，0关闭AC;
				 					bit2-bit3:1开启PTC，0关闭PTC; 
					 				bit4-bit5:1开启风扇，0关闭风扇;  					
				 					--------------------------------------------------
				 					--bit7--bit6--bit5--bit4--bit3--bit2--bit1--bit0--
				 					-- 保留 --保留  --[	风扇控制    ]--[	PTC控制    ]--[  AC控制    ]--
				 					--------------------------------------------------	
					    status[5]  	bit0-3空调工作模式:1:吹面；2：吹面+吹脚；3：吹脚；4：吹脚+除霜;5:除霜
				 					bit4: 为1表示开启内循环，为0默认为外循环；
				 					--------------------------------------------------
				 					--bit7--bit6--bit5--bit4--bit3--bit2--bit1--bit0--
				 					-- 保留 --保留  -- 保留--内循环--[     空调工作模式选择            ]--
				 					--------------------------------------------------				 					
				 		status[6]	风速，1到8，共8档； 																	
	 * @return			1空调离线,0空调正常；
	 */
	//int GetAirCon_Status(out int[] status);

	
	//Air blow mode
	public enum eAirBlowMode {
		BLOW_UNDEF(0x00), BLOW_HEAD(0x01), BLOW_HEAD_FOOT(0x02), BLOW_FOOT(0x03), BLOW_FOOT_DEMIST(0x04),BLOW_DEMIST(0x05);
		private int nMode;
		private eAirBlowMode(int nMode) {
			this.nMode = nMode;
		}
		public int getMask() {
			return this.nMode;
		}
	}
	
	private boolean isACPowerOn = false;
	private boolean isPTCPowerOn = false;
	private boolean isWindPowerOn = false;
	private boolean isInternalCycle = false;
	
	private int nPresetTemp = 25;
	private int nPresetWindSpeed = 1;
	private eAirBlowMode blowMode = eAirBlowMode.BLOW_UNDEF;
	
	private int[] nTemp = new int[7];

	//* @return			1空调故障,0空调正常
	public int retreveACInfo() throws RemoteException {
		
		int ret = R_service.GetAirCon_Status(nTemp);
		if(ret == 0) {

			isACPowerOn 	= ((nTemp[4] & 0x03) == 0x01);	//bit 0000-0011 = 0x03  
			isPTCPowerOn	= ((nTemp[4] & 0x0c) == 0x04); 	//bit 0000-1100 = 0x0C
			isWindPowerOn	= ((nTemp[4] & 0x30) == 0x10);	//bit 0011-0000 = 0x30
			
			switch(nTemp[5] & 0x0f) {
			case 1:
				blowMode = eAirBlowMode.BLOW_HEAD;
				break;
			case 2:
				blowMode = eAirBlowMode.BLOW_HEAD_FOOT;
				break;
			case 3:
				blowMode = eAirBlowMode.BLOW_FOOT;
				break;
			case 4:
				blowMode = eAirBlowMode.BLOW_FOOT_DEMIST;
				break;
			case 5:
				blowMode = eAirBlowMode.BLOW_DEMIST;
				break;
			default:
				blowMode = eAirBlowMode.BLOW_UNDEF;
			}

			isInternalCycle = ((nTemp[5] & 0x10) != 0);		//bit 0001-0000 = 0x10

			nPresetTemp		= nTemp[3];
			
			if(isWindPowerOn) {
				nPresetWindSpeed = nTemp[6];
			}

		}
		else {
			
		}
		
		
		return ret;
	}
	
	// * @return			设置成功返回0，失败返回负值错误代码 
	public int commitACInfo() throws RemoteException {

		int mode = blowMode.getMask() + (isInternalCycle?0x10:0);	//bit 0001-0000 = 0x10
		
		int nSpeedSetting = isWindPowerOn?nPresetWindSpeed:9;	//返回9表示关闭风机
		return 	R_service.setAirCon_Para(isACPowerOn?0x2:0x1, isPTCPowerOn?0x2:0x1, mode, nPresetTemp, nSpeedSetting);

	}
	
	//Current Temperature
	//static int debug=0;
	public int getInsideTemp() {
		//debug = ((debug+20)+1)%50-20;
		//Log.d("AirConditionDriver","getInsideTemp()="+debug);
		//return debug;
		return nTemp[0];
	}
	public int getOutsideTemp() {
		return nTemp[1];
	}
	public int getHumidity() {
		return nTemp[2];
	}
	
	//PTC Power
	public boolean isPtcPowerOn() {
		return this.isPTCPowerOn;
	}
	public int setPtcPowerOn(boolean isPowerOn) throws RemoteException {
		this.isPTCPowerOn = isPowerOn;
		return R_service.setAirCon_Para(0xffff,isPTCPowerOn?0x2:0x1, 0xffff, 0xffff, 0xffff);
	}
	
	//AC Power
	public boolean isACPowerOn() {
		return this.isACPowerOn;
	}
	public int setACPowerOn(boolean isPowerOn) throws RemoteException {
		this.isACPowerOn = isPowerOn;
		return R_service.setAirCon_Para(isACPowerOn?0x2:0x1,0xffff, 0xffff, 0xffff, 0xffff);
	}
	
	//Wind Power
	public boolean isWindPowerOn() {
		return (this.isWindPowerOn);
	}
	public int setWindPowerOn(boolean isPowerOn)  throws RemoteException {
		this.isWindPowerOn = isPowerOn;
		return R_service.setAirCon_Para(0xffff, 0xffff, 0xffff, 0xffff, isWindPowerOn?nPresetWindSpeed:9);
	}

	//Preset Temperature
	public int getPresetTemp() {
		return this.nPresetTemp;
	}
	int dbgTmpCount;
	public int setPresetTemp(int temp) throws RemoteException {
		this.nPresetTemp = temp;
		//ToastUtil.showDbgToast(null, "setTempDBG(#"+(dbgTmpCount++)+"):setPresetTemp=" + temp);
		return R_service.setAirCon_Para(0xffff,0xffff, 0xffff, nPresetTemp, 0xffff);
	}
	
	//Wind Speed
	public int getWindSpeed() {
		return this.nPresetWindSpeed;
	}
	int dbgWindCount;
	public int setWindSpeed(int windSpeed) throws RemoteException {
		this.nPresetWindSpeed = windSpeed;
		//ToastUtil.showDbgToast(null, "setWindDBG(#"+(dbgWindCount++)+"):setPresetTemp=" + windSpeed);
		isWindPowerOn = true;
		//return R_service.setAirCon_Para(0xffff, 0xffff, 0xffff, 0xffff, isWindPowerOn?nPresetWindSpeed:9);
		return R_service.setAirCon_Para(0xffff, 0xffff, 0xffff, 0xffff, nPresetWindSpeed);
	}
	
	//Air blow mode 吹风模式
	public eAirBlowMode getAirBlowMode() {
		return blowMode;
	}
	public int setAirBlowMode(eAirBlowMode eMode) throws RemoteException {
		this.blowMode = eMode;
		
		int mode;
		switch(blowMode) {
		case BLOW_HEAD:
			mode = 1;
			break;
		case BLOW_HEAD_FOOT:
			mode = 2;
			break;
		case BLOW_FOOT:
			mode = 3;
			break;
		case BLOW_FOOT_DEMIST:
			mode = 4;
			break;
		case BLOW_DEMIST:
			mode = 5;
			break;
		case BLOW_UNDEF:
		default:
			mode = 9;
		}
		// @param mode  	
		//	bit0-3空调工作模式:1:吹面；2：吹面+吹脚；3：吹脚；4：吹脚+除霜;5:除霜
		//	bit4: 为1表示开启内循环，为0默认为外循环；
		//mode = 吹风模式(二进制 00000xxx) + 内外循环(二进制 000y0000) 
		//return R_service.setAirCon_Para(0xffff, 0xffff, mode  + (isInternalCycle?0x10:0), 0xffff, 0xffff);
		
		//INFO：需求变更2015-7-2，设置吹风模式（bit0-3不全为0）时忽略bit4循环
		return R_service.setAirCon_Para(0xffff, 0xffff, mode , 0xffff, 0xffff);

	}
	
	//Cycle mode 循环模式 
	public boolean isInternalCycle() {
		return this.isInternalCycle;
	}
	public int setInternalCycle(boolean isOn)  throws RemoteException {
		this.isInternalCycle = isOn;
		
		int mode;
		switch(blowMode) {
		case BLOW_HEAD:
			mode = 1;
			break;
		case BLOW_HEAD_FOOT:
			mode = 2;
			break;
		case BLOW_FOOT:
			mode = 3;
			break;
		case BLOW_FOOT_DEMIST:
			mode = 4;
			break;
		case BLOW_DEMIST:
			mode = 5;
			break;
		case BLOW_UNDEF:
		default:
			mode = 9;
		}
		// @param mode  	
		//	bit0-3空调工作模式:1:吹面；2：吹面+吹脚；3：吹脚；4：吹脚+除霜;5:除霜
		//	bit4: 为1表示开启内循环，为0默认为外循环；
		//mode = 吹风模式(二进制 00000xxx) + 内外循环(二进制 000y0000) 
		//return R_service.setAirCon_Para(0xffff, 0xffff, mode  + (isInternalCycle?0x10:0), 0xffff, 0xffff);
		
		//INFO：需求变更2015-7-2，设置内外循环时mode值为0
		return R_service.setAirCon_Para(0xffff, 0xffff, (isInternalCycle?0x10:0), 0xffff, 0xffff);
	}
	
}
