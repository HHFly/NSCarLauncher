package com.kandi.systemui.driver;

import android.os.RemoteException;

import com.driverlayer.kdos_driverServer.IECarDriver;

public class ConfigDriver38MgnUpgrade {
	private IECarDriver R_service;
	
	public ConfigDriver38MgnUpgrade(IECarDriver R_service) {
		this.R_service = R_service;
	}
	
	/*
	 * @param 参数数组;
	 * @return 升级进度值，百分比；
	 * 
	 * param 定义：
	 * 	param[0]		厂商信息；
	 * 	param[1..2]		硬件版本号，高字节在前，低字节在后
	 * 	param[3..4]		软件版本号，高字节在前，低字节在后
	 * 	param[5..6]		升级文件大小（kb）
	 *	param[7]		升级状态 0x55进入升级,0xAA升级完成,01启动信息错误,02升级失败
	 */
	private int[] param = new int[8]; 
	
	public int getSystemUpdataStatus() throws RemoteException {
		return R_service.GetUpdataStatus(param);
	}
	
	public void getSystemStartUpdataHex(String path, int addr_offset, boolean falg) throws RemoteException {
		R_service.StartUpdataHex(path, addr_offset, falg);
	}
	
	//param[0]		厂商信息；
	public int getVendorInfo() {
		return param[0];
	}
	//param[1..2]		硬件版本号，高字节在前，低字节在后
	public int getHardwareVersion() {
		return param[1]<<8|param[2];
	}
	//param[3..4]		软件版本号，高字节在前，低字节在后
	public int getSoftwareVersion() {
		return param[3]<<8|param[4];
	}
	//param[5..6]		升级文件大小（kb）
	public int getFileSize() {
		return param[5]<<8|param[6];
	}
	//param[7]		升级状态 0x55进入升级,0xAA升级完成,01启动信息错误,02升级失败
	public int getUpgradeState() {
		return param[7];
	}
	
}
