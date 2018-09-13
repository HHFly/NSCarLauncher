package com.kandi.systemui.driver;

import android.os.RemoteException;

public class SystemSettingDriver2 {
	private static SystemSettingDriver2 instance;
	public static SystemSettingDriver2 getInstance() {
		if (instance == null) {
			instance = new SystemSettingDriver2();
		}
		return instance;
	}
	public int zidongliangdutiaojie() throws RemoteException {
		return 0;
	}
	public int bass() throws RemoteException {
		return 0;
	}
	public int voiceleftandright() throws RemoteException {
		return 0;
	}
	public int wifikaiguan() throws RemoteException {
		return 0;
	}
	public int bluetoothkaiguan() throws RemoteException {
		return 0;
	}
}
