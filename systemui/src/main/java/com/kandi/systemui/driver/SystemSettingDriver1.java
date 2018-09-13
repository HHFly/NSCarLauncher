package com.kandi.systemui.driver;

import android.os.RemoteException;

public class SystemSettingDriver1 {
	private static SystemSettingDriver1 instance;
	public static SystemSettingDriver1 getInstance() {
		if (instance == null) {
			instance = new SystemSettingDriver1();
		}
		return instance;
	}
	public int qing() throws RemoteException {
		return 0;
	}
	public int zhengchang() throws RemoteException {
		return 0;
	}
	public int zhong() throws RemoteException {
		return 0;
	}
	public int yundong() throws RemoteException {
		return 0;
	}
	public int jieneng() throws RemoteException {
		return 0;
	}
	public int chumokongzhikaiguan() throws RemoteException {
		return 0;
	}
}
