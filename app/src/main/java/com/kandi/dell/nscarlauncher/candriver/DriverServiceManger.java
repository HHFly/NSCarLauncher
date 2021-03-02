package com.kandi.dell.nscarlauncher.candriver;


import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import com.driverlayer.os_driverServer.IECarDriver;

/**
 * @author ivan.lv
 *
 */
public class DriverServiceManger {

	CarInfoDriver m_carinfoDriver = null;
	void initDrivers(IECarDriver R_service) {
		m_carinfoDriver = (R_service==null)?null:(new CarInfoDriver(R_service));
	}
	
	String sDrvSvrVersion = "(后台服务不可用)";
	private static final String ACTION = "com.driverlayer.os_driverServer.RemoteService";

	private static DriverServiceManger instance;
	public static DriverServiceManger getInstance() {
		if (instance == null) {
			instance = new DriverServiceManger();
		}
		return instance;
	}
	
	private IECarDriver R_service = null;
	private Context context;
	private boolean m_binderflag = false;

	
	private ServiceConnection serviceConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName arg0, IBinder arg1) {
			R_service = IECarDriver.Stub.asInterface(arg1);
			m_binderflag = true;
			Log.i("ServiceConnection","###Service Connected. Ver=" + sDrvSvrVersion);
			initDrivers(R_service);
		}

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			R_service = null;
			m_binderflag = false;
			sDrvSvrVersion="(后台服务不可用)";
			Log.i("ServiceConnection","###Service Disconnected.");
			initDrivers(R_service);
		}

	};

	private DriverServiceManger() {
	}

	public void startService(Context context) {
		this.context = context;
		Intent it = new Intent(ACTION);
		it.setPackage("com.nushine.nshlbus");
		this.context.bindService(it, serviceConnection,	Context.BIND_AUTO_CREATE);
	}

	public void stopService() {
		if (isServiceRunning()) {
			this.context.unbindService(this.serviceConnection);
		} else {
		}
	}

	public boolean isServiceRunning() {
		ActivityManager manager = (ActivityManager) this.context.getSystemService(Context.ACTIVITY_SERVICE);
		for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
			String str = service.service.getClassName();
			if ("com.nushine.nshlbus.com.driverlayer.os_driverServer.DriverManager".equals(str)) {
				return true;
			}
		}
		return false;
	}
	
	public String getVersion() {
		return this.sDrvSvrVersion;
	}

    public CarInfoDriver getCarInfoDriver() {
        return m_carinfoDriver;
    }
}
